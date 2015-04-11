package com.rawad.chess.main;

import java.awt.CardLayout;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Timer;

import com.rawad.chess.gamestates.GameState;
import com.rawad.chess.gamestates.MenuState;
import com.rawad.chess.gamestates.OptionState;
import com.rawad.chess.gamestates.State;
import com.rawad.chess.gamestates.StateEnum;
import com.rawad.chess.net.Client;
import com.rawad.chess.net.Packet02Move;
import com.rawad.chess.net.Packet03Message;
import com.rawad.chess.net.Packet04Reply;
import com.rawad.chess.net.Packet05Lose;
import com.rawad.chess.net.Server;
import com.rawad.chess.pieces.BoardPiece;

/**
 * "Intermediary" Class; stores some info for the rest of the game to use
 * 
 * @author Rawad
 *
 */
public class Game implements ActionListener {//should be added to a StateManager containing only this State
	
	public static final int FPS = 60;
	
	private Screen screen;
	
	private Map<StateEnum, State> states;
	
	private State currentState;
	
	private GameState gameState;
	private MenuState menuState;
	private OptionState optionState;
	
	private Client client;
	private Server server;
	
	private Thread clientThread;
	private Thread serverThread;
	
	private Timer t;
	
	private int color;
	
	private long lastTime;
	private long timePassed;
	
	private boolean loading;
	private boolean running;
	
	public Game(Screen screen) {
		
		screen.setGame(this);
		
		this.screen = screen;
		
		states = new HashMap<StateEnum, State>();
		
		gameState = new GameState(this);
		menuState = new MenuState(this);
		optionState = new OptionState(this);
		
		addState(menuState, StateEnum.MENUSTATE);//has to be first; just how CardLayout works
		addState(gameState, StateEnum.GAMESTATE);
		addState(optionState, StateEnum.OPTIONSTATE);
		
		currentState = menuState;
		
		client = new Client(this, getIpFromFile());
		server = new Server(this);
		
		clientThread = new Thread(client);
		serverThread = new Thread(server);
		
		t = new Timer(1000/FPS, this);
		
		loading = false;
		running = false;
		
		t.start();
	}
	
	public void render(Graphics2D g) {
		currentState.render(g);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		long currentTime = System.currentTimeMillis();
		
		timePassed = currentTime - lastTime;
		
		update(timePassed);
		
		lastTime = currentTime;
		
		screen.repaint();
	}
	
	public void update(long timePassed) {
		
		if(server.isRunning()) {
			setLoading(false);
		}
		
		currentState.update(timePassed);
		
	}
	
	public void cancelLoading() {
		
		//stop server
		server.stop();
		
		setLoading(false);
		
	}

	public void attemptGameStart() {
		
		if(client.isServerLive()) {
			
			setColor(BoardPiece.BLACK);
			
			System.out.println("Client> We've connected!");
			
			try {
				clientThread.start();
			} catch(Exception ex) {
				//also ignore; don't want no crashes
			}
			
			setLoading(false);
			
			setRunning(true);
			
			setTurn(false);
			
		} else {
			startServer();
			
		}
		
	}
	
	public void startServer() {
		
		setColor(BoardPiece.WHITE);
		
		setLoading(true);
		server.setListening();
		
		try {
			serverThread.start();
		} catch(Exception ex) {
			//thread tried to be started more than once, just ignore; keep the whole place from crashing
		}
		
	}
	
	private String getIpFromFile() {
		
		return optionState.getIpFromFile();
		
	}
	
	private void addState(State state, StateEnum type) {
		
		states.put(type, state);
		screen.add(state, type.getID());
		
	}
	
	public void setState(StateEnum state) {
		
		CardLayout layout = (CardLayout) screen.getLayout();
		
		layout.show(screen, state.getID());
		
		currentState = states.get(state);
		
		currentState.activate();
		
		screen.repaint();
	}
	
	public void sendMessage(String message) {
		
		String data = new Packet03Message(message).getData();
		
		if(client.isConnected()) {
			client.sendData(data);
			
		} else if(server.isRunning()) {
			server.sendData(data);
		}
		
	}
	
	public void sendContinue(boolean continuePlaying) {
		
		Packet04Reply packet = new Packet04Reply(continuePlaying);
		
		if(client.isConnected()) {
			client.sendData(packet.getData());
			
		} else if(server.isRunning()) {
			server.sendData(packet.getData());
			
		}
		
	}
	
	public void sendYouLost() {
		
		Packet05Lose packet = new Packet05Lose();
		
		if(client.isConnected()) {
			client.sendData(packet.getData());
			
		} else if(server.isRunning()) {
			server.sendData(packet.getData());
			
		}
		
	}
	
	public void showGameOverScreen(boolean value) {
		gameState.showGameOverScreen(value);
	}
	
	public void setContinue(boolean continuePlaying) {
		gameState.setContinuePlaying(continuePlaying);
	}
	
	public void addText(String text) {
		gameState.addText(text);
	}
	
	public boolean isTurn() {
		
		if(client.isConnected()) {
			return client.isTurn();
			
		} else if(server.isRunning()) {
			return server.isTurn();
		}
		
		return false;
		
	}
	
	public void setTurn(boolean turn) {
		
		if(client.isConnected()) {
			client.setTurn(turn);
			
		} else if(server.isRunning()) {
			server.setTurn(turn);
		}
		
	}
	
	public void sendMove(String oldPosition, String newPosition) {
		
		Packet02Move packet = new Packet02Move(oldPosition, newPosition);
		
		if(client.isConnected()) {
			client.sendData(packet.getData());
//			System.out.println("Sent this from client to server: " + packet.getData());
		} else if(server.isRunning()) {
			server.sendData(packet.getData());
//			System.out.println("Sent this from server to client: " + packet.getData());
		}
		
	}
	
	public void movePiece(String oldPosition, String newPosition) {
		gameState.movePiece(oldPosition, newPosition);
	}
	
	public void requestClose() {
		
		Packet04Reply replyPacket = new Packet04Reply(false);
		
		if(client.isConnected()) {
			client.sendData(replyPacket.getData());
			
		} else if(server.isRunning()) {
			server.sendData(replyPacket.getData());
			
		}
		
		screen.windowClosing(new WindowEvent((Window) screen.getParent(), WindowEvent.WINDOW_CLOSING));
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	/**
	 * Color of the current player (one at the bottom)
	 * 
	 * @param color Should be either Black or White as specified by the {@code BoardPiece} Class
	 */
	public void setColor(int color) {
		
		if(color != BoardPiece.WHITE && color != BoardPiece.BLACK) {
			return;
		}
		
		this.color = color;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public int getWidth() {
		return screen.getWidth();
	}
	
	public int getHeight() {
		return screen.getHeight();
	}
	
	public void setLoading(boolean loading) {
		this.loading = loading;
	}
	
	public boolean isLoading() {
		return loading;
	}
	
}
