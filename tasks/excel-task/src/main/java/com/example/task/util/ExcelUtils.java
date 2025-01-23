package com.example.task.util;

import java.util.List;
import java.util.Map;

public class ExcelUtils {

    /**
     * 헤더 인덱스가 아래 조건인 경우 공백을 반환하고 정상인 경우 데이터를 추출함
     * - null 인 경우
     * - 인덱스가 데이터 추출 범위를 벗어난 경우
     *
     * @param headerName 헤더명
     * @param headerIndex 헤더 인덱스
     * @param rowData 행 데이터
     * @return 셀 값
     */
    public static String safeResolveValue(Map<String, Integer> headerIndex, List<String> rowData, String headerName) {
        Integer index = headerIndex.get(headerName);
        return index != null
               && index >= 0
               && index < rowData.size()
                ? rowData.get(index) : "";
    }
}
