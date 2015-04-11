package com.rawad.chess.pieces;

import com.rawad.chess.board.Board;

public class Knight extends BoardPiece {
	
	public Knight(Board board, String position, int colour) {
		super(board, position, 2, colour);
	}
	
	protected boolean customBlock(String newPosition) {
		
		char oldX = position.charAt(0);
		char oldY = position.charAt(1);
		
		char newX = newPosition.charAt(0);
		char newY = newPosition.charAt(1);
		
		int dx = newX - oldX;
		int dy = newY - oldY;
		
		if(Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			return false;
			
		} else if(Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			return false;
			
		}
		
		return true;
		
	}
	
}
