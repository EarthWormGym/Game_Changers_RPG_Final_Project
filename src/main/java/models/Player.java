package models;

import lombok.Data;

@Data
public class Player {

    public String username;
    public int health;
    public int damage_limit;
    public int defence;
    public String is_alive;
    public int coins;
    public int healthPotions;

    public Player(String username, int health, int damage_limit, int defence, String is_alive, int coins, int healthPotions) {
        this.username = username;
        this.health = health;
        this.damage_limit = damage_limit;
        this.defence = defence;
        this.is_alive = is_alive;
        this.coins = coins;
        this.healthPotions = healthPotions;
    }


    public void recieve_damage(double damage){
        health -= damage;
    }

    public void Heal(){
        if (coins >= 10){
            health = health + 10;
            coins = coins - 10;
        }
    }

    public void AddHealthPotion(){
        if (coins >= 20){
            healthPotions = healthPotions + 1;
            coins = coins - 20;
        }
    }

    public void increase_damage(){
        if (coins >= 10){
            damage_limit = damage_limit + 5;
            coins = coins - 10;
        }
    }

    public void increase_defence(){
        if (coins >= 10){
            defence = defence + 5;
            coins = coins - 10;
        }
    }

}






