package server.model.players.content.treasuretrails.types;

public class ChallengeClue {

    // All the IDs you provided
    public static final int[] MEDIUM_CHALLENGES = {
        2842, 2844, 2846, 2850, 2852, 2854, 7275, 7277, 7279, 7281, 7283, 7285, 12056, 12058, 12060, 12062, 
        12064, 12066, 12068, 12070, 12072, 19735, 19737, 19739, 19741, 19743, 19745, 19747, 19749, 19751, 
        19753, 19755, 19757, 19759, 19763, 19765, 19767, 19769, 19771, 19773, 23132, 23134, 25785
    };

    public static final int[] HARD_CHALLENGES = {
        7269, 7271, 7273, 12567, 12569, 12571, 12573, 12575, 12577, 19847, 19855, 19859, 19885, 19893, 
        19899, 19907
    };

    public enum ChallengeData {
        IRONMAN_TUTOR("Ironman tutor", "How many snakeskins are needed in order to craft 44 boots, 29 vambraces and 34 bandanas?", 666),
        ALI_THE_KEBAB_SELLER("Ali the Kebab seller", "How many coins would you need to purchase 133 kebabs from me?", 399),
        AMBASSADOR_ALVIJAR("Ambassador Alvijar", "Double the miles before the initial Dorgeshuun veteran.", 2505),
        ARETHA("Aretha", "32 - 5x = 22, what is x?", 2),
        AWOWOGEI("Awowogei", "If I have 303 bananas, and share them between 31 friends evenly, only handing out full bananas. How many will I have left over?", 24),
        BARAEK("Baraek", "How many stalls are there in Varrock Square?", 5),
        BOLKOY("Bolkoy", "How many flowers are there in the clearing below this platform?", 13),
        BROTHER_KOJO("Brother Kojo", "On a clock, how many times a day do the minute hand and the hour hand overlap?", 22),
        BROTHER_OMAD("Brother Omad", "What is the next number? 12, 13, 15, 17, 111, 113, 117, 119, 123....?", 129),
        BROTHER_TRANQUILITY("Brother Tranquility", "If I have 49 bottles of rum to share between 7 pirates, how many would each pirate get?", 7),
        BRUNDT_THE_CHIEFTAIN("Brundt the Chieftain", "How many people are waiting for the next bard to perform?", 5),
        CAM_THE_CAMEL("Cam the Camel", "How many items can carry water in Gielinor?", 6),
        CAPN_IZZY_NO_BEARD("Cap'n Izzy No-Beard's parrot", "How many Banana Trees are there in the plantation?", 33),
        CAPTAIN_GINEA("Captain Ginea", "1 soldier can deal with 6 lizardmen. How many soldiers do we need for an army of 678 lizardmen?", 113),
        CAPTAIN_KHALED("Captain Khaled", "How many fishing cranes can you find around here?", 5),
        CAPTAIN_TOBIAS("Captain Tobias", "How many ships are there docked at Port Sarim currently?", 6),
        CAROLINE("Caroline", "How many fishermen are there on the fishing platform?", 11),
        CHARLIE_THE_TRAMP("Charlie the Tramp", "How many coins would I have if I have 0 coins and attempt to buy 10 loaves of bread for 3 coins each?", 0),
        CLERRIS("Clerris", "If I have 1,000 blood runes, and cast 131 ice barrage spells, how many blood runes do I have left?", 738),
        COOK("Cook", "How many cannons does Lumbridge Castle have?", 9),
        DARK_MAGE("Dark mage", "How many rifts are found here in the abyss?", 13),
        DOCKMASTER("Dockmaster", "What is the cube root of 125?", 5),
        DOMINIC_ONION("Dominic Onion", "How many reward points does a herb box cost?", 9500),
        DOOMSAYER("Doomsayer", "What is 40 divided by 1/2 plus 15?", 95),
        DREZEL("Drezel", "Please solve this for x: 7x - 28=21", 7),
        DRUNKEN_SOLDIER("Drunken soldier", "If 13 Shayzien Soldiers kill 46 Lizardmen each in a day, how many Lizardmen have they killed in total in a single day?", 598),
        DUNSTAN("Dunstan", "How much smithing experience does one receive for smelting a blurite bar?", 8),
        EDMOND("Edmond", "How many pigeon cages are there around the back of Jerico's house?", 3),
        ELUNED("Eluned", "A question on elven crystal math. I have 5 and 3 crystals, large and small respectively. A large crystal is worth 10,000 coins and a small is worth but 1,000. How much are all my crystals worth?", 53000),
        EOHRIC("Eohric", "King Arthur and Merlin sit down at the Round Table with 8 knights. How many degrees does each get?", 36),
        EVIL_DAVE("Evil Dave", "What is 333 multiplied by 2?", 666),
        FAIRY_GODFATHER("Fairy Godfather", "There are 3 inputs and 4 letters on each ring How many total individual fairy ring codes are possible?", 64),
        FATHER_AERECK("Father Aereck", "How many gravestones are in the church graveyard?", 19),
        FLAX_KEEPER("Flax keeper", "If I have 1014 flax, and I spin a third of them into bowstring, how many flax do I have left?", 676),
        GABOOTY("Gabooty", "How many buildings are in the village?", 11),
        GALLOW("Gallow", "How many vine patches can you find in this vinery?", 12),
        GNOME_BALL_REFEREE("Gnome ball referee", "What is 57 x 89 + 23?", 5096),
        GNOME_COACH("Gnome Coach", "How many gnomes on the Gnome ball field have red patches on their uniforms?", 6),
        GUARD_VEMMELDO("Guard Vemmeldo", "How many magic trees can you find inside the Gnome Stronghold?", 3),
        HICKTON("Hickton", "How many ranges are there in Catherby?", 2),
        HORPHIS("Horphis", "On a scale of 1-10, how helpful is Logosia?", 1),
        JARDRIC("Jardric", "What is 3 to the power of 0?", 1),
        JETHICK("Jethick", "How many graves are there in the city graveyard?", 38),
        KARIM("Karim", "I have 16 kebabs, I eat one myself and then share the rest equally between 3 friends. How many do they have each?", 5),
        KAYLEE("Kaylee", "How many chairs are there in the Rising Sun?", 18),
        KING_ROALD("King Roald", "How many bookcases are there in the palace library?", 24),
        LISSE_ISAAKSON("Lisse Isaakson", "How many arctic logs are required to make a large fremennik round shield?", 2),
        MADAME_CALDARIUM("Madame Caldarium", "What is 3(5-3)?", 6),
        MANDRITH("Mandrith", "How many scorpions live under the pit?", 28),
        MARISI("Marisi", "How many cities form the Kingdom of Great Kourend?", 5),
        MARTIN_THWAIT("Martin Thwait", "How many natural fires burn in Rogue's Den?", 2),
        MINER_MAGNUS("Miner Magnus", "How many coal rocks are around here?", 8),
        NICHOLAS("Nicholas", "How many windows are in Tynan's shop?", 4),
        NIEVE("Nieve", "How many farming patches are there in Gnome stronghold?", 2),
        STEVE("Steve", "How many farming patches are there in Gnome stronghold?", 2),
        NURSE_WOONED("Nurse Wooned", "How many wounded soldiers are in the camp?", 16),
        OLD_CRONE("Old crone", "What is the combined combat level of each species that live in Slayer tower?", 619),
        ONEIROMANCER("Oneiromancer", "How many Suqah inhabit Lunar isle?", 25),
        ORACLE("Oracle", "If x is 15 and y is 3 what is 3x + y?", 48),
        ORONWEN("Oronwen", "What is the minimum amount of quest points required to reach Lletya?", 20),
        OTTO_GODBLESSED_1("Otto Godblessed", "How many types of dragon are there beneath the whirlpool's cavern?", 2),
        OTTO_GODBLESSED_2("Otto Godblessed", "How many pyre sites are found around this lake?", 3),
        PROFESSOR_GRACKLEBONE("Professor Gracklebone", "How many round tables can be found on this floor of the library?", 9),
        PROSPECTOR_PERCY("Prospector Percy", "During a party, everyone shook hands with everybody else. There were 66 handshakes. How many people were at the party?", 12),
        REGATH("Regath", "What is -5 to the power of 2?", 25),
        SIGLI_THE_HUNTSMAN("Sigli the Huntsman", "What is the combined slayer requirement of every monster in the slayer cave?", 302),
        SIR_KAY("Sir Kay", "How many fountains are there within the grounds of Camelot castle?", 6),
        SIR_PERCIVAL("Sir Percival", "How many cannons are on this here castle?", 5),
        KING_PERCIVAL("King Percival", "How many cannons are on this here castle?", 5),
        SPIRIT_TREE("Spirit tree", "What is the next number in the sequence? 1, 11, 21, 1211, 111221, 31221113112221", 31221113112221L),
        SQUIRE("Squire", "White Knights of Falador are stronger than the Black Knights of the Kinshra. 2 White Knights can handle 3 Kinshra. How many White Knights would we need against an army of 981 Kinshra?", 654),
        STRANGE_OLD_MAN("Strange Old Man", "One pipe fills a barrel in 1 hour while another pipe can fill the same barrel in 2 hours. How many minutes will it take to fill the tank if both pipes are used?", 40),
        SULISAL("Sulisal", "If a fish can feed four people, and I'm hosting 15 guests, how many whole fish will I need?", 4),
        TARIA("Taria", "How many buildings are there in Rimmington?", 7),
        TEICUH("Teicuh", "If a death rune costs 220 coins, an air rune costs 3 coins, and a water rune costs 4 coins, how many coins do I need to cast Water Blast 17 times?", 4097),
        TRAIBORN("Traiborn", "How many air runes would I need to cast 630 wind waves?", 3150),
        UGLUG_NAR("Uglug Nar", "What is 19 to the power of 3?", 6859),
        WEIRD_OLD_MAN("Weird Old Man", "SIX LEGS! All of them have 6! There are 25 of them! How many legs?", 150),
        WISE_OLD_MAN("Wise Old Man", "How many bookcases are in the Wise Old Man's house?", 28),
        ZOO_KEEPER("Zoo keeper", "How many animals in total are there in the zoo?", 50);

        private final String npcName;
        private final String question;
        private final long answer;

        ChallengeData(String npcName, String question, long answer) {
            this.npcName = npcName;
            this.question = question;
            this.answer = answer;
        }

        public String getNpcName() { return npcName; }
        public String getQuestion() { return question; }
        public long getAnswer() { return answer; }
    }

    /**
     * Checks if the inputted answer matches the expected answer for the specific NPC.
     * (Handles NPCs like Otto who have two potential answers)
     */
    public static boolean isCorrect(String npcName, long inputtedAnswer) {
        for (ChallengeData data : ChallengeData.values()) {
            if (data.getNpcName().equalsIgnoreCase(npcName) && data.getAnswer() == inputtedAnswer) {
                return true;
            }
        }
        return false;
    }
}