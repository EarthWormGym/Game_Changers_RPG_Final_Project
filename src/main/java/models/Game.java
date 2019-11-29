package models;

import lombok.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class Game {


    public List<Player> playersArray;


        public Game(Player player, Player enemy){
            playersArray = new ArrayList<Player>();
            playersArray.add(player);
            playersArray.add(enemy);
        }

        public void attack(Player player, Player enemy){
            if(enemy.block_attack().equals("false")){
                System.out.println("enemy damaged");
                enemy.recieve_damage(Math.round(Math.floor(this.random_damage(player))));
                if(enemy.is_alive.equals("false")) {
                    player.coins += 15;
                }
            }else{
                System.out.println("enemy blocked");
            }
        }

        public void enemy_attack(Player player, Player enemy){
            if(player.block_attack().equals("false")) {
                System.out.println("player damaged");
                player.recieve_damage(Math.round(Math.floor(this.random_damage(enemy))));
            }else{
                System.out.println("player blocked");
            }
        }

        public double random_damage(Player character){
            Random rand = new Random();
            int random = (int)(Math.random() * character.damage_limit + 1);
            return (random);
        }
}
