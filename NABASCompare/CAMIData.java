/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hu.deltabio.nabas.compare;

import hu.deltabio.core.io.SimpleFileReader;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import hu.deltabio.nabas.compare.Species;

/**
 *
 * @author deltagene
 */
public class CAMIData {

    public static HashMap<String,Species> readCAMISample(File input) {
        SimpleFileReader reader = new SimpleFileReader(input, SimpleFileReader.FileReaderWriterType.PLAINTEXT);
        String line = reader.readLine();
        HashMap<String,Species> speciesHash = new HashMap<>();
        while (true) {
            if (line == null) {
                break;
            } else if (!line.startsWith("@") && line.length() > 1) {
                String[] cells = line.split("\t");
                if (cells[1].equals("species") && !cells[cells.length - 2].equals("0.0")) {
                    String[] taxonomy = cells[3].split("\\|");
                    String s = taxonomy[taxonomy.length - 1];
                    String g = taxonomy[taxonomy.length - 2];
                    Species presentSpecies = new Species(g, s,Double.parseDouble(cells[cells.length - 2]) );
                    speciesHash.put(s, presentSpecies);
                }
                line = reader.readLine();
            } else {
                line = reader.readLine();
            }

        }
        reader.close();
        return speciesHash;
    }
    
    
    public static HashMap<String, HashMap<String,Species>> readCAMIFolder(String inputFolder) {
                File input = new File(inputFolder);
        File[] camiSamples = input.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("taxonomic_profile");
            }
        });
        HashMap<String, HashMap<String,Species>> camiHash = new HashMap<>();
        for (File f : camiSamples) {
            String sampnum = f.getName().split("_")[2].split("\\.")[0];
            camiHash.put("sample" + sampnum, readCAMISample(f));
        }
    return camiHash;
    }
    
    public static void main(String[] args){
    HashMap<String,Species> sample = readCAMISample(new File("/media/deltagene/microbiome_2/CAMI_data/gastrooral_dir/taxonomic_profile_4.txt"));
    System.out.println(sample);
    }
    
}
