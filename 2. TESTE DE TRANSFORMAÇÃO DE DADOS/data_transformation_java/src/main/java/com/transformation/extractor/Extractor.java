package com.transformation.extractor;

import com.transformation.LogsMain;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Extractor {

    public static List<List<String>> extractTablesFromPDF(String pdfPath) throws IOException {
        LogsMain.logInfo(" Lendo PDF: " + pdfPath);
        File pdfFile = new File(pdfPath);
        PDDocument pdfDocument = PDDocument.load(pdfFile);
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        List<List<String>> allData = new ArrayList<>();

        for (int page = 1; page <= pdfDocument.getNumberOfPages(); page++) {
            Page pdfPage = extractor.extract(page);
            List<Table> tables = sea.extract(pdfPage);
            for (Table table : tables) {
                List<List<RectangularTextContainer>> rows = table.getRows();
                for (List<RectangularTextContainer> row : rows) {
                    List<String> rowData = new ArrayList<>();
                    for (RectangularTextContainer cell : row) {
                        rowData.add(cell.getText());
                    }
                    allData.add(rowData);
                }
            }
        }

        pdfDocument.close();
        LogsMain.logInfo("Tabelas extraídas com sucesso!");
        return allData;
    }
}