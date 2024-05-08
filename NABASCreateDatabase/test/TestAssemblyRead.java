
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author deltagene
 */
public class TestAssemblyRead {

    public static void main(String[] args) {
        HashMap<String, RefSeqAssembly> assemblyHash = new HashMap<>();
        assemblyHash = readAssemblySummary("/home/deltagene/assembly_summary_refseq_merged.txt");

        File inputFolder = new File("/media/deltagene/microbiome_2/CAMI_data/RefSeq_genomic_20190108");
        try{
        selectFiles(inputFolder, assemblyHash);
        } catch (Exception e) {
        System.out.println(e);
        }
    }

    private static HashMap<String, RefSeqAssembly> readAssemblySummary(String input) {
        File inputFile = new File(input);
        HashMap<String, RefSeqAssembly> assemblyHash = new HashMap();
        SimpleFileReader reader = new SimpleFileReader(inputFile, SimpleFileReader.FileReaderWriterType.PLAINTEXT);
        LocalDate seqRelDate;
        String line = reader.readLine();
        while (true) {
            if (line == null) {
                reader.close();
                break;
            } else if (line.startsWith("#")) {
                line = reader.readLine();
            } else {
                RefSeqAssembly ass = new RefSeqAssembly(line.split("\t")[0]);
                ass.versionStatus = line.split("\t")[10];
                ass.assemblyLevel = line.split("\t")[11];
                ass.refseqCategory = line.split("\t")[4];
                ass.taxID = line.split("\t")[5];
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    seqRelDate = LocalDate.parse(line.split("\t")[14], formatter);
                    ass.seqRelDate = seqRelDate;
                } catch (Exception e) {
                    System.out.println(e);
                    break;
                }
                assemblyHash.put(ass.assemblyAccession, ass);
                line = reader.readLine();
            }
        }
        System.out.println("Assembly hash mérete: " + assemblyHash.size());
        return assemblyHash;
    }

    static HashMap<String, RefSeqAssembly> selectFiles(File inputFolder, HashMap<String, RefSeqAssembly> IDs) throws IOException {
        File[] listOfFiles = inputFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.contains(".fai");
            }
        });
        HashMap<String, RefSeqAssembly> validAssemblies = new HashMap<>();
        for (File f : listOfFiles) {
            String accession = f.getName().substring(0, 15);
            RefSeqAssembly newAssembly = IDs.get(accession);
            String localtaxID = newAssembly.taxID;
            if (!validAssemblies.containsKey(localtaxID)) {
                newAssembly.bestAssembly = f;
                validAssemblies.put(localtaxID, newAssembly);
            } else {
                RefSeqAssembly oldAssembly = validAssemblies.get(localtaxID);
                //if (seqRelDate.isBefore(LocalDate.parse("2019-01-08")) && seqRelDate.isAfter(assemblyHash.get(taxID).seqRelDate))
                if (newAssembly.seqRelDate.isAfter(oldAssembly.seqRelDate) && newAssembly.seqRelDate.isBefore(LocalDate.parse("2019-01-08")) && !newAssembly.versionStatus.equals("suppressed")) {
                    newAssembly.bestAssembly = f;
                    validAssemblies.put(newAssembly.taxID, newAssembly);
                }
            }

        }
        System.out.println("Valid assembly hash mérete");
        System.out.println(validAssemblies.size());
        
        return validAssemblies;

    }

}
