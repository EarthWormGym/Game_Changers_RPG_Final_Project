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
    public int critCounter = 0;


        public Game(AtomicReference<Player> player, AtomicReference<Player> enemy){
            playersArray = new ArrayList<>();
            log = new ArrayList<>();
            playersArray.add(player);
            playersArray.add(enemy);
        }

        public void attack(AtomicReference<Player> player, AtomicReference<Player> enemy){
            if(enemy.get().block_attack().equals("false")){
                double damage = this.random_damage(player);
                if(enemy.get().poisoned > 0) {
                    enemy.get().poisoned -= 1;
                    enemy.get().health -= 10;
                    log.add(enemy.get().username + " was damaged for 10 by the lingering poison");
                }
                if(damage == player.get().damage_limit){
                    log.add(player.get().username + " CRITICALLY hit " + enemy.get().username);
                    critCounter += 1;
                }else{
                    log.add(player.get().username + " attacked " + enemy.get().username + " for " + damage);

                }
                enemy.get().recieve_damage(Math.round(Math.floor(damage)), log);
                if(enemy.get().is_alive.equals("false")) {
                    if(drop_key(player, enemy)){
                        log.add("Enemy drops key");
                    }
                    player.get().coins += enemy.get().coin_drop;
                    player.get().battles_won += 1;
                }
            }else{
                log.add(enemy.get().username + " blocked the attack");
            }
        }

        public void poisonAttack(AtomicReference<Player> player, AtomicReference<Player> enemy){
            if(enemy.get().block_attack().equals("false")){
                enemy.get().recieve_damage(20.0, log);
                enemy.get().poisoned += 2;
                log.add(player.get().username + " poisoned " + enemy.get().username + " for 20 damage and it seems to have left an effect");
                if(enemy.get().is_alive.equals("false")) {
                    player.get().coins += enemy.get().coin_drop;
                    player.get().battles_won += 1;
                }
            }else{
                log.add(enemy.get().username + " blocked your poison attack");
            }
        }

        public void enemy_attack(AtomicReference<Player> player, AtomicReference<Player> enemy){
            if(player.get().block_attack().equals("false")) {
                double damage = this.random_damage(enemy);
                if(damage == enemy.get().damage_limit){
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
        int random = (int) (Math.random() * character.get().damage_limit + 1);
        return (random);
    }

        public boolean drop_key(AtomicReference<Player> player, AtomicReference<Player> enemy){
            boolean key = false;
            double key_drop_chance = 0.1;
            double random = Math.random();
            if(enemy.get().health == 0){
                if(random <= key_drop_chance){
                    key = true;
                    player.get().num_keys += 1;
                }
            }
            return key;
        }
}
