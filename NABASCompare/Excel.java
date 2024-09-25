/*............................................................................................................
 ..##....##..######....######.....########.##.....##.########..##........#######..########..########.########.
 ..###...##.##....##..##....##....##........##...##..##.....##.##.......##.....##.##.....##.##.......##.....##
 ..####..##.##........##..........##.........##.##...##.....##.##.......##.....##.##.....##.##.......##.....##
 ..##.##.##.##...####..######.....######......###....########..##.......##.....##.########..######...########.
 ..##..####.##....##........##....##.........##.##...##........##.......##.....##.##...##...##.......##...##..
 ..##...###.##....##..##....##....##........##...##..##........##.......##.....##.##....##..##.......##....##.
 ..##....##..######....######.....########.##.....##.##........########..#######..##.....##.########.##.....## 
 .............................................................................................................
 ................................. NGS eXplorer by Gábor Jaksa ...............................................
 .............................................................................................................
 */
package hu.deltabio.nabas;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;

/**
 *
 * @author Gábor Jaksa
 */
public class Excel {

    File file;
    public XSSFWorkbook wb;
    CreationHelper createHelper;
    HashMap<String, Sheet> sheets = new HashMap<>();
    Sheet currentSheet;

    public String percent1 = "0.0%";
    public String percent2 = "0.00%";
    public HashMap<String, XSSFCellStyle> cellStyles = new HashMap<>();

    POIXMLProperties props;

    public Excel() {
    }

    public Excel(String file) {
        newExcel(file);
    }

    public File getFile() {
        return file;
    }

    public String getCellAsString(Cell cell) {
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }

    public void copySheet(int index, String name) {
        XSSFSheet sheet = wb.cloneSheet(index);
        wb.setSheetName(wb.getSheetIndex(sheet), name);
        currentSheet = sheet;
    }

    public void addCustomProp(String key, String value) {
        if (props == null) {
            wb.getProperties();
        }
        POIXMLProperties.CustomProperties custProp = props.getCustomProperties();
        custProp.addProperty(key, value);
    }

    public XSSFFont createFont(XSSFColor fontColor, short fontHeight, boolean fontBold) {
        XSSFFont font = wb.createFont();
        font.setBold(fontBold);
        font.setColor(fontColor);
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints(fontHeight);

        return font;
    }

    public void freezePane(int row, int col) {
        currentSheet.createFreezePane(row, col);
    }

    public XSSFCellStyle createStyle(String name, XSSFFont font, HorizontalAlignment align, XSSFColor cellColor, boolean cellBorder, XSSFColor cellBorderColor) {
        if (cellStyles.containsKey(name)) {
            return cellStyles.get(name);
        }
        XSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setAlignment(align);
        style.setFillForegroundColor(cellColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        if (cellBorder) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);

            style.setTopBorderColor(cellBorderColor);
            style.setLeftBorderColor(cellBorderColor);
            style.setRightBorderColor(cellBorderColor);
            style.setBottomBorderColor(cellBorderColor);
        }
        cellStyles.put(name, style);
        return style;
    }

    public String getCustomProp(String key) {
        if (props == null) {
            wb.getProperties();
        }
        POIXMLProperties.CustomProperties custProp = props.getCustomProperties();
        CTProperty ct = custProp.getProperty(key);
        return ct.toString();
    }

    public String getCellAsString(int row, int col) {
        Cell cell = getCell(row, col);
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }

    public void autoSizeColumnsWB() {
        int numberOfSheets = wb.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = wb.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                Row row = sheet.getRow(0);
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex, true);
                }
            }
        }
    }

    public void makeBold(Cell c) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        c.setCellStyle(style);
    }

    public void autoSizeColumns() {
        Sheet sheet = currentSheet;
        if (sheet.getPhysicalNumberOfRows() > 0) {
            Row row = sheet.getRow(0);
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                sheet.autoSizeColumn(columnIndex, true);
            }
        }
    }

    public void openExcel(InputStream is) {
        try {
            wb = new XSSFWorkbook(is);
            createHelper = wb.getCreationHelper();
            int sheetnumber = wb.getNumberOfSheets();
            for (int i = 0; i < sheetnumber; i++) {
                XSSFSheet s = wb.getSheetAt(i);
                sheets.put(s.getSheetName(), s);
                currentSheet = s;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void openExcel(String file) {
        InputStream is = null;
        try {
            this.file = new File(file);
            if (!this.file.exists()) {
                System.out.println("Not exists: " + this.file);
            }
            is = new FileInputStream(file);
            openExcel(is);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void openExcel(File file) {
        InputStream is = null;
        try {
            this.file = file;
            if (!this.file.exists()) {
                System.out.println("Not exists: " + this.file);
            }
            is = new FileInputStream(file);
            openExcel(is);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void setCellStyle(int row, int col, String format) {
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(wb.createDataFormat().getFormat(format));
        getCell(row, col).setCellStyle(style);
    }

    public void addImage(BufferedImage img, int col1, int row1) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            //Adds a picture to the workbook
            int pictureIdx = wb.addPicture(imageInByte, Workbook.PICTURE_TYPE_PNG);
            //Returns an object that handles instantiating concrete classes
            CreationHelper helper = wb.getCreationHelper();
            //Creates the top-level drawing patriarch.
            Drawing drawing = currentSheet.createDrawingPatriarch();
            //Create an anchor that is attached to the worksheet
            ClientAnchor anchor = helper.createClientAnchor();
            //create an anchor with upper left cell _and_ bottom right cell
            anchor.setCol1(col1);
            anchor.setRow1(row1);
            /*anchor.setCol2(2); //Column C
            anchor.setRow2(3); //Row 4*/

            //Creates a picture
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            //Reset the image to the original size
            pict.resize();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void newExcel(String file) {
        this.file = new File(file);
        wb = new XSSFWorkbook();
        createHelper = wb.getCreationHelper();
    }

    public Sheet createOrOpenWorkSheet(String name) {
        String safeName = WorkbookUtil.createSafeSheetName(name);
        if (!sheets.containsKey(safeName)) {
            Sheet sheet = wb.createSheet(safeName);
            sheets.put(safeName, sheet);
            currentSheet = sheets.get(safeName);
            return sheet;
        } else {
            currentSheet = sheets.get(safeName);
            return sheets.get(safeName);
        }
    }

    public void centerCell(int row, int column) {
        Cell cell = getCell(row, column);
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
        CellStyle s = cell.getCellStyle();
        s.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    public void setCurrentSheet(String name) {
        currentSheet = sheets.get(name);
    }

    public void getWorkSheet(int name) {
        currentSheet = sheets.get(wb.getSheetAt(name).getSheetName());
    }

    public void getWorkSheet(String name) {
        currentSheet = sheets.get(name);
    }

    public void refreshFormulas() {
        XSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
    }

    public Cell getCell(int row, int column) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.getCell(column);
        if (cell == null) {
            cell = currentRow.createCell(column);
            cell.setCellValue("");
        }
        return cell;
    }

    public Cell getCell(String reference) {
        CellReference cr = new CellReference(reference);
        Row row = getRow(cr.getRow());
        Cell cell = row.getCell(cr.getCol());
        return cell;
    }

    public void addCellComment(int row, int column, String value) {
        Drawing drawing = currentSheet.createDrawingPatriarch();
        ClientAnchor anchor = createHelper.createClientAnchor();
        anchor.setCol1(column);
        anchor.setCol2(column + 7);
        anchor.setRow1(row);
        anchor.setRow2(row + 7);
        Comment comment = drawing.createCellComment(anchor);
        RichTextString str = createHelper.createRichTextString(value);
        comment.setString(str);
        comment.setAuthor("NGS eXplorer");
        getCell(row, column).setCellComment(comment);
    }

    public void mergeCells(int firstRow, int lastRow, int firstCol, int lastCol) {
        addCell(firstRow, firstCol, "");
        currentSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    public int getRowNumber() {
        //System.out.println(currentSheet.getSheetName());
        return currentSheet.getPhysicalNumberOfRows();
    }

    public Row getRow(int index) {
        return currentSheet.getRow(index);
    }

    public Sheet getCurrentSheet() {
        return currentSheet;
    }

    public void addformula(int row, int column, String value) {
        getCell(row, column).setCellFormula(value);
    }

    public void addSum(int row, int column, int firstRow, int lastRow, int firstCol, int lastCol) {
        String sum = "sum(" + getCellCoord(firstRow, firstCol) + ":" + getCellCoord(lastRow, lastCol) + ")";
        getCell(row, column).setCellFormula(sum);
    }

    public String getCellCoord(int row, int column) {
        CellReference cr = new CellReference(getCell(row, column));
        return cr.formatAsString();
    }

    public Cell addCell(int row, int column, String value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public void appendCell(int row, int column, String value) {
        Cell cell = getCell(row, column);
        cell.setCellValue(cell.getStringCellValue() + value);
    }

    public void addCellHTML(int row, int column, String value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        XSSFRichTextString v = new XSSFRichTextString(value);
        cell.setCellValue(v);
    }

    public Cell addCell(int row, int column, Date value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public Cell addCell(int row, int column, int value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public Cell addCellThousandSeparator(int row, int column, int value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        CellStyle ezres = wb.createCellStyle();
        ezres.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(ezres);
        cell.setCellValue(value);
        return cell;
    }

    public Cell addCell(int row, int column, Calendar value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public Cell addCell(int row, int column, boolean value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public Cell addCell(int row, int column, double value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public Cell addCell(int row, int column, RichTextString value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public Cell addCellErrorValue(int row, int column, byte value) {
        Row currentRow = currentSheet.getRow(row);
        if (currentRow == null) {
            currentRow = currentSheet.createRow(row);
        }
        Cell cell = currentRow.createCell(column);
        cell.setCellErrorValue(value);
        return cell;
    }

    @ChangeLog(author = Author.Gabor_Jaksa, comment = "Excel cella másolása", date = "2020-03-30", logTpye = LogType.FEATURE)
    public void copyCell(XSSFCell oldCell, int row, int col) {
        CellType type = oldCell.getCellTypeEnum();
        if (null != type) {
            switch (type) {
                case BLANK:
                    addCell(row, col, oldCell.getStringCellValue());
                    break;
                case BOOLEAN:
                    addCell(row, col, oldCell.getBooleanCellValue());
                    break;
                case ERROR:
                    addCellErrorValue(row, col, oldCell.getErrorCellValue());
                    break;
                case FORMULA:
                    addformula(row, col, oldCell.getCellFormula());
                    break;
                case NUMERIC:
                    addCell(row, col, oldCell.getNumericCellValue());
                    break;
                case STRING:
                    addCell(row, col, oldCell.getRichStringCellValue());
                    break;
            }
        }
    }

    public void save() {
        try {
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveAs(String file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
