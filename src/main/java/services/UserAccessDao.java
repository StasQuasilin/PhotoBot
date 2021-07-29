package services;

import entity.UserAccess;

public interface UserAccessDao {

    String getChatPassword(Long chatId);

    void saveAccess(UserAccess userAccess);
}
