package models;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

public class Sql2oModel implements Model {

    private Sql2o sql2o;

    public Sql2oModel(Sql2o sql2o) {
        this.sql2o = sql2o;

    }


    @Override
    public boolean UsernameExist(String username) {
        boolean does_username_exists = false;
        try (Connection conn = sql2o.open()) {
            List<Players> user1 = conn.createQuery("select user_name from players")
                    .executeAndFetch(Players.class);
            String user = user1.toString();
            if (user.contains(username)) {
                does_username_exists = true;
            }
        }
        return does_username_exists;
    }

    @Override
    public boolean CorrectPassword(String username, String password) {
        boolean correct_password = false;

        try (Connection conn = sql2o.open()) {
            List<Players> player = conn.createQuery("select password from players where user_name=:user_name")
                    .addParameter("user_name", username)
                    .executeAndFetch(Players.class);
            password =  "[Players(user_ID=null, user_name=null, name=null, password=" + password + ", high_score=0)]";
            if (player.toString().equals(password)) {
                correct_password = true;
            }
        }
        return correct_password;
    }

    @Override
    public void createPlayer(String user_id, String username, String full_name, String password, int high_score) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery("insert into players(user_id, user_name, name, password, high_score) VALUES (:user_id, :user_name, :name, :password, :high_score)")
                    .addParameter("user_id", user_id)
                    .addParameter("user_name", username)
                    .addParameter("name", full_name)
                    .addParameter("password", password)
                    .addParameter("high_score", high_score)
                    .executeUpdate();
            conn.commit();
        }
    }

    @Override
    public List<Enemy> newEnemy(Integer counter) {
        try (Connection conn = sql2o.open()) {
            if (counter <= 3) {
                List<Enemy> enemies = conn.createQuery("select enemy_name, health, damage_limit, defence, gif, coin_drop from enemies where difficulty = 'easy' AND already_killed = 'false' ")
                        .executeAndFetch(Enemy.class);
                return enemies;
            } else if (counter > 3 && counter <= 6) {
                List<Enemy> enemies = conn.createQuery("select enemy_name, health, damage_limit, defence, gif, coin_drop from enemies where difficulty = 'medium' AND already_killed = 'false' ")
                        .executeAndFetch(Enemy.class);
                return enemies;
            } else if (counter > 6 && counter <= 9) {
                List<Enemy> enemies = conn.createQuery("select enemy_name, health, damage_limit, defence, gif, coin_drop from enemies where difficulty = 'hard' AND already_killed = 'false' ")
                        .executeAndFetch(Enemy.class);
                return enemies;
            } else if (counter > 9) {
                List<Enemy> enemies = conn.createQuery("select enemy_name, health, damage_limit, defence, gif, coin_drop from enemies where difficulty = 'boss' AND already_killed = 'false' ")
                        .executeAndFetch(Enemy.class);
                return enemies;
            }
        }
        return null;
    }

    @Override
    public void killedEnemy(String enemy_name) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("UPDATE enemies SET already_killed = 'true' WHERE enemy_name = :enemy_name")
                    .addParameter("enemy_name", enemy_name)
                    .executeUpdate();
        }
    }

    @Override
    public void revivingEnemies() {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("UPDATE enemies SET already_killed = 'false'")
                    .executeUpdate();
        }
    }

    @Override
    public void updateHighscore(int playersHighscore, String playerID) {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("UPDATE players SET high_score = :playersHighscore WHERE user_id = :playerID")
                    .addParameter("playersHighscore", playersHighscore)
                    .addParameter("playerID", playerID)
                    .executeUpdate();
        }
    }

    @Override
    public int checkHighscore(String playerID) {
        try (Connection conn = sql2o.open()) {
            List<Integer> score = conn.createQuery("select high_score from players where user_id = :playerID")
                    .addParameter("playerID", playerID)
                    .executeAndFetch(Integer.class);
            return score.get(0);
        }
    }

    @Override
    public String getUserID(String user_name) {
        try (Connection conn = sql2o.open()) {
            List<String> userID = conn.createQuery("select user_id from players where user_name = :user_name")
                    .addParameter("user_name", user_name)
                    .executeAndFetch(String.class);
            return userID.get(0);
        }
    }

    @Override
    public boolean is_username_used(String username) {
        boolean check = false;
        try (Connection conn = sql2o.open()) {
            List<Players> players = conn.createQuery("select user_name from players ")
                    .executeAndFetch(Players.class);
            if(players.toString().contains(username)){
                check = true;
            }
        }
        return check;
    }

    @Override
    public List<Players> get_high_score(){
        try (Connection conn = sql2o.open()) {
            List<Players> player_high_score = conn.createQuery("select user_name, high_score from players order by high_score DESC")
                    .executeAndFetch(Players.class);
            return player_high_score;
        }
    }
}