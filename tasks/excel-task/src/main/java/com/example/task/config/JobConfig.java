package com.example.task.config;

import com.example.task.batch.LoadShareholderDetailTasklet;
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
    private final PlatformTransactionManager transactionManager;

    public JobConfig(JobRepository jobRepository,
                     CreateJobParameter jobParameter,
                     ShareholderTypeDecider shareholderTypeDecider,
                     @Qualifier(DatabaseBeanNames.BATCH_TRANSACTION_MANAGER)
                     PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.jobParameter = jobParameter;
        this.shareholderTypeDecider = shareholderTypeDecider;
        this.transactionManager = transactionManager;
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
                                  @Qualifier("loadShareholderDetailStep") Step loadShareholderDetailStep) {
        log.info("##> ExcelProcessingJob initialized");
        return new JobBuilder("excelProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(loadShareholderDetailStep)
                .next(shareholderTypeDecider)
                .from(shareholderTypeDecider).on("KSD").to(excelStep)
                .end()
                .build();
    }

    /**
     *
     * @param loadShareholderDetailTasklet
     * @return
     */
    @Bean
    public Step loadShareholderDetailStep(LoadShareholderDetailTasklet loadShareholderDetailTasklet) {
        log.info("##> load-shareholder-detail-step initialized");
        return new StepBuilder("load-shareholder-detail-step", jobRepository)
                .tasklet(loadShareholderDetailTasklet, transactionManager)
                .build();
    }

    /**
     * 엑셀 일괄처리 Step 설정
     */
    @Bean
    public Step excelStep(@Qualifier("streamingExcelReader") ItemReader<KsdExcelRow> StreamingExcelItemReader,
                          RedisPipelineWriter redisPipelineWriter) {
        log.info("##> ExcelStep initialized");
        return new StepBuilder("excelStep", jobRepository)
                .<KsdExcelRow, KsdExcelRow>chunk(1000, transactionManager)
                .reader(StreamingExcelItemReader)
                .writer(redisPipelineWriter)
                .build();
    }

//    /**
//     * ItemWriter 설정
//     */
//    @Bean
//    public ItemWriter<KsdExcelRow> excelItemWriter() {
//        log.info("##> ExcelItemWriter initialized");
//        return items -> {
//            log.info("##> ExcelItemWriter started :: {}", jobParameter);
//            items.forEach(System.out::println);
//        };
//    }
}
