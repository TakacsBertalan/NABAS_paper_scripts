/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hu.deltabio.nabas.compare;

import java.util.ArrayList;
import java.util.HashMap;
import hu.deltabio.nabas.Excel;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author deltagene
 */
public class CompareResults {
    static HashMap<String, HashMap<String,Species>> camiSamples = CAMIData.readCAMIFolder("/media/deltagene/microbiome_2/CAMI_data/gastrooral_dir");
    
    public static HashMap<String, HashMap<String,Species>> getCAMISamples(){
        return camiSamples;
    }
    
    public static void main(String[] args){
        HashMap<String, HashMap<String,Species>> nabasSamples = NABASData.readNABASFolder("/media/deltagene/microbiome_2/NABAS_new_db");
        //HashMap<String, HashMap<String,Species>> camiSamples = CAMIData.readCAMIFolder("/media/deltagene/microbiome_2/CAMI_data/gastrooral_dir");
        HashMap<String, HashMap<String, Species>> krakenSamples = KrakenData.readKrakenFolder("/media/deltagene/microbiome_2/CAMI_results/kraken2/");
        HashMap<String, HashMap<String, Species>> metaphlanSamples = MetaphlanData.readMetaphlanFolder("/media/deltagene/microbiome_2/CAMI_results/metaphlan3");
        HashMap<String, HashMap<String, Species>> gottchaSamples = GOTTCHAData.readGOTTCHAFolder("/media/deltagene/microbiome_2/CAMI_results/gottcha");
        System.out.println("NABAS+");
        System.out.println(nabasSamples.keySet());
        System.out.println("Kraken2");
        System.out.println(krakenSamples.keySet());
        System.out.println("Metaphlan3");
        System.out.println(metaphlanSamples.keySet());
        System.out.println("GOTTCHA");
        System.out.println(gottchaSamples.keySet());
        /*
        Excel excel = new Excel("/media/deltagene/microbiome_2/CAMI_results/sample_19_comparisons_with_gottcha_new_test.xlsx");
        excel.save();
        excel.createOrOpenWorkSheet("Diversity metrics");
        excel.addCell(0, 0, "sample name");
        excel.addCell(0, 1, "classifier");
        excel.addCell(0, 2, "Shannon-diversity");
        excel.addCell(0, 3, "Bray-Curtis-distance from CAMI composition");
        excel.addCell(0, 4, "Jaccard-distance from CAMI composition");
        excel.addCell(0, 5, "Precision");
        excel.addCell(0, 6, "Recall");
        excel.addCell(0, 7, "F1 Score");
        excel.addCell(0, 8, "AUPRC");
        writeToExcel("NABAS+", nabasSamples, "sample19", excel);
        writeToExcel("Kraken2", krakenSamples, "sample19", excel);
        writeToExcel("MetaPhlAn3", metaphlanSamples, "sample19", excel);
        writeToExcel("GOTTCHA", gottchaSamples, "sample19", excel);
        excel.save();
        
        HashMap<String, HashMap<String,Species>> metaphlanSamples = MetaphlanData.readMetaphlanFolder("/media/data/Nabas_cikk_results/metaphlan4");
        double sum = 0.0;
        int counter = 0;
        for (String key: camiSamples.keySet()){
            if(krakenSamples.containsKey(key)){
                counter ++;
                System.out.println("Bray-Curtis distance between the two sample");
                System.out.println(calculateBrayCurtis(camiSamples.get(key), krakenSamples.get(key)));
                sum += calculateBrayCurtis(camiSamples.get(key), krakenSamples.get(key));
            }}
        System.out.println("Average Bray-Curtis distance: ");
        System.out.println(sum/counter);
        */
        //Excel excel = new Excel("/media/deltagene/microbiome_2/CAMI_results/filter_test_bin_5_hr_23.xlsx");
        Excel excel = new Excel("/media/deltagene/microbiome_2/CAMI_results/CAMI_results_with_gottcha_09_25_new_pr.xlsx");
        excel.save();
        excel.createOrOpenWorkSheet("Diversity metrics");
        excel.addCell(0, 0, "sample name");
        excel.addCell(0, 1, "classifier");
        excel.addCell(0, 2, "Shannon-diversity");
        excel.addCell(0, 3, "Bray-Curtis-distance from CAMI composition");
        excel.addCell(0, 4, "Jaccard-distance from CAMI composition");
        excel.addCell(0, 5, "Precision");
        excel.addCell(0, 6, "Recall");
        excel.addCell(0, 7, "F1 Score");
        excel.addCell(0, 8, "AUPRC");
        String[] sampleSet = camiSamples.keySet().toArray(new String[camiSamples.keySet().size()]);
        Arrays.sort(sampleSet);
        writeToExcel("NABAS+", nabasSamples, sampleSet, excel);
        writeToExcel("Kraken2", krakenSamples, sampleSet, excel);
        writeToExcel("MetaPhlAn3", metaphlanSamples, sampleSet, excel);
        writeToExcel("GOTTCHA", gottchaSamples, sampleSet, excel);
        excel.createOrOpenWorkSheet("Missing from NABAS");
        excel.addCell(0, 0, "sample name");
        excel.addCell(0,1,"Species is in reference but not found by classifier");
        excel.addCell(0,2,"Species is not in reference but found by classifier");
        excel.addCell(0,3,"Genus is in reference but not found by classifier");
        findMissingSpecies(nabasSamples, sampleSet, excel);
        findMissingGenus(nabasSamples, sampleSet, excel);
        excel.createOrOpenWorkSheet("Missing from Metaphlan");
        excel.addCell(0, 0, "sample name");
        excel.addCell(0,1,"Species is in reference but not found by classifier");
        excel.addCell(0,2,"Species is not in reference but found by classifier");
        excel.addCell(0,3,"Genus is in reference but not found by classifier");
        findMissingSpecies(metaphlanSamples, sampleSet, excel);
        findMissingGenus(metaphlanSamples, sampleSet, excel);
        excel.createOrOpenWorkSheet("Missing from Kraken2");
        excel.addCell(0, 0, "sample name");
        excel.addCell(0,1,"Species is in reference but not found by classifier");
        excel.addCell(0,2,"Species is not in reference but found by classifier");
        //excel.addCell(0,3,"Genus is in reference but not found by classifier");
        findMissingSpecies(krakenSamples, sampleSet, excel);
        //findMissingGenus(krakenSamples, sampleSet, excel);
        excel.createOrOpenWorkSheet("Missing from GOTTCHA");
        excel.addCell(0, 0, "sample name");
        excel.addCell(0,1,"Species is in reference but not found by classifier");
        excel.addCell(0,2,"Species is not in reference but found by classifier");
        findMissingSpecies(nabasSamples, sampleSet, excel);
        
        excel.save();
        }
        
    
    public static double calculateBrayCurtis(HashMap<String,Species> firstCommunity, HashMap<String,Species> secondCommunity){
    /*BC = 1- (Cij/(Si + Sj))
    Where:
    i & j are the two sites,
    Si is the total number of specimens counted on site i,
    Sj is the total number of specimens counted on site j,
    Cij is the sum of only the lesser counts for each species found in both sites.
        
    Both samples need to be in percent!    
    */
    double brayCurtisDistance = 0.0;
    ArrayList<String> inBoth = new ArrayList(firstCommunity.keySet());
    
    inBoth.retainAll(secondCommunity.keySet());
    for (String s : inBoth){
        if(firstCommunity.get(s).relativeAbundance > secondCommunity.get(s).relativeAbundance){
            brayCurtisDistance += secondCommunity.get(s).relativeAbundance;
        } else {
            brayCurtisDistance += firstCommunity.get(s).relativeAbundance;
        }
    }
    brayCurtisDistance = 1- (brayCurtisDistance/100);
    
    return brayCurtisDistance;
    
    }
    
    public static double calculateJaccard(HashMap<String,Species> firstCommunity, HashMap<String,Species> secondCommunity){
    /*Jaccard Similarity = (number of observations in both sets) / (number in either set)
      Jaccard Distance = 1 - Jaccard Similarity
    */
    double jaccardDistance = 0.0;
    ArrayList<String> inEither = new ArrayList(firstCommunity.keySet());
    ArrayList<String> inBoth = new ArrayList(firstCommunity.keySet());
    inBoth.retainAll(secondCommunity.keySet());
    for (String s : secondCommunity.keySet()){
        if (!inEither.contains(s)){
        inEither.add(s);
        }
    }
    jaccardDistance = 1 - ((double) inBoth.size()/(double) inEither.size());
    return jaccardDistance;
    
    }
    public static double calculateShannon(HashMap<String, Species> species) {
        //H = -Σpi * ln(pi)
        double shannonDiversity = 0;
        if (species != null || species.isEmpty()) {
            System.out.println("NULLA");
            return 0.0;
        } else {
            System.out.println("NEM NULLA");
            for (String key : species.keySet()) {
                /*
            System.out.println(key);
            System.out.println(species.get(key));*/
                double ra = species.get(key).relativeAbundance / 100;
                try {
                    if (!species.get(key).relativeAbundance.isNaN() && ra > 0.0) {
                        shannonDiversity += (ra * Math.log(ra));
                    }
                } catch (Exception e) {
                    System.out.println(species);
                    continue;
                }
            }
            shannonDiversity *= -1;
            return shannonDiversity;
        }
    }
    public static void writeToExcel(String method, HashMap<String, HashMap<String, Species>> results, String[] samples, Excel excel){
        int startRow = excel.getRowNumber();
        
        HashMap<String, HashMap<String,Species>> cami = getCAMISamples();
        for (int i = 0; i< samples.length; i++ ){
            String sample = samples[i];
            excel.addCell(i + startRow, 0, sample);
            excel.addCell(i + startRow, 1, method);
            excel.addCell(i + startRow, 2, calculateShannon(results.get(sample)));
            excel.addCell((i + startRow), 3, calculateBrayCurtis(cami.get(sample), results.get(sample)));
            excel.addCell((i + startRow), 4, calculateJaccard(cami.get(sample), results.get(sample)));
            excel.addCell((i + startRow), 5, calculatePrecision(cami.get(sample), results.get(sample)));
            excel.addCell((i + startRow), 6, calculateRecall(cami.get(sample), results.get(sample)));
            excel.addCell((i + startRow), 8, calculateAUPRC(cami.get(sample), results.get(sample)));
            excel.addCell((i + startRow), 7, calculateF1Score(cami.get(sample), results.get(sample)));

        }
    }
    
    public static void writeToExcel(String method, HashMap<String, HashMap<String, Species>> results, String sample, Excel excel){
        int startRow = excel.getRowNumber();
        
        HashMap<String,Species> cami = getCAMISamples().get(sample);
        Iterator resultIterator = results.keySet().iterator();
        for (int i = 0; i< results.keySet().size(); i++ ){
            String result = (String) resultIterator.next();
            excel.addCell(i + startRow, 0, result);
            excel.addCell(i + startRow, 1, method);
            excel.addCell(i + startRow, 2, calculateShannon(results.get(result)));
            excel.addCell((i + startRow), 3, calculateBrayCurtis(cami, results.get(result)));
            excel.addCell((i + startRow), 4, calculateJaccard(cami, results.get(result)));
            excel.addCell((i + startRow), 5, calculatePrecision(cami, results.get(result)));
            excel.addCell((i + startRow), 6, calculateRecall(cami, results.get(result)));
            excel.addCell((i + startRow), 8, calculateAUPRC(cami, results.get(result)));
            excel.addCell((i + startRow), 7, calculateF1Score(cami, results.get(result)));
        }
    }
/*
    public static void writeToExcel(String method, HashMap<String, HashMap<String, Species>> results, String sample, Excel excel){
        int startRow = excel.getRowNumber();
        
        HashMap<String,Species> cami = getCAMISamples().get(sample);
            excel.addCell(startRow, 0, sample);
            excel.addCell(startRow, 1, method);
            excel.addCell(startRow, 2, calculateShannon(results.get(sample)));
            excel.addCell(startRow, 3, calculateBrayCurtis(cami, results.get(sample)));
            excel.addCell(startRow, 4, calculateJaccard(cami, results.get(sample)));
            excel.addCell( startRow, 5, calculatePrecision(cami, results.get(sample)));
            excel.addCell( startRow, 6, calculateRecall(cami, results.get(sample)));
            excel.addCell( startRow, 8, calculateAUPRC(cami, results.get(sample)));
            excel.addCell( startRow, 7, calculateF1Score(cami, results.get(sample)));
        }
    
    */
        public static void findMissingSpecies(HashMap<String, HashMap<String, Species>> results, String[] samples, Excel excel) {
        HashMap<String, ArrayList<String>> missingPerSample = new HashMap();
        HashMap<String, HashMap<String, Species>>reference = CompareResults.camiSamples;
        int startRow = excel.getRowNumber();
        for (int i = 0; i < samples.length; i++) {

            String sample = samples[i];

            excel.addCell(i + startRow, 0, sample);
            ArrayList<String> inRef = new ArrayList(reference.get(sample).keySet());
            ArrayList<String> inRes = new ArrayList(results.get(sample).keySet());
            inRef.removeAll(inRes);
            excel.addCell(i + startRow, 1, String.join(", ", inRef));
            inRef = new ArrayList(reference.get(sample).keySet());
            inRes.removeAll(inRef);
            excel.addCell(i + startRow, 2, String.join(", ", inRes));



        }

    }
    
    public static void findMissingSpecies(HashMap<String, HashMap<String, Species>> reference, HashMap<String, HashMap<String, Species>> results, String[] samples, Excel excel) {
        int startRow = excel.getRowNumber();
        for (int i = 0; i < samples.length; i++) {
            String sample = samples[i];
            excel.addCell(i + startRow, 0, sample);
            ArrayList<String> inRef = new ArrayList(reference.keySet());
            ArrayList<String> inRes = new ArrayList(results.keySet());
            inRef.removeAll(inRes);
            excel.addCell(i + startRow, 1, String.join(", ", inRef));
            inRef = new ArrayList(reference.keySet());
            inRes.removeAll(inRef);
            excel.addCell(i + startRow, 2, String.join(", ", inRes));
        }
    }

    public static void findMissingGenus(HashMap<String, HashMap<String, Species>> results, String[] samples, Excel excel) {
        int startRow = excel.getRowNumber()-20;

        for (int i = 0; i < samples.length; i++) {
            ArrayList<String> generaResult = new ArrayList(collectGenera(results.get(samples[i])));
            ArrayList<String> generaReference = new ArrayList(collectGenera(camiSamples.get(samples[i])));
            generaReference.removeAll(generaResult);
            excel.addCell(i + startRow, 3, String.join(", ", generaReference));

        }
    }

    public static ArrayList<String> collectGenera(HashMap<String, Species> result) {
        ArrayList<String> genera = new ArrayList();
        for (String s : result.keySet()) {
            if (!genera.contains(result.get(s).genus)) {
                genera.add(result.get(s).genus);
            }
        }
        return genera;
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
    
    /*
    public static double calculatePrecision(HashMap<String,Species> reference, HashMap<String,Species> result){
    //Precision = TruePositives / (TruePositives + FalsePositives)
    double precision = 0.0;
    ArrayList<String> inRes = new ArrayList(result.keySet());
    ArrayList<String> inRef = new ArrayList(reference.keySet());
    double lenRes = inRes.size();
    
    inRes.removeAll(inRef);
    double truePositive = lenRes - inRes.size();
    precision = truePositive / lenRes;
    return precision;
    }
    
    public static double calculateRecall(HashMap<String,Species> reference, HashMap<String,Species> result){
    //Recall = TruePositives / (TruePositives + FalseNegatives)
    double recall = 0.0;
    ArrayList<String> inRes = new ArrayList(result.keySet());
    ArrayList<String> inRef = new ArrayList(reference.keySet());
    double lenRef = inRef.size();
    inRef.removeAll(inRes);
    double truePositive = lenRef - inRef.size();
    recall = truePositive/lenRef;
    return recall;
    }
    
    public static double calculateF1Score(HashMap<String,Species> reference, HashMap<String,Species> result){
    //The F1 score is calculated as the harmonic mean of the precision and recall scores
    double f1Score = 0.0;
    double precision = calculatePrecision(reference, result);
    double recall = calculateRecall(reference, result);
    f1Score = (2*precision*recall)/(precision + recall);
    return f1Score;
    }
    */
    public static double calculateAUPRC(HashMap<String,Species> reference, HashMap<String,Species> result){
        double auprc = 0.0;
        ArrayList<Double> precisionList = new ArrayList();
        ArrayList<Double> recallList = new ArrayList();
        double precision = calculatePrecision(reference, result);
        double recall = calculateRecall(reference, result);
        precisionList.add(precision);
        recallList.add(recall);
        for (double threshold=0.0;threshold<=1.0; threshold+=0.01){
            HashMap<String,Species> filteredReference = filterByThreshold(reference, threshold);
            HashMap<String,Species> filteredResult = filterByThreshold(result, threshold);
            //newRecall-recall: height of the trapesoid;
            //precision: longer parallel side, a
            //newPrecision: shorter parallel side, c
            double newPrecision = calculatePrecision(reference, filteredResult);
            double newRecall = calculateRecall(reference, filteredResult);
            //area of a trapesoid: (a+c)/2*height
            double trapesoidArea = (precision + newPrecision)/2*(recall-newRecall);
            auprc += trapesoidArea;
            precision = newPrecision;
            recall = newRecall;
            precisionList.add(precision);
            recallList.add(recall);
        
        }
        System.out.println("PRECISION");
        System.out.println(precisionList);
        System.out.println("RECALL");
        System.out.println(recallList);
        return auprc;
    }
    
    public static HashMap<String, Species> filterByThreshold(HashMap<String,Species> species, double threshold){
        HashMap<String,Species> filteredSpecies = new HashMap();
        for (String key : species.keySet()){
            if (species.get(key).relativeAbundance >= threshold){
                filteredSpecies.put(key, species.get(key));
            }
        }
        return filteredSpecies;
        
    }

}
