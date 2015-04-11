package com.rawad.chess.net;

public abstract class Packet {
	
	public static final String LINE_SEPERATOR = "`.";//because \n breaks everything
	
	protected static final String REGEX = ":";//"|" didn't work as regex... why
	
	private final String id;
	
	public Packet(String id) {
		this.id = id;
		
	}
	
	public String getId() {
		return id;
	}
	
	public abstract String getData();
	
	public static PacketType parsePacket(String packetInfo) {
		
		try {
			return lookUpPacket(packetInfo.substring(0, 2));
		} catch(Exception ex) {
			return PacketType.INVALID;
		}
		
	}
	
	private static PacketType lookUpPacket(String id) {
		
		PacketType[] types = PacketType.values();
		
		for(PacketType t: types) {
			
			if(t.getId().equals(id)) {
				return t;
			}
			
		}
		
		return PacketType.INVALID;
		
	}
	
	public static enum PacketType {
		
		INVALID("00"),
		PING("01"),
		MOVE("02"),
		MESSAGE("03"),
		REPLY("04"),
		LOSE("05");
		
		private final String id;
		
		private PacketType(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
		}
		
	}
	
}
