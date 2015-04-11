package com.rawad.chess.gamestates;

public enum StateEnum {
	
	GAMESTATE("Game State"),
	MENUSTATE("Menu State"),
	OPTIONSTATE("Option State");
	
	private final String id;
	
	private StateEnum(String id) {
		this.id = id;
	}
	
	public String getID() {
		return id;
	}
	
}
