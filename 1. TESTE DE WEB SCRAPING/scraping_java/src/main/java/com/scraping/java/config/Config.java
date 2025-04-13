package com.scraping.java.config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Config {
    public String url;

    @SerializedName("download_directory")
    public String downloadDirectory;

    @SerializedName("output_zip")
    public String outputZip;

    public List<Anexo> anexos;

    public static List<Config> loadAll(String path) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
            return new Gson().fromJson(reader, new com.google.gson.reflect.TypeToken<List<Config>>() {}.getType());
        }
    }

}
