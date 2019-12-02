package models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class Game {


    public List<AtomicReference<Player>> playersArray;
    public List<String> log;


        public Game(AtomicReference<Player> player, AtomicReference<Player> enemy){
            playersArray = new ArrayList<>();
            log = new ArrayList<>();
            playersArray.add(player);
            playersArray.add(enemy);
        }

        public void attack(AtomicReference<Player> player, AtomicReference<Player> enemy){
            if(enemy.get().block_attack().equals("false")){
                double damage = this.random_damage(player);
                if(damage == player.get().damage_limit){
                    log.add(player.get().username + " CRITICALLY hit " + enemy.get().username);
                }else{
                    log.add(player.get().username + " attacked " + enemy.get().username + " for " + damage);

                }
                enemy.get().recieve_damage(Math.round(Math.floor(damage)), log);
                if(enemy.get().is_alive.equals("false")) {
                    player.get().coins += 15;
                    player.get().battles_won += 1;
                }
            }else{
                log.add(enemy.get().username + " blocked the attack");
            }
        }

    public void poisonAttack(AtomicReference<Player> player, AtomicReference<Player> enemy){
        if(enemy.get().block_attack().equals("false")){
            enemy.get().recieve_damage(30.0, log);
            log.add(player.get().username + " poisoned " + enemy.get().username);
            if(enemy.get().is_alive.equals("false")) {
                player.get().coins += 15;
                player.get().battles_won += 1;
            }
        }else{
        }
    }

        public void enemy_attack(AtomicReference<Player> player, AtomicReference<Player> enemy){
            if(player.get().block_attack().equals("false")) {
                double damage = this.random_damage(enemy);
                if(damage == player.get().damage_limit){
                    log.add(enemy.get().username + " CRITICALLY hit " + player.get().username);
                }else{
                    log.add(enemy.get().username + " attacked " + player.get().username + " for " + damage);
                }
                player.get().recieve_damage(Math.round(Math.floor(damage)), log);
            }else{
                log.add(player.get().username + " blocked the attack");
            }
        }

        public double random_damage(AtomicReference<Player> character) {
            Random rand = new Random();
            int random = (int) (Math.random() * character.get().damage_limit + 1);
            return (random);
        }
}
