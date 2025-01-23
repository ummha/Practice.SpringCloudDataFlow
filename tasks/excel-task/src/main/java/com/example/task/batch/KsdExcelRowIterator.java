package com.example.task.batch;

import com.example.task.dto.ExcelRow;
import com.example.task.dto.KsdExcelHeaderNames;
import com.example.task.excel.AbstractExcelRowIterator;
import com.example.task.util.ExcelUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class KsdExcelRowIterator extends AbstractExcelRowIterator<ExcelRow> {

    public KsdExcelRowIterator(InputStream sheetInputStream, List<String> sharedStrings) throws Exception {
        super(sheetInputStream, sharedStrings, ExcelRow.class);
    }

    @Override
    protected ExcelRow mapper(Map<String, Integer> headerIndex, List<String> rowData) {
        return new ExcelRow(
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.SHAREHOLDER_NM),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.REAL_NAME_NO),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.SPECIAL_ACCOUNT_SHAREHOLDER_NO),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.ACTUAL_SHAREHOLDER_NO),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.LF_TYPE),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.NATION),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.CORP_TYPE),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.STOCK_TYPE),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.STOCK_COUNT),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.ZIPCODE),
                ExcelUtils.safeResolveValue(headerIndex, rowData, KsdExcelHeaderNames.ADDRESS)
        );
    }

    @Override
    protected ExcelRow getEOFMarker() {
        return new ExcelRow(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
