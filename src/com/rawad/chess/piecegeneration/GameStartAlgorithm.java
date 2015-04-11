package com.rawad.chess.piecegeneration;

import java.util.HashMap;

import com.rawad.chess.board.Board;
import com.rawad.chess.pieces.Bishop;
import com.rawad.chess.pieces.BoardPiece;
import com.rawad.chess.pieces.King;
import com.rawad.chess.pieces.Knight;
import com.rawad.chess.pieces.Pawn;
import com.rawad.chess.pieces.Queen;
import com.rawad.chess.pieces.Rook;

/**
 * Standard board layout when a game starts
 * 
 * @author Rawad
 *
 */
public class GameStartAlgorithm extends GenerationAlgorithm {
	
	public void createPieces(Board board) {
		
		pieces = new HashMap<String, BoardPiece>();
		
		//pawns
		for(int j = 0; j < 2; j++) {
			for(int i = 0; i < Board.COLUMNS; i++) {
				
				String letter = String.valueOf((char) (BoardPiece.A + i));
				String number = (j == 0? Board.COLUMNS-1:2)+"";
				
				String position = letter + number;
				
				pieces.put(position, new Pawn(board, position, BoardPiece.BLACK-j));//max colour is black; 1
				
			}
			
		}
		
		//A1, H1, A8, H8: rooks
		//B1, G1, B8, G8: knights
		//C1, F1, C8, F8: bishops
		
		//white rooks
		pieces.put("A1", new Rook(board, "A1", BoardPiece.WHITE));
		pieces.put("H1", new Rook(board, "H1", BoardPiece.WHITE));
		
		//black rooks
		pieces.put("A8", new Rook(board, "A8", BoardPiece.BLACK));
		pieces.put("H8", new Rook(board, "H8", BoardPiece.BLACK));
		
		//white knights
		pieces.put("B1", new Knight(board, "B1", BoardPiece.WHITE));
		pieces.put("G1", new Knight(board, "G1", BoardPiece.WHITE));
		
		//black knights
		pieces.put("B8", new Knight(board, "B8", BoardPiece.BLACK));
		pieces.put("G8", new Knight(board, "G8", BoardPiece.BLACK));
		
		//white bishops
		pieces.put("C1", new Bishop(board, "C1", BoardPiece.WHITE));
		pieces.put("F1", new Bishop(board, "F1", BoardPiece.WHITE));
		
		//black bishops
		pieces.put("C8", new Bishop(board, "C8", BoardPiece.BLACK));
		pieces.put("F8", new Bishop(board, "F8", BoardPiece.BLACK));
		
		//white king/queen
		pieces.put("D1", new Queen(board, "D1", BoardPiece.WHITE));
		pieces.put("E1", new King(board, "E1", BoardPiece.WHITE));
		
		//black king/queen
		pieces.put("E8", new Queen(board, "E8", BoardPiece.BLACK));
		pieces.put("D8", new King(board, "D8", BoardPiece.BLACK));
		
	}
	
}
