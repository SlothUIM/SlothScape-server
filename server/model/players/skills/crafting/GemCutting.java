package server.model.players.skills.crafting;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;

public class GemCutting {

    private final Player c;

    public GemCutting(Player c) {
        this.c = c;
    }

    public enum GemData {
        OPAL(1625, 1609, 1, 15, 890),
        JADE(1627, 1611, 13, 20, 891),
        RED_TOPAZ(1629, 1613, 16, 25, 892),
        SAPPHIRE(1623, 1607, 20, 50, 888),
        EMERALD(1621, 1605, 27, 67, 889),
        RUBY(1619, 1603, 34, 85, 887),
        DIAMOND(1617, 1601, 43, 107, 886),
        DRAGONSTONE(1631, 1615, 55, 137, 885),
        ONYX(6571, 6573, 67, 167, 2717),
        ZENYTE(19496, 19493, 89, 200, 2717); // Added Zenyte from your Wiki list!

        private final int uncutId, cutId, levelReq, exp, animation;

        GemData(int uncutId, int cutId, int levelReq, int exp, int animation) {
            this.uncutId = uncutId;
            this.cutId = cutId;
            this.levelReq = levelReq;
            this.exp = exp;
            this.animation = animation;
        }
    }

    public boolean isCuttingGem(int item1, int item2) {
        int chisel = 1755;
        if (item1 == chisel || item2 == chisel) {
            int gemId = (item1 == chisel) ? item2 : item1;
            for (GemData g : GemData.values()) {
                if (g.uncutId == gemId) {
                    cutGem(g);
                    return true;
                }
            }
        }
        return false;
    }

    private void cutGem(GemData gem) {
        if (c.getSkills().getLevel(Skill.CRAFTING) < gem.levelReq) {
            c.sendMessage("You need a Crafting level of " + gem.levelReq + " to cut this gem.");
            return;
        }
        
        c.startAnimation(gem.animation);
        c.getPA().closeAllWindows();
        c.isCrafting = true;

        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (!c.isCrafting || !c.getItems().playerHasItem(gem.uncutId)) {
                    container.stop();
                    return;
                }
                c.getItems().deleteItem(gem.uncutId, 1);
                c.getItems().addItem(gem.cutId, 1);
                c.getSkills().addExperience(gem.exp * Config.CRAFTING_EXPERIENCE, Skill.CRAFTING);
                c.sendMessage("You successfully cut the gem.");
                c.startAnimation(gem.animation);
            }
            @Override
            public void stop() {
                c.isCrafting = false;
                c.startAnimation(65535); 
            }
        }, 3);
    }
}