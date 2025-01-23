package com.example.task.config;

import com.example.task.batch.LoadShareholderDetailStep;
import com.example.task.batch.RedisPipelineWriter;
import com.example.task.batch.ShareholderTypeDecider;
import com.example.task.dto.KsdExcelRow;
import com.example.task.parameter.CreateJobParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.builder.TaskletStepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JobConfig {

    private final JobRepository jobRepository;
    private final CreateJobParameter jobParameter;
    private final ShareholderTypeDecider shareholderTypeDecider;

    public JobConfig(JobRepository jobRepository, CreateJobParameter jobParameter, ShareholderTypeDecider shareholderTypeDecider) {
        this.jobRepository = jobRepository;
        this.jobParameter = jobParameter;
        this.shareholderTypeDecider = shareholderTypeDecider;
        log.info("##> JobConfig initialized");
    }

    @JobScope
    @Bean("jobParameter")
    public CreateJobParameter createJobParameter(@Value("#{jobParameters['chunk-size']}") Integer chunkSize,
                                                 @Value("#{jobParameters['date']}") String date,
                                                 @Value("#{jobParameters['time']}") String time) {
        log.info("##> JobParameter Initialized - chunkSize: {}, date: {}, time: {}", chunkSize, date, time);
        return new CreateJobParameter(chunkSize, date, time);
    }

    @Bean
    public Job excelProcessingJob(@Qualifier("excelStep") Step excelStep,
                                  @Qualifier("shareholderDetailStep") Step loadShareholderDetailStep) {
        log.info("##> ExcelProcessingJob initialized");
        return new JobBuilder("excelProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(loadShareholderDetailStep)
                .next(shareholderTypeDecider)
                .from(shareholderTypeDecider).on("KSD").to(excelStep)
                .end()
                .build();
    }

    @Bean
    public Step shareholderDetailStep(LoadShareholderDetailStep loadShareholderDetailStep,
                                          @Qualifier(DatabaseBeanNames.BATCH_TRANSACTION_MANAGER) PlatformTransactionManager transactionManager) {
        return new StepBuilder("loadShareholderDetailStep", jobRepository)
                .tasklet(loadShareholderDetailStep, transactionManager)
                .build();
    }

    @Bean
    public Step excelStep(@Qualifier("streamingExcelReader") ItemReader<KsdExcelRow> StreamingExcelItemReader,
//                          @Qualifier("excelItemWriter") ItemWriter<ExcelRow> excelItemWriter,
                          RedisPipelineWriter redisPipelineWriter,
                          @Qualifier(DatabaseBeanNames.BATCH_TRANSACTION_MANAGER) PlatformTransactionManager transactionManager) {
        log.info("##> ExcelStep initialized");
        return new StepBuilder("excelStep", jobRepository)
                .<KsdExcelRow, KsdExcelRow>chunk(1000, transactionManager)
                .reader(StreamingExcelItemReader)
                .writer(redisPipelineWriter)
                .build();
    }

    @Bean
    public ItemWriter<KsdExcelRow> excelItemWriter() {
        log.info("##> ExcelItemWriter initialized");
        return items -> {
            log.info("##> ExcelItemWriter started :: {}", jobParameter);
            items.forEach(System.out::println);
        };
    }
}
