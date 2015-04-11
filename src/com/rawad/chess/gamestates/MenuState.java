package com.rawad.chess.gamestates;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import com.Rawad.Input.KeyboardInput;
import com.Rawad.Input.MouseInput;
import com.rawad.chess.guis.Button;
import com.rawad.chess.main.Game;

public class MenuState extends State {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4380718561964367340L;
	
	private static final int OFFSET = 5;//between buttons
	
	private List<Button> buttons;
	
	private Button gameButton;
	private Button settingsButton;
	private Button quitButton;
	
	private int selected;
	private boolean careAboutSelected;
	
	public MenuState(Game game) {
		super(game);
		
		buttons = new ArrayList<Button>();
		
		gameButton = new Button("Play", Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
		settingsButton = new Button("Settings", Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
		quitButton = new Button("Quit", Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
		
		buttons.add(gameButton);
		buttons.add(settingsButton);
		buttons.add(quitButton);
		
		selected = 0;
		
	}
	
	@Override
	public void render(Graphics2D g) {
		
		for(Button butt: buttons) {
			butt.render(g);
		}
		
	}
	
	@Override
	public void update(long timePassed) {
		
		int x = MouseInput.getX();
		int y = MouseInput.getY();
		
		updateButtonPositions();
		
		for(Button butt: buttons) {
			
			if(butt.intersects(x, y) && !careAboutSelected) {
				butt.setHighlighted(true);
				
				
			} else {
				butt.setHighlighted(false);
				
			}
			
		}
		
		if(MouseInput.isLeftMouseDown()) {
			
			buttonActions(x, y);
			
			MouseInput.leftMouse(false);//could remove this but to make sure it's an actual click; could also move this to buttonActions method
		}
		
		updateKeyInputs();
		
	}
	
	private void updateKeyInputs() {
		
		if(KeyboardInput.isKeyDown(KeyEvent.VK_UP)) {
			
			selected--;
			
			careAboutSelected = true;
			
			KeyboardInput.keyUp(KeyEvent.VK_UP);
			
		} else if(KeyboardInput.isKeyDown(KeyEvent.VK_DOWN)) {
			
			selected++;
			
			careAboutSelected = true;
			
			KeyboardInput.keyUp(KeyEvent.VK_DOWN);
			
		} else if(KeyboardInput.isKeyDown(KeyEvent.VK_ENTER) && careAboutSelected) {
			
			careAboutSelected = false;
			
			KeyboardInput.keyUp(KeyEvent.VK_ENTER);
			
			Button butt = buttons.get(selected);
			
			buttonActions(butt.getX(), butt.getY());
			
		}
		
		if(selected < 0) {
			selected += buttons.size();
		}
		
		selected %= buttons.size();
		
		if(careAboutSelected) {
			buttons.get(selected).setHighlighted(true);
		}
		
	}
	
	/**
	 * This'll be responsible for checking if any of the buttons are pressed and dealing with
	 * them accordingly
	 * 
	 * @param x x-coordinate of mouse
	 * @param y y-coordinate of mouse
	 */
	private void buttonActions(int x, int y) {
		
		if(gameButton.intersects(x, y)) {
			
			game.attemptGameStart();
			
			game.setState(StateEnum.GAMESTATE);
			
		} else if(settingsButton.intersects(x, y)) {
			game.setState(StateEnum.OPTIONSTATE);
		
		} else if(quitButton.intersects(x, y)) {
			game.requestClose();
			
		} else {
			careAboutSelected = false;
		}
	}
	
	/**
	 * Could do some other real fancy, layout managing stuff right here fellur.
	 * 
	 */
	private void updateButtonPositions() {
		
		for(int i = 0; i < buttons.size(); i++) {
			Button curButt = buttons.get(i);
			
			curButt.setX(game.getWidth()/2);
			curButt.setY(game.getHeight()/2 + (i*Button.BUTTON_HEIGHT) + (i*OFFSET));
			
		}
		
	}
	
}
