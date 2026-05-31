package server.model.players.skills;

import server.model.players.Player;

public class Skilling {

    private Player player;
    private Skill currentSkill;

    public Skilling(Player player) {
        this.player = player;
    }

    public void setSkill(Skill skill) {
        this.currentSkill = skill;
    }

    public Skill getSkill() {
        return currentSkill;
    }

    public boolean isSkilling() {
        return currentSkill != null;
    }

    public void stop() {
        this.currentSkill = null;
    }
}