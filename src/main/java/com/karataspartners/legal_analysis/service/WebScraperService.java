package com.karataspartners.legal_analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@AllArgsConstructor
@Service
public class WebScraperService {
    private final OpenAiAnalysisService openAiAnalysisService;

    //*******************************************************************************************************

    // Anahtar kelimeleri ve başlıkları sabit olarak tutuyoruz
    private static final Map<String, Set<String>> KEYWORDS = new HashMap<>();

    static {
        Set<String> keywords = new HashSet<>();

        // İptal, İade ve Cayma Politikası
        keywords.add("İptal");
        keywords.add("İade");
        keywords.add("Cayma");
        keywords.add("Değişim");
        keywords.add("Sipariş");
        keywords.add("Teslim");
        keywords.add("Kargo");
        keywords.add("İletişim");
        keywords.add("Tüketici");
        keywords.add("Hasar");
        KEYWORDS.put("İptal, İade ve Cayma Politikası", new HashSet<>(keywords));

        // Mesafeli Hizmet Satış Sözleşmesi
        keywords.clear();
        keywords.add("Mesafeli");
        keywords.add("Hizmet");
        keywords.add("Satış");
        keywords.add("Sözleşmesi");
        keywords.add("Teslimat");
        keywords.add("Ödeme");
        keywords.add("Cayma");
        keywords.add("Tüketici");
        keywords.add("Ürün");
        keywords.add("Şartlar");
        KEYWORDS.put("Mesafeli Hizmet Satış Sözleşmesi", new HashSet<>(keywords));

        // Ticari Elektronik İleti Onay Metni
        keywords.clear();
        keywords.add("Elektronik");
        keywords.add("Ticari");
        keywords.add("İleti");
        keywords.add("Onay");
        keywords.add("6563");
        keywords.add("SMS");
        keywords.add("Mail");
        keywords.add("Kampanya");
        keywords.add("Bilgilendirme");
        keywords.add("KVKK");
        KEYWORDS.put("Ticari Elektronik İleti Onay Metni", new HashSet<>(keywords));

        // Ön Bilgilendirme Formu
        keywords.clear();
        keywords.add("Ön");
        keywords.add("Bilgilendirme");
        keywords.add("Formu");
        keywords.add("Teslimat");
        keywords.add("Ödeme");
        keywords.add("Cayma");
        keywords.add("Tüketici");
        keywords.add("Hizmet");
        keywords.add("Ürün");
        keywords.add("İade");
        KEYWORDS.put("Ön Bilgilendirme Formu", new HashSet<>(keywords));

        // İşlem Rehberi
        keywords.clear();
        keywords.add("İşlem");
        keywords.add("Rehber");
        keywords.add("Teknik");
        keywords.add("Adım");
        keywords.add("Üye");
        keywords.add("Ödeme");
        keywords.add("Sipariş");
        keywords.add("Değişiklik");
        keywords.add("Teslimat");
        keywords.add("Arşiv");
        KEYWORDS.put("İşlem Rehberi", new HashSet<>(keywords));

        // Promosyon Koşulları
        keywords.clear();
        keywords.add("Promosyon");
        keywords.add("Koşul");
        keywords.add("Kampanya");
        keywords.add("Katılımcı");
        keywords.add("Süre");
        keywords.add("Kriter");
        keywords.add("Hüküm");
        keywords.add("İletişim");
        keywords.add("Milli");
        keywords.add("Piyango");
        KEYWORDS.put("Promosyon Koşulları", new HashSet<>(keywords));

        // Gizlilik ve Güvenlik Politikası
        keywords.clear();
        keywords.add("Gizlilik");
        keywords.add("Güvenlik");
        keywords.add("Politika");
        keywords.add("Kredi Kartı");
        keywords.add("Web");
        keywords.add("Ödeme");
        keywords.add("SSL");
        keywords.add("128 Bit");
        keywords.add("IP");
        keywords.add("Mersis");
        KEYWORDS.put("Gizlilik ve Güvenlik Politikası", new HashSet<>(keywords));

        // Ticari Elektronik İleti Uygulama Kılavuzu
        keywords.clear();
        keywords.add("Ticari");
        keywords.add("Elektronik");
        keywords.add("İleti");
        keywords.add("Kaldırma");
        keywords.add("Linki");
        keywords.add("Mersis");
        KEYWORDS.put("Ticari Elektronik İleti Uygulama Kılavuzu", new HashSet<>(keywords));

        // Çerez Politikası
        keywords.add("Çerez");
        keywords.add("Kalıcı");
        keywords.add("Zorunlu");
        keywords.add("performans");
        keywords.add("İşlevsel");
        keywords.add("Üçüncü");
        keywords.add("Tercih");
        keywords.add("Kvkk");
        keywords.add("Ayarlar");
        keywords.add("Süre");
        KEYWORDS.put("Çerez Politikası", new HashSet<>(keywords));

        // Kişisel Verilerin Korunması Politikası
        keywords.clear();
        keywords.add("Kvkk");
        keywords.add("ilgili kişi");
        keywords.add("kişisel veri");
        keywords.add("veri sorumlusu");
        keywords.add("veri işleyen");
        keywords.add("amaç");
        keywords.add("süre");
        keywords.add("saklama");
        keywords.add("imha");
        keywords.add("mersis");
        KEYWORDS.put("Kişisel Verilerin Korunması Politikası", new HashSet<>(keywords));

        // KVK Aydınlatma Metni
        keywords.clear();
        keywords.add("Kişisel Veri");
        keywords.add("Aydınlatma");
        keywords.add("Veri Sorumlusu");
        keywords.add("Veri İşleyen");
        keywords.add("Toplanma");
        keywords.add("İşlenme");
        keywords.add("Kvkk");
        keywords.add("Bilgilendirme");
        keywords.add("Aktarma");
        KEYWORDS.put("KVK Aydınlatma Metni", new HashSet<>(keywords));

        // KVK Açık Rıza Metni
        keywords.clear();
        keywords.add("Elektronik");
        keywords.add("Ticari");
        keywords.add("İleti");
        keywords.add("Onay");
        keywords.add("6563");
        keywords.add("SMS");
        keywords.add("Mail");
        keywords.add("Kampanya");
        keywords.add("Bilgilendirme");
        keywords.add("KVKK");
        KEYWORDS.put("KVK Açık Rıza Metni", new HashSet<>(keywords));

        // KVK Yurt Dışı Açık Rıza Metni
        keywords.clear();
        keywords.add("Elektronik");
        keywords.add("Ticari");
        keywords.add("İleti");
        keywords.add("Onay");
        keywords.add("6563");
        keywords.add("SMS");
        keywords.add("Mail");
        keywords.add("Kampanya");
        keywords.add("Bilgilendirme");
        keywords.add("KVKK");
        KEYWORDS.put("KVK Yurt Dışı Açık Rıza Metni", new HashSet<>(keywords));

        // KVKK6 - CHATBOT, MAIL KVK USULU
        keywords.add("Elektronik");
        keywords.add("Ticari");
        keywords.add("İleti");
        keywords.add("Onay");
        keywords.add("6563");
        keywords.add("SMS");
        keywords.add("Mail");
        keywords.add("Kampanya");
        keywords.add("Bilgilendirme");
        keywords.add("KVKK");
        KEYWORDS.put("CHATBOT, MAIL KVK USULU", new HashSet<>(keywords));

        // KVKK7 - KURUMSAL İLETİŞİM POLİTİKASI
        keywords.clear(); // Önceki anahtar kelimeleri temizle
        keywords.add("Elektronik");
        keywords.add("Ticari");
        keywords.add("İleti");
        keywords.add("Onay");
        keywords.add("6563");
        keywords.add("SMS");
        keywords.add("Mail");
        keywords.add("Kampanya");
        keywords.add("Bilgilendirme");
        keywords.add("KVKK");
        KEYWORDS.put("KURUMSAL İLETİŞİM POLİTİKASI", new HashSet<>(keywords));

        // KVKK8 - KVK BİLGİ FORMU
        keywords.clear(); // Önceki anahtar kelimeleri temizle
        keywords.add("Elektronik");
        keywords.add("Ticari");
        keywords.add("İleti");
        keywords.add("Onay");
        keywords.add("6563");
        keywords.add("SMS");
        keywords.add("Mail");
        keywords.add("Kampanya");
        keywords.add("Bilgilendirme");
        keywords.add("KVKK");
        KEYWORDS.put("KVK BİLGİ FORMU", new HashSet<>(keywords));
    }




    // URL'den içerik çekme metodu
    public String fetchWebsiteContent(String url) {
        try {
            // Jsoup ile web sitesinin HTML içeriğini al
            Document document = Jsoup.connect(url).get();
            // HTML içeriğini sadeleştirip sadece metni döndür
            return document.text();
        } catch (IOException e) {
            // Hata durumunda null döndür
            e.printStackTrace();
            return null;
        }
    }

    // Anahtar kelime tarama ve puanlama metodu
    public Map<String, Object> analyzeContent(String content) {
        Map<String, Object> result = new HashMap<>();
        int totalScore = 0;
        int totalMaxScore = 0;
        Map<String, Set<String>> missingKeywordsByTitle = new HashMap<>();

        // Her başlık için tarama yapıyoruz
        for (Map.Entry<String, Set<String>> entry : KEYWORDS.entrySet()) {
            String title = entry.getKey();
            Set<String> keywords = entry.getValue();
            Set<String> missingKeywords = new HashSet<>();

            int foundKeywords = 0;
            for (String keyword : keywords) {
                if (!content.contains(keyword)) {
                    missingKeywords.add(keyword);  // Eksik anahtar kelimeyi kaydet
                } else {
                    foundKeywords++;
                }
            }

            int maxScoreForTitle = keywords.size() * 10;
            int scoreForTitle = foundKeywords * 10;
            totalScore += scoreForTitle;
            totalMaxScore += maxScoreForTitle;

            if (!missingKeywords.isEmpty()) {
                missingKeywordsByTitle.put(title, missingKeywords);  // Eksik kelimeleri ekle
            }
        }

        // Genel puanı yüzlük sistemde hesaplıyoruz
        double percentageScore = ((double) totalScore / totalMaxScore) * 100;

        // Puanı virgülden sonra iki basamağa sınırla
        DecimalFormat df = new DecimalFormat("0.00");
        String formattedScore = df.format(percentageScore);

        // Puan aralığına göre durum ve not belirleme
        String durum;
        String not;

        if (percentageScore >= 80) {
            durum = "Hukuka Uygun";
            not = "Geçer Not";
        } else if (percentageScore >= 40) {
            durum = "Metin Detaylı Düzenlenmelidir";
            not = "Geçmez Not";
        } else {
            durum = "Metniniz Hukuka Uygun Değil";
            not = "Geçmez Not";
        }

        // Sonuçları hazırlıyoruz
        result.put("score", formattedScore);
        result.put("missingKeywords", missingKeywordsByTitle);
        result.put("durum", durum);
        result.put("not", not);

        // Yapay zeka ile önerileri ekliyoruz
        Map<String, Object> suggestions = analyzeContentWithAI(content);  // AI analizini çağırıyoruz
        result.put("suggestions", suggestions);  // Önerileri sonuçlara ekliyoruz

        return result;
    }




    //*******************************************************************************************************
/*
    // Öneri üreten metod
    private Map<String, String> generateSuggestions(Map<String, Set<String>> missingKeywordsByTitle) {
        Map<String, String> suggestions = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : missingKeywordsByTitle.entrySet()) {
            String title = entry.getKey();
            Set<String> missingKeywords = entry.getValue();

            StringBuilder suggestion = new StringBuilder();
            suggestion.append(title).append(" başlığı için şunları ekleyin: ");

            for (String keyword : missingKeywords) {
                suggestion.append(keyword).append(", ");
            }

            // Öneriyi temizleyip ekliyoruz
            suggestions.put(title, suggestion.toString().replaceAll(", $", ""));
        }

        return suggestions;
    }*/

    //*******************************************************************************************************


    // Yapay zeka ile içerik analizi yapma metodu
    public Map<String, Object> analyzeContentWithAI(String content) {
        Map<String, Object> result = new HashMap<>();

        // Yapay zeka ile analiz yapıyoruz
        String aiAnalysisResult = openAiAnalysisService.analyzeContentWithAI(content);

        // OpenAI yanıtından sadece mesajı alıyoruz ve temizliyoruz
        String aiMessage = cleanText(extractMessageFromOpenAiResponse(aiAnalysisResult));

        result.put("aiAnalysis", aiMessage); // Yapay zeka sonucu ekleniyor

        return result;
    }

    // OpenAI API yanıtından sadece mesajı çeken metot
    private String extractMessageFromOpenAiResponse(String aiResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode messageNode = root.path("choices").get(0).path("message").path("content");
            return messageNode.asText();
        } catch (IOException e) {
            e.printStackTrace();
            return "Yapay zeka yanıtı işlenirken hata oluştu.";
        }
    }
    // Metindeki gereksiz \n gibi karakterleri temizleme metodu
    public String cleanText(String text) {
        return text.replaceAll("\n", " ").trim(); // Tekli backslash yerine çift backslash kullanarak \n karakterini doğru şekilde hedefliyoruz
}




}