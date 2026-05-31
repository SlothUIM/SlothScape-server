package server.model.players.packets;

import server.model.players.Player;
import server.model.players.PacketType;

/**
 * Change appearance
 **/

public class ChangeAppearance implements PacketType {

	private static final int[][] MALE_VALUES = { { 0, 8 }, // head
			{ 10, 17 }, // jaw
			{ 18, 25 }, // torso
			{ 26, 31 }, // arms
			{ 33, 34 }, // hands
			{ 36, 40 }, // legs
			{ 42, 43 }, // feet
	};

	private static final int[][] FEMALE_VALUES = { { 45, 54 }, // head
			{ -1, -1 }, // jaw
			{ 56, 60 }, // torso
			{ 61, 65 }, // arms
			{ 67, 68 }, // hands
			{ 70, 77 }, // legs
			{ 79, 80 }, // feet
	};

	private static final int[][] ALLOWED_COLORS = { { 0, 11 }, // hair color
			{ 0, 15 }, // torso color
			{ 0, 15 }, // legs color
			{ 0, 5 }, // feet color
			{ 0, 7 } // skin color
	};

	@Override
	public void processPacket(final Player c, final int packetType, final int packetSize) {
		int gender = c.getInStream().readUnsignedByte();
		int head = c.getInStream().readUnsignedByte();
		int jaw = c.getInStream().readUnsignedByte();
		int torso = c.getInStream().readUnsignedByte();
		int arms = c.getInStream().readUnsignedByte();
		int hands = c.getInStream().readUnsignedByte();
		int legs = c.getInStream().readUnsignedByte();
		int feet = c.getInStream().readUnsignedByte();
		int hairColour = c.getInStream().readUnsignedByte();
		int torsoColour = c.getInStream().readUnsignedByte();
		int legsColour = c.getInStream().readUnsignedByte();
		int feetColour = c.getInStream().readUnsignedByte();
		int skinColour = c.getInStream().readUnsignedByte();
System.out.println(head+" | | "+torso+" | | "+legs+" | | "+arms+" | | "+gender
		+" | | "+hands+" | | "+jaw);
      			c.playerAppearance[0] = gender; // gender
      			c.playerAppearance[6] = feet; // feet
      			c.playerAppearance[7] = Math.abs(jaw); // beard
      			c.playerAppearance[8] = hairColour; // hair colour
      			c.playerAppearance[9] = torsoColour; // torso colour
      			c.playerAppearance[10] = legsColour; // legs colour
      			c.playerAppearance[11] = feetColour; // feet colour
      			c.playerAppearance[12] = skinColour; // skin colour
				if(head < 0) // head
      c.playerAppearance[1] = head + 256;
      else
      c.playerAppearance[1] = head;
      if(torso < 0)
      c.playerAppearance[2] = torso + 256;
      else
      c.playerAppearance[2] = torso;
      if(arms < 0)
      c.playerAppearance[3] = arms + 256;
      else
      c.playerAppearance[3] = arms;
      if(hands < 0)
      c.playerAppearance[4] = hands + 256;
      else
      c.playerAppearance[4] = hands;
      if(legs < 0)
      c.playerAppearance[5] = legs + 256;
      else
      c.playerAppearance[5] = legs;
      			//private int[] topStyles = { 457, 445, 459, 460, 461, 462, 463, 464, 446, 465, 466, 467, 451, 468, 453, 454, 455, 469, 470, 450, 458, 447, 448,
      					//449, 471, 443, 472, 473, 444, 474, 456, 111, 113, 114, 115, 112, 116, 18, 19, 20, 21, 22, 23, 24, 25 };
      				//595 = theif arms
      			
      			//452 = theif top
      			//451 = bearskin top
      			//450 = longstride top
      			//449 = warlock top
      			//612 = warlock arms
      			//592 = waxed arms
      			c.getPA().removeAllWindows();
      				c.getPA().requestUpdates();
  			}  

}