package com.example.demoprintpdf;

import com.lowagie.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class PdfConverter {
    private PdfConverter() {
    }

    public static byte[] convert(String text) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(text);
            renderer.layout();
            renderer.createPDF(outputStream);

            outputStream.close();
            return outputStream.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
