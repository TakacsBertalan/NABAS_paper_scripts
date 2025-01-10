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
package hu.deltabio.nabas.compare;

import hu.deltabio.core.io.SimpleFileReader;
import static hu.deltabio.nabas.compare.KrakenData.readKrakenSample;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

/**
 *
 * @author Bertalan Takács
 */
public class KrakenData {

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
            } else if (line.split("\t", 0)[3].equals("S") && Double.parseDouble(line.split("\t", 10)[1]) > 100) {
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
