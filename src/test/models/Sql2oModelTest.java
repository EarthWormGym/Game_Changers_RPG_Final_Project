package models;

import org.apache.log4j.BasicConfigurator;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class Sql2oModelTest {

    Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/" + "makersandmortalstest",
            null, null, new PostgresQuirks() {
        {
            // make sure we use default UUID converter.
            converters.put(UUID.class, new UUIDConverter());
        }
    });

    UUID id = UUID.fromString("49921d6e-e210-4f68-ad7a-afac266278cb");
    Player player = new Player("AdamR", 100, 10, 20, "true", 100, 0);
    Player enemy = new Player("Ork", 80, 20, 10, "true", 0, 0);
    public Game game = new Game(player, enemy);

    @BeforeAll
    static void setUpClass() {
        BasicConfigurator.configure();
        Flyway flyway = Flyway.configure().dataSource("jdbc:postgresql://localhost:5432/makersandmortalstest", null, null).load();
        flyway.migrate();

    }
    @BeforeEach
    void setUp() {
        Connection conn = sql2o.beginTransaction();
        conn.createQuery("insert into players(user_id, user_name, name, password, high_score) VALUES (:user_id, :username, :full_name, :password, :high_score)")
                .addParameter("user_id", id)
                .addParameter("username", "example username")
                .addParameter("full_name", "example full name")
                .addParameter("password", "example password")
                .addParameter("high_score", 0)
                .executeUpdate();
        conn.commit();

    }

    @AfterEach
    void tearDown() {
        Connection conn = sql2o.beginTransaction();
        conn.createQuery("TRUNCATE TABLE players")
                .executeUpdate();
        conn.commit();
    }
    @org.junit.jupiter.api.Test
    void createUser() {
        Connection conn = sql2o.open();
        Model model = new Sql2oModel(sql2o);
        boolean result = false;
        List<Players> list_of_players;
        list_of_players = (conn.createQuery("select * from players").executeAndFetch(Players.class));
        String test = "Players(user_ID=49921d6e-e210-4f68-ad7a-afac266278cb, user_name=example username, name=example full name, password=example password, high_score=0)";

        if(list_of_players.toString().contains(test)){
            result = true;
        } else {
            result = false;
        }
        assertTrue(result);
    }

    @org.junit.jupiter.api.Test
    void UsernameExist() {
        Model model = new Sql2oModel(sql2o);
        assertTrue(model.UsernameExist("example username"));
    }

    @org.junit.jupiter.api.Test
    void CorrectPassword(){
        Model model = new Sql2oModel(sql2o);
        assertTrue(model.CorrectPassword("example username","example password"));
    }

    @org.junit.jupiter.api.Test
    void CreatePlayer() {
        assertEquals("AdamR", player.username);
        assertEquals(100, player.health);
        assertEquals(10, player.damage_limit);
        assertEquals(20, player.defence);
        assertEquals("true", player.is_alive);
        assertEquals(100, player.coins);
        assertEquals(0, player.healthPotions);
    }

    @org.junit.jupiter.api.Test
    void testPlayerAndEnemyCreated() {
        List<Player> testarray = new ArrayList<Player>();
        testarray.add(player);
        testarray.add(enemy);
        Game game = new Game(player , enemy);
        assertEquals(testarray, game.playersArray);
    }

    @org.junit.jupiter.api.Test
    void attackingPlayer() {
        Game game = new Game(player , enemy);
        game.attack(enemy, player);
        assertNotEquals(100 , player.health);
    }

    @org.junit.jupiter.api.Test
    void attackingEnemy() {
        Game game = new Game(player , enemy);
        game.attack(player, enemy);
        assertNotEquals(100 , enemy.health);
    }

    @org.junit.jupiter.api.Test
    void shopHealth() {
        player.Heal();
        assertEquals(110, player.health);
    }

    @org.junit.jupiter.api.Test
    void shopDamage() {
        player.increase_damage();
        assertEquals(15, player.damage_limit);
    }

    @org.junit.jupiter.api.Test
    void shopDefence() {
        player.defence = player.defence * 100;
        player.increase_defence();
        assertEquals(25, player.defence);
    }

    @org.junit.jupiter.api.Test
    void enemyDropsCoinsOnDeath() {
        Player enemy = new Player("Ork", 0, 20, 10, "false", 0, 0);
        game.attack(player, enemy);
        assertEquals(115, player.coins);
    }

}