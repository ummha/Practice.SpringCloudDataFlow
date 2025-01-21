package com.example.task.batch;

import com.example.task.dto.RowData;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelRowIterator extends DefaultHandler implements Iterator<RowData> {


    private final InputStream sheetInputStream;
    private final ReadOnlySharedStringsTable sharedStringsTable;
    private List<RowData> rows = new ArrayList<>();
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
    public RowData next() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    }
}
