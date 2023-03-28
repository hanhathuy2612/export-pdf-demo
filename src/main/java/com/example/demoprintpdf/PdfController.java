package com.example.demoprintpdf;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("api/pdf")
@RequiredArgsConstructor
public class PdfController {
    private final SpringTemplateEngine templateEngine;
    private final HttpServletRequest httpServletRequest;
    private final ResourceLoader resourceLoader;
    private static final String PDF_TEMPLATE_URL = "pdf_template.html";

    @GetMapping("download")
    public ResponseEntity<byte[]> download() {
        // Prepare data
        List<User> users = generateUsers();
        // Resolve data
        Context context = buildContext(users);
        String html = templateEngine.process(
                PDF_TEMPLATE_URL,
                context
        );
        // Convert html to byte[]
        byte[] pdfByteData = PdfConverter.convert(html);
        // Response header data
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        httpHeaders.setContentLength(pdfByteData.length);
        httpHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        httpHeaders.setContentDispositionFormData(
                "filename",
                "huhaha.pdf"
        );

        return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI()))
                .headers(httpHeaders)
                .body(pdfByteData);
    }

    private Context buildContext(List<User> users) {
        Context context = new Context();
        context.setVariable("users", users);
        context.setVariable("imageData", formatImageBase64String(convertImageToBase64()));
        return context;
    }

    private String formatImageBase64String(String base64) {
        return String.format("data:image/png;base64,%s", base64);
    }

    private String convertImageToBase64() {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/images/istockphoto-1322123064-612x612.jpg");
            byte[] imageAsBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return Base64.getEncoder().encodeToString(imageAsBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> generateUsers() {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(
                    User.builder()
                            .name("huy")
                            .email("huy@gmail.com")
                            .phone("0344551543")
                            .build()
            );
        }
        return list;
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    public static class User {
        private String name;
        private String email;
        private String phone;
    }
}
