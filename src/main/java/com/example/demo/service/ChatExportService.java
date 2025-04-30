package com.example.demo.service;

import com.example.demo.models.ChatMessage;
import com.example.demo.models.ChatSession;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ChatExportService {

    /**
     * 根据格式直接生成文件的二进制内容，包含 Session 的评分和评论信息
     */
    public byte[] generateFileContent(ChatSession session, List<ChatMessage> messages, String format) {
        switch (format.toLowerCase()) {
            case "csv":
                return exportAsCSV(session, messages);
            case "txt":
                return exportAsTXT(session, messages);
            case "pdf":
                return exportAsPDF(session, messages);
            case "excel":
                return exportAsExcel(session, messages);
            default:
                throw new UnsupportedOperationException("不支持的导出格式：" + format);
        }
    }

    private byte[] exportAsCSV(ChatSession session, List<ChatMessage> messages) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

            writer.write('\uFEFF'); // BOM

            writer.write("Rating,UserComment,CounselorComment,TutorComment\n");
            writer.write(String.format("%s,\"%s\",\"%s\",\"%s\"\n\n",
                    session.getRating() != null ? session.getRating() : "",
                    session.getUserComment() != null ? session.getUserComment().replace("\"", "\"\"") : "",
                    session.getCounselorComment() != null ? session.getCounselorComment().replace("\"", "\"\"") : "",
                    session.getTutorComment() != null ? session.getTutorComment().replace("\"", "\"\"") : ""));

            writer.write("Sender,Sent At,Type,Content\n");
            for (ChatMessage msg : messages) {
                String sender = msg.getSender() != null ? msg.getSender().getId() : "Unknown";
                String time = msg.getSentAt() != null ? dtf.format(msg.getSentAt()) : "";
                String type = msg.getType() != null ? msg.getType().name() : "";
                String content = msg.getContent() != null ? msg.getContent().replace("\"", "\"\"") : "";

                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        sender, time, type, content));
            }

            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出 CSV 文件失败", e);
        }
    }

    private byte[] exportAsTXT(ChatSession session, List<ChatMessage> messages) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {

            writer.write("Rating: " + (session.getRating() != null ? session.getRating() : "") + "\n");
            writer.write("User Comment: " + (session.getUserComment() != null ? session.getUserComment() : "") + "\n");
            writer.write("Counselor Comment: " + (session.getCounselorComment() != null ? session.getCounselorComment() : "") + "\n");
            writer.write("Tutor Comment: " + (session.getTutorComment() != null ? session.getTutorComment() : "") + "\n\n");

            for (ChatMessage msg : messages) {
                String sender = msg.getSender() != null ? msg.getSender().getId() : "Unknown";
                String time = msg.getSentAt() != null ? dtf.format(msg.getSentAt()) : "";
                String type = msg.getType() != null ? msg.getType().name() : "";
                String content = msg.getContent() != null ? msg.getContent() : "";

                writer.write(String.format("[%s] %s (%s): %s\n", time, sender, type, content));
            }

            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出 TXT 文件失败", e);
        }
    }

    private byte[] exportAsPDF(ChatSession session, List<ChatMessage> messages) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            document.add(new Paragraph("会话聊天记录导出").setFontSize(18).setBold());
            document.add(new Paragraph(" "));

            document.add(new Paragraph("评分: " + (session.getRating() != null ? session.getRating() : "")));
            document.add(new Paragraph("用户评论: " + (session.getUserComment() != null ? session.getUserComment() : "")));
            document.add(new Paragraph("咨询师评论: " + (session.getCounselorComment() != null ? session.getCounselorComment() : "")));
            document.add(new Paragraph("辅导员评论: " + (session.getTutorComment() != null ? session.getTutorComment() : "")));

            document.add(new Paragraph(" "));

            for (ChatMessage msg : messages) {
                String sender = msg.getSender() != null ? msg.getSender().getId() : "Unknown";
                String time = msg.getSentAt() != null ? dtf.format(msg.getSentAt()) : "";
                String type = msg.getType() != null ? msg.getType().name() : "";
                String content = msg.getContent() != null ? msg.getContent() : "";

                String line = String.format("[%s] %s (%s): %s", time, sender, type, content);
                document.add(new Paragraph(line));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出 PDF 文件失败", e);
        }
    }

    private byte[] exportAsExcel(ChatSession session, List<ChatMessage> messages) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Chat Records");

            Row metaRow = sheet.createRow(0);
            metaRow.createCell(0).setCellValue("Rating");
            metaRow.createCell(1).setCellValue("User Comment");
            metaRow.createCell(2).setCellValue("Counselor Comment");
            metaRow.createCell(3).setCellValue("Tutor Comment");

            Row metaValues = sheet.createRow(1);
            metaValues.createCell(0).setCellValue(session.getRating() != null ? session.getRating() : 0);
            metaValues.createCell(1).setCellValue(session.getUserComment() != null ? session.getUserComment() : "");
            metaValues.createCell(2).setCellValue(session.getCounselorComment() != null ? session.getCounselorComment() : "");
            metaValues.createCell(3).setCellValue(session.getTutorComment() != null ? session.getTutorComment() : "");

            // 3行空白，聊天记录从第5行开始
            Row header = sheet.createRow(4);
            header.createCell(0).setCellValue("Sender");
            header.createCell(1).setCellValue("Sent At");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Content");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 5;
            for (ChatMessage msg : messages) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(msg.getSender() != null ? msg.getSender().getId() : "Unknown");
                row.createCell(1).setCellValue(msg.getSentAt() != null ? dtf.format(msg.getSentAt()) : "");
                row.createCell(2).setCellValue(msg.getType() != null ? msg.getType().name() : "");
                row.createCell(3).setCellValue(msg.getContent() != null ? msg.getContent() : "");
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("导出 Excel 文件失败", e);
        }
    }
}