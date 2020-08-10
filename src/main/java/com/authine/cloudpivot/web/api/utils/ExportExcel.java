package com.authine.cloudpivot.web.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ExportExcel
 * @Author:lfh
 * @Date:2020/3/13 16:16
 * @Description: 导出excel
 **/
@Slf4j
public class ExportExcel {
    public static <T>void exportExcel(Collection<T> dataset, OutputStream out) {
        exportExcel("POI导出EXCEL文档", null, dataset, out, "yyyy-MM-dd");
    }

    public static <T>void exportExcel(String title, String[] headers, Collection<T> dataset, OutputStream out) {
        exportExcel(title, headers, dataset, out, "yyyy-MM-dd");
    }

    public static <T>void exportExcel(String[] headers, Collection<T> dataset, OutputStream out, String pattern) {
        exportExcel("POI导出EXCEL文档", headers, dataset, out, pattern);
    }
    /**
     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param title
     *            表格标题名
     * @param headers
     *            表格属性列名数组
     * @param dataset
     *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    @SuppressWarnings("unchecked")
    public static<T> void exportExcel(String title, String[] headers,
                                   Collection<T> dataset, OutputStream out, String pattern) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);

        //设置样式风格
        CellStyle cs = workbook.createCellStyle();
        //居中对齐.
        cs.setAlignment(HorizontalAlignment.CENTER);
        //边框
        cs.setBorderBottom(BorderStyle.THIN); //下边框
        cs.setBorderLeft(BorderStyle.THIN);//左边框
        cs.setBorderTop(BorderStyle.THIN);//上边框
        cs.setBorderRight(BorderStyle.THIN);//右边框

        //产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        //遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = (T) it.next();
            //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (short i = 0; i < headers.length; i++) {
                HSSFCell cell = row.createCell(i);
                /*if(i==0) {
                    cell.setCellValue(index);
                    continue;
                }*/
                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
                    Object value = getMethod.invoke(t, new Object[] {});
                    //判断值的类型后进行强制类型转换
                    String textValue;
                    if (value instanceof Date) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        textValue = sdf.format(date);
                    }  else{
                        //其它数据类型都当作字符串简单处理
                        if(null == value){
                            textValue = "";
                        }else {
                            textValue = value.toString();
                        }

                    }
                    //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                    if(textValue!=null){
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if(matcher.matches()){
                            //是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        }else{
                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
                            HSSFFont font3 = workbook.createFont();
                            richString.applyFont(font3);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (SecurityException e) {
                    log.error(e.getMessage(),e);
                } catch (NoSuchMethodException e) {
                    log.error(e.getMessage(),e);
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage(),e);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(),e);
                } catch (InvocationTargetException e) {
                    log.error(e.getMessage(),e);
                }

            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static <T> Workbook exportExcel(String title, String[] headers,
                                           Collection<T> dataset, String realPath, String pattern, Workbook workbook) throws IOException {
        FileOutputStream out = new FileOutputStream(realPath);
        if (workbook == null) {
            workbook = new HSSFWorkbook();
        }
        // 生成一个表格
        Sheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);

        //设置样式风格
        CellStyle cs = workbook.createCellStyle();
        //居中对齐.
        cs.setAlignment(HorizontalAlignment.CENTER);
        //边框
        cs.setBorderBottom(BorderStyle.THIN); //下边框
        cs.setBorderLeft(BorderStyle.THIN);//左边框
        cs.setBorderTop(BorderStyle.THIN);//上边框
        cs.setBorderRight(BorderStyle.THIN);//右边框

        //产生表格标题行
        Row row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);

            cell.setCellStyle(cs);
        }

        //遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = (T) it.next();
            //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (short i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(i);
                /*if(i==0) {
                    cell.setCellValue(index);
                    continue;
                }*/
                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    Class tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
                    //判断值的类型后进行强制类型转换
                    String textValue;
                    if (value instanceof Date) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        textValue = sdf.format(date);
                    } else {
                        //其它数据类型都当作字符串简单处理
                        if (null == value) {
                            textValue = "";
                        } else {
                            textValue = value.toString();
                        }

                    }
                    //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            //是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                            cell.setCellStyle(cs);
                        } else {
                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
                            Font font3 = workbook.createFont();
                            richString.applyFont(font3);
                            cell.setCellValue(richString);
                            cell.setCellStyle(cs);
                        }
                    }
                } catch (SecurityException e) {
                    log.error(e.getMessage(), e);
                } catch (NoSuchMethodException e) {
                    log.error(e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                }

            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return workbook;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet    要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     * @return 设置好的sheet.
     */
    public static Sheet setHSSFValidation(Sheet sheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint
                .createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }

    /**
     * 设置单元格上提示
     *
     * @param sheet         要设置的sheet.
     * @param promptTitle   标题
     * @param promptContent 内容
     * @param firstRow      开始行
     * @param endRow        结束行
     * @param firstCol      开始列
     * @param endCol        结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle,
                                          String promptContent, int firstRow, int endRow, int firstCol,
                                          int endCol) {
        // 构造constraint对象
        DVConstraint constraint = DVConstraint
                .createCustomFormulaConstraint("BB1");
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_view = new HSSFDataValidation(
                regions, constraint);
        data_validation_view.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(data_validation_view);
        return sheet;
    }

    //字段过长，POI导出Excel时下拉列表值超过255的问题（String literals in formulas can't be bigger than 255 characters ASCII）
    public static HSSFDataValidation getDataValidationList4Col(Sheet sheet, int firstRow, int firstCol, int endRow, int endCol, List<String> colName, Workbook wbCreat) {
        String[] dataArray = colName.toArray(new String[0]);
        Sheet hidden = wbCreat.createSheet("hidden");
        Cell cell = null;
        for (int i = 0, length = dataArray.length; i < length; i++) {
            String name = dataArray[i];
            Row row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(name);
        }

        Name namedCell = wbCreat.createName();
        namedCell.setNameName("hidden");
        namedCell.setRefersToFormula("hidden!$A$1:$A$" + dataArray.length);
        //加载数据,将名称为hidden的
        DVConstraint constraint = DVConstraint.createFormulaListConstraint("hidden");

        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        HSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);

        //将第二个sheet设置为隐藏
        wbCreat.setSheetHidden(1, true);

        if (null != validation) {
            sheet.addValidationData(validation);
        }
        return validation;
    }

    /**
     * 创建标题样式
     *
     * @param wb
     * @return
     */
    public static CellStyle createTitleCellStyle(Workbook wb, String fontName, int fontHeightInPoints) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直对齐
        // cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());//背景颜色

        Font headerFont1 = wb.createFont(); // 创建字体样式
        headerFont1.setBold(true); //字体加粗
        headerFont1.setFontName(fontName); // 设置字体类型
        headerFont1.setFontHeightInPoints((short) fontHeightInPoints); // 设置字体大小
        cellStyle.setFont(headerFont1); // 为标题样式设置字体样式

        return cellStyle;
    }

    /**
     *  * 创建标题样式
     * @param wb
     * @return
     */
    public static CellStyle createTitleCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直对齐
        // cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());//背景颜色
        Font headerFont1 = wb.createFont(); // 创建字体样式
        headerFont1.setBold(true); //字体加粗
        headerFont1.setFontName("黑体"); // 设置字体类型
        headerFont1.setFontHeightInPoints((short) 22); // 设置字体大小
        cellStyle.setFont(headerFont1); // 为标题样式设置字体样式
        return cellStyle;
    }
    /**
     * 创建表头样式
     *
     * @param wb
     * @return
     */
    public static CellStyle createHeadCellStyle(Workbook wb, int fontHeightInPoints) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);// 设置自动换行
        // cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());//背景颜色
        cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); //垂直对齐
        // cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框

        Font headerFont = wb.createFont(); // 创建字体样式
        // headerFont.setBold(true); //字体加粗
        // headerFont.setFontName("黑体"); // 设置字体类型
        headerFont.setFontHeightInPoints((short) fontHeightInPoints); // 设置字体大小
        cellStyle.setFont(headerFont); // 为标题样式设置字体样式

        return cellStyle;
    }

    /**
     * 创建表头样式
     *
     *@param wb
     *@return
    */
    public static CellStyle createHeadCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);// 设置自动换行
        // cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());//背景颜色
        cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); //垂直对齐
        // cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框
        Font headerFont = wb.createFont(); // 创建字体样式
        // headerFont.setBold(true); //字体加粗
        // headerFont.setFontName("黑体"); // 设置字体类型
        headerFont.setFontHeightInPoints((short) 12); // 设置字体大小
        cellStyle.setFont(headerFont); // 为标题样式设置字体样式
        return cellStyle;
    }

    /**
     * 创建表头样式
     *
     *@param wb
     *@return
     */
    public static CellStyle createSecondHeadCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); //垂直对齐
        Font headerFont = wb.createFont(); // 创建字体样式
        headerFont.setFontHeightInPoints((short) 14); // 设置字体大小
        cellStyle.setFont(headerFont); // 为标题样式设置字体样式
        return cellStyle;
    }
    /**
     * 创建内容样式
     *
     * @param wb
     * @return
     */
    public static CellStyle createContentCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框

        // 生成12号字体
        Font font = wb.createFont();
        font.setColor((short) 8);
        font.setFontHeightInPoints((short) 10);
        cellStyle.setFont(font);

        return cellStyle;
    }

    /**
     * 自适应宽度(中文支持)
     * @param sheet
     * @param lastRowNum 实际行数
     */
    public  static void setSizeColumn(Sheet sheet, int lastRowNum) {
        for (int columnNum = 0; columnNum < lastRowNum; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                Row currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }
                if (currentRow.getCell(columnNum) != null) {
                    Cell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == CellType.STRING) {
                        int length = currentCell.getStringCellValue().getBytes(StandardCharsets.UTF_8).length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }
    //基于反射遍历实体类，填充excel
    /* * @Author lfh
     * @Description
     * @Date 2020/4/9 9:14
     * @Param [clazz, sheet, workbook,cluNum]  class 实体类  sheet 导出的表  workbook 导出的excel CluNum 导出数据的列数
     * @return void
     **/
    public static  <T> void fillExcelDate(T clazz, Sheet sheet, Workbook workbook,int cluNum){
        Field[] declaredFields = clazz.getClass().getDeclaredFields();
        String[] values = new String[declaredFields.length];
        try {
            Field.setAccessible(declaredFields, true);
            for (int i = 0; i < declaredFields.length; i++) {
                if (null == declaredFields[i].get(clazz)){
                    try {
                        declaredFields[i].set(clazz, "");
                    }catch (Exception e){
                        if (declaredFields[i].getType() == Integer.class){
                            declaredFields[i].set(clazz, 0);
                        }
                        if (declaredFields[i].getType() == Double.class){
                            declaredFields[i].set(clazz, 0D);
                        }


                    }

                }
                values[i] = declaredFields[i].get(clazz).toString();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //追加数据的开始行
        int start = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(start);
        for (int i = 0; i < cluNum; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            // String value = ExcelUtils.getValue(cell);
            cell.setCellType(CellType.STRING);
            String value = cell.getStringCellValue();
            cell.setCellValue(value);
            CellStyle contentCellStyle = ExportExcel.createContentCellStyle(workbook);
            cell.setCellStyle(contentCellStyle);
        }
    }

    public static void outputToWeb(String realPath, HttpServletResponse response,Workbook workbook) throws IOException {
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(realPath.getBytes("gbk"), "iso8859-1"));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        workbook.write(bufferedOutputStream);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }
}
