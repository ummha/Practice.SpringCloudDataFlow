package com.example.task.batch;

import com.example.task.dto.RowData;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelRowIterator extends DefaultHandler implements Iterator<RowData> {


    private final InputStream sheetInputStream;
    private final ReadOnlySharedStringsTable sharedStringsTable;
    private final List<RowData> rows = new ArrayList<>();
    private int rowIndex = 0;

    public ExcelRowIterator(InputStream sheetInputStream, ReadOnlySharedStringsTable sharedStringsTable) throws Exception {
        this.sheetInputStream = sheetInputStream;
        this.sharedStringsTable = sharedStringsTable;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        XMLReader parser = saxParser.getXMLReader(); // 권장 방식으로 XMLReader 생성

        parser.setContentHandler(this);
        parser.parse(new InputSource(sheetInputStream));
    }

    @Override
    public boolean hasNext() {
        return rowIndex < rows.size();
    }

    @Override
    public RowData next() {
        return rows.get(rowIndex++);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        // SAX 파서로 행 데이터 읽기 로직 구현 (셀의 데이터 유형 및 내용 처리)
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        // 셀 데이터 종료 처리
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // 셀 데이터 문자 처리
    }
}
