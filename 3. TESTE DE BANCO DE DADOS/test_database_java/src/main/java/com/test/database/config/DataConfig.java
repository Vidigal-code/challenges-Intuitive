package com.test.database.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.database.LogsMain;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class DataConfig {

    private final List<Map<String, Object>> sources;
    private final String url;
    private final String user;
    private final String password;

    public DataConfig() {
        Dotenv dotenv = Dotenv.load();

        String host = dotenv.get("MYSQL_HOST", "localhost");
        String port = dotenv.get("MYSQL_PORT", "3306");
        String database = dotenv.get("MYSQL_DATABASE", "ans_database");
        this.user = dotenv.get("MYSQL_USER", "root");
        this.password = dotenv.get("MYSQL_PASSWORD", "");

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useSSL=false&serverTimezone=UTC&allowLoadLocalInfile=true";

        try {
            String path = Paths.get("config", "config.json").toString();
            FileReader reader = new FileReader(path);
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            this.sources = new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            LogsMain.logError("Erro ao carregar config.json: " + e.getMessage());
            throw new RuntimeException("Erro ao carregar config.json", e);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public List<Map<String, Object>> getSources() {
        return sources;
    }
}
