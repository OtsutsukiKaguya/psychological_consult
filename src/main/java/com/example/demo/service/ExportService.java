package com.example.demo.service;

import com.example.demo.entity.Mood;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
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

    public byte[] generateFile(List<Mood> records, String format) {
        switch (format.toLowerCase()) {
            case "csv":
                return exportAsCSV(records);
            case "txt":
                return exportAsTXT(records);
            case "pdf":
                return exportAsPDF(records);
            case "excel":
                return exportAsExcel(records);
            default:
                throw new UnsupportedOperationException("不支持的导出格式：" + format);
        }
    }

    private byte[] exportAsCSV(List<Mood> records) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

            writer.write('\uFEFF'); // 添加 BOM
            writer.write("User ID,Mood Date,Mood Type,Mood Content\n");

            for (Mood r : records) {
                String userId = r.getUserId() != null ? r.getUserId() : "";
                String date = r.getMoodDate() != null ? r.getMoodDate().toString() : "";
                String type = r.getMoodType() != null ? r.getMoodType() : "";
                String content = r.getMoodContent() != null ? r.getMoodContent() : "";
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"\n", userId, date, type, content));
            }

            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出 CSV 文件失败", e);
        }
    }

    private byte[] exportAsTXT(List<Mood> records) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {

            for (Mood r : records) {
                String line = String.format("[%s] %s - %s",
                        r.getMoodDate() != null ? r.getMoodDate().toString() : "无日期",
                        r.getMoodType() != null ? r.getMoodType() : "未填写类型",
                        r.getMoodContent() != null ? r.getMoodContent() : "未填写内容");
                writer.write(line);
                writer.newLine();
            }

            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出 TXT 文件失败", e);
        }
    }

    private byte[] exportAsPDF(List<Mood> records) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);

            // 加载中文字体
            FontProgram fontProg = FontProgramFactory.createFont(
                    this.getClass().getResource("/fonts/STSONG.TTF").toExternalForm());
            PdfFont font = PdfFontFactory.createFont(
                    fontProg,
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            document.add(new Paragraph("用户心情记录导出").setFontSize(18).setBold());
            document.add(new Paragraph(" "));

            for (Mood r : records) {
                String date = r.getMoodDate() != null ? r.getMoodDate().toString() : "无日期";
                String type = r.getMoodType() != null ? r.getMoodType() : "未填写类型";
                String content = r.getMoodContent() != null ? r.getMoodContent() : "未填写内容";

                String line = String.format("[%s] %s - %s", date, type, content);
                document.add(new Paragraph(line));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出 PDF 文件失败", e);
        }
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private byte[] exportAsExcel(List<Mood> records) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Mood Records");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("UserID");
            header.createCell(1).setCellValue("MoodDate");
            header.createCell(2).setCellValue("MoodType");
            header.createCell(3).setCellValue("MoodContent");

            int rowNum = 1;
            for (Mood r : records) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getUserId() != null ? r.getUserId() : "");
                row.createCell(1).setCellValue(r.getMoodDate() != null ? r.getMoodDate().format(formatter) : "");
                row.createCell(2).setCellValue(r.getMoodType() != null ? r.getMoodType() : "");
                row.createCell(3).setCellValue(r.getMoodContent() != null ? r.getMoodContent() : "");
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出 Excel 文件失败", e);
        }
    }
}
