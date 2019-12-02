package models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class Player {

    public String username;
    public int health;
    public int damage_limit;
    public double defence;
    public String is_alive;
    public int coins;
    public int healthPotions;
    public int poisonPotions;
    public String gif;
    public int battles_won;
    public String pickedClass;
//    public ArrayList<String> arrayLog;


    public Player(String username, int health, int damage_limit, double defence, String is_alive, int coins, int healthPotions, int poisonPotions, String gif) {

        this.username = username;
        this.health = health;
        this.damage_limit = damage_limit;
        this.defence = defence/100;
        this.is_alive = is_alive;
        this.coins = coins;
        this.healthPotions = healthPotions;
        this.poisonPotions = poisonPotions;
        this.gif = gif;
        this.battles_won = 0;
        this.pickedClass = "false";
//        this.arrayLog = new ArrayList<String>();
    }

        public void recieve_damage(double damage, List<String> log){
        health -= damage;
//        arrayLog.add(username + " took " + damage + " damage");
        if(health <= 0){
            health = 0;
        }
        is_character_alive(log);
    }

    public String block_attack(){
        if(Math.random() >= (1 - defence))
        {
//            arrayLog.add(username + " Blocked attack");
            return "true";
        }
        return "false";
    }

    public void is_character_alive(List<String> log) {
        if (health <= 0) {
            is_alive = "false";
            log.add(username + " has died");
//            arrayLog.add(username + " Died");
        }
    }

    public void Heal(){
        if (coins >= 10){
            health = health + 10;
            coins = coins - 10;
        }
    }

    public void AddHealthPotion(){
        if (coins >= 20){
            if(healthPotions < 3) {
                healthPotions= healthPotions + 1;
                coins = coins - 20;
            }
        }
    }

    public void UseHealthPotion(AtomicReference<Game> game){
        if (healthPotions > 0){
            healthPotions = healthPotions - 1;
            health = health + 15;
            game.get().log.add(username + " recovered 15 health points");
        }
    }



    public void increase_damage(){
        if (coins >= 10){
            damage_limit = damage_limit + 5;
            coins = coins - 10;
        }
    }

    public void AddPoisonPotion(){
        if (coins >= 20){
            if(poisonPotions < 3){
                poisonPotions = poisonPotions + 1;
                coins = coins - 20;
            }
        }
    }

    public void increase_defence(){
        if (coins >= 10){
            if(defence < 0.5){
                defence = defence * 100;
                defence = defence + 5;
                defence = defence / 100;
                coins = coins - 10;
            }
        }
    }

}






