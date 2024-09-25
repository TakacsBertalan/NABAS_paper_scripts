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
