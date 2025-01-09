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
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 *
 * @author Bertalan Takács
 */
public class NABAS {


    public static void startAnalysis(NABASSettings currentSettings) {
        HashMap<String, ArrayList<String>> makeBWA = new HashMap<>();
        HashMap<String, NABASSettings> classificationFiles = new HashMap<>();
        ArrayList<String> r1 = currentSettings.r1;
        ArrayList<String> r2 = currentSettings.r2;
        boolean shm = true;
        int total = 0;
        final String BASH_CMD = "bash";
        String sampleName;
        ArrayList<String> indices = getDataBaseIndices(currentSettings.indexFolder);

        for (int i = 0; i < r1.size(); i++) {
            String shortName = r1.get(i).split("/")[r1.get(i).split("/").length - 1];
            if (shortName.contains("fastq.gz")) {
                sampleName = shortName.substring(0, shortName.length() - 9);
            } else if (shortName.contains("fq.gz")) {
                sampleName = shortName.substring(0, shortName.length() - 6);
            } else {
                System.out.println("Input file format not recognized!");
                break;
            }
            if (!currentSettings.getR1Mask().equals("")){
                sampleName = sampleName.split(currentSettings.getR1Mask())[0];
            }
            if (!classificationFiles.containsKey(sampleName)) {
                classificationFiles.put(sampleName, new NABASSettings());
            }
            for (String index : indices) {
                if (!makeBWA.containsKey(index)) {
                    makeBWA.put(index, new ArrayList<>());
                }
                String systemFile = currentSettings.outputFolder.getName() + File.separator + sampleName + File.separator + sampleName + "_" + index.substring(index.lastIndexOf(File.separator) + 1) + "_File" + i + ".bam";

                if (!new File(currentSettings.getOutputFolder().toString() + File.separator + sampleName).exists()) {
                new File(currentSettings.getOutputFolder().toString() + File.separator + sampleName).mkdirs();
                }
                String file = currentSettings.getOutputFolder().toString() + File.separator + sampleName + File.separator + sampleName + "_" + index.substring(index.lastIndexOf(File.separator) + 1) + "_File" + i + ".bam";
                    String command = "bwa mem -t " + currentSettings.threads + " '" + index.substring(0, index.length()) + "' '" + r1.get(i) + "' '" + r2.get(i) + "' | samtools view -F 4 -o '" + file + "'";
                    makeBWA.get(index).add(command);
                    total++;
                classificationFiles.get(sampleName).files.add(systemFile);

                classificationFiles.get(sampleName).maxDistance = currentSettings.getDistance();
                classificationFiles.get(sampleName).threads = currentSettings.getThreads();
                classificationFiles.get(sampleName).minimumNotNullBin = currentSettings.getMinimumNotNullBin();
                classificationFiles.get(sampleName).measureBin = currentSettings.getMeasureBin();
                classificationFiles.get(sampleName).outputFolder = currentSettings.getOutputFolder();
                classificationFiles.get(sampleName).indexFolder = currentSettings.getIndexFolder();
                classificationFiles.get(sampleName).taxonomyFolder = currentSettings.getTaxonomyFolder();

            }

        }

        int totali = 0;
        for (String index : makeBWA.keySet()) {
            ArrayList<String> commands = makeBWA.get(index);
            if (commands.size() > 1 && shm) {
                String load = "bwa shm '" + index + "'";
                System.out.println(load);
                try {
                    Process process = null;
                    try {
                        String PROG = load;
                        String[] CMD_ARRAY = {BASH_CMD, "-c", PROG};
                        ProcessBuilder processBuilder = new ProcessBuilder(CMD_ARRAY);
                        process = processBuilder.start();
                        InputStream inputStream = process.getInputStream();
                        Core_Utils.setUpStreamGobbler(inputStream, System.out);

                        InputStream errorStream = process.getErrorStream();
                        Core_Utils.setUpStreamGobbler(errorStream, System.err);
                        while (process.isAlive()) {
                            process.waitFor(1000, TimeUnit.MILLISECONDS);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
                int i = 0;
                for (String command : commands) {
                    System.out.println((i + 1) + "/" + commands.size() + " " + (totali + 1) + "/" + total + " " + command);
                    try {
                        Process process = null;
                        String PROG = command;
                        String[] CMD_ARRAY = {BASH_CMD, "-c", PROG};
                        ProcessBuilder processBuilder = new ProcessBuilder(CMD_ARRAY);
                        process = processBuilder.start();
                        InputStream inputStream = process.getInputStream();
                        Core_Utils.setUpStreamGobbler(inputStream, System.out);

                        InputStream errorStream = process.getErrorStream();
                        Core_Utils.setUpStreamGobbler(errorStream, System.err);
                        
                        while (process.isAlive()) {
                            process.waitFor(1000, TimeUnit.MILLISECONDS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                    totali++;
                }
                if (commands.size() > 1 && shm) {
                    String unload = "bwa shm -d";
                    try {
                        Process process = null;
                        String PROG = unload;
                        String[] CMD_ARRAY = {BASH_CMD, "-c", PROG};
                        ProcessBuilder processBuilder = new ProcessBuilder(CMD_ARRAY);
                        process = processBuilder.start();
                        InputStream inputStream = process.getInputStream();
                        Core_Utils.setUpStreamGobbler(inputStream, System.out);

                        InputStream errorStream = process.getErrorStream();
                        Core_Utils.setUpStreamGobbler(errorStream, System.out);
                        while (process.isAlive()) {
                            process.waitFor(1000, TimeUnit.MILLISECONDS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        for (String sample : classificationFiles.keySet()) {
            NABASSettings sett = classificationFiles.get(sample);
            MetagenomeShotgunAnalysis analysis = new MetagenomeShotgunAnalysis();
            analysis.setup(sett.getOutputFolder().toString() + File.separator, sett.getTaxonomyFolder().toString() + File.separator,
                    sett.getThreads(), sett.getDistance(), sett.getMinimumNotNullBin(), sett.getMeasureBin(), indices);
            File[] bams = new File[sett.files.size()];
            for (int i = 0; i < sett.files.size(); i++) {
                bams[i] = new File(sett.files.get(i));
            }
            try{analysis.processFile(bams, sample);
            } catch (Exception e){
                System.out.println(e);
            
            }
        }
        System.out.println("Done");
        //return null;
    }

    static ArrayList<String> getDataBaseIndices(File dataBaseFolder) {

        File files[] = dataBaseFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String string) {
                return string.endsWith(".ann");
            }
        });
        ArrayList<String> fileNames = new ArrayList<String>();
        for (File f : files) {
            fileNames.add(dataBaseFolder.getPath() + "/" + f.getName().substring(0, f.getName().length() - 4));
        }
        return fileNames;
    }

    static ArrayList<String> getReadFiles(String input) {
        ArrayList<String> readFiles = new ArrayList<String>();

        for (String s : input.split(",")) {
            readFiles.add(s);
        }

        return readFiles;
    }

    public String formatPath(String p) {
        String ret = "";
        if (System.getProperty("os.name").equals("Linux")) {
            ret = p;
        } else {
            ret = "/mnt/m" + p.substring(0, 1).toLowerCase() + p.substring(2).replaceAll(Pattern.quote("\\"), "/");
        }
        return ret;
    }

}
