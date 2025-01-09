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
package hu.deltabio.core.genome;

import hu.deltabio.core.Core_Utils;
import hu.deltabio.core.fasta.ACGT;
import hu.deltabio.core.fasta.ACGTSequence;
import hu.deltabio.core.io.GenbankGenomeTarget;
import hu.deltabio.core.io.XmlStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Gábor Jaksa
 */
public final class Genome {

    private ACGTSequence currentReference;
    public ACGTSequence bwt;
    public GenomeProperties properties;
    public HashMap<String, ArrayList<String>> genomeSmallInformation = new HashMap<>();
    //HashMap<String, Counter<Integer>> targets = new HashMap<>();
    //public ArrayList<TargetData> targets = new ArrayList<>();
    public SequenceBoundary sequenceBoundenary;
    public File file;
    public String loadedGeneAnnotation = "";
    public String loadedReference = "";
    public ACGTSequence fullReference = null;
    ZipFile zipFile = null;
    XmlStream xstream;
    HashMap<String, String> mane = null;

    public static int SSA_SIZE = 32;
    public static int CHECKPONTSIZE = 128;
    HashMap<String, ACGTSequence> referenceHash;

    public Genome(String file) {
        this(new File(file));
    }

    public Genome(File file) {
        file = file;
        xstream = new XmlStream();
        openZip();
        getProperties();
        getSequenceBoundary();
        loadMane();
    }

    public BufferedReader getReaderFromEntry(String entry) {
        InputStream stream;
        BufferedReader reader = null;
        try {
            stream = zipFile.getInputStream(new ZipEntry(entry));
            if (stream == null) {
                return null;
            }
            InputStreamReader decoder = new InputStreamReader(stream);
            reader = new BufferedReader(decoder);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return reader;
    }

    public BufferedReader getGZipReaderFromEntry(String entry) {
        InputStream stream;
        BufferedReader reader = null;
        try {
            stream = zipFile.getInputStream(new ZipEntry(entry));
            if (stream == null) {
                return null;
            }
            GZIPInputStream gs = new GZIPInputStream(stream);
            InputStreamReader decoder = new InputStreamReader(gs, StandardCharsets.UTF_8);
            reader = new BufferedReader(decoder);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return reader;
    }

    public void loadMane() {
        InputStream input = getInputGZStreamFromEntry("mane.xml.gz");
        if (input != null) {
            XmlStream xml = new XmlStream();
            mane = (HashMap<String, String>) xml.fromXML(input);
        }
    }

    public HashMap<String, String> getMane() {
        if (mane == null) {
            return new HashMap<>();
        }
        return mane;
    }
    
    
    public boolean hasMobileElements() {
        InputStream input = getInputGZStreamFromEntry("Special/mobileElements.xml.gz");
        return input != null;
    }

    public boolean hasFeatures() {
        InputStream input = getInputGZStreamFromEntry("Special/features.xml.gz");
        return input != null;
    }

    public HashMap<String, ArrayList<GenbankGenomeTarget>> getFeatures() {
        InputStream input = getInputGZStreamFromEntry("Special/features.xml.gz");
        if (input != null) {
            XmlStream xml = new XmlStream();
            return (HashMap<String, ArrayList<GenbankGenomeTarget>>) xml.fromXML(input);
        } else {
            return null;
        }
    }

    public InputStream getInputGZStreamFromEntry(String entry) {
        InputStream stream = null;
        try {
            stream = zipFile.getInputStream(new ZipEntry(entry));
            if (stream == null) {
                return null;
            }
            return new GZIPInputStream(stream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }

    /*
    
    public void clearAligners() {
        System.out.println("aligners.size() is: "+aligners.size());
        
        aligners.clear();
    }*/
    public InputStream getInputStreamFromEntry(String entry) {
        InputStream stream = null;
        try {
            stream = zipFile.getInputStream(new ZipEntry(entry));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }

    public String getAssemblyName() {
        return properties.getGenomeName();
    }

    public void getProperties() {
        properties = new GenomeProperties();
        properties.load(new File(file.getPath().replace(".zgenome", ".rseq")));
    }

    public void openZip() {
        try {
            zipFile = new ZipFile(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeZip() {
        try {
            zipFile.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void getSequenceBoundary() {
        try {
            sequenceBoundenary = new SequenceBoundary(getGZipReaderFromEntry("genome.sb"));
            String entry;
            for (String key : sequenceBoundenary.chrs) {
                entry = "ChrSets/" + key + "_small.xml.gz";
                BufferedReader r = getGZipReaderFromEntry(entry);
                if (r != null) {
                    ArrayList<String> value = (ArrayList<String>) xstream.fromXML(getGZipReaderFromEntry(entry));
                    genomeSmallInformation.put(key, value);
                }
            }
        } catch (Exception e) {

        }
    }


    public ACGTSequence getFromReference(long from, long to) {
        loadFullReference();
        if (from < 0) {
            from = 0;
        }
        return fullReference.subString(from, to);
    }

    public String publishPosition(long bestStart) {
        String chr = sequenceBoundenary.getChrFromOffset(bestStart);
        long pozíció = bestStart - sequenceBoundenary.getCurrentOffsetFromChr(chr) + 1;
        return (chr + ":" + pozíció);
    }

    /*
    public ACGTSequence getReference(String chr, GeneAnnotation gene) {
        long from = gene.from;
        long to = gene.to;
        ACGTSequence ret = getReference(chr, from, to);
        return ret;
    }*/

    public ACGTSequence getReferenceHashed(String chr, long from, long to, HashMap<String, ACGTSequence> hashes) {
        chr = chr.replace("chr", "");
        String entry = "refs/" + chr + ".genome";
        ACGTSequence t = new ACGTSequence();
        if (!hashes.containsKey(chr)) {
            try {
                t = ACGTSequence.loadFrom(new DataInputStream(getInputStreamFromEntry(entry)));
                hashes.put(chr, t);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            t = hashes.get(chr);
        }
        if (from == 0) {
            from = 1;
        }
        if (to > t.length()) {
            to = t.length();
        }
        //System.out.println("getreference: " + chr + " " + (from - 1) + " " + to);
        return t.subString(from - 1, to);
    }

    public ACGTSequence getContig(String chr) {
        try {
            String entry = "refs/" + chr + ".genome";
            InputStream in = getInputStreamFromEntry(entry);
            return ACGTSequence.loadFrom(new DataInputStream(in));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new ACGTSequence();
    }

    public ACGTSequence getReference(String chr, long from, long to) {
        if (chr.startsWith("chr")) {
            chr = chr.substring(4);
        }
        String entry = "refs/" + chr + ".genome";
        ACGTSequence t = new ACGTSequence();
        if (!loadedReference.equals(chr)) {
            try {
                InputStream in = getInputStreamFromEntry(entry);
                t = ACGTSequence.loadFrom(new DataInputStream(in));
                currentReference = t;
                loadedReference = chr;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            t = currentReference;
        }
        if (from == 0) {
            from = 1;
        }
        if (to > t.length()) {
            to = t.length();
        }
        return t.subString(from - 1, to);
    }
    public ACGTSequence getReferenceFromHash(String chr, long from, long to) {
        if (chr.startsWith("chr")) {
            chr = chr.substring(4);
        }
        ACGTSequence t = referenceHash.get(chr);
        if (from <= 0) {
            from = 1;
        }
        if (to > t.length()) {
            to = t.length();
        }
        if (from > t.length()) {
            from = t.length();
        }
        return t.subString(from - 1, to);
    }

    public ACGTSequence getReference(ACGTSequence reference, long from, long to) {
        if (from == 0) {
            from = 1;
        }
        if (to > reference.length()) {
            to = reference.length();
        }
        return reference.subString(from - 1, to);
    }

    public ACGT getReference(ACGTSequence reference, long index) {
        if (index == 0) {
            index = 1;
        }
        //System.out.println("getreference: " + chr + " " + (from - 1) + " " + to);
        if (index > reference.length()) {
            //System.out.println("something wrong: "+index+" vs "+t.length());
            return ACGT.N;
        }
        return reference.getACGT(index - 1);
    }

    public ACGT getReference(String chr, long index) {
        chr = chr.replace("chr", "");
        String entry = "refs/" + chr + ".genome";
        ACGTSequence t = new ACGTSequence();
        if (!loadedReference.equals(chr)) {
            try {
                t = ACGTSequence.loadFrom(new DataInputStream(getInputStreamFromEntry(entry)));
                currentReference = t;
                loadedReference = chr;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            t = currentReference;
        }
        if (index == 0) {
            index = 1;
        }
        //System.out.println("getreference: " + chr + " " + (from - 1) + " " + to);
        if (index > t.length()) {
            //System.out.println("something wrong: "+index+" vs "+t.length());
        }
        return t.getACGT(index - 1);
    }

    public void loadReferenceHash() {
        InputStream genomeStream;
        referenceHash = new HashMap<>();
        Runtime instance = Runtime.getRuntime();
        try {
            for (int i = 0; i < sequenceBoundenary.chrs.size(); i++) {
                genomeStream = getInputStreamFromEntry("refs/" + sequenceBoundenary.chrs.get(i) + ".genome");
                ACGTSequence c = ACGTSequence.loadFrom(new DataInputStream(genomeStream));
                referenceHash.put(sequenceBoundenary.chrs.get(i), c);
            }
            System.out.println("\nReference loaded");
            System.out.println("Total Memory: " + Core_Utils.humanReadableByteCount(instance.totalMemory(), true));
            System.out.println("Used Memory: " + Core_Utils.humanReadableByteCount(instance.totalMemory() - instance.freeMemory(), true));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void loadFullReference() {
        InputStream genomeStream;
        if (fullReference != null) {
            return;
        }
        fullReference = new ACGTSequence();
        Runtime instance = Runtime.getRuntime();
        try {
            for (int i = 0; i < sequenceBoundenary.chrs.size(); i++) {
                genomeStream = getInputStreamFromEntry("refs/" + sequenceBoundenary.chrs.get(i) + ".genome");
                ACGTSequence c = ACGTSequence.loadFrom(new DataInputStream(genomeStream));
                fullReference.append(c);
            }
            System.out.println("\nReference loaded");
            System.out.println("Total Memory: " + Core_Utils.humanReadableByteCount(instance.totalMemory(), true));
            System.out.println("Used Memory: " + Core_Utils.humanReadableByteCount(instance.totalMemory() - instance.freeMemory(), true));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public String getAlignmentLocation(long bestStart) {
        String chr = sequenceBoundenary.getChrFromOffset(bestStart);
        long position;
        position = bestStart - sequenceBoundenary.getCurrentOffsetFromChr(chr) + 1;
        return (chr + ":" + position);
    }

    public ACGTSequence getFullReference() {
        return fullReference;
    }

    @Override
    public String toString() {
        return Core_Utils.filenameWithoutExtension(file);
    }
}
