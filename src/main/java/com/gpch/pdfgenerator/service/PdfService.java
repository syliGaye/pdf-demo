package com.gpch.pdfgenerator.service;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class PdfService {

    private static final String PDF_RESOURCES = "/pdf-resources/";
    private StudentService studentService;
    private SpringTemplateEngine templateEngine;

    @Autowired
    public PdfService(StudentService studentService, SpringTemplateEngine templateEngine) {
        this.studentService = studentService;
        this.templateEngine = templateEngine;
    }

    public File generatePdf() throws IOException, DocumentException, InterruptedException {
        Context context = getContext();
        String html = loadAndFillTemplate(context);
        return renderPdf(html);
    }


    private File renderPdf(String html) throws IOException, DocumentException, InterruptedException {
        File file = File.createTempFile("students", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(35f * 4f / 3f, 20);
        String toExternal = new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm();
        System.out.println("Source ==> "+ new ResourceLoaderUserAgent(renderer.getOutputDevice()).resolveAndOpenStream(toExternal).toString());
        //Thread.sleep(100000); // LOCK is held
        renderer.setDocumentFromString(html, toExternal);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private Context getContext() {
        Context context = new Context();
        context.setVariable("students", studentService.getStudents());
        return context;
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("/pdf/test_2", context);
    }


}
