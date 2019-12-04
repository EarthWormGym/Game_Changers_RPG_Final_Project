import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import org.apache.log4j.BasicConfigurator;
import org.flywaydb.core.Flyway;
import org.sql2o.Sql2o;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;
import spark.ModelAndView;
import java.util.Collections;

import javax.crypto.spec.PSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static spark.Spark.*;
import static spark.Spark.post;

public class Main {




    public static void main(String[] args) {
        BasicConfigurator.configure();

        staticFileLocation("/templates");


        Flyway flyway = Flyway.configure().dataSource("jdbc:postgresql://localhost:5432/makersandmortals", null, null).load();
        flyway.migrate();

        Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/" + "makersandmortals", null, null, new PostgresQuirks() {
            {
                // make sure we use default UUID converter.
                converters.put(UUID.class, new UUIDConverter());
            }
        });

        Model model = new Sql2oModel(sql2o);

        model.revivingEnemies();

        AtomicReference<Player> player = new AtomicReference<>(new Player("Adam", 100, 20, 20, "true", 0, 3, 1, "", 0));
        AtomicReference<Players> players = new AtomicReference<>(new Players("playerUuid.toString()", "username", "fullname", "password", 0));
        List<Enemy> enemies = model.newEnemy(player.get().battles_won);
        int min = 0;
        int max = enemies.size() - 1;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        Enemy randomEnemy = enemies.get(randomNum);
        AtomicReference<Player> enemy = new AtomicReference<>(new Player(randomEnemy.enemy_name, randomEnemy.health, randomEnemy.damage_limit, randomEnemy.defence, "true", 0, 0, 0, randomEnemy.gif, 15));

        AtomicReference<Game> game = new AtomicReference<>(new Game(player, enemy));


        get("/", (req, res) -> {
          res.redirect("/home");
          return null;
        });


        get("/home", (req, res) -> {
            String username = req.session().attribute("user");
            String signedIn = req.session().attribute("Signed_In?");
            if(signedIn == "true"){
                req.session().attribute("user", username);
            }else{
                req.session().attribute("user", "Hello There!");
                username = req.session().attribute("user");
            }
            HashMap battle = new HashMap();
            battle.put("player", player);
            battle.put("enemy", enemy);
            battle.put("username", username);
            return new ModelAndView(battle, "templates/home.vtl");
        }, new VelocityTemplateEngine());

        get("/battle", (req, res) ->{
            HashMap battle = new HashMap();
            battle.put("player", player);
            battle.put("enemy", enemy);
            Collections.reverse(game.get().log);
            battle.put("game", game);
            String username = req.session().attribute("user");
            battle.put("username", username);
            if(player.get().is_alive.equals("false")){
                res.redirect("/died");
            }
            else if(player.get().battles_won == 11){
                res.redirect("/won");
            }
            return new ModelAndView(battle, "templates/battle.vtl");
        }, new VelocityTemplateEngine());

        get("/won", ((request, response) -> {
            HashMap scores = new HashMap();
            int highscore = model.checkHighscore(players.get().user_ID);
            int current_score = player.get().calc_score(game);
            if(current_score > highscore) {
                model.updateHighscore(current_score, players.get().user_ID);
            }
            scores.put("current_score", current_score);
            scores.put("high_score", highscore);
            scores.put("username", players.get().user_name);
            return new ModelAndView(scores, "templates/victory.vtl");
        }), new VelocityTemplateEngine());

        get("/died", ((request, response) -> {
            HashMap scores = new HashMap();
            int highscore = model.checkHighscore(players.get().user_ID);
            int current_score = player.get().calc_score(game);
            if(current_score > highscore) {
                model.updateHighscore(current_score, players.get().user_ID);
            }
            scores.put("current_score", current_score);
            scores.put("high_score", highscore);
            scores.put("username", players.get().user_name);
            return new ModelAndView(scores, "templates/lost.vtl");
        }), new VelocityTemplateEngine());

        get("/newbattle", ((req, res) -> {
            player.get().chest_reward = null;
            game.get().log.clear();
            model.killedEnemy(enemy.get().username);
            List<Enemy> enemiesBattle = model.newEnemy(player.get().battles_won);
            int min1 = 0;
            int max1 = enemiesBattle.size() - 1;
            int randomNum1 = ThreadLocalRandom.current().nextInt(min1, max1 + 1);
            Enemy randomEnemy2 = enemiesBattle.get(randomNum1);
            enemy.set(new Player(randomEnemy2.enemy_name, randomEnemy2.health, randomEnemy2.damage_limit, randomEnemy2.defence, "true", 0, 0, 0, randomEnemy2.gif, randomEnemy2.coin_drop));
            res.redirect("/battle");
            return null;
        }));

        get("/newGame", ((request, response) -> {
            player.set(new Player(player.get().username, player.get().health, player.get().damage_limit, player.get().defence, "true", 0, player.get().healthPotions, player.get().poisonPotions, player.get().gif, 0));
            model.revivingEnemies();
            List<Enemy> new_game_enemies = model.newEnemy(player.get().battles_won);
            int new_game_min = 0;
            int new_game_max = new_game_enemies.size() - 1;
            int new_game_randomNum = ThreadLocalRandom.current().nextInt(new_game_min, new_game_max + 1);
            Enemy new_game_randomEnemy = new_game_enemies.get(new_game_randomNum);
            enemy.set(new Player(new_game_randomEnemy.enemy_name, new_game_randomEnemy.health, new_game_randomEnemy.damage_limit, new_game_randomEnemy.defence, "true", 0, 0, 0, new_game_randomEnemy.gif, new_game_randomEnemy.coin_drop));
            game.set(new Game(player, enemy));
            response.redirect("/battle");
            return null;
        }));

        post("/attack", (req, res) ->{
            TimeUnit.SECONDS.sleep(2);
            res.redirect("/battle");
            return null;
        });

        get("/battleJson", (req, res) -> {
            res.type("application/json");
            game.get().attack(player, enemy);
            if(enemy.get().is_alive.equals("true")){
                game.get().enemy_attack(player, enemy);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(game);
            return json;
        });

        get("/enemy_dead", (req, res) -> {
            res.redirect("/shop");
         return null;
        });

        post("/usehealthpotion", (req, res) ->{
            player.get().UseHealthPotion(game);
            res.redirect("/battle");
            return null;
        });

        post("/usepoisonpotion", (req, res) ->{
            game.get().poisonAttack(player, enemy);
            player.get().poisonPotions = player.get().poisonPotions - 1;
            res.redirect("/battle");
            return null;
        });

        post("/sign_out", (req, res) ->{
            req.session().attribute("Signed_In?", "false");
            res.redirect("/home");
            return null;
        });

        get("/sign_up", (req, res) -> {
            HashMap signup = new HashMap();
            return new ModelAndView(signup, "templates/sign_up.vtl");
        }, new VelocityTemplateEngine());

        get("/signed_up", (req, res) -> {
            HashMap signed = new HashMap();
            return new ModelAndView(signed, "templates/signed_up.vtl");
        }, new VelocityTemplateEngine());

        get("/lost", (req, res) -> {
            HashMap signed = new HashMap();
            return new ModelAndView(signed, "templates/lost.vtl");
        }, new VelocityTemplateEngine());

        get("/win", (req, res) -> {
            HashMap signed = new HashMap();
            return new ModelAndView(signed, "templates/victory.vtl");
        }, new VelocityTemplateEngine());

        post("/signed", (req, res) -> {
            res.redirect("/sign_in");
            return null;
        });

        post("/signed_up", (req, res) -> {
            String username = req.queryParams("username");
            String fullname = req.queryParams("full_name");
            String password = req.queryParams("password");
            if(model.is_username_used(username)){
                res.redirect("/sign_up");
            } else {
                UUID playerUuid = UUID.randomUUID();
                players.set(new Players(playerUuid.toString(), username, fullname, password, 0));
                model.createPlayer(playerUuid.toString(), username, fullname, password, 0);
                res.redirect("/signed_up");
            }
            return null;
        });

        get("/sign_in", (req, res) -> {
            HashMap signin = new HashMap();
            return new ModelAndView(signin, "templates/sign_in.vtl");
        }, new VelocityTemplateEngine());

        post("/signed_in", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            String userID = model.getUserID(username);

            if(model.CorrectPassword(username, password)){
                res.redirect("/signed_in");
            } else {
                res.redirect("/sign_in");
            };

            req.session().attribute("user",username);
            req.session().attribute("Signed_In?","true");
            players.set(new Players(userID, username, "fullname", password, 0));
            player.get().username = username;
            return null;
        });

        get("/signed_in", (req, res) -> {
            HashMap signedin = new HashMap();
            return new ModelAndView(signedin, "templates/signed_in.vtl");
        }, new VelocityTemplateEngine());

        post("/sign_in", (req, res) -> {
            res.redirect("/sign_in");
            return null;
        });

        post("/sign_up", (req, res) -> {
            res.redirect("/sign_up");
            return null;
        });

        post("/home", (req, res) -> {
            res.redirect("/home");
            return null;
        });

        get("/shop", (req, res) ->{
            String username = req.session().attribute("user");
            HashMap battle = new HashMap();
            battle.put("player", player);
            battle.put("username", username);
            return new ModelAndView(battle, "templates/shop.vtl");
        }, new VelocityTemplateEngine());

        post("/health", (req, res) ->{
            player.get().Heal();
           res.redirect("/shop");
           return null;
        });

        post("/healthPotion", (req, res) ->{
            player.get().AddHealthPotion();
            res.redirect("/shop");
            return null;
        });

        post("/poisonPotion", (req, res) ->{
            player.get().AddPoisonPotion();
            res.redirect("/shop");
            return null;
        });

        post("/damage", (req, res) ->{
            player.get().increase_damage();
            res.redirect("/shop");
            return null;
        });

        post("/defence", (req, res) ->{
            player.get().increase_defence();
            res.redirect("/shop");
            return null;
        });

        get("/class", (req, res) ->{
            String username = req.session().attribute("user");
            if(username == "Hello There!"){
                res.redirect("/home");
            }
            HashMap battle = new HashMap();
            battle.put("username", username);
            battle.put("player", player);
            return new ModelAndView(battle, "templates/classSelect.vtl");
        }, new VelocityTemplateEngine());

        post("/knightclass", (req, res) ->{
            player.get().health = 180;
            player.get().damage_limit = 20;
            player.get().defence = 25;
            player.get().healthPotions = 1;
            player.get().poisonPotions = 0;
            player.get().gif = ("players/Knight_idle.gif");
            player.get().pickedClass = "true";
            res.redirect("/class");
            return null;
        });

        post("/archerclass", (req, res) ->{
            player.get().health = 125;
            player.get().damage_limit = 45;
            player.get().defence = 15;
            player.get().healthPotions = 2;
            player.get().poisonPotions = 1;
            player.get().gif = ("players/archer.gif");
            player.get().pickedClass = "true";
            res.redirect("/class");
            return null;
        });

        post("/wizzardclass", (req, res) ->{
            player.get().health = 130;
            player.get().damage_limit = 40;
            player.get().defence = 10;
            player.get().healthPotions = 1;
            player.get().poisonPotions = 3;
            player.get().gif = ("players/Wizard_Character.gif");
            player.get().pickedClass = "true";
            res.redirect("/class");
            return null;
        });

        post("/chest", (req, res) ->{
            double random =  Math.random();
            player.get().num_keys -= 1;
            if(random <= 0.33){
                player.get().health += 50;
                player.get().chest_reward = "health";
            } else if (random <= 0.66 && random > 0.33){
                player.get().damage_limit += 20;
                player.get().chest_reward = "damage";
            } else {
                player.get().defence += 0.2;
                player.get().chest_reward = "defence";
            }
            res.redirect("/shop");
            return null;
        });



        get("/leader_board", (req, res) ->{
            List<Players> leader_board = model.get_high_score();
            System.out.println(leader_board);

            HashMap battle = new HashMap();
            battle.put("scores", leader_board);

            return new ModelAndView(battle, "templates/leader_board.vtl");
        }, new VelocityTemplateEngine());


    }




}
