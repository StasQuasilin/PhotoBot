package utils;

import app.PhotoBot;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class HttpFileSaver {
    private static final String RESULT = "result";
    private static final String FILE_PATH = "file_path";
    private static final String FILE_UNIQUE_ID = "file_unique_id";
    private static final char DOT = '.';
    private final String token;
    private final String fileDir;
    private final Logger log = Logger.getLogger(HttpFileSaver.class);

    public HttpFileSaver(String token, String dir) {
        this.token = token;
        if (dir == null){
            dir = "";
        }
        this.fileDir = dir;
    }

    public void loadPhoto(String photoId, long chatId) {
        String fileLocation = "https://api.telegram.org/bot%s/getFile?file_id=%s";
        getFilePath(String.format(fileLocation, token, photoId), chatId);
    }

    private void getFilePath(String s, long chatId) {
        try {
            URL url = new URL(s);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder builder = new StringBuilder();
            readBuffer(reader, builder);
            reader.close();
            con.disconnect();
            parseResult(builder.toString(), chatId);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    JSONParser parser = new JSONParser();
    private void parseResult(String string, long chatId) throws ParseException, IOException {
        final JSONObject parse = (JSONObject) parser.parse(string);
        final JSONObject result = (JSONObject) parse.get(RESULT);
        final String path = String.valueOf(result.get(FILE_PATH));
        final String extension = path.substring(path.indexOf(DOT));
        final String s = LocalDate.now().toString();
        loadFile(path, this.fileDir + s + "/" + result.get(FILE_UNIQUE_ID) + extension, chatId);
    }

    private void loadFile(String path, String fileName, long chatId) throws IOException {
        String filePath = "https://api.telegram.org/file/bot%s/%s";
        URL url = new URL(String.format(filePath, token, path));
        checkPath(fileName);
        Files.copy(url.openStream(), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
        typeAboutLoad(fileName, chatId);
        log.info("File '" + fileName + "' write successful");
    }

    private void checkPath(String fileName) {
        final File file = new File(fileName);
        if (file.mkdirs()){
            log.info("Path " + file.getParentFile().getAbsolutePath() + " create");
        }
//        file.getParentFile().mkdirs();
    }

    private void typeAboutLoad(String fileName, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("File '" + fileName + "' write successful!");
        try {
            PhotoBot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void readBuffer(BufferedReader reader, StringBuilder builder) throws IOException {
        String line;
        while ((line = reader.readLine()) != null){
            builder.append(line);
        }
    }
}
