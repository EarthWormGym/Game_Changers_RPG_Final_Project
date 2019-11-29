package models;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                List<Enemy> enemies = conn.createQuery("select enemy_name, health, damage_limit, defence, gif from enemies where difficulty = 'easy'")
                        .executeAndFetch(Enemy.class);
                return enemies;
            } else if (counter > 3 && counter <= 6) {
                List<Enemy> enemies = conn.createQuery("select enemy_name, health, damage_limit, defence, gif from enemies where difficulty = 'medium'")
                        .executeAndFetch(Enemy.class);
                return enemies;
            } else if (counter > 6 && counter <= 9) {
                List<Enemy> enemies = conn.createQuery("select enemy_name, health, damage_limit, defence, gif from enemies where difficulty = 'hard'")
                        .executeAndFetch(Enemy.class);
                return enemies;
            } else if (counter > 9) {
                List<Enemy> enemies = conn.createQuery("select enemy_name, health, damage_limit, defence, gif from enemies where difficulty = 'boss'")
                        .executeAndFetch(Enemy.class);
                return enemies;
            }
        }
        return null;
    }
}