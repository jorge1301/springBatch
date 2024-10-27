package com.batch.heroe.batch.step.listener;

import com.batch.heroe.adapters.persistence.JpaHeroRepository;
import com.batch.heroe.core.execution.ExecutionFileNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
public class FileListener implements JobExecutionListener {
    @Autowired
    private JpaHeroRepository heroRepository;

    @Autowired
    private ExecutionFileNames executionFileNames;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {
            this.heroRepository.deleteAll();
            this.executionFileNames.clearFileNames();
            this.listFilesInResources();

        } catch (Exception e) {
            log.error("Error to get executionFileNames", e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            this.executionFileNames.clearFileNames();
            System.out.println("Job completed successfully.");
        }
    }


    private void listFilesInResources() throws IOException {
        Path directory = Paths.get("input");
        if (Files.isDirectory(directory)) {
            try (Stream<Path> paths = Files.list(directory)) {
                paths.filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .forEach(x -> this.executionFileNames.addFileNames(x));
            }
        }
    }
}
