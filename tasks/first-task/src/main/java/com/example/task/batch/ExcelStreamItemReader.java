package com.example.task.batch;

import com.example.task.dto.RowData;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

@Component
@StepScope
public class ExcelStreamItemReader extends AbstractItemStreamItemReader<Map<String, Object>> {

    private Iterator<RowData> rowIterator;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        Path filePath = Paths.get(Paths.get("").toAbsolutePath().toString(), "/files/test.xlsx");

        try {
            OPCPackage opcPackage = OPCPackage.open(filePath.toFile());
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            ReadOnlySharedStringsTable stringsTable = new ReadOnlySharedStringsTable(opcPackage);
            InputStream sheetInputStream = xssfReader.getSheetsData().next();

            rowIterator = new ExcelRowIterator(sheetInputStream, stringsTable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (InputStream inputStream = new FileInputStream(filePath.toFile())){
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> read() {
        return null;
    }


    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        super.close();
    }
}
