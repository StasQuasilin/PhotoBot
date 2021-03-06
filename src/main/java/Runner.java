import app.PhotoBot;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import utils.PropsReader;

import java.io.IOException;
import java.util.Properties;

public class Runner {
    private static final String BOT_PROPS = "bot.properties";
    private static final String TOKEN = "token";
    private static final String USERNAME = "username";
    private static final String FILE_DIR = "fileDir";
    private static final Logger log = Logger.getLogger(Runner.class);

    public static void main(String[] args) throws TelegramApiException, IOException {
        PropsReader propsReader = new PropsReader();
        final Properties read = propsReader.read(BOT_PROPS);
        if (read != null){
            runBot(read);
        }
    }

    private static void runBot(Properties read) throws TelegramApiException {
        final String token = read.getProperty(TOKEN);
        final String username = read.getProperty(USERNAME);
        final String fileDir = read.getProperty(FILE_DIR);
        outputDir(fileDir);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new PhotoBot(token, username, fileDir));
        log.info("Ready...");
    }

    private static void outputDir(String fileDir) {
        if (fileDir != null && !fileDir.isEmpty()){
            log.info("File directory: " + fileDir);
        }
    }
}
