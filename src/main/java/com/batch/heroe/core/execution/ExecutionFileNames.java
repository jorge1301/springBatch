package com.batch.heroe.core.execution;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class ExecutionFileNames {
    private final List<String> fileNames;

    public ExecutionFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public void addFileNames(String fileName) {
        this.fileNames.add(fileName);
    }

    public void clearFileNames() {
        this.fileNames.clear();
    }

}
