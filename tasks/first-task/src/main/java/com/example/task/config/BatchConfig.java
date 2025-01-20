package com.example.task.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;

    @Bean
    public Job helloJob(Step helloStep) {
        return new JobBuilder("hello", jobRepository)
                .start(helloStep)
                .build();
    }

    @Bean
    public Step helloStep(JobRepository jobRepository, Tasklet helloTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep", jobRepository)
                .tasklet(helloTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet helloTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Hello! This is Spring Batch Practice!!");
            return RepeatStatus.FINISHED;
        };
    }
}
