package com.example.task.config;

import com.example.task.dto.ExcelRow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ExcelBatchConfig {

    private final JobRepository jobRepository;

    @Bean
    public Job excelProcessingJob(Step excelStep) {
        return new JobBuilder("excelProcessingJob", jobRepository)
                .start(excelStep)
                .build();
    }

    @Bean
    public Step excelStep(ItemReader<ExcelRow> excelItemReader, ItemWriter<ExcelRow> excelItemWriter, PlatformTransactionManager transactionManager) {
        return new StepBuilder("excelStep", jobRepository)
                .<ExcelRow, ExcelRow>chunk(100, transactionManager)
                .reader(excelItemReader)
                .writer(excelItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<ExcelRow> excelItemWriter() {
        return items -> items.forEach(System.out::println);
    }
}
