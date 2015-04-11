package com.rawad.chess.net;

public class Packet02Move extends Packet {
	
	private String oldPosition;
	private String newPosition;
	
	public Packet02Move(String oldPosition, String newPosition) {
		super(PacketType.MOVE.getId());
		
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
		
	}
	
	public Packet02Move(String data) {
		super(PacketType.MOVE.getId());
		
		String[] split = data.split(REGEX);
		
		oldPosition = split[0];
		newPosition = split[1];
		
	}
	
	public String getOldPosition() {
		return oldPosition;
	}
	
	public String getNewPosition() {
		return newPosition;
	}
	
	@Override
	public String getData() {
		return getId() + REGEX + oldPosition + REGEX + newPosition;
	}
	
}
