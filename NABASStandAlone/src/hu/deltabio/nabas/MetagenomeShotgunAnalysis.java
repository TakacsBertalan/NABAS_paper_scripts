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

import hu.deltabio.core.Core_Utils;
import hu.deltabio.core.LongCounter;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import hu.deltabio.core.Counter;
import hu.deltabio.core.io.SimpleFileReader;
import hu.deltabio.core.io.SimpleFileWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author Gábor Jaksa
 */
public class MetagenomeShotgunAnalysis {

    /**
     * @param args the command line arguments
     */
    int maxMismatch = 10;
    int threads = 6;
    String inputFolder;
    SAMSequenceDictionary headers;
    String outputFolder;
    String taxonomy;
    volatile NCBI_Taxonomy ncbi;
    int minimumColumn = 50;
    int measureColumn = 75;
    ArrayList<String> databaseIndices;
    File[] folders;

    public static void main(String[] args) {

    }

    public MetagenomeShotgunAnalysis() {

    }

    public void setup(String outputFolder, String taxonomy, int threads, int maxMismatch, int column, int measure, ArrayList<String> indices) {
        this.outputFolder = outputFolder;
        this.threads = threads;
        this.maxMismatch = maxMismatch;
        this.taxonomy = taxonomy;
        this.minimumColumn = column;
        this.measureColumn = measure;
        this.databaseIndices = indices;
        readReferences();

    }

    public void addInputs(String inputFolder) {
        folders = new File(inputFolder).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
    }

    public void run() {
        

        for (File current : folders) {
            File[] bams = current.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".bam");
                }
            });
            for (File b : bams){
                System.out.println(b.getName());
            }
            processFile( bams, current.getName());
        }
    }

    public void processFile(File[] bams, String sampleName) {
        String out = bams[0].getParentFile().getParent();
        File bamOut = new File(outputFolder + File.separator + sampleName + ".bam");
        System.out.println(bamOut + " " + bamOut.exists());
        if (ncbi == null) {
            System.out.println("Loading taxonomy");
            NCBI_Taxonomy.taxonomyFolder = taxonomy;
            ncbi = new NCBI_Taxonomy();
            ncbi.getNames();
            ncbi.getNodes();
        }
        if (!bamOut.exists()) {
            HashMap<String, SimpleFileWriter> writers = new HashMap<>();

            File temp = createTiles(bams, writers);

            File bam = getBestAlignment(temp);
            Core_Utils.deleteFolder(temp);
            System.out.println("Alignments done");
            getCoverage(bam, minimumColumn, measureColumn);
        } else {
            File xlsx = new File(outputFolder + File.separator + sampleName + ".ShotgunResult.xlsx");
            System.out.println(xlsx + " " + xlsx.exists());
            if (xlsx.exists()) {
                return;
            }
            getCoverage(bamOut, minimumColumn, measureColumn);
        }
    }

    public void getCoverage(File bamFile, int minimumColumn, int measureColumn) {
        System.out.println("CreateCoverage");
        System.out.println(bamFile);
        
        BamTools bam = new BamTools(bamFile);
        ArrayList<String> chrs = bam.getMappedContigs(10);
        System.out.println(chrs);
        SAMFileHeader h = bam.getHeader();
        SAMSequenceDictionary dict = h.getSequenceDictionary();
        System.out.println(dict);
        System.out.println(bamFile.getParent());
        Excel excel = new Excel(bamFile.getParent() + File.separator + Core_Utils.filenameWithoutExtension(bamFile) + ".ShotgunResult.xlsx");
        
        excel.save();
        
        HashMap<String, String> hash = new HashMap<>();
        hash.put("Bacteria", "Bacteria Archea");
        hash.put("Eukaryote", "Fungi");
        hash.put("Viruses", "Viruses");
        hash.put("Archaea", "Bacteria Archea"); 
        
        Counter<String> rows = new Counter<>();
        ArrayList<String> header = new ArrayList<>();
        header.add("Acession");
        header.add("Kingdom");
        header.add("Phylum");
        header.add("Class");
        header.add("Order");
        header.add("Family");
        header.add("Genus");
        header.add("Species");
        header.add("Genome length");
        header.add("Percent");
        header.add("Read number");
        header.add("Bin list");
        header.add("Hypotetic genome coverage percent");
        header.add("Average coverage on peaks");
        header.add("Real genome coverage percent");
        header.add("Filter passed");

        CellStyle style = excel.wb.createCellStyle();
        style.setDataFormat(excel.wb.createDataFormat().getFormat("0.000%"));
        
        
        for (String c : chrs) {
            System.out.println(c);
            try {
                LongCounter<Integer> coverage = (LongCounter) bam.getCoverageOnchr(c);
                ArrayList<Integer> binList = new ArrayList<>();
                Counter<Integer> coverBins = new Counter<>();
                int genomeLength = dict.getSequence(c).getSequenceLength();
                if (genomeLength == -1) {
                    continue;
                }
                long coverSzum = 0;
                for (int i : coverage.keySet()) {
                    coverSzum += coverage.value0(i);
                }
                int window;// = (genomeLength + 999) / 1000;
                window = getWindowLength100P(genomeLength);
                int db = (genomeLength + window - 1) / window;
                for (int i = 0; i < db; i++) {
                    coverBins.increment(i, 0);
                }
                //double correction = 10000 / (double) window;
                SAMRecordIterator it = bam.getSequencesIterator(c, 1, genomeLength);
                long totalReadLength = 0;
                int readNumber = 0;
                while (it.hasNext()) {
                    SAMRecord rec = it.next();
                    coverBins.increment(rec.getStart() / window);
                    readNumber++;
                    totalReadLength += rec.getBaseQualityString().length();
                }
                //System.out.println(readNumber + " " + genomeLength);

                for (int k : coverBins.keySet()) {
                    binList.add(coverBins.value0(k));
                }
                Collections.sort(binList);
                int id = 2;
                id = Integer.parseInt(c.substring(0, c.indexOf("_")));
                String[] levels = ncbi.getLineageArrayNull(ncbi.findNodes(id));

                String key = hash.get(levels[0]);
                excel.createOrOpenWorkSheet(key);
                int row = rows.value0(key);
                if (row == 0) {
                    for (int i = 0; i < header.size(); i++) {
                        excel.addCell(0, i, header.get(i));
                    }
                }
                int col = 0;
                excel.addCell(row + 1, col, c);
                col++;

                for (int i = 0; i < levels.length; i++) {
                    excel.addCell(row + 1, col, levels[i]);
                    col++;
                }
                excel.addCell(row + 1, col, genomeLength);
                col++;
                excel.addformula(row + 1, col, "K" + (row + 2) + "/$Q$1");
                excel.getCell(row + 1, col).setCellStyle(style);
                col++;

                //excel.addCell(row + 1, col, "P");//percent
                //col++;
                double coveredHipPercent = totalReadLength / (double) genomeLength * 100;
                double realCoverPercent = coverage.keySet().size() / (double) genomeLength * 100;
                if (coveredHipPercent > 100) {
                    coveredHipPercent = 100;
                }
                double coverageValue = getCoverage(binList, window, minimumColumn, measureColumn);
                if (coveredHipPercent / (double) 3.5 >= realCoverPercent) {
                    coverageValue = 0;
                }
                excel.addCell(row + 1, col, coverageValue);//coverage
                col++;

                ArrayList<String> s = new ArrayList<>();
                for (int i : binList) {
                    s.add(Integer.toString(i));
                }
                excel.addCell(row + 1, col, String.join(", ", s));//binlist
                col++;

                excel.addCell(row + 1, col, coveredHipPercent);
                col++;
                excel.addCell(row + 1, col, coverSzum / (double) coverage.keySet().size());
                col++;
                excel.addCell(row + 1, col, realCoverPercent);
                col++;
                excel.addformula(row + 1, col, "O" + (row + 2) + ">M" + (row + 2) + "/3.5");
                col++;

                rows.increment(key);
                excel.addformula(0, 16, "SUMIF(P2:P" + (row + 2) + ",TRUE,K2:K" + (row + 2) + ")");
                //w.writeLn(c + "\t" + String.join("\t",  + "\t" + genomeLength + "\t" + getCoverage(binList, window, 74) + "\t" + Joiner.on("\t").join(binList));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        XSSFRow r1;
        for (int i = 0; i < excel.wb.getNumberOfSheets(); i++) {
            excel.createOrOpenWorkSheet(excel.wb.getSheetName(i));
            for (Row r : excel.getCurrentSheet()) {
                for (Cell c : r) {
                    if (c.getColumnIndex() == 10 && excel.getCellAsString(c).equals("0")) {
                        r1 = (XSSFRow) c.getRow();
                        if (r1.getRowNum() != 0) {
                            r1.getCTRow().setHidden(true);
                        }
                    }
                }
            }
        }
        excel.save();
    }

    int getWindowLength10K() {
        return 10000;
    }

    int getWindowLength100P(int genomeLength) {
        return (genomeLength + 100) / 100;
    }

    double getCoverage(ArrayList<Integer> binList, int window, int minimum, int measure) {
        double correction = (double) 10000 / (double) window;
        int cover = 0;
        if (binList.get(minimum) > 0) {
            cover = binList.get(measure);
        }
        return correction * cover;
    }

    public File getBestAlignment(File tempTiles) {
        System.out.println("Collect best alignments...");
        File input[] = tempTiles.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dat");
            }
        });
        String out = tempTiles.getParent();
        BamTools b = new BamTools();
        SAMFileHeader samFileHeaderTemp = new SAMFileHeader();
        samFileHeaderTemp.setSortOrder(SAMFileHeader.SortOrder.coordinate);
        SAMSequenceDictionary d = this.headers;
        String sample = tempTiles.getName().substring(5);
        samFileHeaderTemp.setSequenceDictionary(d);
        File bam = new File(out + File.separator + sample + ".bam");
        System.out.println(bam.getPath());
        SAMFileWriter writer = new SAMFileWriterFactory().setCreateIndex(true).setTempDirectory(tempTiles).makeBAMWriter(samFileHeaderTemp, false, bam, 5);
        HashMap<String, SAMFileWriter> writers = new HashMap<>();
        writers.put("main", writer);
        SAMRecordBAMSaver saver = new SAMRecordBAMSaver(writers, 6);
        saver.startProcess();
        ParallelTaskBasic task = new ParallelTaskBasic() {
            @Override
            public void allDone() {
                saver.clearCache();
                saver.end = true;
                System.out.println("Bam reading done. Waiting to close...");
                while (true) {
                    if (saver.saveDone) {
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            int index = 0;

            @Override
            public Runnable workingThread() {
                return new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            int currentIndex;
                            synchronized (lock) {
                                currentIndex = index;
                                index++;
                            }
                            if (currentIndex >= input.length) {
                                break;
                            }
                            File bamFile = input[currentIndex];
                            System.out.println("\tcollect " + currentIndex + " " + bamFile);
                            SimpleFileReader reader = new SimpleFileReader(bamFile, SimpleFileReader.FileReaderWriterType.OBJECT);
                            HashMap<String, ArrayList<SAMRecord>> map = new HashMap();
                            while (true) {
                                Object obj = reader.readObject();
                                if (obj == null) {
                                    break;
                                }
                                SAMRecord rec = (SAMRecord) obj;
                                String[] data = Core_Utils.getIlluminaCasava18dot2StyleLociOnlyCoords(rec);
                                String key = data[2] + ":" + data[3] + ":" + rec.getFirstOfPairFlag();
                                if (!map.containsKey(key)) {
                                    map.put(key, new ArrayList<>());
                                }
                                ((ArrayList<SAMRecord>) map.get(key)).add(rec);
                            }
                            for (String k : map.keySet()) {
                                if (k.endsWith("true")) {
                                    SAMRecord r1 = getCandidate(map.get(k));
                                    SAMRecord r2 = getCandidate(map.get(k.replaceAll("true", "false")));
                                    if ((r1 != null && r2 != null) && r1.getContig().equals(r2.getContig()) && Math.abs(r1.getStart() - r2.getStart()) < 5000) {
                                        saver.add("main", r1);
                                        saver.add("main", r2);
                                    }
                                }

                            }

                        }
                    }
                };
            }
        };
        return bam;
    }

    public SAMRecord getCandidate(ArrayList<SAMRecord> recs) {
        if (recs == null) {
            return null;
        }
        if (recs.size() == 1) {
            return recs.get(0);
        }
        ArrayList<SAMRecord> bests = new ArrayList<>();
        int bestMismatch = Integer.MAX_VALUE;
        for (SAMRecord rec : recs) {
            if (rec.isSecondaryOrSupplementary() /*|| rec.hasAttribute("XA")*/) {
                continue;
            }
            int nm = 0;
            if (rec.hasAttribute("NM")) {
                nm = Integer.parseInt(rec.getAttribute("NM").toString());
            }
            for (CigarElement e : rec.getCigar().getCigarElements()) {
                if (e.getOperator() == CigarOperator.S) {
                    nm += e.getLength();
                }
                if (e.getOperator() == CigarOperator.H) {
                    nm += e.getLength();
                }
            }
            if (nm < bestMismatch) {
                bests.clear();
                bestMismatch = nm;
                bests.add(rec);
            } else if (nm == bestMismatch) {
                bests.add(rec);
            }
        }
        if (bests.size() == 1) {
            SAMRecord candidate = bests.get(0);
            return candidate;
        }
        return null;
    }

    public File createTiles(File[] bams, HashMap<String, SimpleFileWriter> writers) {
        SAMRecordTileSaver saver = new SAMRecordTileSaver(writers, 12);
        saver.startProcess();
        File temp = new File(outputFolder + "temp_" + bams[0].getParentFile().getName());
        Core_Utils.deleteFolder(temp);
        temp.mkdirs();
        
        ParallelTaskBasic task = new ParallelTaskBasic(threads) {

            int index = 0;
            int process = 0;

            @Override
            public void allDone() {
                System.out.println("Clear tile cache....");
                saver.clearCache();
                saver.end = true;
                while (true) {
                    if (saver.saveDone) {
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println("read: " + process + " written: " + saver.written);

            }

            @Override
            public Runnable workingThread() {
                return new Runnable() {
                    @Override
                    public void run() {
                        int done = 0;
                        File bamFile;
                        BamTools bam;
                        SAMRecordIterator it;
                        SAMRecord rec = null;
                        String coords[];
                        String key;
                        while (true) {
                            int currentIndex;
                            synchronized (lock) {
                                currentIndex = index;
                                index++;
                            }
                            if (currentIndex >= bams.length) {
                                break;
                            }
                            bamFile = bams[currentIndex];
                            System.out.println("Start " + bamFile);
                            bam = new BamTools(outputFolder + bamFile.getParentFile().getName() + File.separator +  bamFile.getName());
                            it = bam.getAllReadIterator();
                            while (true) {
                                synchronized (lock) {
                                    if (!it.hasNext()) {
                                        System.out.println("\tdone: " + bamFile);
                                        break;
                                    }
                                    if (rec == null) {
                                        rec = it.next();
                                        process++;
                                    }
                                }
                                done++;
                                if (process % 100000 == 0) {
                                    System.out.println(Thread.currentThread().getName() + " \t" + done + " " + bamFile + " " + process);
                                    System.gc();
                                }
                                if (rec.getReferenceName().equals("*") || rec.isSecondaryOrSupplementary()) {
                                    rec = null;
                                    continue;
                                }

                                if (saver.toSave.size() > 20) {
                                    try {
                                        Thread.sleep(100);
                                        continue;
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                int nm = 0;
                                if (rec.hasAttribute("NM")) {
                                    nm = Integer.parseInt(rec.getAttribute("NM").toString());
                                }
                                for (CigarElement e : rec.getCigar().getCigarElements()) {
                                    if (e.getOperator() == CigarOperator.S) {
                                        nm += e.getLength();
                                    }
                                    if (e.getOperator() == CigarOperator.H) {
                                        nm += e.getLength();
                                    }
                                }
                                if (nm <= maxMismatch) {
                                    coords = Core_Utils.getIlluminaCasava18dot2StyleLociOnlyCoords(rec);
                                    key = coords[0] + "_" + coords[1];
                                    synchronized (lock) {
                                        if (!writers.containsKey(key)) {
                                            writers.put(key, new SimpleFileWriter(temp.getPath() + File.separator + key + ".dat", SimpleFileReader.FileReaderWriterType.OBJECT, 256));
                                        }
                                    }
                                    saver.add(key, rec);
                                }
                                rec = null;
                            }

                        }
                    }
                };
            }

        };
        return temp;
    }
    
    public static SAMSequenceDictionary readANNReferences(ArrayList<String> databaseIndices) {

        SAMSequenceDictionary headers = new SAMSequenceDictionary();
        for (String db : databaseIndices) {
            File referenceFile = new File(db + ".ann");
            System.out.println("Reading " + referenceFile.getName());
            SimpleFileReader reader = new SimpleFileReader(referenceFile, SimpleFileReader.FileReaderWriterType.PLAINTEXT);
            String line = reader.readLine();
            String line2;

            String id = "";
            String length;

            while (true) {
                line = reader.readLine();
            line2 = reader.readLine();

            // Check for null or empty lines before proceeding
            if (line == null || line.isEmpty() || line2 == null || line2.isEmpty()) {
                break;
            }

                id = line.split(" ")[1];
                length = line2.split(" ")[1];
                headers.addSequence(new SAMSequenceRecord(id, Integer.parseInt(length)));

            }
            reader.close();
        }
        return headers;
    }
    
        public void readReferences() {

        this.headers = readANNReferences(this.databaseIndices);
    }

}
