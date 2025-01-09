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
package hu.deltabio.core.bio;

import com.google.common.base.Joiner;
import hu.deltabio.core.fasta.ACGTSequence;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Gábor Jaksa
 */
public class FastqRead {

    public String header, seq, plussz, quals;
    public boolean assemblied = false;
    public boolean assemblyFailed = false;
    public boolean PE = true;
    public boolean filterPass = true;
    final private HashMap<String, Object> attributes = new HashMap<>(0);
    public static String lineSeparator = "\n";

    public static String FASTQ_READ_ATTRIBUTE_MC = "MC";
    public static String FASTQ_READ_ATTRIBUTE_DA = "DA";

    public FastqRead(String header, String seq, String plussz, String quals) {
        this.header = header;
        this.seq = seq;
        this.plussz = plussz;
        this.quals = quals;
    }

    @Override
    public String toString() {
        return header + lineSeparator + seq + lineSeparator + plussz + lineSeparator + quals;
    }

    public String getSeq() {
        return seq;
    }

    public String getIlluminaIndex() {
        return header.substring(header.lastIndexOf(":") + 1);
    }

    public void reversecomplement() {
        seq = new ACGTSequence(seq).reverseComplement().toString();
        quals = new StringBuilder(quals).reverse().toString();
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object object) {
        attributes.put(key, object);
    }

    public void copyAttributes(FastqRead orig) {
        attributes.putAll(orig.attributes);
    }

    public String getAttributes() {
        ArrayList<String> ret = new ArrayList<>();
        for (String key : attributes.keySet()) {
            ret.add(key + "=" + attributes.get(key));
        }
        return Joiner.on(", ").join(ret);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }
}
