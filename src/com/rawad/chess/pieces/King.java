package com.rawad.chess.pieces;

import com.rawad.chess.board.Board;

public class King extends BoardPiece {
	
	public King(Board board, String position, int colour) {
		super(board, position, 5, colour);
	}
	
	protected boolean customBlock(String newPosition) {
		
		char oldX = position.charAt(0);
		char oldY = position.charAt(1);
		
		char newX = newPosition.charAt(0);
		char newY = newPosition.charAt(1);
		
		int dy = newY - oldY;
		int dx = newX - oldX;
		
		if(Math.abs(dx) > 1 || Math.abs(dy) > 1) {
			return true;
		}
		
		return false;
	}
	
}
