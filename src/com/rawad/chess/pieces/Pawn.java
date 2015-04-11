package com.rawad.chess.pieces;

import java.util.HashMap;

import com.rawad.chess.board.Board;

public class Pawn extends BoardPiece {
	
	private boolean moved;
	
	public Pawn(Board board, String position, int colour) {
		super(board, position, 0, colour);
		
		moved = false;
	}
	
	protected boolean customBlock(String newPosition) {
		
		char oldX = position.charAt(0);
		char oldY = position.charAt(1);
		
		char newX = newPosition.charAt(0);
		char newY = newPosition.charAt(1);
		
		if(board.getColor() == BLACK) {
			char temp = newY;
			
			newY = oldY;
			oldY = temp;
			
		}
		
		int offsetY = board.getColor() == BLACK? 0:1;
		
		HashMap<String, BoardPiece> pieces = board.getPieces();
		
		String letter = String.valueOf(newX);
		String number = String.valueOf((char) (oldY + offsetY));
		
		//trying to attack diagonally
		if(Math.abs(newX - oldX) == 1 && Math.abs(newY - oldY) == 1) {
			
//			System.out.println("checking diagonal: " + letter + number);
			
			if(pieces.get(letter + number) == null) {
				return true;
			}
			
		} else {
			
			if(oldX != newX) {
				//nope, can't do that
				return true;
			}
			
			//make sure they're not going backwards
			if(oldY > newY) {
				return true;
			}
			
			int maxDistance = moved? 1:2;
			
			int dy = newY - oldY;
			
			if(Math.abs(dy) > maxDistance) {
				return true;
			}
			
			offsetY = board.getColor() == BLACK? 1:0;
			
			//make sure they're not intersecting with anyone
			for(int i = 1; i <= Math.abs(dy); i++) {
				
				letter = String.valueOf(oldX);
				number = String.valueOf((char) (oldY + i - offsetY));
				
				if(pieces.get(letter + number) != null) {
					return true;
				}
				
			}
			
		}
		
		moved = true;
			
		return false;
	}
	
}
