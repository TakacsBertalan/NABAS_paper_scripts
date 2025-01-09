
import java.io.File;
import java.time.LocalDate;
import java.util.Date;



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author deltagene
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
