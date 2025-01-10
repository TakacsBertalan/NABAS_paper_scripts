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



import hu.deltabio.nabas.Excel;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author Bertalan Takács
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
            nabasHash.put(sampleName, readNABASSample(f, 1.7, 5));
            } catch (Exception e){
                System.out.println(e);
                System.out.println(f);
            }
        }
    return nabasHash;
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
    

}
