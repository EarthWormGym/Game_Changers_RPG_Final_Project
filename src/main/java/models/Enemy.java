package models;

public class Enemy {
    public String enemy_name;
    public Integer health;
    public Integer damage_limit;
    public Integer defence;
    public String gif;
    public int coin_drop;

    public Enemy(String enemy_name, Integer health, Integer damage_limit, Integer defence, String gif, int coin_drop) {
        this.enemy_name = enemy_name;
        this.health = health;
        this.damage_limit = damage_limit;
        this.defence = defence;
        this.gif = gif;
        this.coin_drop = coin_drop;
    }
}
