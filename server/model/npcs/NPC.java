package server.model.npcs;

import server.util.Misc;
import server.world.Boundary;
import server.world.Location;
import server.model.npcs.combat.CombatScript;
import server.model.npcs.combat.CombatScriptHandler;
import server.util.Buffer;
import server.model.items.*;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.MapInstance;
import server.model.players.combat.CombatType;
import server.model.players.combat.Damage;
import server.model.players.combat.Hitmark;
import server.model.players.skills.construction.HouseDungeon;
import server.model.players.skills.mining.motherlode.*;
import java.util.Queue;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;
import server.clip.Tile;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPCDumbPathFinder;
import server.model.npcs.combat.NPCCombatStats;
import server.model.npcs.NPCClipping;
import server.model.Entity;
import server.model.HealthStatus;

public class NPC extends Entity{

	@Getter @Setter private CombatScript combatScript;

	@Getter private NPCCombatStats stats;
	public int npcId;
	public String name;
	public int npcType;
	public int combat;
	public int absX, absY;
	public int heightLevel;
	public int makeX, makeY, maxHit, defence, attack, moveX, moveY, direction,
			walkingType, size;
	public int spawnX, spawnY;
	public int viewX, viewY;
	public int lastX, lastY;
	public boolean teleporting = false;

	public int hangManStatus;
	public boolean inDungeon;
	public int jesterAnim;
	public String hangManAnswer, currentHangMan;
	public Player lastAttackerStone;
    public MapInstance mapInstance;
	private long lastRandomWalk;
	private long lastRandomWalkHome;

	public boolean inDungeon()
	{
		return mapInstance instanceof HouseDungeon;
	}
	private long randomWalkDelay;
	private long randomStopDelay;
	public long lastRandomlySelectedPlayer = System.currentTimeMillis();
	@Getter @Setter public int targetingDelay = 0;

	public int totalAttacks;

	public boolean spawnedMinions;

	public int minHit;

	public long lastSpecialAttack;

	public int singleCombatDelay;


	public List<Location> payDirtPath; // full path
	public int payDirtTargetIndex = 0;  // next tile to walk to
	public List<Location> npcPath;
	public int npcForceWalkIndex;
	public boolean mining;
	public int miningTimer; // ticks until vein respawns
	public OreTile targetVein; // current vein being mined
	// Add to NPC class if not already there
	public int stuckTicks = 0;

	public int payDirtOwnerId = -1;
	public int lastAttackerStoneId = -1;         // who owns the pay-dirt
	public boolean paused = false;       // is it currently paused due to stopped wheel
	public List<OreStack> payDirtPending = new ArrayList<>();

	public boolean miningAnimStarted = false;

	public int miningAnimCooldown = 0;

	public boolean hasUltimateForceDamage;

	public static int talkingNPC;
    public int npcAttackAnimation;
    public int npcBlockAnimation;
    public int npcAttack2Animation;
    public int npcDeathAnimation;
	/**
	 * attackType: 0 = melee, 1 = range, 2 = mage
	 */

	public Location targetedLocation;
	public CombatType attackType1;
	public int attackType, projectileId, endGfx, spawnedBy, hitDelayTimer, HP,
			MaxHP, hitDiff, animNumber, actionTimer, enemyX, enemyY;
	public boolean applyDead, isDead, needRespawn, respawns;
	public boolean walkingHome, underAttack, summoner, trapNotice;
	public int freezeTimer, attackTimer, killerId, killedBy, oldIndex,
			underAttackBy, summonedBy;
	public long lastDamageTaken;
	public int impTimer;
	@Setter public boolean randomWalk;
	@Setter public boolean neverWalkHome;
	public boolean dirUpdateRequired;
	public boolean animUpdateRequired;
	public boolean hitUpdateRequired;
	public boolean updateRequired;
	public boolean forcedChatRequired;
	public boolean faceToUpdateRequired;
	public boolean randomfollow;
	public int firstAttacker;
	public String forcedText;
	private int projectileDelay = 0;
	public int getProjectileDelay() {
		return projectileDelay;
	}
	
	public void setProjectileDelay(int delay) {
		projectileDelay = delay;
	}
	/*public NPC(int _npcId, int _npcType, NPCDefinitions definition) {
		super(_npcId, definition.getNpcName());
		this.definition = definition;
		npcId = _npcId;
		npcType = _npcType;
		combat = -1;
		direction = -1;
		isDead = false;
		applyDead = false;
		actionTimer = 0;
		randomWalk = true;
	}*/
	public NPC(int _npcId, int _npcType, NPCDefinitions definition) {
		super(_npcId, definition.getNpcName());
		this.definition = definition;
		npcType = _npcType;
		direction = -1;
		isDead = false;
		applyDead = false;
		actionTimer = 0;
		randomWalk = true;
		this.stats = NPCCombatStats.getStatsFor(_npcId);
		if (definition != null) {
			this.combatScript = CombatScriptHandler.getScript(definition);
			if (this.combatScript != null) {
				this.combatScript.init(this);
			}
		}		
	}


	/**
	 * Checks if a specific coordinate is underneath the NPC's footprint.
	 * ZERO MEMORY ALLOCATION.
	 */
	public boolean insideOf(int x, int y) {
		int size = getSize();
		return x >= getX() && x < getX() + size && y >= getY() && y < getY() + size;
	}

	public int getOffset() {
		return (int) Math.floor(NPCHandler.getNpcDef()[this.npcType].size / 2);
	}

	/**
	 * Gets the exact distance from this actor.
	 * 
	 * @param x
	 * @param y
	 * @return the exact distance between ponits.
	 */
	public double getDistance(int x, int y) {
		if (insideOf(x, y)) {
			return 0;
		}

		int size = getSize();
		int minX = getX();
		int maxX = minX + size - 1;
		int minY = getY();
		int maxY = minY + size - 1;

		// Calculate the shortest delta X and Y to the bounding box
		int dx = Math.max(0, Math.max(minX - x, x - maxX));
		int dy = Math.max(0, Math.max(minY - y, y - maxY));

		return Math.sqrt(dx * dx + dy * dy);
	}
	/**
	 * Gets the border around the edges of the actor.
	 * 
	 * @return the border around the edges of the actor, depending on the actor's
	 *         size.
	 */
	public Point[] getBorder() {
		int x = getX();
		int y = getY();
		int size = getSize();
		if (size <= 1) {
			return new Point[] { new Point(x, y) };
		}

		Point[] border = new Point[(size) + (size - 1) + (size - 1) + (size - 2)];
		int j = 0;

		border[0] = new Point(x, y);

		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < (i < 3 ? (i == 0 || i == 2 ? size : size) - 1
					: (i == 0 || i == 2 ? size : size) - 2); k++) {
				if (i == 0)
					x++;
				else if (i == 1)
					y++;
				else if (i == 2)
					x--;
				else if (i == 3) {
					y--;
				}
				border[(++j)] = new Point(x, y);
			}
		}

		return border;
	}
	public void updateNPCMovement(Buffer str) {
		if (direction == -1) {
			if (updateRequired) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else { //walking only
			str.writeBits(1, 1);
			str.writeBits(2, running ? 2 : 1);
			str.writeBits(3, Misc.xlateDirectionToClient[direction]);
			if (running)
				str.writeBits(3, Misc.xlateDirectionToClient[direction]);
			str.writeBits(1, updateRequired ? 1 : 0);
		}
	}
/**
 * Sends the request to a client that the npc should be transformed into
 * another.
 * 
 *
 *            the id of the new npc
 */
public void requestTransform(int id) {
	transformId = id;
	npcType = id;
	this.stats = NPCCombatStats.getStatsFor(npcType);
	transformUpdateRequired = true;
	updateRequired = true;
}

	public boolean transformUpdateRequired = false, isTransformed = false;
	public int transformId;
	public void shearSheep(Player c, int itemNeeded, int itemGiven, int animation, final int currentId, final int newId, int transformTime) {
		if (!c.getItems().playerHasItem(itemNeeded)) {
			c.sendMessage("You need " + ItemAssistant.getItemName(itemNeeded).toLowerCase() + " to do that.");
			return;
		}
		if (transformId == newId) {
			c.sendMessage("This sheep has already been shorn.");
			return;
		}
		if (NPCHandler.npcs[getIndex()].isTransformed) {
			return;
		}
		if (animation > 0) {
			c.startAnimation(animation);
		}
		requestTransform(newId);
		c.getItems().addItem(itemGiven, 1);
		c.sendMessage("You get some " + ItemAssistant.getItemName(itemGiven).toLowerCase() + ".");
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				requestTransform(currentId);
				container.stop();
			}

			@Override
			public void stop() {
				NPCHandler.npcs[getIndex()].isTransformed = false;
			}
		}, transformTime);
	}
	public void appendTransformUpdate(Buffer str) {
    	str.writeWordBigEndianA(transformId);
	}
	/**
	 * Text update
	 **/

	public void forceChat(String text) {
		forcedText = text;
		forcedChatRequired = true;
		updateRequired = true;
	}

	/**
	 * Graphics
	 **/

	public int mask80var1 = 0;
	public int mask80var2 = 0;
	protected boolean mask80update = false;

	public void appendMask80Update(Buffer str) {
		str.writeWord(mask80var1);
		str.writeDWord(mask80var2);
	}

	public void gfx100(int gfx) {
		mask80var1 = gfx;
		mask80var2 = 6553600;
		mask80update = true;
		updateRequired = true;
	}

	public void gfx100(int gfx, int height) {
		mask80var1 = gfx;
		mask80var2 = 65536 * height;
		mask80update = true;
		updateRequired = true;
	}
	public void gfx0(int gfx) {
		mask80var1 = gfx;
		mask80var2 = 65536;
		mask80update = true;
		updateRequired = true;
	}

	public void appendAnimUpdate(Buffer str) {
		str.writeWordBigEndian(animNumber);
		str.writeByte(1);
	}

	public NPCDefinitions getDefinition() {
		return definition;
	}
	private NPCDefinitions definition;
	
	public int getSize() {
		if (definition == null)
			return 1;
		return definition.getSize();
	}
	/**
	 * 
	 Face
	 * 
	 **/

	public int FocusPointX = -1, FocusPointY = -1;
	public int face = 0;

	private void appendSetFocusDestination(Buffer str) {
		str.writeWordBigEndian(FocusPointX);
		str.writeWordBigEndian(FocusPointY);
	}

	public void turnNpc(int i, int j) {
		FocusPointX = 2 * i + 1;
		FocusPointY = 2 * j + 1;
		updateRequired = true;

	}

	public void appendFaceEntity(Buffer str) {
		str.writeWord(face);
		str.writeWord(summonedBy);
	}

	public void faceEntity(int index) {
		if (!facePlayer) {
			if (face == -1) {
				return;
			}
			face = -1;
		} else {
			face = index + 32768;
		}
		dirUpdateRequired = true;
		updateRequired = true;
	}
		public boolean canFacePlayer() {
		return facePlayer;
	}
		/*public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}*/


	public int getHeight() {
		return height;
	}

	public void teleport(int x, int y, int z) {
		teleporting = true;
		this.setX(x);
		this.setY(y);
		setHeight(z);
	}
	/**
	 * Makes the npcs either able or unable to face other players
	 * 
	 * @param facePlayer
	 *            {@code true} if the npc can face players
	 */
	public void setFacePlayer(boolean facePlayer) {
		this.facePlayer = facePlayer;
	}
	private boolean facePlayer = true;
	public void faceEntity(Entity entity) {
		if (!facePlayer || entity == null) {
			if (face == -1) {
				return;
			}
			face = -1;
		} else {
			face = entity.getIndex() + (entity.isPlayer() ? 32768 : 0);
		}
		dirUpdateRequired = true;
		updateRequired = true;
	}
	public void facePlayer(int player) {
		boolean face2 = true;
		if(npcType > 308 && npcType < 335)
			face2 = false;
		if(face2){
			face = player + 32768;
			dirUpdateRequired = true;
			updateRequired = true;
		}
	}

	public void appendFaceToUpdate(Buffer str) {
		str.writeWordBigEndian(viewX);
		str.writeWordBigEndian(viewY);
	}
	public void appendNPCUpdateBlock(Buffer str) {
		if (!updateRequired)
			return;
		int updateMask = 0;
		if (animUpdateRequired)
			updateMask |= 0x10;
		if (hitUpdateRequired2)
			updateMask |= 8;
		if (mask80update)
			updateMask |= 0x80;
		if (dirUpdateRequired)
			updateMask |= 0x20;
		if (forcedChatRequired)
			updateMask |= 1;
		if (hitUpdateRequired)
			updateMask |= 0x40;
		if (FocusPointX != -1)
			updateMask |= 4;
		if (transformUpdateRequired)
			updateMask |= 2;
		str.writeByte(updateMask);

		if (animUpdateRequired)
			appendAnimUpdate(str);
		if (hitUpdateRequired2)
			appendHitUpdate2(str);
		if (mask80update)
			appendMask80Update(str);
		if (dirUpdateRequired)
			appendFaceEntity(str);
		if (forcedChatRequired) {
			str.writeString(forcedText);
		}
		if (transformUpdateRequired) {	
			appendTransformUpdate(str);
		}
		if (hitUpdateRequired)
			appendHitUpdate(str);
		if (FocusPointX != -1)
			appendSetFocusDestination(str);

	}

	public void clearUpdateFlags() {
		updateRequired = false;
		forcedChatRequired = false;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		animUpdateRequired = false;
		dirUpdateRequired = false;
		transformUpdateRequired = false;
		mask80update = false;
		forcedText = null;
		moveX = 0;
		moveY = 0;
		direction = -1;
		FocusPointX = -1;
		FocusPointY = -1;
		teleporting = false;
	}

	public int getNextWalkingDirection() {
		int dir;
		dir = Misc.direction(getX(), getY(), (getX() + moveX), (getY() + moveY));
		if (dir == -1)
			return -1;
		if (teleporting)
			return -1;
		dir >>= 1;
		setX(getX() + moveX);
		setY(getY() + moveY);
		return dir;
	}

	public void startAnimation(int animationId) {
		animNumber = animationId;
		animUpdateRequired = true;
		updateRequired = true;
	}

	public void getNextNPCMovement() {
		direction = -1;
		if (freezeTimer == 0) {
			direction = getNextWalkingDirection();
		
		}
		updateRequired = true;
	}
	private boolean globalRoaming = false;
	private int roamTargetX = -1, roamTargetY = -1;

	public boolean isGlobalRoaming() {
	    return globalRoaming;
	}

	public void setGlobalRoaming(boolean roam) {
	    globalRoaming = roam;
	}

	public int getRoamTargetX() {
	    return roamTargetX;
	}

	public int getRoamTargetY() {
	    return roamTargetY;
	}

	public void setRoamTarget(int x, int y) {
	    roamTargetX = x;
	    roamTargetY = y;
	}

	public boolean hasWalkQueue() {
	    return !walkQueue.isEmpty();
	}

	private Queue<Location> walkQueue = new LinkedList<>();

	public void setWalkQueue(List<Location> path) {
		walkQueue.clear();
		walkQueue.addAll(path);
	}
	public Queue<Location> getWalkQueue() {
		return walkQueue;
	}
	public Location pollWalkStep() {
		return walkQueue.poll();
	}

	public boolean hasWalkSteps() {
		return !walkQueue.isEmpty();
	}

	public Location nextWalkStep() {
	    return walkQueue.poll();
	}

	@Override
	/*public void appendHitUpdate(Buffer str) {
		if (getHealth().getAmount() <= 0) {
			isDead = true;
		}
		if (hitmark1 != null && !hitmark1.isMiss() && hitDiff == 0) {
			hitDiff = 0;
			hitmark1 = Hitmark.MISS;
		}
		str.writeByteC(hitDiff);
		if (hitmark1 != null) {
			str.writeByteS(hitmark1.getId());
		} else {
			str.writeByteS(0);
		}
		str.writeWord(health.getAmount());
		str.writeWord(health.getMaximum());
	}*/
	public void appendHitUpdate(Buffer str) {
		if (getHealth().getAmount() <= 0) {
			isDead = true;
		}
		if (hitmark1 != null && !hitmark1.isMiss() && hitDiff == 0) {
			hitDiff = 0;
			hitmark1 = Hitmark.MISS;
		}
		str.writeByteC(hitDiff);
		if (hitmark1 != null) {
			str.writeByteS(hitmark1.getId());
		} else {
			str.writeByteS(0);
		}
		str.writeWord(getHealth().getAmount());
		str.writeWord(getHealth().getMaximum());
	}
	public int hitDiff2 = 0;
	public boolean hitUpdateRequired2 = false;
	@Override
	/*public void appendHitUpdate2(Buffer str) {
		if (getHealth().getAmount() <= 0) {
			isDead = true;
		}
		if (hitmark2 != null && !hitmark2.isMiss() && hitDiff2 == 0) {
			hitDiff2 = 0;
			hitmark2 = Hitmark.MISS;
		}
		str.writeByteA(hitDiff2);
		if (hitmark2 != null) {
			str.writeByteC(hitmark2.getId());
		} else {
			str.writeByteC(0);
		}
		str.writeWord(getHealth().getAmount());
		str.writeWord(getHealth().getMaximum());
	}*/
	public void appendHitUpdate2(Buffer str) {

		if (getHealth().getAmount() <= 0) {
			isDead = true;
		}
		if (hitmark2 != null && !hitmark2.isMiss() && hitDiff2 == 0) {
			hitDiff2 = 0;
			hitmark2 = Hitmark.MISS;
		}
		str.writeByteA(hitDiff2);
		if (hitmark2 != null) {
			str.writeByteC(hitmark2.getId());
		} else {
			str.writeByteC(0);
		}
		str.writeByteA(getHealth().getAmount());
		str.writeByte(getHealth().getMaximum());
	}
	public int appendDamage(Entity player, int damage, Hitmark h) {
		appendDamage(damage, h);
		addDamageTaken(player, damage);
		return damage;
	}
	public void handleHitMask(int damage) {
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			hitDiff = damage;
		} else if (!hitUpdateRequired2) {
			hitUpdateRequired2 = true;
			hitDiff2 = damage;
		}
		updateRequired = true;
	}
	public void appendDamage(int damage, Hitmark hitmark) {
		if (damage < 0) {
			damage = 0;
			hitmark = Hitmark.MISS;
		}
		if (hitmark == Hitmark.HEAL_PURPLE) {
			getHealth().increase(damage);
		}
		if (getHealth().getAmount() - damage < 0) {
			damage = getHealth().getAmount();
		}
		if (hitmark != Hitmark.HEAL_PURPLE)
			getHealth().reduce(damage);
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			hitDiff = damage;
			hitmark1 = hitmark;
		} else if (!hitUpdateRequired2) {
			hitUpdateRequired2 = true;
			hitDiff2 = damage;
			hitmark2 = hitmark;
		}
		updateRequired = true;
	}
	public int appendDamage(Entity source, Damage damage) {
		if (combatScript != null) {
			damage.setAmount((int) Math.round(damage.getAmount() * combatScript.getDamageReduction(this)));
			combatScript.handleRecievedHit(this, source, damage);
		}
		appendDamage(damage.getAmount(), damage.getHitmark());
		addDamageTaken(source, damage.getAmount());
		return damage.getAmount();
	}

	public int getH() {
		return heightLevel;
	}

			public boolean inMulti() {
				if((getX() >= 3136 && getX() <= 3327 && getY() >= 3519 && getY() <= 3607) || 
				(getX() >= 3190 && getX() <= 3327 && getY() >= 3648 && getY() <= 3839) ||  
				(getX() >= 3200 && getX() <= 3390 && getY() >= 3840 && getY() <= 3967) || 
				(getX() >= 2992 && getX() <= 3007 && getY() >= 3912 && getY() <= 3967) || 
				(getX() >= 2946 && getX() <= 2959 && getY() >= 3816 && getY() <= 3831) || 
				(getX() >= 3008 && getX() <= 3199 && getY() >= 3856 && getY() <= 3903) || 
				(getX() >= 3008 && getX() <= 3071 && getY() >= 3600 && getY() <= 3711) || 
				(getX() >= 3072 && getX() <= 3327 && getY() >= 3608 && getY() <= 3647) ||
				(getX() >= 2624 && getX() <= 2690 && getY() >= 2550 && getY() <= 2619) ||
				(getX() >= 2371 && getX() <= 2422 && getY() >= 5062 && getY() <= 5117) ||
				(getX() >= 2896 && getX() <= 2927 && getY() >= 3595 && getY() <= 3630) ||
				(getX() >= 2892 && getX() <= 2932 && getY() >= 4435 && getY() <= 4464) ||
				(getX() >= 2256 && getX() <= 2287 && getY() >= 4680 && getY() <= 4711) ||
				(getX() >= 2862 && getX() <= 2876 && getY() >= 5351 && getY() <= 5369) ||
				(getX() >= 2918 && getX() <= 2936 && getY() <= 5331 && getY() >= 5318) ||
				(getX() >= 2842 && getX() <= 2889 && getY() <= 5296 && getY() >= 5258) ||
				(getX() >= 2907 && getX() <= 2889 && getY() <= 5276 && getY() >= 5258) ||
				(getX() <= 2943 && getX() >= 2815 && getY() <= 5375 && getY() >= 5246) ||
				(getX() <= 2959 && getX() >= 2914 && getY() <= 4406 && getY() >= 4357) ||
				(getX() <= 2434 && getX() >= 2365 && getY() <= 3138 && getY() >= 3069) || inBandosGWD()
				|| Boundary.isIn(this, Boundary.CATACOMBS) || Boundary.isIn(this, Boundary.ZEAH_BOUNDARY)) {
					return true;
				}
				return false;
			}
public boolean inZammyGWD() {		
		if(getX() <= 2918 && getX() >= 2936 && getY() >= 5331 && getY() <= 5318) {
			return true;
		}
		return false;
	};
	public boolean inArmadylGWD() {		
		if(getX() <= 2842 && getX() >= 2824 && getY() >= 5296 && getY() <= 5308) {
			return true;
		}
		return false;
	}
	public boolean inBandosGWD() {		
		if(getX() >= 2864 && getX() <= 2876 && getY() >= 5351 && getY() <= 5369) {
			return true;
		}
		return false;
	}
	public boolean inSaraGWD() {		
		if(getX() <= 2907 && getX() >= 2889 && getY() <= 5276 && getY() >= 5258) {
			return true;
		}
		return false;
	}
	public boolean inGodWars() {		
		if(getX() <= 2953 && getX() >= 2816 && getY() <= 5368 && getY() >= 5248) {
			return true;
		}
		return false;
	}
	public boolean inWild() {
		if (getX() > 2941 && getX() < 3392 && getY() > 3525 && getY() < 3966
				|| getX() > 2941 && getX() < 3392 && getY() > 9918 && getY() < 10366) {
			return true;
		}
		return false;
	}
	
	public long getLastRandomWalk() {
		return lastRandomWalk;
	}

	public long getLastRandomWalkhome() {
		return lastRandomWalkHome;
	}

	public void setLastRandomWalkHome(long lastRandomWalkHome) {
		this.lastRandomWalkHome = lastRandomWalkHome;
	}

	public long getRandomStopDelay() {
		return randomStopDelay;
	}

	public void setRandomStopDelay(long randomStopDelay) {
		this.randomStopDelay = randomStopDelay;
	}

	public void setLastRandomWalk(long lastRandomWalk) {
		this.lastRandomWalk = lastRandomWalk;
	}

	public long getRandomWalkDelay() {
		return randomWalkDelay;
	}

	public void setRandomWalkDelay(long randomWalkDelay) {
		this.randomWalkDelay = randomWalkDelay;
	}

	public void setNoRespawn(boolean b) {
		this.noRespawn = b;
	}
	
	public boolean isNoRespawn() {
		return noRespawn;
	}
	private long walkPauseTime = 0L;

	public long getWalkPauseTime() {
		return walkPauseTime;
	}

	public void setWalkPauseTime(long time) {
		this.walkPauseTime = time;
	}

	private boolean noRespawn;

	private boolean running;
	public boolean isDragon() {
		switch (npcType) {
		case 137:
		case 139:
		case 239:
		case 241:
		case 242:
		case 243:
		case 244:
		case 245:
		case 246:
		case 247:
		case 248:
		case 249:
		case 250:
		case 251:
		case 252:
		case 253:
		case 254:
		case 255:
		case 256:
		case 257:
		case 258:
		case 259:
		case 260:
		case 261:
		case 262:
		case 263:
		case 264:
		case 265:
		case 266:
		case 267:
		case 268:
		case 269:
		case 270:
		case 271:
		case 272:
		case 273:
		case 274:
		case 275:
		case 1871:
		case 1872:
		case 2642:
		case 2918:
		case 2919:
		case 4000:
		case 4385:
		case 5194:
		case 5872:
		case 5873:
		case 5878:
		case 5879:
		case 5880:
		case 5881:
		case 5882:
		case 6500:
		case 6501:
		case 6502:
		case 6593:
		case 6636:
		case 6652:
		case 7039:
		case 7253:
		case 7254:
		case 7255:
		case 7273:
		case 7274:
		case 7275:
		case 8027:
		case 7553:
		case 7554:
		case 7555:
		case 8609:
			return true;
		}
		return false;
	}


	@Override
	public boolean susceptibleTo(HealthStatus status) {
		switch (npcType) {
		case 2042:
		case 2043:
		case 2044:
		case 6720:
		case 7413:
		case 7544:
		case 5129:
		case 4922:
		case 7604:
		case 7605:
		case 7606:
			return false;
		}
		return true;
	}

	public void kill() {
		isDead = true;
		applyDead = true;
		actionTimer = 0;
	}
}
