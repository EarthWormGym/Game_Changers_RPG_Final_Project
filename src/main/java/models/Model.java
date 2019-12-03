package models;


import java.sql.Date;
import java.util.List;
import java.util.UUID;

public interface Model {
    boolean UsernameExist(String username);
    boolean CorrectPassword(String username, String password);
    void createPlayer(String user_id, String username, String full_name, String password, int high_score);
    List newEnemy(Integer counter);
    void killedEnemy(String enemy_name);
    void revivingEnemies();
    void updateHighscore(int playersHighscore, String playerID);
    int checkHighscore(String playerID);
    String getUserID(String user_name);
    boolean is_username_used(String username);
}


