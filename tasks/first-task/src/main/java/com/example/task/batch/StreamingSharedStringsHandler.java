package com.example.task.batch;

import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * qName:
 * <li>si: Shared String Item(공유문열항목), sharedStrings.xml에 저장된 공유 문자열 항목을 나타냄</li>
 * <li>c: Cell(셀 데이터), 셀 데이터가 공유 문자열 테이블을 참조하는 경우 사용됨, t="s" 속성을 가진 c 태그는 v 태그 값으로 공유 문자열 테이블의 인덱스를 참조함</li>
 * <li>t: Text(텍스트), 문자열 데이터를 나타냄, 일반적으로 sharedStrings.xml 에서 si 태그 내부에 포함되어있음</li>
 * <li>v: Value(샐 값), 셀의 실제 값을 나타냄, 셀의 데이터 유형(t 속성)에 따라 해석됨</li>
 * <br>
 *
 * attributes:
 * <li>r: 셀의 위치</li>
 * <li>t: 셀 데이터 유형(옵션, s=공유문자열, n=숫자, b=Boolean)</li>
 * <li>t="s": Shared String(공유 문자열 참조), 셀 데이터가 공유 문자열 테이블을 참조하는 경우 사용됨, t="s" 속성을 가진 c 태그는 v 태그 값으로 공유 문자열 테이블의 인덱스를 참조함.</li>
 *
 * <pre>
 * # e.g.
 * < sheetData >
 *     < row r="1" >
 *         # A1 셀: 공유 문자열 테이블 0번 인덱스
 *         < c r="A1" t="s" >< v >0< /v >< /c >
 *         # B1 셀: 숫자 값 123.45
 *         < c r="B1" t="n" >< v >123.45< /v >< /c >
 *     < /row >
 *     < row r="2" >
 *         # A2 셀: 공유 문자열 테이블 1번 인덱스
 *         < c r="A2" t="s" >< v >1< /v >< /c >
 *         # B2 셀: 숫자 값 678.90
 *         < c r="B2" t="n" >< v >678.90< /v >< /c >
 *     < /row >
 * < /sheetData >
 * </pre>
 */
public class StreamingSharedStringsHandler extends DefaultHandler {
    @Getter
    private final List<String> sharedStrings = new ArrayList<>();
    private final StringBuilder currentValue = new StringBuilder();
    private boolean isString = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("si".equals(qName)) { // Shared String Item 시작
            currentValue.setLength(0); // 초기화
            isString = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("si".equals(qName)) { // Shared String Item 종료
            sharedStrings.add(currentValue.toString());
            isString = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (isString) {
            currentValue.append(ch, start, length);
        }
    }
}
