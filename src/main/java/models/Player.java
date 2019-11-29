package models;

import lombok.Data;

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

    }

    public void recieve_damage(double damage){
        health -= damage;
        if(health <= 0){
            health = 0;
        }
        is_character_alive();
    }

    public boolean block_attack(){
        System.out.println(Math.random());
        if(Math.random() >= (1 - defence))
        {
            return true;
        }
        return false;
    }

    public void is_character_alive() {
        if (health <= 0) {
            is_alive = "false";
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
            healthPotions = healthPotions + 1;
            coins = coins - 20;
        }
    }

    public void UseHealthPotion(){
        if (healthPotions > 0){
            healthPotions = healthPotions - 1;
            health = health + 15;
        }
    }



    public void increase_damage(){
        if (coins >= 10){
            damage_limit = damage_limit + 5;
            coins = coins - 10;
        }
    }

    public void AddPosionPotion(){
        if (coins >= 20){
            poisonPotions = poisonPotions + 1;
            coins = coins - 20;
        }
    }

    public void increase_defence(){
        if (coins >= 10){
            defence = defence + 5;
            coins = coins - 10;
        }
    }

}






