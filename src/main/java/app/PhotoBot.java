package app;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import services.UserAccessService;
import utils.HttpConnector;

import java.util.List;

public class PhotoBot extends TelegramLongPollingBot {

    private final UserAccessService userAccessService = new UserAccessService();
    private static PhotoBot instance;

    public static PhotoBot getInstance() {
        return instance;
    }

    private final String token;
    private final String username;
    private final HttpConnector connector;

    public PhotoBot(String token, String username, String fileDir) {
        this.token = token;
        this.username = username;
        connector = new HttpConnector(token, fileDir);
        instance = this;
    }

    public String getBotUsername() {
        return username;
    }

    public String getBotToken() {
        return token;
    }

    public void onUpdateReceived(Update update) {
        final Message message = update.getMessage();
        final Long chatId = message.getChatId();
        if (message.isCommand()){
            handleCommand(message.getText().substring(1), chatId);
        } else {
            if (userAccessService.isValid(chatId)){
                checkPhotos(message.getPhoto(), chatId);
            }
        }
    }

    private void handleCommand(String text, Long chatId) {
        if(userAccessService.login(text, chatId)){
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Success! Now, You can send a photos!");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPhotos(List<PhotoSize> photo, long chatId) {
        if(photo != null){
            handlePhotos(photo, chatId);
        }
    }

    private void handlePhotos(List<PhotoSize> photo, long chatId) {
        String photoId = "";
        int maxSize = 0;
        for (PhotoSize photoSize : photo){
            final Integer fileSize = photoSize.getFileSize();
            if (fileSize > maxSize){
                maxSize = fileSize;
                photoId = photoSize.getFileId();
            }
        }
        connector.loadPhoto(photoId, chatId);
    }
}
