package models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class Game {


    public List<AtomicReference<Player>> playersArray;


        public Game(AtomicReference<Player> player, AtomicReference<Player> enemy){
            playersArray = new ArrayList<>();
            playersArray.add(player);
            playersArray.add(enemy);
        }

        public void attack(AtomicReference<Player> player, AtomicReference<Player> enemy){
            if(enemy.get().block_attack() == false){
                enemy.get().recieve_damage(Math.round(Math.floor(this.random_damage(player))));
                if(enemy.get().is_alive.equals("false")) {
                    player.get().coins += 15;
                }
            }else{
                System.out.println("enemy blocked");
            }
        }

        public void enemy_attack(AtomicReference<Player> player, AtomicReference<Player> enemy){
            if(player.get().block_attack() == false) {
                player.get().recieve_damage(Math.round(Math.floor(this.random_damage(enemy))));
            }
        }

        public double random_damage(AtomicReference<Player> character){
            Random rand = new Random();
            int random = (int)(Math.random() * character.get().damage_limit + 1);
            return (random);
        }
}
