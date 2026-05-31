package server.model.items.containers;

import server.model.players.Player;

public class ArmourCase extends CostumeRoomContainer {
//2496_0
	public enum ArmourSet implements CostumeData {
        // A
        ANCIENT_HALO(29241, 29242, new int[]{24201}, "Ancient halo"),
        ANGLERS_OUTFIT(29245, 29246, new int[]{13258, 13259, 13260, 13261}, "Angler's outfit"),
        ARDOUGNE_KNIGHT(29249, 29250, new int[]{23785, 23787, 23789}, "Ardougne knight"),
        ARMADYL_HALO(29253, 29254, new int[]{24192}, "Armadyl halo"),
        // B
        BANDOS_HALO(29257, 29258, new int[]{24195}, "Bandos halo"),
        BOMBER_JACKET(29261, 29262, new int[]{9945, 9944}, "Bomber jacket"),
        BUILDERS_COSTUME(29265, 29266, new int[]{10862, 10863, 10864, 10865}, "Builder's costume"),
        // C
        CASTLE_WARS_1(29269, 29270, new int[]{4069, 4070, 4071, 4072, 4503, 4504, 4505}, "Castle wars (1)"),
        CASTLE_WARS_2(29273, 29274, new int[]{4508, 4509, 4510, 4511, 4512, 4513, 4514}, "Castle wars (2)"),
        CASTLE_WARS_3(29277, 29278, new int[]{4517, 4518, 4519, 4520, 4521, 4522, 4523}, "Castle wars (3)"),
        CW_SWORD_1(29281, 29282, new int[]{4068}, "CW Sword (1)"),
        CW_SWORD_2(29285, 29286, new int[]{4503}, "CW Sword (2)"),
        CW_SWORD_3(29289, 29290, new int[]{4508}, "CW Sword (3)"),
        CHOMPY_HATS(29293, 29294, new int[]{2978, 2979, 2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987, 2988, 2989, 2990, 2991, 2992, 2993, 2994, 2995, 13071, 13072, 13073}, "Chompy hats"),
        // D & E
        DESERT_CAMO(29297, 29298, new int[]{10061, 10063}, "Desert camo"),
        ELITE_BLACK(29301, 29302, new int[]{13642, 13640, 13644}, "Elite black"),
        // F & G
        FARMERS_OUTFIT(29305, 29306, new int[]{13642, 13640, 13644, 13646}, "Farmer's outfit"),
        GRAAHK_HUNTER(29309, 29310, new int[]{10047, 10049, 10051}, "Graahk hunter"),
        GUTHIX_HALO(29313, 29314, new int[]{12639}, "Guthix halo"),
        HAM_ROBES(29317, 29318, new int[]{4298, 4300, 4302, 4304, 4306, 4308, 4310}, "Ham robes"),
        // H, I, J
        HELM_OF_RAEDWALD(29321, 29322, new int[]{19687}, "Helm of raedwald"),
        INITIATE_ARMOUR(29325, 29326, new int[]{5574, 5575, 5576}, "Initiate armour"),
        INQUISITORS(29329, 29330, new int[]{24419, 24420, 24421}, "Inquisitor's"),
        JUNGLE_CAMO(29333, 29334, new int[]{10057, 10059}, "Jungle camo"),
        JUSTICIAR(29337, 29338, new int[]{22326, 22327, 22328}, "Justiciar"),
        // K, L, M
        KYATT_HUNTER(29341, 29342, new int[]{10035, 10037, 10039}, "Kyatt hunter"),
        LARUPIA_HUNTER(29345, 29346, new int[]{10041, 10043, 10045}, "Larupia hunter"),
        LUMBERJACK(29349, 29350, new int[]{10939, 10940, 10941, 10933}, "Lumberjack"),
        MINING_GLOVES(29353, 29354, new int[]{21343, 21392, 21345}, "Mining gloves"),
        MOURNER_GEAR(29357, 29358, new int[]{6065, 6066, 6067, 6068, 6069, 6070}, "Mourner gear"),
        // O & P
        OBSIDIAN_ARMOUR(29361, 29362, new int[]{21298, 21301, 21304}, "Obsidian armour"),
        PENANCE_GLOVES(29365, 29366, new int[]{10547}, "Penance gloves"),
        FIGHTER_HAT(29369, 29370, new int[]{10548}, "Fighter hat"),
        FIGHTER_TORSO(29373, 29374, new int[]{10551}, "Fighter torso"),
        HEALER_HAT(29377, 29378, new int[]{10550}, "Healer hat"),
        RANGER_HAT(29381, 29382, new int[]{10549}, "Ranger hat"),
        RUNNER_BOOTS(29385, 29386, new int[]{10552}, "Runner boots"),
        RUNNER_HAT(29389, 29390, new int[]{10554}, "Runner hat"),
        PENANCE_SKIRT(29393, 29394, new int[]{10555}, "Penance skirt"),
        POLAR_CAMO(29397, 29398, new int[]{10065, 10067}, "Polar camo"),
        PROSPECTOR(29401, 29402, new int[]{12013, 12014, 12015, 12016}, "Prospector"),
        PROSELYTE(29405, 29406, new int[]{9672, 9674, 9676, 9678}, "Proselyte"),
        // R & S
        ROCK_SHELL(29409, 29410, new int[]{6128, 6129, 6130, 6145, 6151}, "Rock-shell"),
        ROGUE_EQUIPMENT(29413, 29414, new int[]{5553, 5554, 5555, 5556, 5557}, "Rogue equipment"),
        SARA_HALO(29417, 29418, new int[]{12637}, "Saradomin halo"),
        SHAYZIEN_T1(29421, 29422, new int[]{13359, 13361, 13360, 13358, 13357}, "Shayzien Armour(T1)"),
        SHAYZIEN_T2(29425, 29426, new int[]{13364, 13366, 13365, 13363, 13362}, "Shayzien Armour(T2)"),
        SHAYZIEN_T3(29429, 29430, new int[]{13369, 13371, 13370, 13368, 13367}, "Shayzien Armour(T3)"),
        SHAYZIEN_T4(29433, 29434, new int[]{13374, 13376, 13375, 13373, 13372}, "Shayzien Armour(T4)"),
        SHAYZIEN_T5(29437, 29438, new int[]{13379, 13381, 13380, 13378, 13377}, "Shayzien Armour(T5)"),
        SNAKESKIN(29441, 29442, new int[]{6322, 6324, 6326, 6328, 6330}, "Snakeskin"),
        SPINED_ARMOUR(29445, 29446, new int[]{6131, 6133, 6135, 6143, 6149}, "Spined armour"),
        STRONGHOLD_BOOTS(29449, 29450, new int[]{9005, 9006}, "Stronghold boots"),
        // T, V, W, X, Z
        TRIBAL_MASK_C(29453, 29454, new int[]{6317}, "Tribal mask (C)"),
        TRIBAL_MASK_D(29457, 29458, new int[]{6315}, "Tribal mask (D)"),
        TRIBAL_MASK_P(29461, 29462, new int[]{6313}, "Tribal mask (P)"),
        TWISTED_CANE(29465, 29466, new int[]{24128}, "Twisted cane"),
        VOID_KNIGHT(29469, 29470, new int[]{8839, 8840, 8842}, "Void knight"),
        ELITE_VOID(29473, 29474, new int[]{13072, 13073, 8842}, "Elite void"),
        VOID_MAGE_HELM(29477, 29478, new int[]{11663}, "Void mage helm"),
        VOID_MAGE_OR(29481, 29482, new int[]{24183}, "Void mage (i)"),
        VOID_MELEE_HELM(29485, 29486, new int[]{11665}, "Void melee helm"),
        VOID_MELEE_OR(29489, 29490, new int[]{24185}, "Void melee (i)"),
        VOID_RANGER_HELM(29493, 29494, new int[]{11664}, "Void ranger helm"),
        VOID_RANGER_OR(29497, 29498, new int[]{24184}, "Void ranger (i)"),
        VYRE_NOBLE(29501, 29502, new int[]{9634, 9636, 9638}, "Vyre noble"),
        WHITE_KNIGHT(29505, 29506, new int[]{6623, 6617, 6625, 6629, 6633, 6609, 6611}, "White knight"),
        WOODLAND_CAMO(29509, 29510, new int[]{6657, 6658}, "Woodland camo"),
        XERICIAN_ROBES(29513, 29514, new int[]{13385, 13387, 13389}, "Xerician robes"),
        ZAMORAK_HALO(29517, 29518, new int[]{12632}, "Zamorak halo");

        // ... Keep the rest of your enum code exactly the same ...

        private final int labelId, itemInterfaceId;
        private final int[] items;
        private final String name;

        ArmourSet(int labelId, int itemInterfaceId, int[] items, String name) {
            this.labelId = labelId;
            this.itemInterfaceId = itemInterfaceId;
            this.items = items;
            this.name = name;
        }

        @Override public int getLabelId() { return labelId; }
        @Override public int getItemInterfaceId() { return itemInterfaceId; }
        @Override public int[] getItems() { return items; }
        @Override public String getName() { return name; }
        @Override public boolean contains(int itemId) {
            for (int id : items) if (id == itemId) return true;
            return false;
        }
    }

    @Override public String getContainerName() { return "Armour Case"; }
    @Override public int getInterfaceId() { return 29236; }
    @Override public int getTitleId() { return 29238; }
    @Override public int getInventoryId() { return 5063; }
    @Override public CostumeData[] getCostumeData() { return ArmourSet.values(); }

    @Override
    public int getMaxSets(Player c) {
        // Enforce POH Tier Limits for Armour Case
        switch(c.getFancyBox().getDressBoxTier()) {
            case 1: return 5;  // Oak
            case 2: return 10; // Teak
            case 3: return 74; // Mahogany
            default: return 0;
        }
    }
}