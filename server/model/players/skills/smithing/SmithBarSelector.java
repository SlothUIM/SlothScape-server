package server.model.players.skills.smithing;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.smithing.Smithing.SmithBar;
public final class SmithBarSelector {
    private SmithBarSelector() {}

    /**
     * Decide which bar to use:
     * 1) If player has a preferred bar AND still meets level & has item -> use it.
     * 2) Else pick best available.
     */
    public static SmithBar choose(Player c) {
        if (c.preferredSmithBarId != null) {
            SmithBar pref = SmithBar.byItemId(c.preferredSmithBarId);
            if (pref != null
                    && c.getSkills().getLevel(Skill.SMITHING) >= pref.level
                    && c.getItems().playerHasItem(pref.itemId)) {
                return pref;
            } else {
                // Invalidate if no longer valid
                c.preferredSmithBarId = null;
            }
        }
        return SmithBar.bestAvailable(c);
    }
}