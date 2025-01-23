package com.example.task.batch;

import com.example.task.dto.RowData;
import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExcelRowIterator extends DefaultHandler implements Iterator<RowData> {
    private static final RowData EOF_MARKER = new RowData(true, null, null); // EOF 표시
    private final BlockingQueue<RowData> queue = new LinkedBlockingQueue<>(1000);

    private final InputStream sheetInputStream;
    private final List<String> sharedStrings;

    private final List<RowData> rows = new ArrayList<>(); // 모든 행 데이터를 저장

    private final StringBuilder currentCellValue = new StringBuilder(); // 현재 셀 데이터를 저장
    private final List<String> currentRowData = new ArrayList<>(); // 현재 행 데이터를 저장
    private String currentCellType; // 현재 셀 데이터 유형 (e.g., "s", "n")

    @Getter
    private long rowCount = 0; // 총 행 개수를 추적하는 변수
    private boolean isHeaderRow = true; // 첫 행인지 여부
    private RowData nextRowData; // 다음 반환할 RowData

    private final String H_COMP_NO = "발행회사고객번호";
    private final String H_STANDARD_DT = "권리기준일자";
    private final String H_SHAREHOLDER_NM = "주주명";
    private final String H_REAL_NAME_NO = "실명번호(주민번호)";
    private final String H_SPECIAL_ACCOUNT_SHAREHOLDER_NO = "특별계좌주주번호";
    private final String H_ACTUAL_SHAREHOLDER_NO = "실질주주번호";
    private final String H_LF_TYPE = "내외국인구분";
    private final String H_NATION = "국가코드";
    private final String H_CORP_TYPE = "법인구분";
    private final String H_STOCK_TYPE = "주식종류";
    private final String H_STOCK_COUNT = "소유주식수";
    private final String H_ZIPCODE = "우편번호";
    private final String H_ADDRESS = "주소";
    // 헤더 데이터를 저장
    private final Map<String, Integer> header = new HashMap<>() {{
        put(H_COMP_NO, null);
        put(H_STANDARD_DT, null);
        put(H_SHAREHOLDER_NM, null);
        put(H_REAL_NAME_NO, null);
        put(H_SPECIAL_ACCOUNT_SHAREHOLDER_NO, null);
        put(H_ACTUAL_SHAREHOLDER_NO, null);
        put(H_LF_TYPE, null);
        put(H_NATION, null);
        put(H_CORP_TYPE, null);
        put(H_STOCK_TYPE, null);
        put(H_STOCK_COUNT, null);
        put(H_ZIPCODE, null);
        put(H_ADDRESS, null);
    }};

    public ExcelRowIterator(InputStream sheetInputStream, List<String> sharedStrings) throws Exception {
        this.sheetInputStream = sheetInputStream;
        this.sharedStrings = sharedStrings;

        // SAX 파서를 별도 쓰레드에서 실행
        Thread parserThread = new Thread(() -> {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                parser.parse(new InputSource(sheetInputStream), this);
                queue.put(EOF_MARKER); // EOF 마커 추가
            } catch (Exception e) {
                throw new RuntimeException("Error during parsing", e);
            }
        });
        parserThread.start();
    }

    @Override
    public boolean hasNext() {
        if (nextRowData == EOF_MARKER) {
            return false; // EOF에 도달
        }
        try {
            nextRowData = queue.take(); // 대기하며 데이터 가져오기
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting for data", e);
        }
        return nextRowData != EOF_MARKER;
    }

    @Override
    public RowData next() {
        if (nextRowData == EOF_MARKER) {
            throw new IllegalStateException("No more data available.");
        }
        return nextRowData;
    }

    /**
     * attributes
     * - si: Shared String Item, 공유문열항목
     * - c: Cell(셀 데이터), 셀 데이터가 공유 문자열 테이블을 참조하는 경우 사용됨, t="s" 속성을 가진 <c> 태그는 v 태그 값으로 공유 문자열 테이블의 인덱스를 참조함
     * - r: 셀의 위치
     * - t: 셀 데이터 유형(옵션, s=공유문자열, n=숫자, b=Boolean)
     * -
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        // 셀 데이터를 읽고 sharedStrings에서 값을 참조
        if ("c".equals(qName)) { // 셀 데이터 시작
            String cellType = attributes.getValue("t"); // 데이터 유형 (s=Shared String, n=Number 등)
            currentCellValue.setLength(0); // 문자 데이터 초기화
            currentCellType = cellType; // 현재 셀 유형 저장
        } else if ("row".equals(qName)) { // 행(row) 시작
            currentRowData.clear(); // 현재 행의 데이터를 초기화
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("c".equals(qName)) { // 셀 데이터 종료
            String value = parseCellValue(currentCellValue.toString(), currentCellType);
            currentRowData.add(value); // 행 데이터에 값 추가
            return;
        }
        if ("row".equals(qName)) { // 행(row) 종료
            if (isHeaderRow) { // 첫 행(헤더) 처리
                for (int i = 0; i < currentRowData.size(); i++) { // 헤더 저장
                    header.put(currentRowData.get(i).trim(), i);
                }
                isHeaderRow = false; // 헤더 처리를 완료했으므로 플래그 변경
                return;
            }
            // 데이터 행 처리
            if (!currentRowData.isEmpty()) { // 데이터가 존재하면 처리
                RowData rowData = new RowData(
                        false, resolveRowData(H_SHAREHOLDER_NM), resolveRowData(H_ACTUAL_SHAREHOLDER_NO)
                );
                try {
                    queue.put(rowData); // 데이터를 큐에 추가
                    rowCount++; // 행 개수 증가
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted while adding data to queue", e);
                }
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (length > 0) { // SAX 파서가 제공하는 데이터가 있는 경우
            currentCellValue.append(ch, start, length); // 데이터를 StringBuilder에 추가
        }
    }

    private String parseCellValue(String value, String type) {
        if ("s".equals(type)) { // Shared String 처리
            int idx = Integer.parseInt(value);
            return sharedStrings.get(idx);
        }
        return value; // 다른 데이터 유형 처리
    }

    private String resolveRowData(final String headerName) {
        Integer index = header.get(headerName);
        return index == null
                || index >= currentRowData.size()
                || index < 0
                ? "" : currentRowData.get(index);
    }
}
