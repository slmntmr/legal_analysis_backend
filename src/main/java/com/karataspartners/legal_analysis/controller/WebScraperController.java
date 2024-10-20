package com.karataspartners.legal_analysis.controller;

import com.karataspartners.legal_analysis.service.WebScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/crawler")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class WebScraperController {

    private final WebScraperService webScraperService; // Web Scraper Servisi
    private final MessageSource messageSource; // Dil dosyalarından mesajları çekmek için

    //*******************************************************************************************************
    // URL geçerliliğini kontrol eden metod
    private boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //*******************************************************************************************************
    @GetMapping("/crawl")
    public ResponseEntity<?> crawlWebsite(@RequestParam String url, @RequestHeader(name = "Accept-Language", required = false) String acceptLanguageHeader) {
        // Varsayılan Türkçe Locale
        Locale locale = Locale.forLanguageTag("tr");

        if (acceptLanguageHeader != null && !acceptLanguageHeader.isEmpty()) {
            String[] languages = acceptLanguageHeader.split(",");
            if (languages.length > 0) {
                locale = Locale.forLanguageTag(languages[0]);
            }
        }

        // Global Locale ayarı
        LocaleContextHolder.setLocale(locale);

        System.out.println("İstek Dili: " + acceptLanguageHeader);
        System.out.println("Seçilen Locale: " + locale);
        System.out.println("İstek URL'si: " + url);
        System.out.println("İstek Dili (LocaleContextHolder): " + LocaleContextHolder.getLocale());

        // URL geçersizse
        if (!isValidURL(url)) {
            // `MessageSource` kullanarak mesajları çekiyoruz
            String invalidUrlMessage = messageSource.getMessage("invalid.url", null, locale);
            System.out.println("Geçersiz URL Mesajı: " + invalidUrlMessage);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", invalidUrlMessage));
        }

        // İçerik kontrolü
        String content = webScraperService.fetchWebsiteContent(url);

        if (content != null && !content.isEmpty()) {
            Map<String, Object> analysis = webScraperService.analyzeContent(content);
            return ResponseEntity.ok(analysis);
        } else {
            // İçerik bulunamadığında dil dosyasından mesaj çekiyoruz
            String contentNotFoundMessage = messageSource.getMessage("content.not.found", null, locale);
            System.out.println("İçerik Alınamadı Mesajı: " + contentNotFoundMessage);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", contentNotFoundMessage));
        }
    }
}
