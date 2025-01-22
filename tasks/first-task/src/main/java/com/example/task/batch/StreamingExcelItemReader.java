package com.example.task.batch;

import com.example.task.dto.ExcelRow;
import com.example.task.dto.RowData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class StreamingExcelItemReader extends AbstractItemStreamItemReader<ExcelRow> {

    private ExcelRowIterator rowIterator;

    private OPCPackage opcPackage = null;

    private int rowCount = 0; // 전체 행 개수를 추적하는 변수

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            // 엑셀 파일 경로 설정
            Path filePath = Paths.get(Paths.get("").toAbsolutePath().toString(), "/files/test.xlsx");

            // 엑셀 파일 열기
            opcPackage = OPCPackage.open(filePath.toFile());
            XSSFReader xssfReader = new XSSFReader(opcPackage);

            // SharedStringTable 또는 ReadOnlySharedStringTable 은 sharedString.xml 전체 정보를 메모리에 할당함
            // ReadOnlySharedStringsTable stringsTable = new ReadOnlySharedStringsTable(opcPackage);

            // 1. sharedStrings.xml 스트리밍 처리 (Streaming 방식 채택)
            InputStream sharedStringsStream = xssfReader.getSharedStringsData();
            List<String> sharedStrings = parseSharedStrings(sharedStringsStream);

            // 2. Sheet 데이터 스트리밍 처리
            InputStream sheetInputStream = xssfReader.getSheetsData().next();
            rowIterator = new ExcelRowIterator(sheetInputStream, sharedStrings);
        } catch (Exception e) {
            throw new ItemStreamException("Failed to open Excel file", e);
        }
    }

    @Override
    public ExcelRow read() {
        if (rowIterator != null && rowIterator.hasNext()) {
            RowData rowData = rowIterator.next();
            rowCount++; // 행 개수를 증가시킴
            return new ExcelRow(rowData.column1(), rowData.column2());
        }
        return null; // End of file
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);
        executionContext.putInt("rowCount", rowCount);
        log.info("##> rowCount-1: {}", rowCount);
        log.info("##> rowCount-2: {}", rowIterator.getRowCount());
    }

    @Override
    public void close() throws ItemStreamException {
        try {
            if (opcPackage != null) {
                opcPackage.close();
            }
            long totalRow = rowIterator.getRowCount();
        } catch (IOException e) {
            throw new ItemStreamException("Failed to close Excel file", e);
        } finally {
            opcPackage = null;
            rowIterator = null;
        }
    }

    private List<String> parseSharedStrings(InputStream sharedStringsStream) throws Exception {
        StreamingSharedStringsHandler handler = new StreamingSharedStringsHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(new InputSource(sharedStringsStream), handler);

        return handler.getSharedStrings();
    }
}
