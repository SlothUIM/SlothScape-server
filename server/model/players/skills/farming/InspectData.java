package server.model.players.skills.farming;

import java.util.HashMap;
import java.util.Map;

public enum InspectData {

	MARIGOLD(5096, new String[][] {
			{ "The seeds have only just been planted." },
			{ "The marigold plants have developed leaves." },
			{ "The marigold plants have begun to grow their",
					"flowers. The new flowers are orange and small at",
					"first." },
			{ "The marigold plants are larger, and more",
					"developed in their petals." },
			{ "The marigold plants are ready to harvest. Their",
					"flowers are fully matured." } }), ROSEMARY(5097,
			new String[][] {
					{ "The seeds have only just been planted." },
					{ "The rosemary plant is taller than before." },
					{ "The rosemary plant is bushier and taller than",
							"before." },
					{ "The rosemary plant is developing a flower bud at",
							"its top." },
					{ "The plant is ready to harvest. The rosemary",
							"plant's flower has opened." } }), NASTURTIUM(
			5098,
			new String[][] {
					{ "The nasturtium seed has only just been planted." },
					{ "The nasturtium plants have started to develop",
							"leaves." },
					{ "The nasturtium plants have grown more leaves,",
							"and nine flower buds." },
					{ "The nasturtium plants open their flower buds." },
					{
							"The plants are ready to harvest. The nasturtium",
							"plants grow larger than before and the flowers",
							"fully open." } }), WOAD(5099, new String[][] {
			{ "The woad seed has only just been planted." },
			{ "The woad plant produces more stalks, that split",
					"in tow near the top." },
			{ "The woad plant grows more segments from its",
					"intitial stalks." },
			{ "The woad plant develops flower buds on the end",
					"of each of its stalks." },
			{ "The woad plant is ready to harvest. The plant has",
					"all of its stalks pointing directly up, with",
					"all flowers open." } }), LIMPWURT(
			5100,
			new String[][] {
					{ "The seed has only just been planted." },
					{ "The limpwurt plant produces more roots." },
					{ "The limpwurt plant produces an unopened pink",
							"flower bud and continues to grow larger." },
					{ "The limpwurt plant grows larger, with more loops",
							"in its roots. The flower bud is still unopened." },
					{
							"The limpwurt plant is ready to harvest. The",
							"flower finally opens wide, with a spike in the",
							"middle." } }),
	POTATOES(5318, new String[][] {
		{ "The potato seeds have only just been planted." },
		{ "The potato plants have grown to double their",
				"previous height." },
		{ "The potato plants now are the same height as the",
				"surrounding weeds." },
		{ "The potato plants now spread their branches wider,",
				"not growing as much as before." },
		{ "The potato plants are ready to harvest. A white",
				"flower at the top of each plant opens up." } }), ONIONS(
		5319, new String[][] {
				{ "The onion seeds have only just been planted." },
				{ "The onions are partially visible and the stems",
						"have grown." },
				{ "The top of the onion of the onion plant is clear",
						"above the ground and the onion is white." },
				{ "The onion plant is slightly larger than before and",
						"the onion is cream coloured." },
				{ "The onion stalks are larger than before and the",
						"onion is now light and brown coloured." } }), CABBAGES(
		5324,
		new String[][] {
				{ "The cabbage seeds have only just been planted,",
						"the cabbages are small and bright green." },
				{ "The cabbages are much larger, with more leaves",
						"surrounding the head." },
				{ "The cabbages are larger than before, and textures",
						"of leaves are now easily observable." },
				{ "The cabbage head has swollen larger, and the",
						"surrounding leaves are more close to the ground." },
				{ "The cabbage plants are ready to harvest. The",
						"centre of each cabbage head is light green coloured." } }), TOMATOES(
		5322,
		new String[][] {
				{ "The tomato seeds have only just been planted." },
				{ "The tomato plants grow twice as large as before." },
				{ "The tomato plants grow larger, and small green",
						"tomatoes are now observable." },
				{
						"The tomato plants grow thicker to hold up the",
						"weight of the tomatoes. The tomatoes are now light",
						"orange and slightly larger on the plant." },
				{
						"The tomato plants are ready to harvest. The tomato",
						"plants leaves are larger and the tomatoes are",
						"ripe red." } }), SWEETCORNS(
		5320,
		new String[][] {
				{ "The sweetcorn plants have only just been planted." },
				{ "The sweetcorn plants are waist tall now and are",
						"leafy." },
				{ "The sweetcorn plants are slightly taller than",
						"before and slightly thicker." },
				{ "The sweetcorn leaves are larger at the base, and",
						"the plants are slightly taller." },
				{ "Closed corn cobs are now observable on the",
						"sweetcorn plants." },
				{ "The sweetcorn plants are ready to harvest. The",
						"corn cobs are open and visibly yellow." } }), STRAWBERRIES(
		5323,
		new String[][] {
				{ "The strawberry seeds have only just been planted." },
				{ "The strawberry plants have more leaves than before." },
				{ "The strawberry plants have even more leaves and is",
						"slightly taller than before." },
				{ "Each strawberry plant has opened one white",
						"flower each." },
				{ "The strawberry plants are slightly larger, and",
						"have small strawberries visible at their bases." },
				{ "The strawberry plants are slightly larger, opened",
						"a second flower each, and have more strawberries." },
				{ "The strawberry plants are ready to harvest. The",
						"strawberries are almost as large as the flowers." } }), WATERMELONS(
		5321,
		new String[][] {
				{ "The watermelon seeds have only just been planted." },
				{ "The watermelon vines have grown longer than before." },
				{ "The watermelon vines have grown longer than before." },
				{ "The watermelon vines have started to curl, ",
						"and another vine has sprouted from the centre." },
				{
						"The watermelon vines have continued growing longer,",
						"and another vine has sprouted." },
				{ "Small watermelons are visibly growing on the vines now." },
				{ "The watermelons on the vines have grown larger than before." },
				{ "The watermelon plants are ready to harvest. ",
						"The watermelons on the vines are large and ripe." } });
	private int seedId;
	private String[][] messages;

	private static Map<Integer, InspectData> seeds = new HashMap<Integer, InspectData>();

	static {
		for (InspectData data : InspectData.values()) {
			seeds.put(data.seedId, data);
		}
	}

	InspectData(int seedId, String[][] messages) {
		this.seedId = seedId;
		this.messages = messages;
	}

	public static InspectData forId(int seedId) {
		return seeds.get(seedId);
	}

	public int getSeedId() {
		return seedId;
	}

	public String[][] getMessages() {
		return messages;
	}
}