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

import htsjdk.samtools.SAMRecord;
import hu.deltabio.core.io.SimpleFileWriter;
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
public class SAMRecordTileSaver extends ParallelTaskBasic {

    volatile Queue toSave;
    volatile boolean end = false;
    int cacheSize = 500;

    volatile HashMap<String, SimpleFileWriter> writers;

    volatile HashMap<String, ArrayList<SAMRecord>> cache;
    volatile Hashtable<String, Boolean> busy = new Hashtable<>();

    volatile Hashtable<String, Boolean> writerBusy;
    volatile boolean saveDone = false;
    volatile int workingThreads;
    volatile public int written = 0;
    SAMRecordSaveData temp;

    public SAMRecordTileSaver(HashMap<String, SimpleFileWriter> writers, int threads) {
        super(threads, false, false);
        this.writers = writers;
        cache = new HashMap<>();
        writerBusy = new Hashtable<>();
        toSave = new LinkedList();
        workingThreads = threads;
    }

    synchronized public void add(String key, SAMRecord rec) {
        if (!cache.containsKey(key)) {
            cache.put(key, new ArrayList<>(cacheSize));
        }
        if (cache.get(key).size() >= cacheSize) {
            temp = new SAMRecordSaveData(key, (ArrayList<SAMRecord>) cache.get(key).clone());
            toSave.add(temp);
            cache.get(key).clear();

            int inMemory = 0;
            for (String k : cache.keySet()) {
                inMemory += cache.get(k).size();
            }
            //System.out.println("key: " + key + " tosave: " + toSave.size() + " busy: " + busy.size() + " inMemoryObject: " + inMemory);
        }
        cache.get(key).add(rec);
    }

    public void clearCache() {
        for (String key : cache.keySet()) {
            while (toSave.size() > 20) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.out.println(toSave.size());
            }
            temp = new SAMRecordSaveData(key, (ArrayList<SAMRecord>) cache.get(key).clone());
            toSave.add(temp);
            cache.get(key).clear();
        }
        System.gc();
        System.out.println("\tTile cache cleared");
    }

    @Override
    public Runnable workingThread() {
        return new Runnable() {
            @Override
            public void run() {
                SAMRecordSaveData data = null;
                while (true) {
                    if (data != null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (end && toSave.isEmpty() && data == null) {
                        synchronized (lock) {
                            workingThreads--;
                            if (workingThreads == 0) {
                                System.out.println("Closing tiles for save...");
                                for (String key : writers.keySet()) {
                                    SimpleFileWriter w = writers.get(key);
                                    w.closeObject();

                                }
                                saveDone = true;
                                System.out.println("\tAll files saved to tiles...");
                            }
                        }
                        /*if (inMainThlockread()) {
                            for (String key : writers.keySet()) {
                                SimpleFileWriter w = writers.get(key);
                                w.closeObject();
                            }
                            saveDone = true;
                            System.out.println("All files saved to tiles...");
                        }*/
                        //System.out.println("Break  tile thread " + Thread.currentThread().getName());
                        break;
                    }
                    synchronized (lock) {
                        if (!toSave.isEmpty() && data == null) {
                            try {
                                data = (SAMRecordSaveData) toSave.remove();
                            } catch (NoSuchElementException e) {
                                toSave.clear();
                                //System.out.println("Exception: tosave " + toSave + " end: " + end);
                                System.out.println("Exception: tosave size " + toSave.size() + " " + end);
                                System.out.println("Exception: data " + data);
                                continue;
                            }
                        }
                    }
                    if (data != null && writers.containsKey(data.key)) {

                        synchronized (lock) {
                            if (busy.containsKey(data.key)) {
                                continue;
                            }
                            busy.put(data.key, true);
                            //System.out.println("\twrite: " + data.key + " " + inqueue);
                            /*if (inqueue.contains(data.key)) {
                                //coint++;
                                //if (coint % 100 == 0) {
                                //    System.out.println("Deadlock " + Thread.currentThread().getName());
                                // }
                                continue;
                            }
                            if(data.key.equals("null")) {
                                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! NULLLLL !!!!!!!!!!!!!!!!!!!");
                            }*/
                            //inqueue.add(data.key);
                        }
                        SimpleFileWriter w = writers.get(data.key);

                        for (SAMRecord current : data.recs) {
                            //try {
                            w.writeObject(current);
                            synchronized (lock) {
                                written++;
                            }
                            /*} catch (Exception e) {
                                System.out.println("  -------- current:  " + current);
                                System.out.println("  -------------- w:  " + w);
                                e.printStackTrace();
                            }*/
                        }
                        w.resetObject();
                        //inqueue.remove(data.key);
                        busy.remove(data.key);
                        data = null;
                        //coint = 0;
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
