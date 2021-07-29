package utils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FileReader {

    private final Logger log = Logger.getLogger(FileReader.class);

    public String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        String fileContent = null;
        if (file.exists()){
            fileContent = readFileContent(file);
        } else {
            createFile(file);
        }
        return fileContent;
    }

    private void createFile(File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("/" + UUID.randomUUID().toString().substring(0, 13));
        log.info("You can change access key in " + file.getAbsolutePath());
        writer.close();
    }

    private String readFileContent(File file) throws IOException {
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
}
