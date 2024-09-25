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

/**
 *
 * @author deltagene
 */
public class MetaphlanData {

    public static HashMap<String, Species> readMetaphlanSample(File input) {
        SimpleFileReader reader = new SimpleFileReader(input, SimpleFileReader.FileReaderWriterType.PLAINTEXT);
        String line = reader.readLine();
        HashMap<String, Species> species = new HashMap<>();
        while (true) {
            if (line == null) {
                break;
            } else if (!line.startsWith("#") && line.length() > 1 && line.contains("\t") && line.contains("s__") && !line.contains("t__")) {
                String[] cells = line.split("\t");
                if (Double.parseDouble(cells[2]) > 0.0) {
                    String[] taxonomy = cells[0].split("\\|");
                    String s = taxonomy[taxonomy.length - 1].split("s__")[1];
                    s = s.replace("_", " ");
                    String g = taxonomy[taxonomy.length - 2].split("g__")[1];
                    g = g.replace("_", " ");
                    Species presentSpecies = new Species(g, s, Double.parseDouble(cells[2]));
                    species.put(s, presentSpecies);

                }

            }
            line = reader.readLine();
        }
        reader.close();
        return species;

    }

    public static HashMap<String, HashMap<String,Species>> readMetaphlanFolder(String inputFolder) {
                File input = new File(inputFolder);
        File[] camiSamples = input.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("taxonomic_report");
            }
        });
        HashMap<String, HashMap<String,Species>> metaphlanHash = new HashMap<>();
        for (File f : camiSamples) {
            metaphlanHash.put(f.getName().split("_")[0], readMetaphlanSample(f));
        }
    return metaphlanHash;
    }
    
    public static void main(String[] args){

    /*
    for (int i = 0; i < old.keySet().size(); i ++){
    System.out.println(old.get(old.keySet().));
    
    }
*/
    double aggregatedF1 = 0.0;
    for(int i = 0; i < 20; i++){
    HashMap<String, Species> input = readMetaphlanSample(new File("/media/data/Nabas_cikk_results/metaphlan3/CAMI/sample"+Integer.toString(i) + "_taxonomic_report"));
    HashMap<String, Species> reference = CAMIData.readCAMISample(new File("/media/deltagene/microbiome_2/CAMI_data/gastrooral_dir/taxonomic_profile_"+Integer.toString(i)+".txt"));
    aggregatedF1 += calculateF1Score(input, reference);
    }
     
    System.out.println("Average F1 score:");
    System.out.println(aggregatedF1/20.0);
    }
    
    public static double calculatePrecision(HashMap<String, Species> input, HashMap<String, Species> reference){
        ArrayList<String> groundTruth = new ArrayList(reference.keySet());
        ArrayList<String> found = new ArrayList(input.keySet());
        ArrayList<String> truePositives = new ArrayList<>();
        ArrayList<String> falsePositives = new ArrayList<>();
        
        for (String s: found){
        if (groundTruth.contains(s)){
            truePositives.add(s);
        } else {
            falsePositives.add(s);
        }
        }        
                System.out.println(truePositives);
        System.out.println(truePositives.size()/(double) (truePositives.size() + falsePositives.size()));
        return truePositives.size()/(double)(truePositives.size() + falsePositives.size());
}
    
    public static double calculateRecall(HashMap<String, Species> input, HashMap<String, Species> reference){
    ArrayList<String> groundTruth = new ArrayList(reference.keySet());
        ArrayList<String> found = new ArrayList(input.keySet());
        ArrayList<String> truePositives = new ArrayList<>();
        ArrayList<String> falseNegatives = new ArrayList<>();
        
        for (String s: groundTruth){
        if (found.contains(s)){
            truePositives.add(s);
        } else {
            falseNegatives.add(s);
        }
        }
        System.out.println(truePositives);
        System.out.println(falseNegatives);
        return truePositives.size()/(double)(truePositives.size() + falseNegatives.size());}
    
    public static double calculateF1Score(HashMap<String, Species> input, HashMap<String, Species> reference){
        double prec = calculatePrecision(input,reference);
        double recall = calculateRecall(input,reference);
        System.out.println(prec);
        System.out.println(recall);
        return (2*prec*recall)/(double)(prec+recall);
    }
}
