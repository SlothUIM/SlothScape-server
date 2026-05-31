package server.model.npcs;

import java.io.FileInputStream;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import server.util.XStreamUtil;

public class NPCDefinition {

	private static NPCDefinition[] definitions = null;

	  public static void init() throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        try {
            Document document = builder.parse(new FileInputStream("./Data/cfg/npcDefinitions.xml"));
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("npcDefinition");
            definitions = new NPCDefinition[3790];

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    int size = Integer.parseInt(element.getElementsByTagName("size").item(0).getTextContent());
                    // Extract other attributes similarly

                    NPCDefinition def = new NPCDefinition();
                    def.setId(id);
                    def.setSize(size);
                    // Set other attributes
					//System.out.println("ID: " + id +" Size: "+size);
                    definitions[id] = def;
                }
            }
        } catch (SAXException e) {
            // Handle SAXException (e.g., log or rethrow as IOException)
            throw new IOException("Error parsing XML", e);
        }
    }

	public static NPCDefinition forId(int id) {
		NPCDefinition d = definitions[id];
		if (d == null) {
			d = produceDefinition(id);
		}
		return d;
	}

	private int id;
	private String name, examine;
	private int respawn = 0, combat = 0, hitpoints = 1, maxHit = 0, size = 1, attackSpeed = 4000, attackAnim = 422, defenceAnim = 404, deathAnim = 2304, attackBonus = 20, defenceMelee = 20, defenceRange = 20, defenceMage = 20;

	private boolean attackable = false;
	private boolean aggressive = false;
	private boolean retreats = false;
	private boolean poisonous = false;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
    public void setSize(int id) {
        this.id = id;
    }
    public void setId(int size) {
        this.size = size;
    }
	public String getExamine() {
		return examine;
	}

	public int getRespawn() {
		return respawn;
	}

	public int getCombat() {
		return combat;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public int getMaxHit() {
		return maxHit;
	}

	public int getSize() {
		return size;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	public boolean retreats() {
		return retreats;
	}

	public boolean isPoisonous() {
		return poisonous;
	}

	public static NPCDefinition produceDefinition(int id) {
		NPCDefinition def = new NPCDefinition();
		def.id = id;
		def.name = "NPC #" + def.id;
		def.examine = "It's an NPC.";
		return def;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public int getAttackAnimation() {
		return attackAnim;
	}

	public int getDefenceAnimation() {
		return defenceAnim;
	}

	public int getDeathAnimation() {
		return deathAnim;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public int getAttackBonus() {
		return attackBonus;
	}

	public int getDefenceRange() {
		return defenceRange;
	}

	public int getDefenceMelee() {
		return defenceMelee;
	}

	public int getDefenceMage() {
		return defenceMage;
	}

}
