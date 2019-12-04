package models;

import org.apache.log4j.BasicConfigurator;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
    AtomicReference<Player> player = new AtomicReference<>(new Player("AdamR", 100, 10, 20, "true", 100, 0, 1, "", 0));
    AtomicReference<Player> enemy = new AtomicReference<>(new Player("Ork", 80, 20, 10, "true", 0, 0, 0, "", 15));
    AtomicReference<Game> game = new AtomicReference<>(new Game(player, enemy));
    Model model = new Sql2oModel(sql2o);


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
        assertTrue(model.UsernameExist("example username"));
    }

    @org.junit.jupiter.api.Test
    void CorrectPassword(){
        assertTrue(model.CorrectPassword("example username","example password"));
    }

    @org.junit.jupiter.api.Test
    void CreatePlayerClass() {
        assertEquals("AdamR", player.get().username);
        assertEquals(100, player.get().health);
        assertEquals(10, player.get().damage_limit);
        assertEquals(0.2, player.get().defence);
        assertEquals("true", player.get().is_alive);
        assertEquals(100, player.get().coins);
        assertEquals(0, player.get().healthPotions);
        assertEquals(1,player.get().poisonPotions);
        assertEquals("", player.get().gif);
        assertEquals(0, player.get().battles_won);
    }

    @org.junit.jupiter.api.Test
    void CreatePlayerDatabase(){
        Players player1 = new Players("49921d6e-e210-4f68-ad7a-afac266278cb", player.get().username, "example full name", "example password", 0);
        model.createPlayer("49921d6e-e210-4f68-ad7a-afac266278cb", player.get().username, "example full name", "example password", 0);
        List<Players> player;
        Connection conn = sql2o.open();
        player = (conn.createQuery("select * from players").executeAndFetch(Players.class));
        String test = "Players(user_ID=49921d6e-e210-4f68-ad7a-afac266278cb, user_name=example username, name=example full name, password=example password, high_score=0)";
        boolean result = false;
        if(player.toString().contains(test)){
            result = true;
        } else {
            result = false;
        }
        assertTrue(result);
    }

    @org.junit.jupiter.api.Test
    void testPlayerAndEnemyCreated() {
        List<AtomicReference<Player>> testarray = new ArrayList<>();
        testarray.add(player);
        testarray.add(enemy);
        Game game = new Game(player , enemy);
        assertEquals(testarray, game.playersArray);
    }

    @org.junit.jupiter.api.Test
    void attackingPlayer() {
        AtomicReference<Player> player = new AtomicReference<>(new Player("AdamR", 100, 10, 0, "true", 100, 0, 1, "", 15));
        Game game = new Game(player , enemy);
        game.attack(enemy, player);
        assertNotEquals(100 , player.get().health);
    }

    @org.junit.jupiter.api.Test
    void attackingEnemy() {
        AtomicReference<Player> enemy = new AtomicReference<>(new Player("Ork", 80, 20, 0, "true", 0, 0, 0, "", 15));
        Game game = new Game(player , enemy);
        game.enemy_attack(player, enemy);
        assertNotEquals(100 , enemy.get().health);
    }

    @org.junit.jupiter.api.Test
    void shopHealth() {
        player.get().Heal();
        assertEquals(115, player.get().health);
    }

    @org.junit.jupiter.api.Test
    void shopDamage() {
        player.get().increase_damage();
        assertEquals(15, player.get().damage_limit);
    }

    @org.junit.jupiter.api.Test
    void shopDefence() {
        player.get().increase_defence();
        assertEquals(0.25, player.get().defence);
    }

    @org.junit.jupiter.api.Test
    void enemyDropsCoinsOnDeath() {
        AtomicReference<Player> enemy = new AtomicReference<>(new Player("Ork", 0, 20, 0, "false", 0, 0, 0, "", 15));
        game.get().attack(player, enemy);
        assertEquals(115, player.get().coins);
    }

    @org.junit.jupiter.api.Test
    void createEnemy(){
        Enemy enemy = new Enemy("Goblin", 50, 15, 10, "goblin.gif", 15);
        assertEquals("Goblin", enemy.enemy_name);
        assertEquals(50, enemy.health);
        assertEquals(15, enemy.damage_limit);
        assertEquals(10, enemy.defence);
        assertEquals("goblin.gif", enemy.gif);
    }

    @org.junit.jupiter.api.Test
    void addHealthPotion(){
        player.get().AddHealthPotion();
        assertEquals(1, player.get().healthPotions);
        assertEquals(85, player.get().coins);
    }

    @org.junit.jupiter.api.Test
    void addPoisonPotion(){
        player.get().AddPoisonPotion();
        assertEquals(2, player.get().poisonPotions);
        assertEquals(85, player.get().coins);
    }

    @org.junit.jupiter.api.Test
    void useHealthPotion(){
        player.get().AddHealthPotion();
        player.get().UseHealthPotion(game);
        assertEquals(0, player.get().healthPotions);
        assertEquals(125, player.get().health);
    }

    @org.junit.jupiter.api.Test
    void newEnemyEasy(){
        List<Enemy> enemy;
//        Connection conn = sql2o.open();
//        conn.createQuery()
        enemy = model.newEnemy(1);
        assertEquals("Goblin", enemy.get(0).enemy_name);
        assertEquals("Sewer Rat", enemy.get(1).enemy_name);
        assertEquals("Giant Spider", enemy.get(2).enemy_name);
        assertEquals("Orc", enemy.get(3).enemy_name);
    }

    @org.junit.jupiter.api.Test
    void newEnemyMedium(){
        List<Enemy> enemy;
//        Connection conn = sql2o.open();
//        conn.createQuery()
        enemy = model.newEnemy(4);
        assertEquals("Blade Smith", enemy.get(0).enemy_name);
        assertEquals("Forest Bear", enemy.get(1).enemy_name);
        assertEquals("Corrupt Shaman", enemy.get(2).enemy_name);
        assertEquals("Shadow Assassin", enemy.get(3).enemy_name);
    }

    @org.junit.jupiter.api.Test
    void newEnemyHard(){
        List<Enemy> enemy;
//        Connection conn = sql2o.open();
//        conn.createQuery()
        enemy = model.newEnemy(7);
        assertEquals("Elemental Wizard", enemy.get(0).enemy_name);
        assertEquals("Forgotten Paladin", enemy.get(1).enemy_name);
        assertEquals("Infernal Drake", enemy.get(2).enemy_name);
        assertEquals("Tree ent", enemy.get(3).enemy_name);
    }

    @org.junit.jupiter.api.Test
    void newEnemyBoss(){
        List<Enemy> enemy;
        enemy = model.newEnemy(10);
        assertEquals("BOSS : Bone Master", enemy.get(1).enemy_name);
        assertEquals("BOSS : Dragon King", enemy.get(2).enemy_name);
        assertEquals("BOSS : Lich King", enemy.get(0).enemy_name);
    }

    @org.junit.jupiter.api.Test
    void killEnemy(){
        model.revivingEnemies();
        model.killedEnemy("BOSS : Lich King");
        Connection conn = sql2o.open();
        List<String> enemy;
        enemy = conn.createQuery("select already_killed from enemies where enemy_name = 'BOSS : Lich King'")
                .executeAndFetch(String.class);
        assertEquals("true", enemy.get(0));
    }

    @org.junit.jupiter.api.Test
    void reviveEnemy(){
        model.killedEnemy("BOSS : Lich King");
        model.revivingEnemies();
        List<String> enemy;
        Connection conn = sql2o.open();
        enemy = conn.createQuery("select already_killed from enemies where enemy_name = 'BOSS : Lich King'")
                .executeAndFetch(String.class);
        assertEquals("false", enemy.get(0));
    }

    @org.junit.jupiter.api.Test
    void updatingHighscores() {
        model.createPlayer("49921d6e-e210-4f68-ad7a-afac266278cb", player.get().username, "example full name", "example password", 0);
        model.updateHighscore(1000, "49921d6e-e210-4f68-ad7a-afac266278cb" );
        List<String> highscore;
        Connection conn = sql2o.open();
        highscore = conn.createQuery("select high_score from players where user_name = :username")
                .addParameter("username", "example username")
                .executeAndFetch(String.class);
        assertEquals(1000, model.checkHighscore("49921d6e-e210-4f68-ad7a-afac266278cb"));
    }

    @org.junit.jupiter.api.Test
    void checkUsernameDoesExist() {
        boolean check = model.is_username_used("example username");
        assertEquals(true, check);
    }

    @org.junit.jupiter.api.Test
    void checkUsernameDoesNotExist() {
        Connection conn = sql2o.beginTransaction();
        conn.createQuery("TRUNCATE TABLE players")
                .executeUpdate();
        conn.commit();
        boolean check = model.is_username_used("example username");
        assertEquals(false, check);
    }

    @org.junit.jupiter.api.Test
    void getHighScore() {
        model.createPlayer("49921d6e-e210-4f68-ad7a-afac266278cb", player.get().username, "example full name", "example password", 1000);
        Connection conn = sql2o.open();
        List<Players> highscores = model.get_high_score();
        assertEquals(1000,highscores.get(0).high_score);
    }
}