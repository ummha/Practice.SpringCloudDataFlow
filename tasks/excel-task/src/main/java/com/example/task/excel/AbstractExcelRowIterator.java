package com.example.task.excel;

import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractExcelRowIterator<T extends ExcelRowSchema> extends DefaultHandler implements Iterator<T> {
    private final T EOF_MARKER; // EOF 기준

    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>(1000);

    private final InputStream sheetInputStream;
    private final List<String> sharedStrings;

    private final StringBuilder currentCellValue = new StringBuilder(); // 현재 셀 데이터를 저장
    private final List<String> currentRowData = new ArrayList<>(); // 현재 행 데이터를 저장
    private String currentCellType; // 현재 셀 데이터 유형 (e.g., "s", "n")
    private final Class<T> headerDefinitionClazz; // 헤더 정보

    @Getter
    private long rowCount = 0; // 총 행 개수를 추적하는 변수
    private Map<String, Integer> headerIndex;
    private boolean isHeaderRow = true; // 첫 행인지 여부
    private T nextRowData; // 다음 반환할 RowData

    public AbstractExcelRowIterator(InputStream sheetInputStream, List<String> sharedStrings, Class<T> headerDefinitionClazz) throws Exception {
        this.sheetInputStream = sheetInputStream;
        this.sharedStrings = sharedStrings;
        this.headerDefinitionClazz = headerDefinitionClazz;
        this.EOF_MARKER = getEOFMarker();

        // SAX 파서를 별도 쓰레드에서 실행
        Thread parserThread = new Thread(() -> {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                parser.parse(new InputSource(sheetInputStream), this);
                queue.put(this.EOF_MARKER); // EOF 마커 추가
            } catch (Exception e) {
                throw new RuntimeException("Error during parsing", e);
            }
        });
        parserThread.start();
    }

    /**
     * EOF 객체 반환
     * - 반환된 인스턴스로 End Of File 기준을 설정한다.
     * @return EOF Marker
     */
    protected abstract T getEOFMarker();

    /**
     * 각 행 별로 추출할 데이터로 매핑 처리
     * @param headerIndex 헤더 인덱스 정보
     * @param rowData 행 데이터
     * @return 추출할 데이터
     */
    protected abstract T mapper(Map<String, Integer> headerIndex, List<String> rowData);

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
    public T next() {
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
            /* 첫 행 처리 (헤더) */
            if (isHeaderRow) {
                Map<String, Integer> tempHeader = resolveHeaderIndex();
                headerIndex = Collections.unmodifiableMap(tempHeader);
                isHeaderRow = false; // 헤더 처리를 완료했으므로 플래그 변경
                return;
            }
            /* 데이터 행 처리 */
            if (currentRowData.isEmpty()) { // 데이터가 없으면 처리 안함
                return;
            }

            if (headerIndex == null) {
                throw new RuntimeException("Excel Header not found.");
            }

            try {
                queue.put(mapper(headerIndex, currentRowData)); // 데이터를 큐에 추가
                rowCount++; // 행 개수 증가
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while adding data to queue", e);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (length > 0) { // SAX 파서가 제공하는 데이터가 있는 경우
            currentCellValue.append(ch, start, length); // 데이터를 StringBuilder에 추가
        }
    }

    private Map<String, Integer> resolveHeaderIndex() {
        Map<String, Integer> tempHeader = new HashMap<>();
        for (Field field : headerDefinitionClazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelHeader.class)) {
                String headerName = field.getAnnotation(ExcelHeader.class).value();
                tempHeader.put(headerName, -1);
            }
        }
        for (int i = 0; i < currentRowData.size(); i++) { // 헤더 저장
            if (tempHeader.get(currentRowData.get(i).trim()) != null) {
                tempHeader.put(currentRowData.get(i).trim(), i);
            }
        }
        return tempHeader;
    }

    private String parseCellValue(String value, String type) {
        if ("s".equals(type)) { // Shared String 처리
            int idx = Integer.parseInt(value);
            return sharedStrings.get(idx);
        }
        return value; // 다른 데이터 유형 처리
    }
}
