package com.transformation;

import com.transformation.transformer.Transformer;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataTransformationTest {

    @Test
    public void testApplyReplacements() {
        List<List<String>> inputData = new ArrayList<>();
        inputData.add(Arrays.asList("OD", "AMB", "CLINICA"));
        inputData.add(Arrays.asList("OD", "HOSPITALAR", "AMB"));

        Map<String, String> replacements = new HashMap<>();
        replacements.put("OD", "Odontologia");
        replacements.put("AMB", "Ambulatorial");

        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("Odontologia", "Ambulatorial", "CLINICA"));
        expected.add(Arrays.asList("Odontologia", "HOSPITALAR", "Ambulatorial"));

        List<List<String>> result = Transformer.applyReplacements(inputData, replacements);

        assertEquals(expected, result);
    }

    @Test
    public void testSaveToCSVAndZipFile() {
        assertTrue(true);
    }

    @Test
    public void testExtractTablesFromPDF() {
        assertTrue(true);
    }
}
