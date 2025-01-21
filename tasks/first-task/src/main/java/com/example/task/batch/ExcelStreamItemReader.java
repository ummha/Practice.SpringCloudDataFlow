package com.example.task.batch;

import com.example.task.dto.ExcelRow;
import com.example.task.dto.RowData;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Component
@StepScope
public class ExcelStreamItemReader extends AbstractItemStreamItemReader<ExcelRow> {

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
    }

    @Override
    public ExcelRow read() {
        if (rowIterator != null && rowIterator.hasNext()) {
            RowData rowData = rowIterator.next();
            return new ExcelRow(rowData.rowIndex(), rowData.column1(), rowData.column2());
        }
        return null; // End of file
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        rowIterator = null;
    }
}
