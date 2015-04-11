package com.rawad.chess.net;

/**
 * This is just to communicate whether or not each player would like to restart
 * the game or not after finishing a round
 * 
 * @author Rawad
 *
 */
public class Packet04Reply extends Packet {
	
	private boolean continuePlaying;
	
	public Packet04Reply(String info) {
		super(PacketType.REPLY.getId());
		
		String[] data = info.split(REGEX);
		
		continuePlaying = Boolean.valueOf(data[1]);
		
	}
	
	public Packet04Reply(boolean continuePlaying) {
		super(PacketType.REPLY.getId());
		
		this.continuePlaying = continuePlaying;
	}
	
	public boolean isContinue() {
		return continuePlaying;
	}
	
	@Override
	public String getData() {
		return getId() + REGEX + continuePlaying;
	}

}
