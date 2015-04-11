package com.rawad.chess.gamestates;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.rawad.chess.main.Game;

/**
 * Extends JPanel so certain Swing things can be used with it
 * 
 * @author Rawad
 *
 */
public abstract class State extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2089317642835815349L;
	
	protected Game game;
	
	public State(Game game) {
		this.game = game;
		
		setBackground(Color.WHITE);
	}
	
	public abstract void render(Graphics2D g);
	
	public abstract void update(long timePassed);
	
	public void activate() {}
	
	public void deactivate() {}
	
}
