package com.batch.heroe.batch.config;

import com.batch.heroe.batch.step.listener.FileListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Slf4j
@Configuration
public class JobConfig {


    @Bean
    public JobExecutionListener listener() {
        return new FileListener();
    }

    @Bean
    public Job job(JobRepository jobRepository,
                   @Qualifier("heroPartitionStep") Step step,
                   JobExecutionListener listener) {
        return new JobBuilder("fileJob".concat(String.valueOf(Instant.now().getNano())), jobRepository)
                .start(step)
                .listener(listener)
                .build();
    }
}
