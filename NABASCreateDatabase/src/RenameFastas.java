/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Takács Bertalan
 */
import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;


public class RenameFastas {
    
    private static HashMap<String, RefSeqAssembly> readAssemblySummary(File inputFile) {
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
                ass.taxID = line.split("\t")[6];
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
        System.out.println("Number of all assemblies read in: " + assemblyHash.size());
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
                if (newAssembly.seqRelDate.isAfter(oldAssembly.seqRelDate) && newAssembly.seqRelDate.isBefore(LocalDate.parse("2019-01-08")) && !newAssembly.versionStatus.equals("suppressed")) {
                    newAssembly.bestAssembly = f;
                    validAssemblies.put(newAssembly.taxID, newAssembly);
                }
            }
            if (localtaxID.equals("1642646")){
                System.out.println(validAssemblies.get(localtaxID));
            }

        }
        System.out.println("Number of valid assemblies:");
        System.out.println(validAssemblies.size());

        return validAssemblies;

    }

    static void renameAndCopy(HashMap<String, RefSeqAssembly> IDs, File outputFolder, String outputPrefix, boolean headerOnly) {
        long maxBase = (long) 8 * 1000 * 1000 * 1000;
        int part = 1;
        long totalBase = 0;
        SimpleFileWriter headerWriter = new SimpleFileWriter(outputFolder + "/header.html", SimpleFileReader.FileReaderWriterType.PLAINTEXT);
        SimpleFileWriter outputWriter = new SimpleFileWriter(outputFolder + "/" + outputPrefix + "_1.fa.gz", SimpleFileReader.FileReaderWriterType.GZ, 1024);
        String toPad = pad("", 300, 'N');
        int genomeCounter = 0;
        int fileCounter = 0;

        for (RefSeqAssembly assembly : IDs.values()) {
            String taxid = assembly.taxID;
            File bestAssembly = assembly.bestAssembly;
            genomeCounter++;

            String newName = taxid + "_" + assembly.assemblyAccession;
            int fullLength = 0;
            if (!headerOnly){
            outputWriter.write(">" + newName + "\n");
            }
            
            SimpleFileReader fastaReader = new SimpleFileReader(bestAssembly, SimpleFileReader.FileReaderWriterType.GZ);
            boolean newContig = false;
            String line = fastaReader.readLine();
            while (true) {
                if (line == null) {
                    fastaReader.close();
                    break;
                } else if (line.startsWith(">") && newContig) {
                    if (!headerOnly) {
                        outputWriter.write(toPad);
                    }
                    totalBase += 300;
                    fullLength += 300;
                    line = fastaReader.readLine();

                } else if (line.startsWith(">") && !newContig) {
                    newContig = true;
                    line = fastaReader.readLine();

                } else {
                                if (!headerOnly){
                    outputWriter.write(line + "\n");
                                }
                    totalBase += line.length();
                    fullLength += line.length();
                    line = fastaReader.readLine();
                }
            }
            try {
                headerWriter.writeLn(newName + "\t" + String.valueOf(fullLength));
            } catch (Exception e) {
                System.out.println("ERROR WRITING THE HEADER FILE");
                System.out.println(newName + "\t" + String.valueOf(fullLength));
                System.out.println(e);
            }
            if (genomeCounter % 10 == 0) {
                System.out.println("Done with " + String.valueOf(round((double) genomeCounter / IDs.size() * 100, 2)) + "% of the genome files");
            }
            if (totalBase > maxBase && !headerOnly) {
                fileCounter++;
                totalBase = 0;
                outputWriter.close();
                part++;
                outputWriter = new SimpleFileWriter(outputFolder + "/" + outputPrefix + "_" + part + ".fa.gz", SimpleFileReader.FileReaderWriterType.GZ);
                totalBase = 0;
            }
        }
        outputWriter.close();
        headerWriter.close();
        System.out.println("Number of files: " + String.valueOf(fileCounter + 1));
    }
    

    
    public static String pad(String mit, int mennyire, char mivel) {
        String vissza = mit;
        while (vissza.length() < mennyire) {
            vissza += mivel;
        }
        return vissza;
    }

    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    public static void main(String[] args) {
        long timestampStart = System.currentTimeMillis() / 1000;
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        File inputDictionary;
        File inputFolder;
        File outputFolder;
        String outputPrefix = "NABAS_database";

        options.addOption("h", "help", false, "Display this message");
        options.addOption("i", "input-folder", true, "Location of the folder containing the input fasta.gz files [required]");
        options.addOption("d", "input-dictionary", true, "Tab separated file containing the refseq assembly and ncbi tax ids [required]");
        options.addOption("o", "output-folder", true, "Output folder [required]");
        options.addOption("p", "output-prefix", true, "Output file prefix. Optional. Default: NABAS_database");
        options.addOption("headeronly", "Create only the header.html file, without writing the fastas");

        String header = "This program expects the fasta files in the input folder as gzipped and having the RefSeq assembly id as filename\n\n";
        String footer = "\nPlease report issues at the github repository of this project https://github.com/TakacsBertalan/NABAS_paper_scripts";

        try {
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("NABASCreateDatabase", header, options, footer, true);
                //formatter.printHelp("NABASCreateDatabase", options);
                return;
            } else if (commandLine.hasOption("p")) {
                outputPrefix = commandLine.getOptionValue("output-prefix");
            }
            inputFolder = new File(commandLine.getOptionValue("input-folder"));
            inputDictionary = new File(commandLine.getOptionValue("input-dictionary"));
            outputFolder = new File(commandLine.getOptionValue("output-folder"));

            System.out.println("Collecting RefSeq and NCBI ids");

            HashMap<String, RefSeqAssembly> ids = readAssemblySummary(inputDictionary);
            HashMap <String, RefSeqAssembly> validAssemblies = null;
            try{
            validAssemblies = selectFiles(inputFolder, ids);
            } catch (Exception e) {
                System.out.println(e);
        }
            if (!commandLine.hasOption("headeronly")) {

                System.out.println("Creating fasta.gz files and header.html");
                try{
                    renameAndCopy(validAssemblies, outputFolder, outputPrefix, false);
                } catch (Exception e){
                    System.out.println(e);
                }
            } else {
                System.out.println("Creating header.html");
try{
                    renameAndCopy(validAssemblies, outputFolder, outputPrefix, true);
                } catch (Exception e){
                    System.out.println(e);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timestampEnd = System.currentTimeMillis() / 1000;
        System.out.println("Elapsed time in minutes: " + String.valueOf((timestampEnd - timestampStart) / 60));
    }
    
}
