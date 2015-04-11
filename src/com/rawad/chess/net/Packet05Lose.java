package com.rawad.chess.net;

public class Packet05Lose extends Packet {
	
	public Packet05Lose() {
		super(PacketType.LOSE.getId());
		
	}
	
	@Override
	public String getData() {
		return getId() + REGEX;
	}

}
