package com.rawad.chess.pieces;

import com.rawad.chess.board.Board;
import com.rawad.chess.movement.DiagnolI;

public class Bishop extends BoardPiece implements DiagnolI {
	
	public Bishop(Board board, String position, int colour) {
		super(board, position, 1, colour);
	}
	
}
