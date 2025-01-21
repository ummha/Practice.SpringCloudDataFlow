package com.example.task.config;

import com.example.task.parameter.CreateJobParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Slf4j
@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final CreateJobParameter jobParameter;

    public BatchConfig(JobRepository jobRepository, CreateJobParameter jobParameter) {
        this.jobRepository = jobRepository;
        this.jobParameter = jobParameter;
        log.info("##> BatchConfig Initialized");
    }

    @Bean("jobParameter")
    @JobScope
    CreateJobParameter jobParameter(@Value("#{jobParameters['chunk-size']}") Integer chunkSize,
                                    @Value("#{jobParameters['date']}") String date,
                                    @Value("#{jobParameters['time']}") String time) {
        log.info("##> JobParameter Initialized - chunkSize: {}, date: {}, time: {}", chunkSize, date, time);
        return new CreateJobParameter(chunkSize, date, time);
    }

    @Bean
    public Job helloJob(@Qualifier("helloStep") Step helloStep,
                        @Qualifier("nestStep") Step nextStep,
                        @Qualifier("finalStep") Step finalStep) {
        return new JobBuilder("helloJob", jobRepository)
                .start(helloStep)
                .next(nextStep)
                .next(finalStep)
                .build();
    }

    @Bean
    @Qualifier("helloStep")
    public Step helloStep(@Qualifier("helloTasklet") Tasklet helloTasklet,
                          PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep", jobRepository)
                .tasklet(helloTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("nestStep")
    public Step nextStep(@Qualifier("nextTasklet") Tasklet nextTasklet,
                         PlatformTransactionManager transactionManager) {
        return new StepBuilder("nextStep", jobRepository)
                .tasklet(nextTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("finalStep")
    public Step finalStep(@Qualifier("finalTasklet") Tasklet finalTasklet,
                          PlatformTransactionManager transactionManager) {
        return new StepBuilder("finalStep", jobRepository)
                .tasklet(finalTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("helloTasklet")
    public Tasklet helloTasklet() {
        return (contribution, chunkContext) -> {
            log.info("##> This is HelloTasklet!");
            contribution.getStepExecution().getExecutionContext().put("helloKey", "helloTasklet");
            contribution.getStepExecution().getJobExecution().getExecutionContext().put("publicData1", "publicData1");
            log.info("##> Let's find ExecutionContext in HelloTasklet! - key: helloKey, value: {}", contribution.getStepExecution().getExecutionContext().get("helloKey"));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @Qualifier("nextTasklet")
    public Tasklet nextTasklet() {
        return (contribution, chunkContext) -> {
            log.info("##> This is NextTasklet!");
            contribution.getStepExecution().getExecutionContext().put("nextKey", "nextTasklet");
            contribution.getStepExecution().getJobExecution().getExecutionContext().put("publicData2", "publicData2");
            log.info("##> Let's find ExecutionContext in NextTasklet! - key: helloKey, value: {}", contribution.getStepExecution().getExecutionContext().get("helloKey"));
            log.info("##> Let's find ExecutionContext in NextTasklet! - key: nextKey, value: {}", contribution.getStepExecution().getExecutionContext().get("nextKey"));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @Qualifier("finalTasklet")
    public Tasklet finalTasklet() {
        return (contribution, chunkContext) -> {
            log.info("##> This is FinalTasklet!");
            log.info("##> ExecutionContext in FinalTasklet");
            for(Map.Entry<String, Object> entry : contribution.getStepExecution().getExecutionContext().entrySet()) {
                log.info("##> key: {}, value: {}", entry.getKey(), entry.getValue());
            }

            log.info("##> ExecutionContext in Job");
            for(Map.Entry<String, Object> entry : contribution.getStepExecution().getJobExecution().getExecutionContext().entrySet()) {
                log.info("##> key: {}, value: {}", entry.getKey(), entry.getValue());
            }
            log.info("##> Let's find StepScope in FinalTasklet : {}", jobParameter);
            return RepeatStatus.FINISHED;
        };
    }
}
