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
package hu.deltabio.core.io;

import hu.deltabio.core.io.SimpleFileReader.FileReaderWriterType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 *
 * @author Gábor Jaksa
 */
public class SimpleFileWriter implements LineWriter {

    public String fájl;
    private static final double MEG = 1024;
    BufferedWriter buffer;
    public static int bufferSizeInMB = 8;
    public String lineSeparator = System.lineSeparator();
    ObjectOutputStream writer;

    public SimpleFileWriter(String file, FileReaderWriterType type) {
        this(file, type, SimpleFileWriter.bufferSizeInMB * (int) SimpleFileWriter.MEG);
    }

    public SimpleFileWriter(String file, FileReaderWriterType type, int currentBufferSize) {
        fájl = file;
        if (null != type) {
            switch (type) {
                case BZ2:
                    try {
                        BZip2CompressorOutputStream bzout = new BZip2CompressorOutputStream(new FileOutputStream(new File(file)));
                        buffer = new BufferedWriter(new OutputStreamWriter(bzout, "UTF-8"), currentBufferSize);
                    } catch (UnsupportedEncodingException ex) {
                        System.out.println(ex.getMessage());
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case GZ:
                    GZIPOutputStream zip;
                    try {
                        zip = new GZIPOutputStream(new FileOutputStream(new File(file)));
                        buffer = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"), currentBufferSize);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case PLAINTEXT:
                    try {
                        buffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"), currentBufferSize);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case OBJECT:
                    try {
                         writer = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file), currentBufferSize));
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public SimpleFileWriter(File file, FileReaderWriterType type) {
        this(file.getPath(), type);
    }

    public void delete() {
        close();
        new File(fájl).delete();
    }
    
    public void closeObject () {
        try {
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            buffer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void write(String str) {
        try {
            buffer.write(str);
        } catch (Exception ex) {
            System.out.println("Write probléma: " + ex.getMessage());
        }
    }

    @Override
    public void writeLn(String str) {
        try {
            buffer.write(str + lineSeparator);
        } catch (IOException ex) {
            System.out.println("WriteLn probléma: " + ex.getMessage());
        }
    }

    public void flush() {
        try {
            buffer.flush();
        } catch (IOException ex) {
            System.out.println("flush hiba van: " + ex.getMessage());
        }
    }

    public void flushObject() {
        try {
            writer.flush();
        } catch (IOException ex) {
            System.out.println("flush hiba van: " + ex.getMessage());
        }
    }

    public void resetObject() {
        try {
            writer.reset();
        } catch (IOException ex) {
            System.out.println("reset hiba van: " + ex.getMessage());
        }
    }

    public void writeObject(Object o) {
        try {
            writer.writeObject(o);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public Writer getWriter() {
        return buffer;
    }

}
