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

import hu.deltabio.core.io.SimpleFileReader;
import hu.deltabio.core.io.SimpleFileWriter;
import hu.deltabio.core.io.XmlStream;
import java.io.*;
import java.util.HashMap;

/**
 *
 * @author Gábor Jaksa
 */
public final class GenomeProperties {

    private HashMap<String, Object> properties = new HashMap<>();

    public GenomeProperties(String genomeName, String createdDate) {
        properties.put("genomeName", genomeName);
        properties.put("createdDate", createdDate);
    }

    public GenomeProperties() {
    }
    
    

    public void save(File file) {
        SimpleFileWriter w;
        XmlStream xstream = new XmlStream();
        String xml = xstream.toXML(properties);
        w = new SimpleFileWriter(file, SimpleFileReader.FileReaderWriterType.PLAINTEXT);
        w.write(xml);
        w.close();
    }

    public void load(File file) {
        XmlStream xstream = new XmlStream();
        try{
            properties = (HashMap<String, Object>) xstream.fromXML(file);
        } catch (Exception e) {
            System.out.println("Failed: "+file);
            e.printStackTrace();
        }
    }

    public String getGenomeName() {
        return (String) properties.get("genomeName");
    }


    public String getCreatedDate() {
        return (String) properties.get("createdDate");
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

}
