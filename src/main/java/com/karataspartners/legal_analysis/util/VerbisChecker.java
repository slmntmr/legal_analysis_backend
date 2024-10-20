package com.karataspartners.legal_analysis.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class VerbisChecker {

    public String checkVerbisRecord(String url) {
        if (!isValidURL(url)) {
            return "Geçersiz URL. Lütfen http:// veya https:// ile başlayan geçerli bir URL girin.";
        }

        try {
            Connection connection = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                    .timeout(10000);
            Document doc = connection.get();

            String companyName = "";
            for (Element element : doc.getAllElements()) {
                String text = element.text();
                // Şirket adını çıkarıyoruz
                if (text.contains("Anonim Şirketi") || text.contains("A.Ş.") ||
                        text.contains("Limited Şirketi") || text.contains("Ltd. Şti.")) {
                    String[] parts = text.split(" ");
                    if (parts.length > 0) {
                        companyName = parts[0]; // Şirketin ismini alıyoruz
                    }
                    break;
                }
            }

            System.out.println("Şirket Adı: " + companyName); // Debug

            if (!companyName.isEmpty()) {
                String encodedCompanyName = URLEncoder.encode(companyName, StandardCharsets.UTF_8.toString());
                String verbisURL = "https://verbis.kvkk.gov.tr/sorgula?sirket=" + encodedCompanyName;

                // Verbis'e sorgu at
                Connection.Response response = Jsoup.connect(verbisURL).execute();
                System.out.println("HTTP Yanıt Kodu: " + response.statusCode()); // Debug

                Document verbisDoc = response.parse();
                System.out.println(verbisDoc.text()); // Debug

                // "kayıtlı" kelimesini kontrol et
                if (verbisDoc.text().contains("kayıtlı") || verbisDoc.text().contains("var")) {
                    return url + " için Verbis kaydı bulundu.";
                }
            }
            return url + " için Verbis kaydı bulunamadı.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Verbis kaydı kontrol edilirken hata oluştu: " + e.getMessage();
        }
    }




    //********************************************************************************************
    public String checkETBISRecord(String url) {
        if (!isValidURL(url)) {
            return "Geçersiz URL. Lütfen http:// veya https:// ile başlayan geçerli bir URL girin.";
        }

        try {
            Connection connection = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                    .timeout(10000);
            Document doc = connection.get();

            String companyName = "";
            for (Element element : doc.getAllElements()) {
                String text = element.text();
                // Şirket adını çıkarıyoruz
                if (text.contains("Anonim Şirketi") || text.contains("A.Ş.") ||
                        text.contains("Limited Şirketi") || text.contains("Ltd. Şti.")) {
                    String[] parts = text.split(" ");
                    if (parts.length > 0) {
                        companyName = parts[0]; // Şirketin ismini alıyoruz
                    }
                    break;
                }
            }

            // Şirket adını ETBİS üzerinde sorguluyoruz
            if (!companyName.isEmpty()) {
                String encodedCompanyName = URLEncoder.encode(companyName, StandardCharsets.UTF_8.toString());
                String etbisURL = "https://www.eticaret.gov.tr/sirketsorgula?sirket=" + encodedCompanyName;
                Document etbisDoc = Jsoup.connect(etbisURL).get();
                if (etbisDoc.text().contains("kayıtlı")) {
                    return url + " için ETBİS kaydı bulundu.";
                }
            }
            return url + " için ETBİS kaydı bulunamadı.";
        } catch (IOException e) {
            e.printStackTrace();
            return "ETBİS kaydı kontrol edilirken hata oluştu: " + e.getMessage();
        }
    }
    //********************************************************************************************
    private boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
}
}
}