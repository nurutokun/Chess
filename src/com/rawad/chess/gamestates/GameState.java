package com.rawad.chess.gamestates;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.JFrame;

import com.Rawad.Input.MouseInput;
import com.rawad.chess.animations.LoadingCircle;
import com.rawad.chess.board.Board;
import com.rawad.chess.guis.Button;
import com.rawad.chess.guis.Messenger;
import com.rawad.chess.main.Game;
import com.rawad.chess.piecegeneration.GameStartAlgorithm;
import com.rawad.chess.pieces.BoardPiece;

/**
 * Two options: Could show empty board with a window saying "connecting..." until
 * a client is found or (more likely) the board will be loaded up the instance the
 * activate() method is called. Because in single-player, this makes more sense
 * 
 * @author Rawad
 *
 */
public class GameState extends State {
	
	private static final long serialVersionUID = 404387930512864520L;
	
	private static final int INSET = 50;
	
	private Board board;
	
	private LoadingCircle animLoader;
	
	private JFrame mesHolder;
	
	private Messenger messenger;
	
	private Button cancelButton;
	private Button mesOpener;
	
	private Button replayButton;
	private Button quitButton;
	
	private boolean showGameOverScreen;
	private boolean continuePlaying;
	private boolean iContinue;
	
	//all these should probably communicate through the Game Object e.g. the color of the current player
	public GameState(Game game) {
		super(game);
		
		board = new Board();
		
		animLoader = new LoadingCircle();
		
		messenger = new Messenger(this);
		
		cancelButton = new Button("Exit To Main Menu", Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
		mesOpener = new Button("Messenger", Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
		
		replayButton = new Button("Replay", Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
		quitButton = new Button("Exit to Main Menu", Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
		
		mesHolder = new JFrame("Messenger");
		
		mesHolder.add(messenger, BorderLayout.CENTER);
		mesHolder.pack();
		mesHolder.setLocationRelativeTo(this);
		mesHolder.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		mesHolder.addWindowListener(new MesCloseHandler());
		
//		setLayout(messenger.getFilledLayout(this));
		
		showGameOverScreen = false;
		continuePlaying = false;
		iContinue = false;
		
	}
	
	@Override
	public void render(Graphics2D g) {
		super.paintComponent(g);
		
		board.render(g);
		
		if(game.isLoading()) {
			g.drawImage(animLoader.getCurrentImage(), 0, 0, null);
			
			cancelButton.render(g);
		}
		
		int x = game.getWidth()/2 - 20;
		int y = board.getY() + board.getHeight() + 10;
		
		g.setColor(Color.CYAN);
		
		if(game.isRunning()) {
			
			if(game.isTurn()) {
				
				g.fillOval(x, y, 20, 20);
				g.drawString("Your Turn", x + 20, y + 15);
				
			} else {
				g.drawOval(x, y, 20, 20);
				g.drawString("Their Turn", x + 20, y + 15);
				
			}
			
			mesOpener.render(g);
			
		}
		
		if(showGameOverScreen) {
			
			g.setColor(new Color(0, 0, 0, 80));
			
			g.fillRect(0, 0, getWidth(), getHeight());
			
			replayButton.render(g);
			quitButton.render(g);
			
		}
		
//		AffineTransform af = g.getTransform();
//		g.translate(messenger.getX(), messenger.getY());
//		messenger.paintComponents(g);
//		g.setTransform(af);
//		messenger.render(g);
		
	}
	
	@Override
	public void update(long timePassed) {
		
		board.setWidth(game.getWidth() - (2 * INSET));
		board.setHeight(game.getHeight() - (2 * INSET));
		
		board.setX((game.getWidth() - board.getWidth())/2);
		board.setX((game.getHeight() - board.getHeight())/2);
		
		int x = MouseInput.getX();
		int y = MouseInput.getY();
		
		if(game.isLoading()) {
			animLoader.update(timePassed);
			
			cancelButton.setX(animLoader.getCurrentImage().getWidth() + (Button.BUTTON_WIDTH/2));
			cancelButton.setY((Button.BUTTON_HEIGHT/2));
			
			if(MouseInput.isLeftMouseDown()) {
				if(cancelButton.intersects(x, y)) {
					game.cancelLoading();
					
					MouseInput.leftMouse(false);
					
					game.setState(StateEnum.MENUSTATE);
					
				}
			}
			
		}
		
		buttonHighlight(x, y);
		
		if(MouseInput.isLeftMouseDown()) {
			buttonActions(x, y);
		}
		
		if(game.isRunning()) {
			
			mesOpener.setX(mesOpener.getWidth()/2);
			mesOpener.setY(getHeight());
			
			messenger.update(timePassed);
			
			if(MouseInput.isLeftMouseDown()) {
				
				if(game.isTurn() && board.handleMouse(x, y)) {
					
					String oldPosition = board.getLastMoved()[0];
					String newPosition = board.getLastMoved()[1];
					
					game.sendMove(oldPosition, newPosition);
					
					game.setTurn(false);
					
					checkKingCheck(oldPosition, newPosition);
				}
				
				MouseInput.leftMouse(false);
				
			}
			
		} else {
			
			mesHolder.setVisible(false);
		}
		
		if(showGameOverScreen) {
			
			replayButton.setX(getWidth()/2);
			replayButton.setY(getHeight()/2);
			
			quitButton.setX(getWidth()/2);
			quitButton.setY(getHeight()/2 + quitButton.getHeight());
			
			if(continuePlaying && iContinue) {
				
				game.setRunning(true);
				
				board.createBoardPieces(game.getColor(), new GameStartAlgorithm());
				
				showGameOverScreen = false;
				
				continuePlaying = false;
				iContinue = false;
			}
			
		}
		
	}
	
	private void checkKingCheck(String oldPosition, String newPosition) {
		
		HashMap<String, BoardPiece> pieces = board.getPieces();
		
		if(board.isKingDown()) {
			//stop game
			game.setRunning(false);
			
			showGameOverScreen = true;
			
			game.sendYouLost();
		}
		
		//check if King is in check
		
	}
	
	private void buttonActions(int x, int y) {
		
		if(mesOpener.intersects(x, y) && !mesHolder.isVisible()) {
			mesHolder.setVisible(true);
			
			MouseInput.leftMouse(false);
		}
		
		if(showGameOverScreen) {
			
			if(replayButton.intersects(x, y)) {
				
				game.sendContinue(true);
				
				iContinue = true;
			
				
			} else if(quitButton.intersects(x, y)) {
				
				game.sendContinue(false);
				
				game.setState(StateEnum.MENUSTATE);
				
				game.cancelLoading();
				
				showGameOverScreen = false;
				
				continuePlaying = false;
				iContinue = false;
				
				MouseInput.leftMouse(false);
			}
			
		}
	}
	
	private void buttonHighlight(int x, int y) {
		
		if(cancelButton.intersects(x, y) && game.isLoading()) {
			cancelButton.setHighlighted(true);
		} else {
			cancelButton.setHighlighted(false);
		}
		
		if(mesOpener.intersects(x, y) && game.isRunning()) {
			mesOpener.setHighlighted(true);
		} else {
			mesOpener.setHighlighted(false);
		}
		
		if(replayButton.intersects(x, y) && showGameOverScreen) {
			replayButton.setHighlighted(true);
		} else {
			replayButton.setHighlighted(false);
		}
		
		if(quitButton.intersects(x, y) && showGameOverScreen) {
			quitButton.setHighlighted(true);
		} else {
			quitButton.setHighlighted(false);
		}
		
	}
	
	public void sendMessage(String message) {
		game.sendMessage(message);
	}
	
	public void addText(String text) {
		
		messenger.addMessage(text);
		
	}
	
	public void movePiece(String oldPosition, String newPosition) {
		
		board.setNewPiecePosition(oldPosition, newPosition);
		
	}
	
	public void activate() {
		
		//get color properly; if this is the host then they selected their color
		//otherwise they get whatever is left for them. Should differentiate
		//between single and multi-player to pass in the appropriate algorithm
		//for the game.
		board.createBoardPieces(game.getColor(), new GameStartAlgorithm());
		
	}
	
	public void deactivate() {
		
		//could save board object, retaining all its pieces, in a file or something
		//for reopening later; singleplayer
		
	}
	
	public void showGameOverScreen(boolean showGameOverScreen) {
		this.showGameOverScreen = showGameOverScreen;
	}
	
	public void setContinuePlaying(boolean continuePlaying) {
		
		if(!continuePlaying) {
			buttonActions(quitButton.getX()+1, quitButton.getY()+1);
		}
		
		this.continuePlaying = continuePlaying;
	}
	
	public boolean isRunning() {
		return game.isRunning();
	}
	
	private static class MesCloseHandler implements WindowListener {
		
		@Override
		public void windowOpened(WindowEvent e) {}
		
		@Override
		public void windowClosing(WindowEvent e) {
			
			JFrame frame = (JFrame) e.getComponent();
			
			frame.setVisible(false);
			
		}
		
		@Override
		public void windowClosed(WindowEvent e) {}
		
		@Override
		public void windowIconified(WindowEvent e) {}
		
		@Override
		public void windowDeiconified(WindowEvent e) {}
		
		@Override
		public void windowActivated(WindowEvent e) {}
		
		@Override
		public void windowDeactivated(WindowEvent e) {}
		
	}
	
}
