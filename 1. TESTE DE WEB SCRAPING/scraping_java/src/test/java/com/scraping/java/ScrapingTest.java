package com.scraping.java;

import com.scraping.java.config.Anexo;
import com.scraping.java.config.Config;
import com.scraping.java.directory.DirectoryManager;
import com.scraping.java.directory.PDFDownloader;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScrapingTest {

    private List<Config> configList;

    @BeforeAll
    public void setup() throws IOException {
        configList = Config.loadAll("config/config.json");
    }

    @Test
    public void testLoadMultipleConfigs() {
        assertNotNull(configList);
        assertFalse(configList.isEmpty());
    }

    @Test
    public void testEachConfigStructure() {
        for (Config config : configList) {
            assertNotNull(config.url, "URL não pode ser nula");
            assertNotNull(config.downloadDirectory, "Diretório de download não pode ser nulo");
            assertNotNull(config.outputZip, "Nome do zip não pode ser nulo");
            assertNotNull(config.anexos, "Lista de anexos não pode ser nula");
            assertFalse(config.anexos.isEmpty(), "Lista de anexos não pode estar vazia");
        }
    }

    @Test
    public void testCreateDirectory() {
        String testDir = "temp_test_dir";
        DirectoryManager.createDirectory(testDir);
        File dir = new File(testDir);
        assertTrue(dir.exists() && dir.isDirectory());
        dir.delete();
    }

    @Test
    public void testPDFDownloaderInitialization() {
        Config firstConfig = configList.get(0);
        PDFDownloader downloader = new PDFDownloader(
                firstConfig.url,
                firstConfig.downloadDirectory,
                firstConfig.anexos
        );
        assertNotNull(downloader);
    }

    @Test
    public void testAnexosRegexConfiguredProperly() {
        Config firstConfig = configList.get(0);
        List<Anexo> anexos = firstConfig.anexos;
        assertTrue(anexos.size() >= 2, "Deve haver pelo menos dois anexos para este teste");

        Anexo anexo1 = anexos.get(0);
        Anexo anexo2 = anexos.get(1);

        assertNotNull(anexo1.nome);
        assertNotNull(anexo1.regex);
        assertNotNull(anexo2.nome);
        assertNotNull(anexo2.regex);
    }

    @Test
    public void testLoadLegacySingleConfig() {
        try {
            List<Config> legacyList = Config.loadAll("config/config.json");
            assertNotNull(legacyList);
            assertFalse(legacyList.isEmpty(), "A lista de configurações não pode estar vazia");

            Config legacy = legacyList.get(0);
            assertNotNull(legacy.url);
            assertNotNull(legacy.downloadDirectory);
            assertNotNull(legacy.outputZip);
            assertNotNull(legacy.anexos);
            assertFalse(legacy.anexos.isEmpty());
        } catch (IOException e) {
            fail("IOException thrown: " + e.getMessage());
        }
    }

}
