package com.scraping.java.directory;

import com.scraping.java.LogsMain;
import com.scraping.java.config.Anexo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PDFDownloader {

    private final String url;
    private final String downloadDir;
    private final List<Anexo> anexos;

    public PDFDownloader(String url, String downloadDir, List<Anexo> anexos) {
        this.url = url;
        this.downloadDir = downloadDir;
        this.anexos = anexos;
    }

    public List<String> downloadAnexos() throws IOException {
        LogsMain.logWarning("Acessando: " + url);
        List<String> downloaded = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href$=.pdf]");

        for (Anexo anexo : anexos) {
            Pattern pattern = Pattern.compile(anexo.regex, Pattern.CASE_INSENSITIVE);
            for (Element link : links) {
                String linkText = link.text();
                String href = link.attr("href");
                if (pattern.matcher(linkText).find() || pattern.matcher(href).find()) {
                    String fullUrl = href.startsWith("http") ? href : "https://www.gov.br" + (href.startsWith("/") ? href : "/" + href);
                    String filename = downloadDir + File.separator + anexo.nome.replaceAll(" ", "_") + ".pdf";
                    if (downloadFile(fullUrl, filename)) {
                        downloaded.add(filename);
                    }
                    break;
                }
            }
        }
        return downloaded;
    }

    private boolean downloadFile(String fileURL, String savePath) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            LogsMain.logInfo("Download concluído: " + savePath);
            return true;
        } catch (IOException e) {
            LogsMain.logInfo("Erro ao baixar: " + fileURL + " - " + e.getMessage());
            return false;
        }
    }
}
