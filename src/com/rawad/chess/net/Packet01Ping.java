package com.rawad.chess.net;

public class Packet01Ping extends Packet {
	
	public static final String PING = "ping";
	public static final String PONG = "pong";
	
	private String message;
	
	public Packet01Ping(String message) {
		super(Packet.PacketType.PING.getId());
		
		this.message = message;
		
	}
	
	@Override
	public String getData() {
		return getId() + REGEX + message + "\n";
	}
	
}
