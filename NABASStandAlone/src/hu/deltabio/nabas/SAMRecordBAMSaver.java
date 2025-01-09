/*                                                                                                                                                  
NNNNNNNN        NNNNNNNN               AAA               BBBBBBBBBBBBBBBBB               AAA                 SSSSSSSSSSSSSSS                      
N:::::::N       N::::::N              A:::A              B::::::::::::::::B             A:::A              SS:::::::::::::::S                     
N::::::::N      N::::::N             A:::::A             B::::::BBBBBB:::::B           A:::::A            S:::::SSSSSS::::::S                     
N:::::::::N     N::::::N            A:::::::A            BB:::::B     B:::::B         A:::::::A           S:::::S     SSSSSSS       +++++++       
N::::::::::N    N::::::N           A:::::::::A             B::::B     B:::::B        A:::::::::A          S:::::S                   +:::::+       
N:::::::::::N   N::::::N          A:::::A:::::A            B::::B     B:::::B       A:::::A:::::A         S:::::S                   +:::::+       
N:::::::N::::N  N::::::N         A:::::A A:::::A           B::::BBBBBB:::::B       A:::::A A:::::A         S::::SSSS          +++++++:::::+++++++ 
N::::::N N::::N N::::::N        A:::::A   A:::::A          B:::::::::::::BB       A:::::A   A:::::A         SS::::::SSSSS     +:::::::::::::::::+ 
N::::::N  N::::N:::::::N       A:::::A     A:::::A         B::::BBBBBB:::::B     A:::::A     A:::::A          SSS::::::::SS   +:::::::::::::::::+ 
N::::::N   N:::::::::::N      A:::::AAAAAAAAA:::::A        B::::B     B:::::B   A:::::AAAAAAAAA:::::A            SSSSSS::::S  +++++++:::::+++++++ 
N::::::N    N::::::::::N     A:::::::::::::::::::::A       B::::B     B:::::B  A:::::::::::::::::::::A                S:::::S       +:::::+       
N::::::N     N:::::::::N    A:::::AAAAAAAAAAAAA:::::A      B::::B     B:::::B A:::::AAAAAAAAAAAAA:::::A               S:::::S       +:::::+       
N::::::N      N::::::::N   A:::::A             A:::::A   BB:::::BBBBBB::::::BA:::::A             A:::::A  SSSSSSS     S:::::S       +++++++       
N::::::N       N:::::::N  A:::::A               A:::::A  B:::::::::::::::::BA:::::A               A:::::A S::::::SSSSSS:::::S                     
N::::::N        N::::::N A:::::A                 A:::::A B::::::::::::::::BA:::::A                 A:::::AS:::::::::::::::SS                      
NNNNNNNN         NNNNNNNAAAAAAA                   AAAAAAABBBBBBBBBBBBBBBBBAAAAAAA                   AAAAAAASSSSSSSSSSSSSSS                        
                                             developed by Gábor Jaksa and Bertalan Takács
 */
package hu.deltabio.nabas;

import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 *
 * @author Gábor Jaksa
 */
public class SAMRecordBAMSaver extends ParallelTaskBasic {

    volatile Queue toSave;
    volatile boolean end = false;
    int cacheSize = 500;

    volatile HashMap<String, SAMFileWriter> writers;

    volatile HashMap<String, ArrayList<SAMRecord>> cache;

    volatile Hashtable<String, Boolean> busy = new Hashtable<>();
    volatile boolean saveDone = false;
    volatile boolean allAdded = false;
    volatile int workingThreads = 0;

    public SAMRecordBAMSaver(HashMap<String, SAMFileWriter> writers, int threads) {
        super(threads, false, false);
        this.writers = writers;
        cache = new HashMap<>();
        this.workingThreads = threads;
    }

    synchronized public void add(String key, SAMRecord rec) {
        if (!cache.containsKey(key)) {
            cache.put(key, new ArrayList<>(cacheSize));
        }
        if (cache.get(key).size() >= cacheSize) {
            toSave.add(new SAMRecordSaveData(key, (ArrayList<SAMRecord>) cache.get(key).clone()));
            cache.get(key).clear();
        }
        cache.get(key).add(rec);
    }

    public void clearCache() {
        System.out.println("Clear cache");
        for (String key : cache.keySet()) {
            toSave.add(new SAMRecordSaveData(key, (ArrayList<SAMRecord>) cache.get(key).clone()));
            cache.get(key).clear();
        }
        System.out.println("\tCache done...");
    }

    @Override
    public void before() {
        toSave = new LinkedList();
    }

    @Override
    public Runnable workingThread() {
        return new Runnable() {
            @Override
            public void run() {
                SAMRecordSaveData data = null;
                while (true) {
                    if (end && toSave.isEmpty() && data == null) {
                        synchronized (lock) {
                            workingThreads--;
                            if (workingThreads == 0) {
                                for (String key : writers.keySet()) {
                                    SAMFileWriter w = writers.get(key);
                                    w.close();
                                }
                                saveDone = true;
                                System.out.println("\tAll SAMRecords saved to bam(s)...");
                            }
                        }
                        break;
                    }
                    synchronized (lock) {
                        /*if (!toSave.isEmpty() && data == null) {
                            data = (SAMRecordSaveData) toSave.remove();
                        }*/

                        if (!toSave.isEmpty() && data == null) {
                            try {
                                data = (SAMRecordSaveData) toSave.remove();
                            } catch (NoSuchElementException e) {
                                toSave.clear();
                                System.out.println("Exception BAM: tosave " + toSave + " end: " + end);
                                System.out.println("Exception BAM: tosave size_ " + toSave.size());
                                System.out.println("Exception BAM: data " + data);
                                continue;
                            }
                        }
                    }
                    if (data != null) {
                        synchronized (lock) {
                            if (busy.containsKey(data.key)) {
                                continue;
                            }
                            busy.put(data.key, true);
                        }
                        SAMFileWriter w = writers.get(data.key);
                        for (SAMRecord current : data.recs) {
                            if (w != null) {
                                w.addAlignment(current);
                            } else {
                                System.out.println(data.key);
                                System.out.println(writers.keySet());
                            }
                        }
                        busy.remove(data.key);
                        data = null;
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
    }
}
