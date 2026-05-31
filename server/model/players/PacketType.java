package server.model.players;

public interface PacketType {
	public void processPacket(Player player, int packetType, int packetSize);
}
