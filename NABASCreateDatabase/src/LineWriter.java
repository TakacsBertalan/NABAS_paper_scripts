/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author Gábor Jaksa
 */
public interface LineWriter {

    public void writeLn(String str);
    public void close();
    
    public void setLineSeparator(String separator);
}
