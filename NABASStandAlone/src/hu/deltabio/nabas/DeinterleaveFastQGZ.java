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

import hu.deltabio.core.io.SimpleFileReader;
import hu.deltabio.core.io.SimpleFileWriter;
import java.io.File;

/**
 *
 * @author Bertalan Takács
 */
public class DeinterleaveFastQGZ {

    public static void deinterleave(String inputFile) {
        File f = new File(inputFile);
        SimpleFileReader reader = new SimpleFileReader(f, SimpleFileReader.FileReaderWriterType.GZ);
        SimpleFileWriter r1Writer = new SimpleFileWriter(f.getParent() + "/" + "r1.fastq.gz", SimpleFileReader.FileReaderWriterType.GZ);
        SimpleFileWriter r2Writer = new SimpleFileWriter(f.getParent() + "/" + "r2.fastq.gz", SimpleFileReader.FileReaderWriterType.GZ);
        String line = reader.readLine();
        boolean r1 = true;
        int i = 0;
        while (true) {
            if (line == null) {
                reader.close();
                break;
            } else if (r1) {
                r1Writer.writeLn(line);
                i++;
                line = reader.readLine();
                if (i % 4 == 0) {
                    r1 = false;
                }
            } else if (!r1) {
                r2Writer.writeLn(line);
                i++;
                line = reader.readLine();
                if (i % 4 == 0) {
                    r1 = true;
                }
            if (i % 40000 == 0 ){
                System.out.println("Done with " + i/4 + "reads");
            }
            }
        }
        r1Writer.close();
        r2Writer.close();
    }
}
