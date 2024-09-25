/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hu.deltabio.nabas.compare;

import hu.deltabio.core.io.SimpleFileReader;
import static hu.deltabio.nabas.compare.KrakenData.readKrakenSample;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

/**
 *
 * @author deltagene
 */
public class KrakenData {
    public static void main(String[] args){
        File inputFile = new File("/media/deltagene/microbiome_2/CAMI_results/kraken2/sample16_taxonomic_report");
        HashMap<String, Species> species = readKrakenSample(inputFile);
        for (String s: species.keySet()){
            System.out.println(species.get(s).toString());
        
        }
    
    }
    public static HashMap<String,Species> readKrakenSample(File input) {
        HashMap<String,Species> species = new HashMap<>();
        SimpleFileReader reader = new SimpleFileReader(input, SimpleFileReader.FileReaderWriterType.PLAINTEXT);
        String line = reader.readLine();
        double allClassifiedReads = 0;
        while (true) {
            if (line == null) {
                break;
            } else if (line.contains("root")) {
                allClassifiedReads = Double.parseDouble(line.split("\t")[1]);
            } else if (line.split("\t", 0)[3].equals("S") && Double.parseDouble(line.split("\t", 0)[1]) > 100) {
                double rA = Double.valueOf(line.split("\t", 0)[1]) / allClassifiedReads*100;
                Species presentSpecies = new Species(line.split("\t", 0)[5].strip(), rA);
                species.put(line.split("\t", 0)[5].strip(), presentSpecies);
            }
            line = reader.readLine();

        }
        return species;
    }
    
    public static HashMap<String, HashMap<String,Species>> readKrakenFolder(String inputFolder) {
                File input = new File(inputFolder);
        File[] camiSamples = input.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("taxonomic_report");
            }
        });
        HashMap<String, HashMap<String,Species>> krakenHash = new HashMap<>();
        for (File f : camiSamples) {
            krakenHash.put(f.getName().split("_")[0], readKrakenSample(f));
        }
    return krakenHash;
    }
}
