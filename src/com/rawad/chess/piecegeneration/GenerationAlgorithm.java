package com.rawad.chess.piecegeneration;

import java.util.HashMap;

import com.rawad.chess.board.Board;
import com.rawad.chess.pieces.BoardPiece;

/**
 * Used to (hopefully) implement multiple levels for single player
 * and be able to generate them properly. Could also use this to check
 * if the person beat the level. In terms of levels, should add a "LevelsAlgorithm"
 * subclass to this which adds a method that checks if the level is beat
 * 
 * @author Rawad
 *
 */
public abstract class GenerationAlgorithm {
	
	protected HashMap<String, BoardPiece> pieces;
	
	public HashMap<String, BoardPiece> generatePieces(Board board) {
		
		createPieces(board);
		
		return pieces;
	}
	
	protected abstract void createPieces(Board board);
	
}
