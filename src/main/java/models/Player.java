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
    public int poisoned;
    public int hpPotsUsed;
    public int pPotsUsed;
    public int num_keys;
    public String chest_reward;
    public int coin_drop;

    public Player(String username, int health, int damage_limit, double defence, String is_alive, int coins, int healthPotions, int poisonPotions, String gif, int coin_drop) {

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
        this.poisoned = 0;
        this.hpPotsUsed = 0;
        this.pPotsUsed = 0;
        this.num_keys = 0;
        this.coin_drop = coin_drop;

        this.chest_reward = null;
    }

    public void recieve_damage(double damage, List<String> log){
    health -= damage;
    if(health <= 0){
        health = 0;
    }
    is_character_alive(log);
    }

    public String block_attack(){
        if(Math.random() >= (1 - defence))
        {
            return "true";
        }
        return "false";
    }

    public void is_character_alive(List<String> log) {
        if (health <= 0) {
            is_alive = "false";
            log.add(username + " has died");
        }
    }

    public void Heal(){
        if (coins >= 10){
            health = health + 15;
            coins = coins - 10;
        }
    }

    public void AddHealthPotion(){
        if (coins >= 15){
            if(healthPotions < 3) {
                healthPotions= healthPotions + 1;
                coins = coins - 15;
            }
        }
    }

    public void UseHealthPotion(AtomicReference<Game> game){
        if (healthPotions > 0){
            healthPotions = healthPotions - 1;
            health = health + 25;
            hpPotsUsed += 1;
            game.get().log.add(username + " recovered 25 health points");
        }
    }



    public void increase_damage(){
        if (coins >= 10){
            damage_limit = damage_limit + 5;
            coins = coins - 10;
        }
    }

    public void AddPoisonPotion(){
        if (coins >= 15){
            if(poisonPotions < 3){
                poisonPotions = poisonPotions + 1;
                coins = coins - 15;
            }
        }
    }

    public void increase_defence(){
        if (coins >= 10){
            if(defence < 0.5){
                defence = defence * 100;
                defence = defence + 5;
                defence = defence / 100;
                coins = coins - 15;
            }
        }
    }

    public int calc_score(AtomicReference<Game> game){
        int healthPotionsPointsAdd = 0;
        int healthPotionsPointsSub = 0;
        int poisonPotionsPointsAdd = 0;
        int poisonPotionsPointsSub = 0;
        int healthPoints = health * 137;
        if(hpPotsUsed <= 2){
            healthPotionsPointsAdd = 316;
        }else{
            healthPotionsPointsSub = hpPotsUsed * 27;
        }
        if(pPotsUsed <= 2){
            poisonPotionsPointsAdd = 321;
        } else {
            poisonPotionsPointsSub = pPotsUsed * 27;
        }
        int coinPoints = coins * 32;
        int critPoints = game.get().critCounter * 183;
        int total_score = healthPoints + coinPoints + healthPotionsPointsAdd - healthPotionsPointsSub + poisonPotionsPointsAdd - poisonPotionsPointsSub + critPoints;
        return total_score;
    }

}






