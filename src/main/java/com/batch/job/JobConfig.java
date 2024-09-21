package com.batch.job;

import com.batch.dto.Heroes;
import com.batch.job.partition.FilePartition;
import com.batch.model.Hero;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;

@Slf4j
@Configuration
public class JobConfig {

    @Bean
    @StepScope
    @Qualifier("fileReader")
    public FlatFileItemReader<Heroes> flatFileItemReader(
            @Value("#{stepExecutionContext['fileName']}") String fileName,
            @Value("#{stepExecutionContext['start']}") int start,
            @Value("#{stepExecutionContext['end']}") int end) {
        return new FlatFileItemReaderBuilder<Heroes>()
                .name("fileReader")
                .resource(new FileSystemResource("input/" + fileName))
                .linesToSkip(start - 1)
                .lineMapper(new LineMapper<Heroes>() {
                    @Override
                    public Heroes mapLine(String line, int lineNumber) throws Exception {
                        if (lineNumber >= start && lineNumber <= end) {
                            String[] fields = line.split(",");
                            return new Heroes(
                                    Long.parseLong(fields[0].trim()),
                                    fields[1].trim(),
                                    fields[2].trim(),
                                    fields[3].trim(),
                                    Integer.parseInt(fields[4].trim()),
                                    fields[5].trim(),
                                    fields[6].trim()
                            );
                        } else {
                            return null;
                        }
                    }
                })
                .build();
    }

    @Bean
    @Qualifier("heroProcessor")
    public HeroProcessor heroProcessor() {
        return new HeroProcessor();
    }

    @Bean
    @Qualifier("heroWriter")
    @Transactional
    public JpaItemWriter<Hero> writer(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Hero> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public JobExecutionListener listener() {
        return new FileListener();
    }

    @Bean
    @Qualifier("filePartitioner")
    public Partitioner partitioner() {
        return new FilePartition();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    @Qualifier("fileStep")
    public Step fileStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         @Qualifier("fileReader") FlatFileItemReader<Heroes> reader,
                         @Qualifier("heroProcessor") ItemProcessor<Heroes, Hero> heroProcessor,
                         @Qualifier("heroWriter") JpaItemWriter<Hero> writer) {
        return new StepBuilder("fileStep", jobRepository)
                .<Heroes, Hero>chunk(100, transactionManager)
                .reader(reader)
                .processor(heroProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    @Qualifier("filePartitionStep")
    public Step filePartitionStep(JobRepository jobRepository,
                                  @Qualifier("filePartitioner") Partitioner partitioner,
                                  @Qualifier("fileStep") Step fileStep,
                                  TaskExecutor taskExecutor) {
        return new StepBuilder("filePartitionStep", jobRepository)
                .partitioner("fileStep", partitioner)
                .gridSize(10)
                .step(fileStep)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository,
                   @Qualifier("filePartitionStep") Step filePartitionStep,
                   JobExecutionListener listener) {
        return new JobBuilder("fileJob".concat(String.valueOf(Instant.now().getNano())), jobRepository)
                .start(filePartitionStep)
                .listener(listener)
                .build();
    }
}
