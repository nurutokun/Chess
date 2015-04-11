package com.rawad.chess.net;

public class Packet03Message extends Packet {
	
	private String message;
	
	public Packet03Message(String message) {
		super(PacketType.MESSAGE.getId());
		
		this.message = message;
		
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String getData() {
		return getId() + REGEX + message;
	}

}
