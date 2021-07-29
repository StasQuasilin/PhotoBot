package services;

import entity.UserAccess;
import sql.Hibernator;

public class UserAccessDaoImpl implements UserAccessDao {

    private static final String CHAT_ID = "chatId";
    private final Hibernator hibernator = Hibernator.getInstance();

    @Override
    public String getChatPassword(Long chatId) {
        String accessKey = null;
        final UserAccess access = hibernator.get(UserAccess.class, CHAT_ID, chatId);
        if (access != null){
            accessKey = access.getPassword();
        }
        return accessKey;
    }

    @Override
    public void saveAccess(UserAccess userAccess) {
        hibernator.save(userAccess);
    }
}
