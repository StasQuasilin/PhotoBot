package services;

import app.PhotoBot;
import entity.UserAccess;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.FileReader;
import utils.Utils;

import java.io.IOException;

public class UserAccessService {

    private static final String FILE_NAME = "user_access.properties";
    private final FileReader reader = new FileReader();
    private UserAccessDao dao = new UserAccessDaoImpl();

    public boolean isValid(Long chatId) {
        final String chatPassword = dao.getChatPassword(chatId);
        boolean valid = false;
        if (chatPassword != null){
            valid = checkUserPassword(chatPassword, chatId);
        } else {
            typeAboutWrongKey(chatId);
        }
        return valid;
    }

    public boolean login(String text, Long chatId) {
        boolean login = false;
        if (checkUserPassword(text, chatId)){
            UserAccess userAccess = new UserAccess();
            userAccess.setChatId(chatId);
            userAccess.setPassword(text);
            dao.saveAccess(userAccess);
            login = true;
        }
        return login;
    }

    private boolean checkUserPassword(String pass, Long chatId) {
        String s = null;
        try {
            s = reader.readFile(FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkPassword(s, pass, chatId);

    }

    private boolean checkPassword(String s, String pass, Long chatId) {
        boolean check = false;
        if (Utils.isNotNull(s)){
            check = s.equals(pass);
            if(!check){
                typeAboutWrongKey(chatId);
            }
        }
        return check;
    }

    private void typeAboutWrongKey(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("You access key is wrong!");
        try {
            PhotoBot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
