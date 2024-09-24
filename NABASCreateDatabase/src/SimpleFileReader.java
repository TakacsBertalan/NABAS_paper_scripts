/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;


/**
 *
 * @author Gábor Jaksa
 */
public final class SimpleFileReader {

    FileChannel myFileChannel;
    BufferedReader myBufferedReader;
    private boolean isinterrupted = false;
    private int procent = 0;
    boolean updateable = false;
    private long size;
    boolean closed = false;
    String file;
    ObjectInputStream objectReader;

    public enum FileReaderWriterType {
        GZ, BZ2, PLAINTEXT, OBJECT;
    }

    public SimpleFileReader(InputStream input) {
        this(false, input);
    }

    //zipReader = new GZIPInputStream(myInputStream);
    public SimpleFileReader(boolean gz, InputStream input) {
        try {
            if (gz) {
                GZIPInputStream zipReader = new GZIPInputStream(input);
                InputStreamReader myInputStreamReader = new InputStreamReader(zipReader, StandardCharsets.UTF_8);
                myBufferedReader = new BufferedReader(myInputStreamReader);
            } else {
                InputStreamReader myInputStreamReader = new InputStreamReader(input, StandardCharsets.UTF_8);
                myBufferedReader = new BufferedReader(myInputStreamReader);
            }

        } catch (Exception ex) {
            System.out.println("inputstream: " + ex.getMessage());
        }
    }

    public SimpleFileReader(InputStream input, boolean jar) {
        try {
            InputStreamReader myInputStreamReader = new InputStreamReader(input);
            myBufferedReader = new BufferedReader(myInputStreamReader);
        } catch (Exception ex) {
            System.out.println("inputstream: " + ex.getMessage());
        }
    }

    public SimpleFileReader(File file, FileReaderWriterType type) {
        init(file.getPath(), type);
    }

    public SimpleFileReader(String file, FileReaderWriterType type) {
        init(file, type);
    }

    public SimpleFileReader(String file) {
        init(file);
    }

    public SimpleFileReader(File file) {
        init(file.getPath());
    }

    public void interrupt() {
        isinterrupted = true;
    }

    public boolean isInterrupted() {
        return isinterrupted;
    }

    public void compress(boolean delete) {
        String f = file;
        System.out.println("file is: " + f);
        String ext = f.substring(f.lastIndexOf("."));
        String name = f.substring(0, f.lastIndexOf("."));
        System.out.println(name + ext + ".gz");
        SimpleFileWriter w = new SimpleFileWriter(name + ext + ".gz", SimpleFileReader.FileReaderWriterType.GZ);

        while (true) {
            String line = readLine();
            if (line == null) {
                close();
                w.close();
                break;
            }
            w.writeLn(line);
        }
    }

    public boolean isUpdateable() {
        return updateable;
    }

    public boolean isOpen() {
        return myFileChannel.isOpen();
    }

    public void setUnUpdateable() {
        updateable = false;
    }

    public String getFile() {
        return file;
    }

    public int getProcent() {
        return procent - 1;
    }

    public void init(String file, FileReaderWriterType type) {
        try {
            FileInputStream myInputStream;
            myInputStream = new FileInputStream(new File(file));
            if (null != type) {
                switch (type) {
                    case BZ2:
                        bz2(myInputStream);
                        break;
                    case GZ:
                        gzip(myInputStream);
                        break;
                    case OBJECT:
                        object(myInputStream);
                        break;
                    default:
                        plainText(myInputStream);
                        break;
                }
            }
            myFileChannel = myInputStream.getChannel();
            size = getChannelSize();
            this.file = file;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void init(String file) {
        try {
            FileInputStream myInputStream;
            myInputStream = new FileInputStream(new File(file));
            if (file.endsWith(".bz2")) {
                bz2(myInputStream);
            } else if (file.endsWith(".gz")) {
                gzip(myInputStream);
            } else if (file.endsWith(".bgzf")) {
                gzip(myInputStream);
            } else {
                plainText(myInputStream);
            }
            myFileChannel = myInputStream.getChannel();
            size = getChannelSize();
            this.file = file;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void plainText(FileInputStream myInputStream) {
        InputStreamReader myInputStreamReader;
        myInputStreamReader = new InputStreamReader(myInputStream, StandardCharsets.UTF_8);
        myBufferedReader = new BufferedReader(myInputStreamReader);
    }

    public void bz2(FileInputStream myInputStream) {
        BZip2CompressorInputStream bzIn;
        try {
            InputStreamReader myInputStreamReader;
            bzIn = new BZip2CompressorInputStream(myInputStream);
            myInputStreamReader = new InputStreamReader(bzIn, StandardCharsets.UTF_8);
            myBufferedReader = new BufferedReader(myInputStreamReader);
        } catch (IOException ex) {
            System.out.println("bz2: " + ex.getMessage());
        }
    }

    public String[] getOneReadAsString() {
        String[] ret = new String[4];
        String check = readLine();
        if (check == null) {
            return null;
        }
        ret[0] = check;
        ret[1] = readLine();
        ret[2] = readLine();
        ret[3] = readLine();
        return ret;
    }

    public void gzip(FileInputStream myInputStream) {
        GZIPInputStream zipReader;
        try {
            InputStreamReader myInputStreamReader;
            zipReader = new GZIPInputStream(myInputStream);
            myInputStreamReader = new InputStreamReader(zipReader, StandardCharsets.UTF_8);
            myBufferedReader = new BufferedReader(myInputStreamReader);
        } catch (IOException ex) {
            System.out.println("gzip: " + ex.getMessage());
        }
    }

    private void object(FileInputStream myInputStream) {
        try {
            objectReader = new ObjectInputStream(new GZIPInputStream(myInputStream));
        } catch (IOException ex) {
            System.out.println("object: " + ex.getMessage());
        }
    }

    public Object readObject() {
        try {
            return objectReader.readObject();
        } catch (Exception ex) {
            return null;
        }

    }

    public void closeObject() {
        try {
            objectReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            if (myBufferedReader != null) {
                myBufferedReader.close();
            }
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    public long getChannelPosition() {
        try {
            return myFileChannel.position();
        } catch (IOException ex) {

        }
        return -1;
    }

    public long getChannelSize() {
        try {
            return myFileChannel.size();
        } catch (IOException ex) {
            System.out.println("getChannelSize: " + ex.getMessage());
        }
        return 0;
    }

    public String readLineStream() {
        try {
            String line = myBufferedReader.readLine();
            if (line == null) {
                closed = true;
                close();
            }
            return line;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String readLine() {
        try {
            long pos = getChannelPosition();
            if (pos >= procent * size / 100) {
                updateable = true;
                procent++;
            }
            String line = myBufferedReader.readLine();
            if (line == null) {
                closed = true;
                close();
            }
            return line;
        } catch (Exception ex) {
            //System.out.println("readLine: " + ex.getMessage());
        }
        return null;
    }

    public boolean isClosed() {
        return closed;
    }

    public BufferedReader getReader() {
        return myBufferedReader;
    }
}

