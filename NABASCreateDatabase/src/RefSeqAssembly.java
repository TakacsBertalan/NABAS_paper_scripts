
import java.io.File;
import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author Takács Bertalan
 */
public class RefSeqAssembly {

    String assemblyAccession;
    String taxID;
    String speciesName;
    //reference, represenatative, na
    String refseqCategory;
    //replaced, suppressed, na
    String versionStatus;
    //contig, scaffold, genome
    String assemblyLevel;
    LocalDate seqRelDate;
    File bestAssembly;

    public RefSeqAssembly(String assemblyID) {
        this.assemblyAccession = assemblyID;
    }

}
