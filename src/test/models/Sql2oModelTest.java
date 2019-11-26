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

    @BeforeAll
    static void setUpClass() {
        BasicConfigurator.configure();
        Flyway flyway = Flyway.configure().dataSource("jdbc:postgresql://localhost:5432/makersandmortalstest", null, null).load();
        flyway.migrate();

    }
    @BeforeEach
    void setUp() {
        Connection conn = sql2o.beginTransaction();
        conn.createQuery("insert into users(username, full_name, password) VALUES (:username, :full_name, :password)")
                .addParameter("username", "example username")
                .addParameter("full_name", "example full name")
                .addParameter("password", "example password")
                .executeUpdate();
        conn.commit();

    }

    @AfterEach
    void tearDown() {
        Connection conn = sql2o.beginTransaction();
        conn.createQuery("TRUNCATE TABLE players, users")
                .executeUpdate();
        conn.commit();
    }

    @Test
    void createPost() {
    }

    @Test
    void getAllPosts() {
    }
    @Test
    void createUser() {
        Connection conn = sql2o.open();
        Model model = new Sql2oModel(sql2o);
        boolean result = false;
        model.createUser("example username2", "example full name2", "example password2");
        List<Users> list_of_users;
        list_of_users = (conn.createQuery("select * from users").executeAndFetch(Users.class));
        String test = "Users(username=example username2, full_name=example full name2, password=example password2)";

        if(list_of_users.toString().contains(test)){
            result = true;
        } else {
            result = false;
        }
        assertTrue(result);
    }

    @Test
    void UsernameExist() {
        Model model = new Sql2oModel(sql2o);
        assertTrue(model.UsernameExist("example username"));
    }

    @Test
    void CorrectPassword(){
        Model model = new Sql2oModel(sql2o);
        assertTrue(model.CorrectPassword("example username","example password"));
    }


}