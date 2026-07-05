package utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ExcelTestData {
    private static final String DEFAULT_RESOURCE = "testdata/auth-test-data.xlsx";
    private static final String OVERRIDE_FILE_PROPERTY = "auth.testdata.file";

    private ExcelTestData() {
    }

    public static Object[][] rows(String sheetName, String... testCaseIds) {
        Set<String> selectedIds = Arrays.stream(testCaseIds).collect(Collectors.toSet());
        return readSheet(sheetName).stream()
                .filter(ExcelTestData::shouldRun)
                .filter(row -> selectedIds.isEmpty() || selectedIds.contains(row.get("testCaseId")))
                .map(ExcelTestData::resolvePlaceholders)
                .map(row -> new Object[]{row})
                .toArray(Object[][]::new);
    }

    public static boolean isTrue(Map<String, String> row, String key) {
        String value = row.getOrDefault(key, "").trim().toLowerCase(Locale.ROOT);
        return value.equals("true") || value.equals("yes") || value.equals("y") || value.equals("1");
    }

    public static String value(Map<String, String> row, String key) {
        return row.getOrDefault(key, "");
    }

    public static String description(Map<String, String> row) {
        return row.getOrDefault("testCaseId", "Excel row") + " - " + row.getOrDefault("scenario", "");
    }

    private static List<Map<String, String>> readSheet(String sheetName) {
        try (InputStream inputStream = openWorkbookStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found in auth test data workbook: " + sheetName);
            }

            DataFormatter formatter = new DataFormatter();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Sheet has no header row: " + sheetName);
            }

            List<String> headers = new ArrayList<>();
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                headers.add(formatter.formatCellValue(headerRow.getCell(cellIndex)).trim());
            }

            List<Map<String, String>> data = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);
                if (currentRow == null) {
                    continue;
                }

                Map<String, String> rowData = new LinkedHashMap<>();
                boolean hasValue = false;
                for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
                    String value = formatter.formatCellValue(currentRow.getCell(cellIndex)).trim();
                    rowData.put(headers.get(cellIndex), value);
                    hasValue = hasValue || !value.isBlank();
                }
                if (hasValue) {
                    data.add(rowData);
                }
            }
            return data;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read auth test data workbook", ex);
        }
    }

    private static InputStream openWorkbookStream() throws IOException {
        String overridePath = System.getProperty(OVERRIDE_FILE_PROPERTY);
        if (overridePath != null && !overridePath.isBlank()) {
            return Files.newInputStream(Path.of(overridePath));
        }

        InputStream classpathStream = ExcelTestData.class.getClassLoader().getResourceAsStream(DEFAULT_RESOURCE);
        if (classpathStream == null) {
            throw new IllegalStateException("Auth test data workbook not found on classpath: " + DEFAULT_RESOURCE);
        }
        return classpathStream;
    }

    private static boolean shouldRun(Map<String, String> row) {
        String runValue = row.getOrDefault("run", "true").trim();
        return runValue.isBlank() || isTrue(row, "run");
    }

    private static Map<String, String> resolvePlaceholders(Map<String, String> row) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "");

        Map<String, String> resolved = new LinkedHashMap<>();
        row.forEach((key, value) -> resolved.put(key, value
                .replace("{timestamp}", timestamp)
                .replace("{uuid}", uuid)));
        return resolved;
    }
}
