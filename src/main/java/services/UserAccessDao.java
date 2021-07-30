package services;

import entity.UserAccess;

public interface UserAccessDao {

    UserAccess getUserAccess(long chatId);

    String getChatPassword(Long chatId);

    void saveAccess(UserAccess userAccess);
}
