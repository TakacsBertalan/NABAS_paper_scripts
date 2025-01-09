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
package hu.deltabio.core;

import htsjdk.samtools.SAMRecord;
import hu.deltabio.core.fasta.ACGT;
import hu.deltabio.core.fasta.ACGTSequence;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Gábor Jaksa
 */
public class Core_Utils {

    public static File source = null;

    public static void setUpStreamGobbler(final InputStream is, final PrintStream ps) {

        final InputStreamReader streamReader = new InputStreamReader(is);
        new Thread(new Runnable() {
            public void run() {
                BufferedReader br = new BufferedReader(streamReader);
                String line = null;
                try {
                    while ((line = br.readLine()) != null) {
                        ps.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static String encodeFileToBase64Stream(InputStream initialStream) {
        try {
            byte[] fileContent = new byte[initialStream.available()];
            initialStream.read(fileContent);
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void decorateWindows(Boolean decorate) {
        try {
            Class classParams[] = {Boolean.TYPE};
            Method m = JFrame.class.getMethod("setDefaultLookAndFeelDecorated", classParams);
            Object methodParams[] = {decorate};
            m.invoke(null, methodParams);
            m = JDialog.class.getMethod("setDefaultLookAndFeelDecorated", classParams);
            m.invoke(null, methodParams);
            System.setProperty("sun.awt.noerasebackground", "true");
            System.setProperty("sun.awt.erasebackgroundonresize", "false");
        } catch (Exception ex) {
        }

    }

    public static int getIndexOfNonWhitespaceAfterWhitespace(String string) {
        char[] characters = string.toCharArray();
        boolean lastWhitespace = false;
        for (int i = 0; i < string.length(); i++) {
            if (Character.isWhitespace(characters[i])) {
                lastWhitespace = true;
            } else if (lastWhitespace) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isPrimerValidForGC(ACGTSequence primer, double minGC, double maxGC) {
        boolean vissza = true;
        int primerhossz = primer.length();
        int GC = (int) (primer.fastCount(ACGT.G, 0, primerhossz) + primer.fastCount(ACGT.C, 0, primerhossz));
        if (GC > maxGC * primerhossz) {
            return false;
        }
        if (GC < minGC * primerhossz) {
            return false;
        }
        return vissza;
    }

    public static double BMI(double height, double weight) {
        double BMI = weight / (height * height);
        return BMI;
    }

    public static Image getScaledImage(String ICON_NAME, int size) {
        ImageIcon icon = new ImageIcon(Core_Utils.class.getResource(ICON_NAME));

        return new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)).getImage();
    }

    public static double percentile(List<Double> values, double percentile) {
        return percentile(values, percentile, false);
    }

    public static double percentile(List<Double> values, double percentile, boolean presorted) {
        if (!presorted) {
            Collections.sort(values);
        }
        int index = (int) Math.ceil((percentile / 100) * values.size());
        if (index == 0) {
            if (values.isEmpty()) {
                return 0;
            }
            return values.get(0);
        }
        return values.get(index - 1);
    }

    public static String[] getIlluminaCasava18dot2StyleLoci(SAMRecord rec) {
        String readname = rec.getReadName();
        String temp[] = readname.split(":| ");
        return new String[]{temp[3] + ":" + temp[4] + ":" + temp[5] + ":" + temp[6], temp[7]};
    }

    public static String[] getIlluminaCasava18dot2StyleLociOnlyCoords(SAMRecord rec) {
        String readname = rec.getReadName();
        String temp[] = readname.split(":| ");
        return new String[]{temp[3], temp[4], temp[5], temp[6]};
    }

    public static Object[] getIlluminaCasava18dot2StyleLociObject(SAMRecord rec) {
        String readname = rec.getReadName();
        String temp[] = readname.split(":| ");
        return new Object[]{temp[2], Byte.parseByte(temp[7]) * 100 + Byte.parseByte(temp[3]), Short.parseShort(temp[4]),
            Short.parseShort(temp[5]), Short.parseShort(temp[6])};//flowcellid, lane, strand, 3 tile
    }

    public static String currentTime() {
        Calendar now = GregorianCalendar.getInstance();
        return new SimpleDateFormat("HH:mm:ss").format(now.getTime());

    }

    public static String getDate_YYY_MM_dd() {
        Calendar now = GregorianCalendar.getInstance();
        return new SimpleDateFormat("YYYY-MM-dd").format(now.getTime());
    }

    public static boolean classExists(String clName) {
        try {
            Class.forName(clName);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static void printStackTrace() {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stacks) {
            System.err.println(e);
        }
    }

    public static Comparator<Object> sortIgnoreCaseComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                String s1 = (String) o1;
                String s2 = (String) o2;
                return s1.toLowerCase().compareTo(s2.toLowerCase());
            }
        };

    }

    public static int getMaxThreads() {
        Runtime r = Runtime.getRuntime();
        return r.availableProcessors();
    }

    public static String speedStat(long duration, int illesztve) {
        final double seconds = ((double) duration / 1000000000);
        double másodpercenként = 1 / seconds;
        double percenként = másodpercenként * illesztve * 60;
        double órántként = percenként * 60;
        return Core_Utils.formatNumber((int) percenként) + "/min, " + Core_Utils.formatNumber((int) órántként) + "/h";
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String currentDate() {
        Calendar now = GregorianCalendar.getInstance();
        int m = now.get(Calendar.MONTH) + 1;
        String hónap;
        if (m < 10) {
            hónap = "0" + m;
        } else {
            hónap = m + "";
        }
        //
        m = now.get(Calendar.DAY_OF_MONTH);
        String nap;
        if (m < 10) {
            nap = "0" + m;
        } else {
            nap = m + "";
        }
        //
        m = now.get(Calendar.HOUR_OF_DAY);
        String óra;
        if (m < 10) {
            óra = "0" + m;
        } else {
            óra = m + "";
        }
        //
        m = now.get(Calendar.MINUTE);
        String perc;
        if (m < 10) {
            perc = "0" + m;
        } else {
            perc = m + "";
        }
        //
        m = now.get(Calendar.SECOND);
        String mperc;
        if (m < 10) {
            mperc = "0" + m;
        } else {
            mperc = m + "";
        }

        String ido = now.get(Calendar.YEAR) + ". " + hónap + ". " + nap + ". " + óra + ":" + perc + ":" + mperc;
        return ido;
    }

    public static String getYesOrNo(boolean b) {
        if (b) {
            return "yes";
        } else {
            return "no";
        }
    }

    public static String getYOrN(boolean b) {
        if (b) {
            return "y";
        } else {
            return "n";
        }
    }

    public static String dateForFileName() {
        Calendar now = GregorianCalendar.getInstance();
        return new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(now.getTime());

    }

    public static String getCoiFile(File file) {
        String path = file.getPath();
        return path.substring(0, path.length() - 4) + ".coi";
    }

    public static boolean isAbstract(Class someClass) {
        return Modifier.isAbstract(someClass.getModifiers());
    }

    public static boolean isJar() {
        Class<?> clazz = Core_Utils.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        return classPath.startsWith("jar");
    }

    public static String getFastqIndexFile(File file) {
        return file.getPath() + ".zindex";
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static HashMap<String, String> getFavoriteTranscriptHashmap(String text) {
        HashMap<String, String> ret = new HashMap<>();
        if (text.trim().equals("")) {
            return ret;
        }
        try {
            String lines[] = text.split("\\n");
            for (String line : lines) {
                String temp[] = line.split("\\t");
                ret.put(temp[1], temp[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String formatNumber(long value) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    /**
     * HIDE: WARNING: An illegal reflective access operation has occurred
     */
    public static void hideWarnings() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }

    public static String getRelativeFileAsString(String subFolder) {
        return Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + subFolder;
    }

    public static File getRelativeFileAsFile(String subFolder) {
        return new File(Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + subFolder);
    }

    public static String getRelativeDataFileAsString(String subFolder) {
        if (Core_Utils.source != null) {
            return Core_Utils.source + File.separator + "Data" + File.separator + subFolder;
        }
        return Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + "Data" + File.separator + subFolder;
    }

    public static File getRelativeDataFileAsFile(String subFolder) {
        if (Core_Utils.source != null) {
            return new File(Core_Utils.source + File.separator + "Data" + File.separator + subFolder);
        }
        return new File(Paths.get(".").toAbsolutePath().normalize().toString() + File.separator + "Data" + File.separator + subFolder);
    }

    /**
     * YYYY.MM.dd formátumú dátum
     *
     * @return
     */
    public static String date() {
        Calendar now = GregorianCalendar.getInstance();
        return new SimpleDateFormat("YYYY.MM.dd").format(now.getTime());

    }

    public static BufferedImage grayScaleImage(BufferedImage colorImage) {
        BufferedImage grayImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        BufferedImageOp grayscaleConv = new ColorConvertOp(colorImage.getColorModel().getColorSpace(), grayImage.getColorModel().getColorSpace(), null);
        grayscaleConv.filter(colorImage, grayImage);
        return grayImage;
    }

    public static BufferedImage createImage(JPanel panel) {
        panel.setDoubleBuffered(false);
        int w = panel.getWidth();
        int h = panel.getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        panel.paint(g);
        return bi;
    }

    public static String formatXML(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isOverlapping(int x1, int x2, int y1, int y2) {
        return Math.max(x1, y1) <= Math.min(x2, y2);
    }

    public static int getOverlapping(int start1, int end1, int start2, int end2) {
        int totalRange = Math.max(end1, end2) - Math.min(start1, start2);
        int sumOfRanges = (end1 - start1) + (end2 - start2);
        int overlappingInterval = 0;
        if (sumOfRanges > totalRange) {
            overlappingInterval = Math.min(end1, end2) - Math.max(start1, start2);
        }
        return overlappingInterval;
    }

    public static String formatXML(String input) {
        return formatXML(input, 2);
    }

    public static String filenameWithoutExtension(String file) {
        file = new File(file).getPath();
        String kiterjesztéssel = file.substring(file.lastIndexOf(File.separator) + 1);
        String levonva = kiterjesztéssel.substring(0, kiterjesztéssel.lastIndexOf("."));
        return levonva;
    }

    public static String filenameWithExtension(String file) {
        file = new File(file).getPath();
        String kiterjesztéssel = file.substring(file.lastIndexOf(File.separator) + 1);
        return kiterjesztéssel;
    }

    public static String filenameWithExtension(File file) {
        return filenameWithExtension(file.getPath());
    }

    public static String filenameWithoutExtension(File file0) {
        String file = file0.getPath();
        String kiterjesztéssel = file.substring(file.lastIndexOf(File.separator) + 1);
        String levonva = kiterjesztéssel.substring(0, kiterjesztéssel.lastIndexOf("."));
        return levonva;
    }

    public static String pad(String mit, int mennyire) {
        String vissza = mit;
        while (vissza.length() < mennyire) {
            vissza += " ";
        }
        return vissza;
    }

    public static String pad(String mit, int mennyire, char mivel) {
        String vissza = mit;
        while (vissza.length() < mennyire) {
            vissza += mivel;
        }
        return vissza;
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String padLeftN(String str, int length) {
        return String.format("%" + (length - str.length()) + "s", "").replace(" ", "N") + str;
    }

    public static String padLeft0(String str, int length) {
        return String.format("%" + (length - str.length()) + "s", "").replace(" ", "0") + str;
    }

    public static double round(double number, int scale) {
        if (Double.isInfinite(number)) {
            return number;
        }
        return new BigDecimal(number).setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static float roundFloat(double number, int scale) {
        return new BigDecimal(number).setScale(scale, RoundingMode.HALF_EVEN).floatValue();
    }

    public static String getPercent(double o1, double o2) {
        if (o2 == 0) {
            return "0.0%";
        }
        return Core_Utils.round(o1 / o2 * 100, 1) + "%";
    }

    public static double getPercentDouble(double o1, double o2, int scale) {
        if (o2 == 0) {
            return 0.0;
        }
        return Core_Utils.round(o1 / o2 * 100, scale);
    }

    public static String getPercent(double o1, double o2, int scale) {
        if (o2 == 0) {
            return "0.0%";
        }
        return Core_Utils.round(o1 / o2 * 100, scale) + "%";
    }

    public static String getPercentWithoutSign(double o1, double o2) {
        if (o2 == 0) {
            return "0.0";
        }
        return Core_Utils.round(o1 / o2 * 100, 1) + "";
    }

    public static String getPercentWithoutSign(double o1, double o2, int scale) {
        if (o2 == 0) {
            return "0.0";
        }
        return Core_Utils.round(o1 / o2 * 100, scale) + "";
    }

    static public boolean deleteFolder(String path) {
        return Core_Utils.deleteFolder(new File(path));
    }

    static public boolean deleteFolder(File path) {
        if (!path.isDirectory()) {
            return Core_Utils.deleteFile(path);
        }
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    Core_Utils.deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

    static public boolean deleteFile(String path) {
        return (new File(path).delete());
    }

    static public boolean deleteFile(File path) {
        return (path.delete());
    }

    public static String nanoTimeToSec(long duration) {
        final double seconds = ((double) duration / 1000000000);
        return new DecimalFormat("#.######").format(seconds);
    }

    public static String convertNanotimeTo_DD_HH_mm_ss(long timeInNanotime) {
        StringBuilder sb = new StringBuilder();
        long seconds = timeInNanotime / 1000000000;
        long days = seconds / (3600 * 24);
        appendTime(sb, days, "d");
        seconds -= (days * 3600 * 24);
        long hours = seconds / 3600;
        appendTime(sb, hours, "h");
        seconds -= (hours * 3600);
        long minutes = seconds / 60;
        appendTime(sb, minutes, "m");
        seconds -= (minutes * 60);
        appendTime(sb, seconds, "s");
        return sb.toString();
    }

    private static void appendTime(StringBuilder sb, long value, String text) {
        if (value > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(value).append(text);
        }
    }

    public static String convertNanotimeTo_HH_mm_ss(long timeInNanotime) {
        timeInNanotime = ((long) timeInNanotime / 1000000000);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(new Date(timeInNanotime * 1000L));
        return time;
    }

    public static ArrayList<String> splitEqually(String text, int size) {
        ArrayList<String> ret = new ArrayList<>((text.length() + size - 1) / size);
        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    public static String convertSecondTo_HH_mm_ss(long timeInSeconds) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(new Date(timeInSeconds * 1000L));
        return time;
    }

}
