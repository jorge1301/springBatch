package com.batch.job.partition;

import com.batch.job.FileNames;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilePartition implements Partitioner {

    @Autowired
    FileNames fileNames;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>();
        for (String fileName : this.fileNames.getFileNames()) {
            int totalLines = countRecords(fileName);
            int partitionSize = totalLines / gridSize;
            int start = 1;

            for (int i = 0; i < gridSize; i++) {
                ExecutionContext context = new ExecutionContext();
                int end = (i == gridSize - 1) ? totalLines : start + partitionSize - 1;
                context.putInt("start", start);
                context.putInt("end", end);
                context.putString("fileName", fileName);

                result.put(fileName.split("\\.")[0].concat("partition").concat(String.valueOf(i)), context);
                start = end + 1;
            }
        }
        return result;
    }

    private int countRecords(String fileName) {
        try {
            FileReader fileReader = new FileReader("input/".concat(fileName));
            CSVParser parser = new CSVParser(fileReader, CSVFormat.DEFAULT);
            int count = 0;
            for (CSVRecord record : parser) {
                count++;
            }
            return count;
        } catch (Exception e) {
            log.error("Error to get fileName", e);
            return 0;
        }
    }
}
