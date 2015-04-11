package com.rawad.chess.pieces;

import com.rawad.chess.board.Board;
import com.rawad.chess.movement.DiagnolI;
import com.rawad.chess.movement.HorizontalI;
import com.rawad.chess.movement.VerticalI;

public class Queen extends BoardPiece implements HorizontalI, VerticalI,
		DiagnolI {
	
	public Queen(Board board, String position, int colour) {
		super(board, position, 4, colour);
	}
	
}
