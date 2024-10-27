package com.batch.heroe.batch.step.reader;

import com.batch.heroe.adapters.api.dto.Heroes;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.FileSystemResource;

public class HeroReader {
    public FlatFileItemReader<Heroes> flatFileItemReader(
            String fileName,
            int start,
            int end) {
        return new FlatFileItemReaderBuilder<Heroes>()
                .name("fileReader")
                .resource(new FileSystemResource("input/" + fileName))
                .linesToSkip(start - 1)
                .lineMapper((line, lineNumber) -> {
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
                })
                .build();
    }
}
