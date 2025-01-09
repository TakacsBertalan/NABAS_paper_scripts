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
package hu.deltabio.core.genome;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Gábor Jaksa
 */
public class SequenceBoundary {

    public ArrayList<String> chrs = new ArrayList<>();
    public ArrayList<Integer> lengths = new ArrayList<>();
    public HashMap<String, Integer> lengthMap = new HashMap<>();
    public HashMap<String, Long> lengthIndex = new HashMap<>();

    public SequenceBoundary(BufferedReader reader) {
        long tempOffset = 0;
        try {
            String sor;
            while (true) {
                sor = reader.readLine();
                if (sor == null) {
                    reader.close();
                    break;
                }
                String temp[] = sor.split(" ");
                chrs.add(temp[0]);
                int current = Integer.parseInt(temp[1]);
                lengthIndex.put(temp[0], tempOffset);
                tempOffset += current;
                lengths.add(current);
                lengthMap.put(temp[0], current);
            }
        } catch (IOException | NumberFormatException ex) {
            System.out.println("Error in loading sequence boundary: " + ex.getMessage());
        }
    }

    public long getOffset(String aktChr) {
        long vissza = 0;
        for (int i = 0; i < lengths.size(); i++) {
            if (chrs.get(i).equals(aktChr)) {
                break;
            }
            vissza += lengths.get(i);
        }
        return vissza;
    }

    public int getChrLength(String aktChr) {
        for (int i = 0; i < lengths.size(); i++) {
            if (chrs.get(i).equals(aktChr)) {
                return lengths.get(i);
            }
        }
        return -1;
    }

    public int getChrLengthMap(String aktChr) {
        if (!lengthMap.containsKey(aktChr)) {
            return -1;
        }
        return lengthMap.get(aktChr);
    }

    public String getChrFromOffset(long offset) {
        long temp = 0;
        String chrAktuális = "";
        for (int i = 0; i < lengths.size(); i++) {
            if (temp <= offset) {
                chrAktuális = this.chrs.get(i);
            } else {
                break;
            }
            temp += lengths.get(i);
        }
        if (offset > temp) {
            return chrs.get(chrs.size() - 1);
        } else if (offset < 0) {
            return chrs.get(0);
        }
        return chrAktuális;
    }

    public long getCurrentOffsetFromChr(String quarryChr) {
        /*long temp = 0;
        for (int i = 0; i < lengths.size(); i++) {
            if (!quarryChr.equals(this.chrs.get(i))) {
                temp += lengths.get(i);
            } else {
                //temp += hosszak.get(i);
                break;
            }
        }*/
        if (quarryChr == null) {
            System.out.println("quarrychr " + quarryChr);
        }
        if (lengthIndex == null) {
            System.out.println("lengthIndex " + lengthIndex);
        }
        if (lengthIndex.get(quarryChr) == null) {
            System.out.println("ret is null " + quarryChr);
        }

        return lengthIndex.get(quarryChr);
    }
}
