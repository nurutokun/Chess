package com.rawad.chess.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.rawad.chess.gamestates.StateEnum;
import com.rawad.chess.main.Game;
import com.rawad.chess.net.Packet.PacketType;

public class Server implements Runnable {
	
	public static final int PORT = 8698;
	
	private Game game;
	
	private ServerSocket socket;
	private Socket clientSocket;
	
	private BufferedReader inputReader;
	private BufferedWriter outputWriter;
	
	private boolean running;
	private boolean awaitConnection;
	private boolean turn;//true if it's the host's turn
	
	public Server(Game game) {
		//use localhost; I think with Static IP on, the IP given there should be the same used here
		
		this.game = game;
		
	}
	
	public void run() {
		
		while(true) {
			
			/*now for some reason it needs some junk up here to... revive it? i don't know man, but it's weird... As long as
			  it's a method, it doesn't really seem to care what it is...*/
			try {
				Thread.sleep(0);
			} catch(InterruptedException ex) {
				System.out.println("Thread didn't sleep well...");
			}
			/**/
			
			if(awaitConnection) {
				listenForConnection();
			}
			
			if(running) {
				
				try {
					
					String info = inputReader.readLine();
					
					info = info.replaceAll(Packet.LINE_SEPERATOR, "\n");
					
					parseInfo(info);
					
				} catch(Exception ex) {
					System.out.println("Server> Connection problem: " + ex);
					
					stop();
					
					game.setState(StateEnum.MENUSTATE);
				}
				
			}
			
		}
		
	}
	
	public void sendData(String info) {
		
		info = info.trim();
		
		info = info.replaceAll("\n", Packet.LINE_SEPERATOR);
		
		try {
			
			if(!info.equals("")) {
				outputWriter.write(info + "\r");
				outputWriter.flush();
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	private void parseInfo(String info) {
		
		PacketType type = Packet.parsePacket(info);
		
		switch(type) {
		
		case PING:
			
			Packet01Ping packet = new Packet01Ping(Packet01Ping.PONG);
			
			sendData(packet.getData());
			break;
			
		case MOVE:
			
			Packet02Move movePacket = new Packet02Move(info.substring(3));
			
			game.movePiece(movePacket.getOldPosition(), movePacket.getNewPosition());
			game.setTurn(true);
			
			break;
		
		case MESSAGE:
			
			Packet03Message messagePacket = new Packet03Message(info.substring(3));
			
			game.addText("Client> " + messagePacket.getMessage());
			
			break;
			
		case REPLY:
			
			Packet04Reply replyPacket = new Packet04Reply(info);
			
			game.setContinue(replyPacket.isContinue());
			
			break;
			
		case LOSE:
			
			game.setRunning(false);
			game.showGameOverScreen(true);
			
			break;
			
		case INVALID:
			
		default:
			System.out.println("Server> invalid packet");
			break;
		
		}
		
	}
	
	private void listenForConnection() {
		
		try {
			
			System.out.println("Server> I'm now listening for a connection...");
			
			socket = new ServerSocket(PORT);
			
			clientSocket = socket.accept();
			
			inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outputWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			
			running = true;
			turn = true;
			
			game.setRunning(true);
			
			System.out.println("Server> I accepted connection from: " + clientSocket.getInetAddress().getHostAddress());
			
		} catch(Exception ex) {
			System.out.println("Server> " + ex + "; I probably got manually shut down");
		}
		
		awaitConnection = false;
		
	}
	
	public void setListening() {
		awaitConnection = true;
	}
	
	public void stop() {
		
		try {
			socket.close();
			
			running = false;
			awaitConnection = false;
			
			turn = false;
			
			game.setRunning(false);
			
		} catch(IOException ex) {
			System.out.println("Server> I attempted to shut myself down, but I couldn't: " + ex);
			
		} catch(NullPointerException ex) {
			System.out.println("NULLL");
			
		}
	}
	
	public boolean isTurn() {
		return turn;
	}
	
	public void setTurn(boolean turn) {
		this.turn = turn;
	}
	
	public boolean isRunning() {
		return running;
	}
	
}
