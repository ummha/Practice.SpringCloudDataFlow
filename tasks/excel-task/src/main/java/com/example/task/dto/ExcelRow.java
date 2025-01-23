package com.example.task.dto;

import com.example.task.excel.ExcelHeader;
import com.example.task.excel.ExcelRowSchema;

public record ExcelRow(
        @ExcelHeader(KsdExcelHeaderNames.SHAREHOLDER_NM)
        String shareholderNm,
        @ExcelHeader(KsdExcelHeaderNames.REAL_NAME_NO)
        String realNameNo,
        @ExcelHeader(KsdExcelHeaderNames.SPECIAL_ACCOUNT_SHAREHOLDER_NO)
        String specialAccountShareholderNo,
        @ExcelHeader(KsdExcelHeaderNames.ACTUAL_SHAREHOLDER_NO)
        String actualShareholderNo,
        @ExcelHeader(KsdExcelHeaderNames.LF_TYPE)
        String lfType,
        @ExcelHeader(KsdExcelHeaderNames.NATION)
        String nation,
        @ExcelHeader(KsdExcelHeaderNames.CORP_TYPE)
        String corpType,
        @ExcelHeader(KsdExcelHeaderNames.STOCK_TYPE)
        String stockType,
        @ExcelHeader(KsdExcelHeaderNames.STOCK_COUNT)
        String stockCount,
        @ExcelHeader(KsdExcelHeaderNames.ZIPCODE)
        String zipcode,
        @ExcelHeader(KsdExcelHeaderNames.ADDRESS)
        String address
) implements ExcelRowSchema {
}
