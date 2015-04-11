package com.rawad.chess.pieces;

import com.rawad.chess.board.Board;
import com.rawad.chess.movement.HorizontalI;
import com.rawad.chess.movement.VerticalI;

public class Rook extends BoardPiece implements HorizontalI, VerticalI {
	
	public Rook(Board board, String position, int colour) {
		super(board, position, 3, colour);
	}
	
}
