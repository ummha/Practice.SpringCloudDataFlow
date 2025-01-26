package com.example.task.batch;

import com.example.task.dto.KsdExcelRow;
import com.example.task.dto.KsdExcelHeaderNames;
import com.example.task.excel.AbstractExcelRowIterator;
import com.example.task.util.ExcelUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class KsdExcelRowIterator extends AbstractExcelRowIterator<KsdExcelRow> {

    public KsdExcelRowIterator(Path filePath) throws Exception {
        super(filePath, KsdExcelRow.class);
    }

    @Override
    protected KsdExcelRow mapper(Map<String, Integer> headerIndex, List<String> rowData) {
        return new KsdExcelRow(
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
    protected KsdExcelRow getEOFMarker() {
        return new KsdExcelRow(null,
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
