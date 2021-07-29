package utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class PropsReader {

    private final Logger logger = Logger.getLogger(PropsReader.class);

    private static final String EMPTY = "";

    public Properties read(String fileName) throws IOException {
        Properties properties = new Properties();
        File file = new File(fileName);
        if (file.exists()){
            readFile(file, properties);
        } else {
            createFile(file, properties);
        }

        return properties;
    }

    private void createFile(File file, Properties properties) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        properties.store(fos, EMPTY);
        logger.info("File " + file.getAbsolutePath() + " create successful. You can fill it");
        fos.close();
        throw new FileNotFoundException();
    }

    private void readFile(File file, Properties properties) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        properties.load(fis);
        fis.close();
    }
}
