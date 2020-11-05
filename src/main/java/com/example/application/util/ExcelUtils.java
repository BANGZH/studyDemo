package com.example.application.util;

import com.example.application.exceptionn.BasicException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * @ClassName ClientExcelUtils
 * @Description: ClientExcelUtils 类（或接口）是
 * @Author: zhonghanbang
 * @Date: 2020/10/1915:25
 */
@Slf4j
public class ExcelUtils {

    private static final String XLS = "xls";

    private static final String XLSX = "xlsx";

    private static final String BLANK = "BLANK";

    /**
     * 读入excel文件，解析后返回
     * @param file
     * @throws IOException
     */
    public static List<String[]> readExcel(MultipartFile file) throws IOException {
        //检查文件
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);

                        switch (cell.getCellType().toString()) {
                            // 处理公式类型
                            case "FORMULA":
                                try {
                                    cells[cellNum] = String.valueOf( Double.valueOf(cell.getNumericCellValue()).intValue());
                                } catch (IllegalStateException e) {
                                    cells[cellNum] = String.valueOf(cell.getRichStringCellValue());
                                }
                                break;
                            default:
                                cells[cellNum] = getCellValue(cell);
                                break;
                        }
                    }
                    list.add(cells);
                }
            }
            workbook.close();
        }
        return list;
    }


    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();

            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith(XLS)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(XLSX)){
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            log.info(e.getMessage(),e);
        }
        return workbook;
    }

    public static void checkFile(MultipartFile file) throws IOException {
        //判断文件是否存在
        if(null == file){
            log.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if(!fileName.endsWith(XLS) && !fileName.endsWith(XLSX)){
            log.error(fileName + "不是excel文件");
            throw new IOException(fileName + "不是excel文件");
        }
    }

    /**
     * 获取单元格的值
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell){
        String cellValue = "";
        if(cell == null){
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if(cell.getCellType() == STRING){
            cell.setCellType(STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()){
            case NUMERIC: //数字
                short format = cell.getCellStyle().getDataFormat();
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = null;
                    if (format == 20 || format == 32) {
                        sdf = new SimpleDateFormat("HH:mm");
                    } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                        // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        double value = cell.getNumericCellValue();
                        Date date = org.apache.poi.ss.usermodel.DateUtil
                                .getJavaDate(value);
                        cellValue = sdf.format(date);
                    }else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }
                    try {
                        cellValue = sdf.format(cell.getDateCellValue());// 日期
                    } catch (Exception e) {
                        log.error("读取导入数据时出错",e);
                        throw new BasicException("读取导入数据时出错,请刷新后重试或联系管理员.");
                    }finally{
                        sdf = null;
                    }
                }  else {
                    cellValue = String.valueOf( Double.valueOf(cell.getNumericCellValue()).intValue());;
                }
                break;
            case STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case BLANK: //空值
                cellValue = "";
                break;
            case ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
    public static List getExcelHeader(MultipartFile excelFile){
        List<String> header =  new ArrayList<>();
        Workbook workbook = getWorkBook(excelFile);
        if (null == workbook) {
            throw new BasicException("上传的Excel文件中数据格式不正确，请检查！");
        } else {
            Sheet sheet =workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            //表头数据是否为空判断
            if (isRowEmpty(row)){
                throw new BasicException("上传的Excel文件中数据格式不正确，请检查！");
            }
            int cellNum = row.getLastCellNum();
            for (int i = 0; i < (cellNum); i++) {
                if (!isCellEmpty(row.getCell(i))){
                    header.add(row.getCell(i).getStringCellValue().toLowerCase());
                }
            }
        }
        return header;
    }

    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.valueOf(BLANK)){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为空单元格
     * @param cell
     * @return
     */
    private static boolean isCellEmpty(final Cell cell) {
        if (cell == null || cell.getCellType() == CellType.valueOf(BLANK)) {
            return true;
        }
        if (cell.getCellType() == CellType.valueOf(BLANK) && cell.getStringCellValue().isEmpty()) {
            return true;
        }
        return false;
    }

}
