package com.rawad.chess.gamestates;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JLabel;

import com.Rawad.Input.MouseInput;
import com.rawad.chess.guis.Button;
import com.rawad.chess.guis.TextField;
import com.rawad.chess.main.Game;

public class OptionState extends State {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5102737601479384995L;
	
	private static final String FILE_NAME = "res/info.txt";
	
	private GroupLayout layout;
	
	private JLabel label;
	private TextField textField;
	
	private Button menuButton;
	
	public OptionState(Game game) {
		super(game);
		
		label = new JLabel("IP Address You'd Like to Connect To:");
		textField = new TextField(getIpFromFile());
		
		label.setForeground(Color.BLUE);
		label.setBackground(Color.RED);
		
		layout = new GroupLayout(this);
		
		setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				
				layout.createSequentialGroup()
				.addComponent(label)
				.addComponent(textField)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(label)
//						.addComponent(textField)
				).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
//						.addComponent(label)
						.addComponent(textField)
				)
				
		);
		
		layout.setVerticalGroup(
				
				layout.createBaselineGroup(false, false)
				.addComponent(label)
				.addComponent(textField)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//						.addComponent(label)
//						.addComponent(textField)
				)
				
		);
		
		menuButton = new Button("Main Menu", Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
		
		add(label);
		add(textField);
		
	}
	
	@Override
	public void render(Graphics2D g) {
		menuButton.render(g);
		
		label.paint(g);
		textField.paint(g);
	}
	
	@Override
	public void update(long timePassed) {
		
		int x = MouseInput.getX();
		int y = MouseInput.getY();
		
		menuButton.setX(game.getWidth()/2);
		menuButton.setY(game.getHeight()/2);
		
		if(MouseInput.isLeftMouseDown()) {
			
			if(menuButton.intersects(x, y)) {
				
				saveData(textField.getText().trim());
				
				game.setState(StateEnum.MENUSTATE);
			}
			
			MouseInput.leftMouse(false);
		}
		
		if(menuButton.intersects(x, y)) {
			menuButton.setHighlighted(true);
			
		} else {
			menuButton.setHighlighted(false);
			
		}
		
	}
	
	private void saveData(String data) {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FILE_NAME)))) {
			
			writer.write("IP Address = " + data);
			
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public String getIpFromFile() {
		
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(FILE_NAME)))) {
			
			String line = reader.readLine();
			
			String[] splits = line.split("=");
			
			String ip = splits[1].trim();
			
			return ip;
			
		} catch(IOException | ArrayIndexOutOfBoundsException | NullPointerException ex) {
			ex.printStackTrace();
		}
		
		return "localhost";
		
	}
	
}
