package com.example.task.config;

import com.example.task.parameter.CreateJobParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExcelBatchConfig {

    private final CreateJobParameter jobParameter;

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
}
