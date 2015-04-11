package com.rawad.chess.guis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.Rawad.Input.MouseInput;
import com.rawad.chess.gamestates.GameState;

public class Messenger extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7008211799693692791L;
	
	private static final Dimension MIN_SIZE = new Dimension(20, 20);
	private static final Dimension MAX_SIZE = new Dimension(70, 70);
	
	private GameState game;
	
	private JTextArea outputArea;//shows messages from everyone
	private JTextArea inputArea;//gets input from player
	
	private JScrollPane inputScrollPane;
	private JScrollPane outputScrollPane;
	
	private Button closeButton;
	
	private boolean maximized;
	
	public Messenger(GameState game) {
		
		this.game = game;
		
		outputArea = new JTextArea("You are online...\n");
		inputArea = new JTextArea("Send Message");
		
		inputScrollPane = new JScrollPane();
		outputScrollPane = new JScrollPane();
		
		closeButton = new Button("X", MIN_SIZE.width, MIN_SIZE.height);
		
		outputArea.setEditable(false);
		
		inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		inputScrollPane.setViewportView(inputArea);
		outputScrollPane.setViewportView(outputArea);
		
		setBackground(Color.RED);
		
		inputArea.addKeyListener(new MessageSenderListener());
		
		inputArea.setAutoscrolls(true);
		outputArea.setAutoscrolls(true);
		
		setLayout(new BorderLayout());
		
		add(outputScrollPane, BorderLayout.CENTER);
		add(inputScrollPane, BorderLayout.SOUTH);
		
		maximized = false;
		
		setPreferredSize(MAX_SIZE);
//		setMaximumSize(MAX_SIZE);
		
	}
	
	public void render(Graphics2D g) {
		
		closeButton.render(g);
		
	}
	
	public void update(long timePassed) {
		
		/*
		int x = MouseInput.getX();
		int y = MouseInput.getY();
		
		closeButton.setX(getX() + getWidth());
		closeButton.setY(getY() + 0);
		
		if(game.isRunning()) {
			
			if(MouseInput.isLeftMouseDown()) {
				
//				System.out.println(x + ", " + y + " | " + closeButton.getX() + ", " + closeButton.getY());
				
				if(intersects(x, y) && !maximized) {
					
					System.out.println("is maximized");
					
					maximized = true;
					
					setPreferredSize(MAX_SIZE);
					
				} else if(closeButton.intersects(x, y) && maximized) {
					
					System.out.println("is shrunken");
					
					maximized = false;
					
					setPreferredSize(MIN_SIZE);
					
				}
				
				revalidate();
				
				MouseInput.leftMouse(false);
			}
			
		}*/
		
	}
	
	public boolean intersects(int x, int y) {
		
		if(	x > this.getX() && x <= this.getX() + getWidth() &&
			y > this.getY() && y <= this.getY() + getHeight()) {
			
			return true;
		}
		
		return false;
		
	}
	
	private void sendMessage(String message) {
		
		outputArea.setText(outputArea.getText() + "Me> " + message + "\n");
		
		game.sendMessage(message);
	}
	
	public void addMessage(String text) {
		
		outputArea.setText(outputArea.getText() + text + "\n");
		
	}
	
	public GroupLayout getFilledLayout(JPanel panel) {
		
		GroupLayout layout = new GroupLayout(panel);
		
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				layout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(this, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addContainerGap(panel.getWidth(), Short.MAX_VALUE)));
		
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				GroupLayout.Alignment.TRAILING,
				layout
						.createSequentialGroup()
						.addContainerGap(panel.getHeight(), Short.MAX_VALUE)
						.addComponent(this, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE).addContainerGap()));
		
		return layout;
	}
	
	private class MessageSenderListener implements KeyListener {
		
		@Override
		public void keyTyped(KeyEvent e) {
			
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			JTextArea field = (JTextArea) e.getSource();
			int keyCode = e.getKeyCode();
			
			switch(keyCode) {
			
			case KeyEvent.VK_ENTER:
				
				String fieldText = field.getText();
				
				if((e.getModifiers() | KeyEvent.SHIFT_DOWN_MASK) == 65) {
					
					int index = field.getCaretPosition();
					
					fieldText = fieldText.substring(0, index);
					
					fieldText += "\n";
					
					fieldText += field.getText().substring(index);
					
					field.setText(fieldText);
					
				} else {
					
					if(game.isRunning()) {
						
						try {
							
							if(!fieldText.isEmpty()) {
								sendMessage(fieldText);
								field.setText("");
								field.setCaretPosition(0);
							}
							
						} catch(Exception ex) {
							System.out.println("Couldn't send message: " + ex);
						}
					}
					
				}
				
				e.consume();
				
				break;
			
			}
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			
		}
		
		
		
	}
	
}
