package com.rawad.chess.main;

import java.awt.EventQueue;
import java.awt.Frame;

public class ChessLauncher {
	
	private static Frame frame;
	
	private static Screen screen;
	
	private static Game game;
	
	public static void main(String[] args) {
		
		frame = new Frame("Chess v0.1");
		
		screen = new Screen(500, 500);
		
		game = new Game(screen);
		
		frame.add(screen);
		
		frame.setSize(screen.getSize());
		
		frame.addKeyListener(screen);
		frame.addWindowListener(screen);
		
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				frame.setVisible(true);
			}
			
		});
		
	}
	
}
