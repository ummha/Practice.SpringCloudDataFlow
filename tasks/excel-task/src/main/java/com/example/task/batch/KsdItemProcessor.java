package com.example.task.batch;

import com.example.task.dto.KsdExcelRow;
import com.example.task.dto.KsdItem;
import org.springframework.batch.item.ItemProcessor;

public class KsdItemProcessor implements ItemProcessor<KsdExcelRow, KsdItem> {

    @Override
    public KsdItem process(KsdExcelRow item) throws Exception {
        return null;
    }
}
