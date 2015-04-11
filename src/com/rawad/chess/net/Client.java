package com.rawad.chess.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.rawad.chess.gamestates.StateEnum;
import com.rawad.chess.main.Game;
import com.rawad.chess.net.Packet.PacketType;

public class Client implements Runnable {
	
	private Game game;
	
	private Socket socket;
	
	private BufferedReader inputReader;
	private BufferedWriter outputWriter;
	
	private InetAddress address;
	
	private boolean connected;
	private boolean turn;//true if it's the client's turn
	
	public Client(Game game, String address) {
		
		this.game = game;
		
		connected = false;
		try {
			this.address = InetAddress.getByName(address);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void run() {
		
		while(true) {
			
			try {
				Thread.sleep(0);
			} catch(InterruptedException ex) {
				System.out.println("Thread didn't sleep well...");
			}
			
			if(connected) {
				try {
					
					String info = inputReader.readLine();
					
					info = info.replaceAll(Packet.LINE_SEPERATOR, "\n");
					
					parseInfo(info);
					
				} catch(NullPointerException ex) {
					System.out.println("Client> inputReader is null");
					
				} catch(IOException ex) {
					System.out.println("Client> I've disconnected from the server");
					
					connected = false;
					
					game.setRunning(false);
					
					game.setState(StateEnum.MENUSTATE);
					
				}
				
			}
			
		}//while in game, do thing or something. Could explicitly call this method to reboot system or
		// could have nested while loops so that this is always running but only certain parts based on other flags
		
	}
	
	private void parseInfo(String info) {
		
		PacketType type = Packet.parsePacket(info);
		
		switch(type) {
		
		case MOVE:
			
			Packet02Move movePacket = new Packet02Move(info.substring(3));
			
//			System.out.println("Client> Moving from: " + packet.getOldPosition() + " to: " + packet.getNewPosition());
			
			game.movePiece(movePacket.getOldPosition(), movePacket.getNewPosition());
			game.setTurn(true);
			
			break;
			
		case MESSAGE:
			
			Packet03Message messagePacket = new Packet03Message(info.substring(3));
			
			game.addText("Server> " + messagePacket.getMessage());
			
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
			System.out.println("Client> Invalid Packet: " + info);
			break;
		
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
	
	public boolean isServerLive() {
		
		try {
			
			game.setLoading(true);
			
			socket = new Socket(address, Server.PORT);
			
			inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			sendData(new Packet01Ping(Packet01Ping.PING).getData());
			
			String message = inputReader.readLine().substring(3);//inclusive
			
			System.out.println("Client> Got this when pinging: " + message);
			
			if(message.equals(Packet01Ping.PONG)) {
				connected = true;
			}
			
		} catch(Exception ex) {
			System.out.println("Client> I couldn't connect to the server; either IP is incorrect or it's just closed: " + ex);
			connected = false;
		}
		
		return connected;
		
	}
	
	public boolean isTurn() {
		return turn;
	}
	
	public void setTurn(boolean turn) {
		this.turn = turn;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
}
