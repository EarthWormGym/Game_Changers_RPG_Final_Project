import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import org.apache.log4j.BasicConfigurator;
import org.flywaydb.core.Flyway;
import org.sql2o.Sql2o;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;
import spark.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;
import static spark.Spark.post;

public class Main {



//    public static Player player = new Player("Adam", 100,15,20,"true", 100, 3, 1);
//
//    public static Player enemy = new Player("Ork", 10,10,10,"true", 0 , 0 , 0);
//
//    public static Game game = new Game(player, enemy);

    public static void main(String[] args) {
        BasicConfigurator.configure();
//        staticFileLocation("/main");
        staticFileLocation("/templates");
//        staticFileLocation("/main/JavaScript/jquery.js");
//        externalStaticFileLocation("../../JavaScript/jquery.js");

        Flyway flyway = Flyway.configure().dataSource("jdbc:postgresql://localhost:5432/makersandmortals", null, null).load();
        flyway.migrate();

        Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/" + "makersandmortals", null, null, new PostgresQuirks() {
            {
                // make sure we use default UUID converter.
                converters.put(UUID.class, new UUIDConverter());
            }
        });

        Model model = new Sql2oModel(sql2o);

        int min = 0;
        int max = 3;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        Player player = new Player("Adam", 100,100,20,"true", 100, 3, 1, "");

        List<Enemy> enemies = model.newEnemy(player.battles_won);
        Enemy randomEnemy = enemies.get(randomNum);
        Player enemy = new Player(randomEnemy.enemy_name, randomEnemy.health,randomEnemy.damage_limit,randomEnemy.defence,"true", 0 , 0 , 0, randomEnemy.gif);

        System.out.println(enemy.gif);
        Game game = new Game(player, enemy);


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
                req.session().attribute("user", "Prepare for carnage");
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
            int min1 = 0;
            int max1 = 3;
            int randomNum1 = ThreadLocalRandom.current().nextInt(min1, max1 + 1);
            List<Enemy> enemiesBattle = model.newEnemy(player.battles_won);
            Enemy randomEnemy2 = enemiesBattle.get(randomNum1);
            Player randomEnemyobj = new Player(randomEnemy2.enemy_name, randomEnemy2.health,randomEnemy2.damage_limit,randomEnemy2.defence,"true", 0 , 0 , 0, randomEnemy2.gif);
            battle.put("player", player);
            battle.put("enemy", randomEnemyobj);
            return new ModelAndView(battle, "templates/battle.vtl");
        }, new VelocityTemplateEngine());

        get("/newbattle", ((req, res) -> {
            player.battles_won += 1;
            System.out.println(player.battles_won);
            res.redirect("/battle");
            return null;
        }));


        post("/attack", (req, res) ->{
            TimeUnit.SECONDS.sleep(2);
            res.redirect("/battle");
            return null;
        });

        get("/battleJson", (req, res) -> {
            res.type("application/json");

            game.attack(player, req.queryParams(enemy));
            game.enemy_attack(player, enemy);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(game);
            return json;
        });

        get("/enemy_dead", (req, res) -> {
            res.redirect("/shop");
         return null;
        });

        post("/usehealthpotion", (req, res) ->{
            player.UseHealthPotion();
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

        post("/signed", (req, res) -> {
            res.redirect("/sign_in");
            return null;
        });

        post("/signed_up", (req, res) -> {
            String username = req.queryParams("username");
            String fullname = req.queryParams("full_name");
            String password = req.queryParams("password");
            UUID playerUuid = UUID.randomUUID();
            model.createPlayer(playerUuid.toString(), username, fullname, password, 0);
            res.redirect("/signed_up");
            return null;
        });

        get("/sign_in", (req, res) -> {
            HashMap signin = new HashMap();
            return new ModelAndView(signin, "templates/sign_in.vtl");
        }, new VelocityTemplateEngine());

        post("/signed_in", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if(model.CorrectPassword(username, password)){
                res.redirect("/signed_in");
            } else {
                res.redirect("/sign_in");
            };

            req.session().attribute("user",username);
            req.session().attribute("Signed_In?","true");
            player.username = username;
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
            //Player player = new Player("Adam", 100,10,20,"true", 0 );
            String username = req.session().attribute("user");
            HashMap battle = new HashMap();
            battle.put("player", player);
            battle.put("username", username);
            return new ModelAndView(battle, "templates/shop.vtl");
        }, new VelocityTemplateEngine());

        post("/health", (req, res) ->{
            player.Heal();
           res.redirect("/shop");
           return null;
        });

        post("/healthPotion", (req, res) ->{
            player.AddHealthPotion();
            res.redirect("/shop");
            return null;
        });

        post("/poisonPotion", (req, res) ->{
            player.AddPosionPotion();
            res.redirect("/shop");
            return null;
        });

        post("/damage", (req, res) ->{
            player.increase_damage();
            res.redirect("/shop");
            return null;
        });

        post("/defence", (req, res) ->{
            player.defence = player.defence * 100;
            player.increase_defence();
            player.defence = player.defence / 100;
            res.redirect("/shop");
            return null;
        });


    }
}
