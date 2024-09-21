package com.batch.job;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class FileNames {
    private final List<String> fileNames;

    public FileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public void addFileNames(String fileName) {
        this.fileNames.add(fileName);
    }

    public void clearFileNames() {
        this.fileNames.clear();
    }

}
