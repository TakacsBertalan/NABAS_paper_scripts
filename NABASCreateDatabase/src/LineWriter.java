/**
 *
 * @author Gábor Jaksa
 */
public interface LineWriter {

    public void writeLn(String str);
    public void close();
    
    public void setLineSeparator(String separator);
}
