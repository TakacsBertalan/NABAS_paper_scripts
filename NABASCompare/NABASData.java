/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hu.deltabio.nabas.compare;



import hu.deltabio.nabas.Excel;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author deltagene
 */
public class NABASData {
    

    
    public static HashMap<String, Species> readNABASSample(File inputFile, double threshold, int binNumber) {
        HashMap<String, Species> species = new HashMap<>();
        Excel excel = new Excel();
        excel.openExcel(inputFile);
        excel.createOrOpenWorkSheet("Bacteria Archea");
        excel.refreshFormulas();
        for (int i = 1; i < excel.getRowNumber(); i++) {
            if(Double.parseDouble(excel.getCellAsString(i,12))/threshold < Double.parseDouble(excel.getCellAsString(i,10))){
                try {
                    Species presentSpecies = new Species(excel.getCellAsString(i, 6),excel.getCellAsString(i, 7), excel.getCell(i, 9).getNumericCellValue()*100);
                    ArrayList<String> bins = new ArrayList();
                    presentSpecies.bins = excel.getCellAsString(i, 11).split(", ");
                    species.put(excel.getCellAsString(i, 7),presentSpecies);
                } catch (Exception e) {
                    System.out.println(excel.getRow(i));
                    System.out.println("ITT VAN A GEBASZ: " + i);
                    System.out.println(e);
                    break;
                }
            }
            
        }
        return filterByBin(species,binNumber);

    }
    
    public static HashMap<String, HashMap<String,Species>> readNABASFolder(String inputFolder){
        
    File input = new File(inputFolder);
        File[] nabasSamples = input.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(".ShotgunResult.xlsx");
            }
        });
        HashMap<String, HashMap<String,Species>> nabasHash = new HashMap<>();
        for (File f : nabasSamples) {
            String sampleName = f.getName().split("\\.")[0];
            try{
            nabasHash.put(sampleName, readNABASSample(f, 1.8, 5));
            } catch (Exception e){
                System.out.println(e);
                System.out.println(f);
            }
        }
    return nabasHash;
    }
    
    public static double calculateShannon(HashMap<String, Species> species){
        //H = -Σpi * ln(pi)
        double shannonDiversity = 0;
        for(String key : species.keySet()){
            double ra = species.get(key).relativeAbundance;
            shannonDiversity += (ra*Math.log(ra));
        }
        shannonDiversity *= -1;
        return shannonDiversity;
    }
    
    public static HashMap<String, Species> filterByBin(HashMap<String, Species> species, int binNumber ){
        HashMap<String, Species> filteredSpecies = new HashMap();
        for (String key: species.keySet()){
            if (Integer.parseInt(species.get(key).bins[binNumber]) > 0){
                filteredSpecies.put(key, species.get(key));
            }
        
        
        }
        return filteredSpecies;
    }
    
    
        public static void main(String[] args){
    double aggregatedF1 = 0.0;
    
    double aggregatedTreshhold = 0.;
    double aggregatedBin = 0;
    for(int i = 0; i < 20; i++){
        double bestF1Score = 0.0;
        int bestBin = 0;
        double bestThreshold = 0.0;
        for (int j = 0; j < 100; j += 1){
            for (double k = 0.0; k < 10; k += 0.1){
    HashMap<String, Species> input = readNABASSample(new File("/media/deltagene/microbiome_2/NABAS_new_db/sample"+ Integer.toString(i) + ".ShotgunResult.xlsx"), k, j);
    HashMap<String, Species> reference = CAMIData.readCAMISample(new File("/media/deltagene/microbiome_2/CAMI_data/gastrooral_dir/taxonomic_profile_"+Integer.toString(i)+".txt"));
    //aggregatedF1 += calculateF1Score(input, reference);
    double F1Score = calculateF1Score(input, reference);
    if (F1Score > bestF1Score){
        bestF1Score = F1Score;
        bestBin = j;
        bestThreshold = k;
    }
    }
        }
    System.out.println("SAMPLE" + i);
    System.out.println(bestF1Score);
    System.out.println(bestBin);
    System.out.println(bestThreshold);
    aggregatedTreshhold += bestThreshold;
    aggregatedF1 += bestF1Score;
    aggregatedBin += bestBin;
    }

    System.out.println("Average F1 score:");
    System.out.println(aggregatedF1/20.0);
    System.out.println("Average Treshold");
    System.out.println(aggregatedTreshhold / 20.0);
    System.out.println("Average Bin");
    System.out.println(aggregatedBin / 20.0);
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
        return truePositives.size()/(double)(truePositives.size() + falseNegatives.size());}
    
    public static double calculateF1Score(HashMap<String, Species> input, HashMap<String, Species> reference){
        double prec = calculatePrecision(input,reference);
        double recall = calculateRecall(input,reference);
        return (2*prec*recall)/(double)(prec+recall);
    }
}
