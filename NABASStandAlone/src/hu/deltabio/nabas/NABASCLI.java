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

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Bertalan Takács
 */
public class NABASCLI {

    public static void main(String[] args) {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        ArrayList<String> r1 = new ArrayList<String>();
        ArrayList<String> r2 = new ArrayList<String>();
        File outputFolder;
        File referenceFolder;
        File taxonomyFolder;
        ArrayList<String> references;
        int threads = 10;
        int maxMismatch = 10;
        int minNotNullBin = 50;
        int measureBin = 75;
        String r1Mask = "";
        String r2Mask = "";

        options.addOption("h", "help", false, "Display this message");
        options.addOption("r1", true, "Comma separated list of R1 files [required]");
        options.addOption("r2", true, "Comma separated list of R2 files [required]");
        options.addOption("d", "database-folder", true, "Location of the BWA indexed reference [required]");
        options.addOption("o", "output-folder", true, "Output folder [required]. The temporary and final .bam and .xlsx will be written into this folder.");
        options.addOption("t", "threads", true, "Number of threads to utilize at the BWA alignment step. Optional, default: 10");
        options.addOption("taxonomy", true, "Location of the taxonomy folder. Optional, default: database folder");
        options.addOption("maxMismatch", true, "Maximum allowed mismatches. Optional, default: 10");
        options.addOption("minNotNullBin", true, "Number of bins that need to contain reads in order for the species to be accepted as present in the sample. Optional, default: 50");
        options.addOption("measureBin", true, "Number of bin to be counted at the calculation of the relative abundance. Optional, default: 75");
        options.addOption("r1Mask", true, "Mask string for R1 files. Optional, default: \"\"");
        options.addOption("r2Mask", true, "Mask string for R2 files. Optional, default: \"\"");

        String header = "This is the CLI version of NABAS+: a novel alignment-based biome analysis software\nNABAS+ expects a BWA indexed reference database with a Taxonomy folder in the same folder as the index";
        String footer = "\nPlease report issues at the github repository of this project https://github.com/TakacsBertalan/NABAS_paper_scripts";
        try {
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption("h") || commandLine.getOptions().length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("NABAS", header, options, footer, true);
                return;
            }
            if (commandLine.hasOption("t")) {
                
                threads = Integer.parseInt(commandLine.getOptionValue("threads"));
            }
            if (commandLine.hasOption("maxMismatch")) {
                maxMismatch = Integer.parseInt(commandLine.getOptionValue("maxMismatch"));
            }
            if (commandLine.hasOption("minNotNullBin")) {
                minNotNullBin = Integer.parseInt(commandLine.getOptionValue("minNotNullBin"));
            }
            if (commandLine.hasOption("measureBin")) {
                measureBin = Integer.parseInt(commandLine.getOptionValue("measureBin"));
            }
            if (commandLine.hasOption("r1Mask")) {
                r1Mask = commandLine.getOptionValue("r1Mask");
            }
            if (commandLine.hasOption("r2Mask")) {
                r2Mask = commandLine.getOptionValue("r2Mask");
            }

            if (commandLine.getOptionValue("r1").split(",").length != commandLine.getOptionValue("r2").split(",").length) {
                System.out.println("The r1 and r2 lists need to be the same length!");
                return;
            } else if (commandLine.getOptionValue("r1").split(",").length > 0) {
                r1 = NABAS.getReadFiles(commandLine.getOptionValue("r1"));
                r2 = NABAS.getReadFiles(commandLine.getOptionValue("r2"));
                Collections.sort(r1);
                Collections.sort(r2);
            }

            if (commandLine.hasOption("taxonomy")) {
                taxonomyFolder = new File(commandLine.getOptionValue("taxonomy"));

            } else {
                taxonomyFolder = new File(commandLine.getOptionValue("database-folder") + File.separator + "Taxonomy" + File.separator);

            }

            outputFolder = new File(commandLine.getOptionValue("output-folder"));
            referenceFolder = new File(commandLine.getOptionValue("database-folder") + File.separator);
            references = new ArrayList<String>(NABAS.getDataBaseIndices(referenceFolder));

            //Create NABASSettings object
            NABASSettings settings = new NABASSettings();
            settings.r1 = r1;
            settings.r2 = r2;
            settings.threads = threads;
            settings.maxDistance = maxMismatch;
            settings.measureBin = measureBin;
            settings.minimumNotNullBin = minNotNullBin;
            settings.outputFolder = outputFolder;
            settings.indexFolder = referenceFolder;
            settings.r1Mask = r1Mask;
            settings.r2Mask = r2Mask;
            System.out.println("Taxonomy folder: " + taxonomyFolder.getPath());
            settings.taxonomyFolder = taxonomyFolder;

            NABAS.startAnalysis(settings);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
