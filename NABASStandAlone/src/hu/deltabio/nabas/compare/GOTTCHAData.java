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
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

/**
 *
 * @author Bertalan Takács
 */
public class GOTTCHAData {
    public static HashMap<String,Species> readGOTTCHASample(File input) {
        SimpleFileReader reader = new SimpleFileReader(input, SimpleFileReader.FileReaderWriterType.PLAINTEXT);
        String line = reader.readLine();
        HashMap<String,Species> speciesHash = new HashMap<>();
        while (true) {
            if (line == null) {
                break;
            } else if (!line.startsWith("@") && line.length() > 1) {
                String[] cells = line.split("\t");
                if (cells[0].equals("species") && !cells[2].equals("0.0") && !cells[2].isBlank()) {
                    String s = cells[1];
                    Species presentSpecies = new Species(s,Double.parseDouble(cells[2])*100 );
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
    
    
    public static HashMap<String, HashMap<String,Species>> readGOTTCHAFolder(String inputFolder) {
                File input = new File(inputFolder);
        File[] camiSamples = input.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(".gottcha.tsv");
            }
        });
        HashMap<String, HashMap<String,Species>> camiHash = new HashMap<>();
        for (File f : camiSamples) {
            String sampnum = f.getName().split("_")[0];
            camiHash.put(sampnum, readGOTTCHASample(f));
        }
    return camiHash;
    }
    
    
    public static void main(String[] args){
    /*
        HashMap<String, Species> speciesHash = readGOTTCHASample(new File("/media/data/Nabas_cikk_results/gottcha/cami_gastrooral_paired_end_new/sample5_S0_L001_R2_001.gottcha.tsv"));
    for (String key : speciesHash.keySet()){
        System.out.println(key);
        System.out.println(speciesHash.get(key).relativeAbundance);
    }*/
    HashMap<String, HashMap<String, Species>> sampleHash = readGOTTCHAFolder("/media/data/Nabas_cikk_results/gottcha/cami_gastrooral_paired_end_new");
    System.out.println(sampleHash.get("sample19"));
    //System.out.println("Shannon");
    System.out.println(CompareResults.calculateShannon(sampleHash.get("sample14")));
    }
}
