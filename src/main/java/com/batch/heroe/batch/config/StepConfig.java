package com.batch.heroe.batch.config;

import com.batch.heroe.adapters.api.dto.Heroes;
import com.batch.heroe.batch.step.processor.HeroProcessor;
import com.batch.heroe.batch.step.reader.HeroReader;
import com.batch.heroe.batch.step.writter.HeroWriter;
import com.batch.heroe.core.domain.Hero;
import jakarta.transaction.Transactional;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StepConfig {
    @Bean
    @Qualifier("heroPartitionStep")
    public Step heroPartitionStep(JobRepository jobRepository,
                                  @Qualifier("heroPartitioner") Partitioner partitioner,
                                  @Qualifier("heroStep") Step step,
                                  TaskExecutor taskExecutor) {
        return new StepBuilder("heroPartitionStep", jobRepository)
                .partitioner("heroStep", partitioner)
                .gridSize(10)
                .step(step)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    @Qualifier("heroPartitioner")
    public Partitioner partitioner() {
        return new FilePartition();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    @Qualifier("heroStep")
    public Step heroStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         @Qualifier("heroReader") FlatFileItemReader<Heroes> reader,
                         @Qualifier("heroProcessor") ItemProcessor<Heroes, Hero> processor,
                         @Qualifier("heroWriter") ItemWriter<Hero> writer) {
        return new StepBuilder("heroStep", jobRepository)
                .<Heroes, Hero>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @Qualifier("heroReader")
    @StepScope
    public FlatFileItemReader<Heroes> heroReader(
            @Value("#{stepExecutionContext['fileName']}") String fileName,
            @Value("#{stepExecutionContext['start']}") int start,
            @Value("#{stepExecutionContext['end']}") int end) {
        return new HeroReader().flatFileItemReader(fileName, start, end);
    }

    @Bean
    @Qualifier("heroProcessor")
    public HeroProcessor heroProcessor() {
        return new HeroProcessor();
    }

    @Bean
    @Qualifier("heroWriter")
    @Transactional
    public HeroWriter writer() {
        return new HeroWriter();
    }
}
