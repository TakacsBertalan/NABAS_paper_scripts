/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fixfastqheader;

/**
 *
 * @author deltagene
 */

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class FixFastqHeader {
    
public FixFastqHeader(String args[]) {
        String output = "./";
        String sampleName = "DefaultSampleName";
        String r1 = "";
        String r2 = "";
        boolean interleaved = false;
        String header = "Create a dummy casava 1.8 header to fastq files";
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        options.addOption("h", "help", false, "Display this message");
        options.addOption("r1", true, "R1 file");

        options.addOption("r2", true, "R2 file");

        options.addOption("IPE", false, "Interleaved paired end fastq");

        options.addOption("o", "out", true, "Output folder (ends with file separator)");

        options.addOption("s", "sample", true, "Sample name");
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar FixFastqHeader.jar", header, options, "", true);
                //formatter.printHelp("NABASCreateDatabase", options);
                return;
            } else if (commandLine.getOptionValue("r1") == null || (commandLine.getOptionValue("r2") == null && !commandLine.hasOption("IPE"))) {
                System.out.println("Required input file is missing");
                return;
            } else {
                output = commandLine.getOptionValue("o");
                sampleName = commandLine.getOptionValue("s");
                if (sampleName == null){
                    sampleName = "DefaultSampleName";
                }
                r1 = commandLine.getOptionValue("r1");
                r2 = commandLine.getOptionValue("r2");
            }
            if (commandLine.hasOption("IPE")) {
                interleaved = true;
            }
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        SimpleFileWriter writer1 = new SimpleFileWriter(output + sampleName + "_S0_L001_R1_001.fastq.gz", SimpleFileReader.FileReaderWriterType.GZ);
        SimpleFileWriter writer2 = new SimpleFileWriter(output + sampleName + "_S0_L001_R2_001.fastq.gz", SimpleFileReader.FileReaderWriterType.GZ);
        int tileindex = 1;
        int count = 0;
        String lineSeparator = "\n";

        if (interleaved) {
            boolean isRead1 = false;
            System.out.println("Interleaved");
            SimpleFileReader reader = new SimpleFileReader(r1, SimpleFileReader.FileReaderWriterType.GZ);
            String line = reader.readLine();
            while (true) {
                if (line == null) {
                    reader.close();
                    break;
                }
                if (count % 400000 == 0) {
                    tileindex++;
                }
                if (count % 4 == 0) {
                    isRead1 = !isRead1;
                    if (isRead1) {
                        writer1.writeLn("@DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + count / 8 + " 1:Y:0:A");
                    } else {
                        writer2.writeLn("@DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + count / 8 + " 2:Y:0:A");
                    }
                } else if (count % 4 == 2) {
                    //isRead1 = !isRead1;
                    if (isRead1) {
                        writer1.writeLn("+DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + (count-2) / 8 + " 1:Y:0:A");
                    } else {
                        writer2.writeLn("+DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + (count-2) / 8 + " 2:Y:0:A");
                    }
                } else {
                    if (isRead1) {
                        writer1.writeLn(line);
                    } else {
                        writer2.writeLn(line);
                    }
                }
                count++;

                if (count % 80000 == 0) {
                    System.out.println(count / 8 + " read pairs have been written");
                }
                line = reader.readLine();
            }

        } else {
            System.out.println("Nem interleaved");
            SimpleFileReader reader1 = new SimpleFileReader(r1, SimpleFileReader.FileReaderWriterType.GZ);
            SimpleFileReader reader2 = new SimpleFileReader(r2, SimpleFileReader.FileReaderWriterType.GZ);
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            while (true) {
                if (line1 == null) {
                    reader1.close();
                    reader2.close();
                    break;
                }
                if (count % 400000 == 0) {
                    tileindex++;
                }
                if (count % 4 == 0) {
                    writer1.writeLn("@DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + count/4 + " 1:Y:0:A");
                    writer2.writeLn("@DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + count/4 + " 2:Y:0:A");
                } else if (count % 4 == 2) {
                    writer1.writeLn("+DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + (count - 2) /4 + " 1:Y:0:A");
                    writer2.writeLn("+DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + (count -2) /4 + " 2:Y:0:A");
                } else {
                    writer1.writeLn(line1);
                    writer2.writeLn(line2);
                }
                count++;
                line1 = reader1.readLine();
                line2 = reader2.readLine();
                if (count % 80000 == 0){
                    System.out.println(count/4 + " read pairs have been written");
                }
            }
        }

        writer1.close();
        writer2.close();
}
    public static void main(String[] args) {
                    new FixFastqHeader(args);
    }

}

/*
    public ReheaderFastq() {
    }
    
    

    public ReheaderFastq(String args[]) {
        Option configR1 = new Option("r1", true, "R1 file");
        options.addOption(configR1);

        Option configR2 = new Option("r2", true, "R2 file");
        options.addOption(configR2);

        Option optionInterleaved = new Option("IPE", false, "Interleaved paired end fastq");
        options.addOption(optionInterleaved);

        Option configOut = new Option("o", "out", true, "Output folder (ends with file separator)");
        options.addOption(configOut);

        Option configSample = new Option("s", "sample", true, "Sample name");
        options.addOption(configSample);
        parse(args);
        
        String version_num =  this.getClass().getPackage().getImplementationVersion();
        System.out.println("version: "+version_num);

        String output = cmd.getOptionValue("o");
        String sampleName = cmd.getOptionValue("s");
        String r1 = cmd.getOptionValue("r1");
        String r2 = cmd.getOptionValue("r2");

        boolean interleaved = cmd.hasOption("IPE");
        NGSFileWriter writer1 = new NGSFileWriter(output + sampleName + "_S0_L001_R1_001.fastq.gz", NGSFileType.GZ);
        writer1.lineSeparator = "\n";
        NGSFileWriter writer2 = new NGSFileWriter(output + sampleName + "_S0_L001_R2_001.fastq.gz", NGSFileType.GZ);
        writer2.lineSeparator = "\n";
        int tileindex = 1;
        int count = 0;

        if (interleaved) {
            NGSFileReader reader1 = new NGSFileReader(r1);
            while (true) {
                FastqRecord read1 = reader1.readFastqRecord();
                if (read1 == null) {
                    reader1.close();
                    break;
                }
                if (reader1.isUpdateable()) {
                    System.out.println(reader1.getPercent() + "% done");
                    reader1.setUnUpdateable();
                }
                FastqRecord read2 = reader1.readFastqRecord();
                count++;
                if (count % 100000 == 0) {
                    tileindex++;
                }
                read1.header = "DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + count + " 1:Y:0:A";
                read2.header = "DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + count + " 2:Y:0:A";
                writer1.writeLn(read1.toString());
                writer2.writeLn(read2.toString());
            }
        } else {
            System.out.println("Nem interleaved");
            NGSFileReader reader1 = new NGSFileReader(r1);
            NGSFileReader reader2 = new NGSFileReader(r2);
            while (true) {
                FastqRecord read1 = reader1.readFastqRecord();
                if (read1 == null) {
                    reader1.close();
                    reader2.close();
                    break;
                }
                if (reader1.isUpdateable()) {
                    System.out.println(reader1.getPercent() + "% done");
                    reader1.setUnUpdateable();
                }
                FastqRecord read2 = reader2.readFastqRecord();
                count++;
                if (count % 100000 == 0) {
                    tileindex++;
                }
                read1.header = "DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + count + " 1:Y:0:A";
                read2.header = "DUMMY:1:DUMMY_FC:1:" + tileindex + ":1:" + count + " 2:Y:0:A";

                writer1.writeLn(read1.toString());
                writer2.writeLn(read2.toString());
            }
        }

        writer1.close();
        writer2.close();
    }

    public static void main(String[] args) {
        new ReheaderFastq(args);
    }

    @Override
    public String toolName() {
        return "Create a dummy casava 1.8 header to fastq files";
    }

}
*/