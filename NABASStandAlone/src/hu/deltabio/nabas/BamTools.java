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
import hu.deltabio.core.fasta.ACGTSequence;
import htsjdk.samtools.BAMIndex;
import htsjdk.samtools.BAMIndexMetaData;
import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileHeader.SortOrder;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamInputResource;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.util.CloserUtil;
import hu.deltabio.core.genome.Genome;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Gábor Jaksa
 */
public class BamTools {

    SAMFileHeader samFileHeader;
    List<String> COMMENT;
    File file;
    File indexfileFile;

    public String comment = "";

    SAMFileWriterFactory f;
    public File temp = Core_Utils.getRelativeFileAsFile("Temp");

    volatile public SAMFileWriter outputBam;
    public String outputSamOrBamFile;

    public BamTools(File file) {
        samFileHeader = SamReaderFactory.makeDefault().getFileHeader(file);
        COMMENT = samFileHeader.getComments();
        this.file = file;
        String bai = file.getPath();
        indexfileFile = new File(bai.substring(0, bai.length() - 4) + ".bai");
        if (!indexfileFile.exists()) {
            indexfileFile = new File(file.getPath() + ".bai");
        }
    }

    public BamTools(String file) {
        samFileHeader = SamReaderFactory.makeDefault().getFileHeader(new File(file));
        COMMENT = samFileHeader.getComments();
        this.file = new File(file);
    }

    public BamTools() {
        f = new SAMFileWriterFactory();
    }

    public void setTempWriter(String output, String sampleName) {
        outputSamOrBamFile = output + File.separator + sampleName + ".temp.bam";
        outputBam = f.makeBAMWriter(samFileHeader, false, new File(outputSamOrBamFile));
    }

    public void setNewBamHeader(Genome genom) {
        samFileHeader = new SAMFileHeader();
        samFileHeader.addComment("Created by NGS Explorer");
        samFileHeader.addComment("Genome=" + genom.getAssemblyName());
        SAMSequenceDictionary d = new SAMSequenceDictionary();
        for (int k = 0; k < genom.sequenceBoundenary.chrs.size(); k++) {
            d.addSequence(new SAMSequenceRecord(genom.sequenceBoundenary.chrs.get(k), genom.sequenceBoundenary.lengths.get(k)));
        }
        samFileHeader.setSequenceDictionary(d);
        System.out.println("USED genome:  " + genom.getAssemblyName());
    }

    public SAMFileWriter copyFrom(File outFile, BamTools bam) {
        SAMFileHeader samFileHeaderOld = bam.samFileHeader;
        SAMFileWriter outputBamTemp = new SAMFileWriterFactory().setCreateIndex(true).setTempDirectory(temp).makeBAMWriter(
                samFileHeaderOld, false, outFile, 5);
        return outputBamTemp;
    }

    public SAMFileWriter newBam(File outFile, Genome genome) {
        SAMFileHeader samFileHeaderTemp = new SAMFileHeader();
        samFileHeaderTemp.setSortOrder(SortOrder.coordinate);
        samFileHeaderTemp.addComment("Genome=" + genome.getAssemblyName());
        SAMSequenceDictionary d = new SAMSequenceDictionary();
        for (int k = 0; k < genome.sequenceBoundenary.chrs.size(); k++) {
            d.addSequence(new SAMSequenceRecord(genome.sequenceBoundenary.chrs.get(k), genome.sequenceBoundenary.lengths.get(k)));
        }
        samFileHeaderTemp.setSequenceDictionary(d);
        SAMFileWriter outputBamTemp = new SAMFileWriterFactory().setCreateIndex(true).setTempDirectory(temp).makeBAMWriter(
                samFileHeaderTemp, false, outFile, 5);
        return outputBamTemp;
    }

    
    public String getIndexStat() {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        SamReader myReader = factory.open(resource);
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader.Indexing ind = myReader.indexing();
        BAMIndex index = ind.getIndex();
        StringBuilder b = new StringBuilder();
        SAMFileHeader header = myReader.getFileHeader();
        b.append("Contig\tSequences\n");
        for (int i = 0; i < header.getSequenceDictionary().size(); i++) {
            BAMIndexMetaData meta = index.getMetaData(i);
            SAMSequenceRecord rec = header.getSequence(i);
            b.append(rec.getSequenceName()).append("\t");
            b.append(meta.getAlignedRecordCount()).append(System.lineSeparator());
        }
        return b.toString();
    }

    public ArrayList<String> getMappedContigs() {
        return getMappedContigs(1);
    }

    public ArrayList<String> getMappedContigs(int min) {
        ArrayList<String> ret = new ArrayList<>();
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        SamReader myReader = factory.open(resource);
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader.Indexing ind = myReader.indexing();
        BAMIndex index = ind.getIndex();
        SAMFileHeader header = myReader.getFileHeader();
        for (int i = 0; i < header.getSequenceDictionary().size(); i++) {
            BAMIndexMetaData meta = index.getMetaData(i);
            SAMSequenceRecord rec = header.getSequence(i);
            if (meta.getAlignedRecordCount() >= min) {
                ret.add(rec.getSequenceName());
            }
        }
        return ret;
    }

    public int getMappedReads() {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        SamReader myReader = factory.open(resource);
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader.Indexing ind = myReader.indexing();
        BAMIndex index = ind.getIndex();
        SAMFileHeader header = myReader.getFileHeader();
        int count = 0;
        for (int i = 0; i < header.getSequenceDictionary().size(); i++) {
            BAMIndexMetaData meta = index.getMetaData(i);
            count += meta.getAlignedRecordCount();
        }
        return count;
    }

    public void createStrictBam() {
        String p = file.getPath();
        p = p.substring(0, p.lastIndexOf(".")) + ".Strict.bam";
        SAMFileWriter writer = copyFrom(new File(p), this);
        SAMRecordIterator it = this.getAllReadIterator();
        while (it.hasNext()) {
            SAMRecord rec = it.next();
            if (rec.isValid() == null) {
                writer.addAlignment(rec);
            } else {
                System.out.println(rec.getCigar() + " " + rec.isValid());
            }
        }
        writer.close();
    }

    public void setTemp(File temp) {
        this.temp = temp;
    }

    public void setTemp(String temp) {
        this.temp = new File(temp);
    }

    public void index(File file) {
        // Open sam file
        final SamInputResource resource = SamInputResource.of(file);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader samReader = factory.open(resource);

        // Force sort
        samReader.getFileHeader().setSortOrder(SortOrder.coordinate);
        File fina = new File(file.getParent() + File.separator + file.getName().replaceAll(".temp", ""));
        System.out.println(fina);
        final SAMFileWriter samWriter = new SAMFileWriterFactory().setCreateIndex(true).setTempDirectory(temp).makeBAMWriter(
                samReader.getFileHeader(), false, fina, 5);
        int i = 0;
        for (final SAMRecord samRecord : samReader) {
            samWriter.addAlignment(samRecord);
            i++;
            if (i % 1000000 == 0) {
                System.out.println(i / 1000000);
                System.out.flush();
            }
        }
        System.out.println("end");
        try {
            samReader.close();
            samWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        file.delete();
    }

    public void closeBAM(SAMFileWriter outputBam) {
        outputBam.close();
        CloserUtil.close(outputBam);

    }

    public SAMFileHeader getHeader() {
        return samFileHeader;
    }

    public List<String> getComment() {
        return COMMENT;
    }

    public File getFile() {
        return file;
    }

    public String getGenomeName() {
        String genomeNév = "???";
        for (final String comm : COMMENT) {
            if (comm.contains("Genome=")) {
                String currGenome = comm.substring(comm.indexOf("Genome=") + 7);
                if (currGenome.equals("Saccharomyces cerevisiae - R64-1-1")) {
                    currGenome = "saccharomyces_cerevisiae - R64-1-1";
                } else if (currGenome.equals("GRCh38.p10")) {
                    currGenome = "homo_sapiens - GRCh38";
                }
                return currGenome;
            }
        }

        return genomeNév;
    }


    public SAMRecordIterator getAllReadIterator() {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader myReader = factory.open(resource);
        return myReader.iterator();

    }

    public int getUnMappedSequencesNumber() {
        int db = 0;
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        try ( SamReader myReader = factory.open(resource)) {
            factory.validationStringency(ValidationStringency.SILENT);
            SAMRecordIterator it = myReader.iterator();
            while (it.hasNext()) {
                try {
                    SAMRecord r = it.next();
                    if (r.getReferenceName().equals("*")) {
                        db++;
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return db;
    }


    public static ACGTSequence remove(ACGTSequence read, String mappedReference) {
        mappedReference = mappedReference.replaceAll("-", "");
        ACGTSequence temp = read;
        int from = Math.min(mappedReference.length() - 5, read.length());
        read = read.subString(from, read.length());
        int cut = temp.length() - read.length();
        return read;
    }

    public void repairSAMRecords(File file) {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();

        BamTools t = new BamTools(file);
        SAMFileHeader header = t.getHeader();
        SAMFileWriterFactory f = new SAMFileWriterFactory();
        String tempfile = file.getPath().replaceAll(".bam", ".ToRepair.temp.bam");
        SAMFileWriter outputBam = f.makeBAMWriter(header, false, new File(tempfile));

        String tempfile2 = file.getPath().replaceAll(".bam", ".Repaired.temp.bam");
        SAMFileWriter outputBam2 = f.makeBAMWriter(header, false, new File(tempfile2));

        ArrayList<ACGTSequence> r1Chunk = new ArrayList<>();
        String fT = new ACGTSequence("GACGTGTGCTCTTCCGATCT").reverseComplement().toString();
        for (int i = 5; i < fT.length(); i++) {
            ACGTSequence c = new ACGTSequence(fT.substring(0, i));
            r1Chunk.add(c);
        }
        Collections.reverse(r1Chunk);
        System.out.println(r1Chunk);

        try ( SamReader myReader = factory.open(resource)) {
            factory.validationStringency(ValidationStringency.SILENT);
            // SAMFileHeader header = myReader.getFileHeader();
            header.setSortOrder(SAMFileHeader.SortOrder.coordinate);
            SAMRecordIterator it = myReader.iterator();
            while (it.hasNext()) {
                try {
                    SAMRecord r = it.next();
                    if (!r.getReferenceName().equals("*")) {
                        int editDistance = Integer.parseInt(r.getAttribute("NM").toString());
                        if (editDistance > 2) {
                            //System.out.println(r.getReadString());
                            outputBam.addAlignment(r);
                            Cigar c = r.getCigar();
                            for (CigarElement element : c.getCigarElements()) {
                                //System.out.println(element.getOperator());
                            }

                            outputBam2.addAlignment(r);
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            outputBam.close();
            CloserUtil.close(outputBam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCoverage(File input, String chr, int pos) {
        int count;
        File indexfileFile = new File(input.getPath().replace(".bam", ".bai"));
        if (!indexfileFile.exists()) {
            indexfileFile = new File(input.getPath() + ".bai");
        }
        SamInputResource resource = SamInputResource.of(input).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        SamReader myReader = factory.open(resource);
        factory.validationStringency(ValidationStringency.SILENT);
        SAMRecordIterator it = myReader.queryOverlapping(chr, pos, pos + 1);
        count = 0;
        SAMRecord rec;
        while (it.hasNext()) {
            try {
                rec = it.next();
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public LongCounter<Integer> getCoverageOnchr(String chr) {
        SAMRecordIterator it = getSequencesIteratorOnChr(chr);
        SAMRecord rec;
        LongCounter<Integer> coverage = new LongCounter<>();

        while (it.hasNext()) {
            rec = it.next();
            for (int i = rec.getAlignmentStart(); i <= rec.getAlignmentEnd(); i++) {
                coverage.increment(i);
            }
        }
        return coverage;
    }

    public LongCounter<Integer> getCoverageOnchr(String chr, int from, int to) {
        SAMRecordIterator it = getSequencesIterator(chr, from, to);
        SAMRecord rec;
        LongCounter<Integer> coverage = new LongCounter<>();

        while (it.hasNext()) {
            rec = it.next();
            for (int i = rec.getAlignmentStart(); i <= rec.getAlignmentEnd(); i++) {
                coverage.increment(i);
            }
        }
        return coverage;
    }

    public LongCounter<Integer> getCoverageOnchrWithoutSecondary(String chr) {
        SAMRecordIterator it = getSequencesIteratorOnChr(chr);
        SAMRecord rec;
        LongCounter<Integer> coverage = new LongCounter<>();

        while (it.hasNext()) {
            rec = it.next();
            if (rec.isSecondaryAlignment()) {
                continue;
            }
            for (int i = rec.getAlignmentStart(); i <= rec.getAlignmentEnd(); i++) {
                coverage.increment(i);
            }
        }
        return coverage;
    }

    public LongCounter<Integer> getCoverageOnchrWithoutSecondary(String chr, int from, int to) {
        SAMRecordIterator it = getSequencesIterator(chr, from, to);
        SAMRecord rec;
        LongCounter<Integer> coverage = new LongCounter<>();
        while (it.hasNext()) {
            rec = it.next();
            if (rec.isSecondaryAlignment()) {
                continue;
            }
            for (int i = rec.getAlignmentStart(); i <= rec.getAlignmentEnd(); i++) {
                coverage.increment(i);
            }
        }
        return coverage;
    }

    public List<SAMSequenceRecord> getReferences() {
        SamReaderFactory factory = SamReaderFactory.makeDefault();
        SAMFileHeader header = factory.getFileHeader(file);
        SAMSequenceDictionary dict = header.getSequenceDictionary();
        List<SAMSequenceRecord> rec = dict.getSequences();
        return rec;
    }

    public int getSequencesNumber(String ref, int start, int end) {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader myReader = factory.open(resource);
        int count = 0;
        SAMRecordIterator it = myReader.queryOverlapping(ref, start, end);
        while (it.hasNext()) {
            try {
                SAMRecord r = it.next();
                count++;
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return count;
    }

    public int getSequencesNumberWithoutSecondary(String ref, int start, int end) {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader myReader = factory.open(resource);

        int count = 0;
        SAMRecordIterator it = myReader.queryOverlapping(ref, start, end);
        while (it.hasNext()) {
            try {
                SAMRecord rec = it.next();
                if (rec.isSecondaryAlignment()) {
                    continue;
                }
                count++;
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return count;
    }

    public SAMRecordIterator getSequencesIteratorOnChr(String ref) {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader myReader = factory.open(resource);
        SAMRecordIterator it = myReader.queryOverlapping(ref, 0, getChrLength(ref));
        return it;
    }

    public SAMRecordIterator getSequencesIterator(String ref, int from, int to) {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader myReader = factory.open(resource);
        SAMRecordIterator it = myReader.queryOverlapping(ref, from - 1, to + 1);
        return it;
    }
    public SAMRecordIterator getSequencesIteratorContained(String ref, int from, int to) {
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader myReader = factory.open(resource);
        SAMRecordIterator it = myReader.queryContained(ref, from - 1, to + 1);
        return it;
    }

    public int getChrLength(String chr) {
        SAMSequenceRecord rec = samFileHeader.getSequence(chr);
        return rec.getSequenceLength();
    }

    public ArrayList<SAMRecord> getSequencesWithoutSecondary(String ref, int start, int end) {
        return getSequencesWithoutSecondary(ref, start, end, false);
    }

    public ArrayList<SAMRecord> getSequencesWithoutSecondary(String ref, int start, int end, boolean contains) {
        ArrayList<SAMRecord> newRecords = new ArrayList<>();

        long startTime = System.nanoTime();
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader myReader = factory.open(resource);

        int count = 0;
        SAMRecordIterator it;
        if (contains) {
            it = myReader.queryContained(ref, start, end);
        } else {
            it = myReader.queryOverlapping(ref, start, end);
        }
        while (it.hasNext()) {
            try {
                SAMRecord r = it.next();
                if (r.getCigarString().equals("*") || r.isSecondaryAlignment() || r.isSecondaryOrSupplementary()) {
                    continue;
                }
                newRecords.add(r);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            comment = "<html><b>" + count + "</b> db SAMRecord betöltési ideje: " + Core_Utils.nanoTimeToSec(System.nanoTime() - startTime) + " sec</html>";
            //System.out.println(comment);
            myReader.close();
        } catch (Exception e) {
            System.out.println("External error :" + e.getMessage());
            e.printStackTrace();
        }
        return newRecords;
    }

    public ArrayList<SAMRecord> getSequences(String ref, int start, int end, boolean secondary) {
        return getSequences(ref, start, end, false, secondary);
    }

    public ArrayList<SAMRecord> getSequences(String ref, int start, int end, boolean contains, boolean secondary) {
        ArrayList<SAMRecord> newRecords = new ArrayList<>();

        long startTime = System.nanoTime();
        final SamInputResource resource = SamInputResource.of(file).index(indexfileFile);
        final SamReaderFactory factory = SamReaderFactory.makeDefault();
        factory.validationStringency(ValidationStringency.SILENT);
        SamReader myReader = factory.open(resource);

        int count = 0;
        SAMRecordIterator it;
        if (contains) {
            it = myReader.queryContained(ref, start, end);
        } else {
            it = myReader.queryOverlapping(ref, start, end);
        }
        while (it.hasNext()) {
            try {
                SAMRecord r = it.next();
                if (r.getCigarString().equals("*")) {
                    continue;
                }
                if (!secondary && r.isSecondaryAlignment()) {

                } else {

                    newRecords.add(r);
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            comment = "<html><b>" + count + "</b> db SAMRecord betöltési ideje: " + Core_Utils.nanoTimeToSec(System.nanoTime() - startTime) + " sec</html>";
            //System.out.println(comment);
            myReader.close();
        } catch (Exception e) {
            System.out.println("External error :" + e.getMessage());
            e.printStackTrace();
        }
        return newRecords;
    }

}
