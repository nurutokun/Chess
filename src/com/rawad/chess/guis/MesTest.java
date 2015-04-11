package com.rawad.chess.guis;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MesTest {
	
	private static JFrame frame;
	private static CustomPanel panel;
	
	private static Messenger messenger;
	
	public static void main(String[] args) {
		
		frame = new JFrame("Title");
		
		panel = new CustomPanel();
		
		messenger = new Messenger(null);
		
		panel.setLayout(messenger.getFilledLayout(panel));
		
		frame.add(panel);
		
		frame.pack();
		
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private static class CustomPanel extends JPanel {
		
		private static final long serialVersionUID = 1;
		
		public void paintComponent(Graphics g) {
			g.setColor(Color.RED);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
	}

}
