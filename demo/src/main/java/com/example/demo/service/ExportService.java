package com.example.demo.service;

import com.example.demo.entity.Mood;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.itextpdf.layout.Document; //iText PDF Document 类
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class ExportService {
    private final String EXPORT_DIR = "D:/mood_exports";

    public String generateFile(List<Mood> records, String format, String filename) {
        // 创建导出目录（如果不存在）
        File dir = new File(EXPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        switch (format.toLowerCase()) {
            case "csv":
                return exportAsCSV(records, filename);
            case "txt":
                return exportAsTXT(records, filename);
            case "pdf":
                return exportAsPDF(records, filename);
//            case "excel":
//                return exportAsExcel(records, filename);
            default:
                throw new UnsupportedOperationException("不支持的导出格式：" + format);
        }
    }

    private String exportAsCSV(List<Mood> records, String filename) {
        String path = EXPORT_DIR + "/" + filename;
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)) {
            writer.write('\uFEFF'); // 添加 UTF-8 BOM，防止 Excel 打开乱码
            writer.write("User ID,Mood Date,Mood Type,Mood Content\n");

            for (Mood r : records) {
                String userId = r.getUserId() != null ? r.getUserId() : "";
                String date = r.getMoodDate() != null ? r.getMoodDate().toString() : "";
                String type = r.getMoodType() != null ? r.getMoodType() : "";
                String content = r.getMoodContent() != null ? r.getMoodContent() : "";

                // 注意防止 CSV 注入漏洞或破坏结构，建议用双引号包裹文本内容
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        userId, date, type, content));
            }

        } catch (IOException e) {
            throw new RuntimeException("导出 CSV 文件失败", e);
        }
        return path;
    }


    private String exportAsTXT(List<Mood> records, String filename) {
        String path = EXPORT_DIR + "/" + filename;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Mood r : records) {
                writer.write(String.format("[%s] %s - %s", r.getUserId(),r.getMoodDate(), r.getMoodType(), r.getMoodContent()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("导出 TXT 文件失败", e);
        }
        return path;
    }


    private String exportAsPDF(List<Mood> records, String filename) {
        String path = EXPORT_DIR + "/" + filename;
        try (PdfWriter writer = new PdfWriter(path);
             PdfDocument pdfDoc = new PdfDocument(writer);
             com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc)) {

            document.add(new Paragraph("用户心情记录导出").setFontSize(18).setBold());

            for (Mood r : records) {
                String date = r.getMoodDate() != null ? r.getMoodDate().toString() : "无日期";
                String type = r.getMoodType() != null ? r.getMoodType() : "未填写类型";
                String content = r.getMoodContent() != null ? r.getMoodContent() : "未填写内容";

                String line = String.format("[%s] %s - %s", date, type, content);
                document.add(new Paragraph(line));
            }

            System.out.println("导出记录调试 >>>");
            for (Mood r : records) {
                System.out.println("Date: " + r.getMoodDate());
                System.out.println("Type: " + r.getMoodType());
                System.out.println("Content: " + r.getMoodContent());
            }

        } catch (Exception e) {
            throw new RuntimeException("导出 PDF 文件失败", e);
        }
        return path;
    }



//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    private String exportAsExcel(List<Mood> records, String filename) {
//        String path = EXPORT_DIR + "/" + filename;
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("Mood Records");
//
//            Row header = sheet.createRow(0);
//            header.createCell(0).setCellValue("UserID");
//            header.createCell(1).setCellValue("MoodDate");
//            header.createCell(2).setCellValue("MoodType");
//            header.createCell(3).setCellValue("MoodContent");
//
//            int rowNum = 1;
//            for (Mood r : records) {
//                Row row = sheet.createRow(rowNum++);
//                row.createCell(0).setCellValue(r.getUserId());
////                row.createCell(1).setCellValue(r.getMoodDate());
//                row.createCell(1).setCellValue(r.getMoodDate().format(formatter));
//
//                row.createCell(2).setCellValue(r.getMoodType());
//                row.createCell(3).setCellValue(r.getMoodContent());
//            }
//
//            try (FileOutputStream out = new FileOutputStream(path)) {
//                workbook.write(out);
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException("导出 Excel 文件失败", e);
//        }
//        return path;
//    }

}
