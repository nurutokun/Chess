package com.rawad.chess.pieces;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.rawad.chess.board.Board;
import com.rawad.chess.movement.DiagnolI;
import com.rawad.chess.movement.HorizontalI;
import com.rawad.chess.movement.Moveable;
import com.rawad.chess.movement.VerticalI;

public abstract class BoardPiece implements Moveable {
	
	public static final int WHITE = 0;//"sprite" sheet has row 0 for white pieces
	public static final int BLACK = 1;//and row 1 for black pieces
	
	public static final int LENGTH = 64;
	
	public static final int A = 65;//add to 0 to get character value 'A'
	public static final int ONE = 49;
	
	private static final String TEXTURE_SHEET_PATHNAME = "res/chess.png";
	
	private static final int COLOUR_MASK = 1;
	
	private static BufferedImage PICTURE_SHEET;
	
	protected final int colour;
	protected final int piece;
	
	protected Board board;//for on-screen x-coordinates and such
	
	protected BufferedImage texture;
	
	protected String position;//A1, A2, A3... B1, B2... C1, C2, etc.
	
	protected int x;//indexes on board. Can be readjusted when screen size changes
	protected int y;//based on the board's square's width/length
	
//	protected int position;//optional; fancy bit stuff
	
	//8 columns and 8 rows
	//0xFF = 256 = 1111 1111
	//1111 & 1111 = 8 & 8 = 'H'/Column & 8/Row
	//mask left half to get right half
	//bit shift right by ROWS_BIT_LENGTH to get left half
	
	/**
	 * Basic constructor
	 * 
	 * @param board {@code Board} object to be stored in this {@code BoardPiece}
	 * @param position Length2 {@code String} containing the letter/number combo position
	 * @param piece Index of the piece across the sprite sheet
	 * @param colour Should be either 0 or 1
	 */
	public BoardPiece(Board board, String position, int piece, int colour) {
		
		this.board = board;
		
		
		this.x = toX(position.charAt(0));
		this.y = Board.ROWS - toY(position.charAt(1)) - 1;//subtracts it to get index
		
		if(board.getColor() == BLACK) {
			
			this.x = Board.COLUMNS - this.x - 1;
			this.y = Board.ROWS - this.y - 1;
			
		}
		
		this.position = position;
		
		this.colour = colour%2;
		this.piece = piece;
		
		loadTexture((piece << 1) | this.colour);
	}
	
	static {
		
		try {
			PICTURE_SHEET = ImageIO.read(new File(TEXTURE_SHEET_PATHNAME));
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}

	public void render(Graphics2D g) {
		
		AffineTransform af = g.getTransform();
		
		int squareW = board.getSquareWidth();
		int squareH = board.getSquareHeight();
		
		int x = (this.x * squareW) + board.getX();
		int y = (this.y * squareH) + board.getY();
		
		int deltaW = squareW - texture.getWidth();
		int deltaH = squareH - texture.getHeight();
		
		g.translate(deltaW/2, deltaH/2);
		
		g.drawImage(texture, x, y, null);
		
//		g.drawString(position, x, y);
//		g.drawRect(x, y, LENGTH, LENGTH);
//		g.drawString(deltaW + ", " + deltaH + " width,height: " + squareW + ", " + squareH, x, y);
		
		g.setTransform(af);
		
	}
	
	/**
	 * {@code x} and {@code y} must both be indexes to where to set
	 * the new piece's position; not coordinates on a grid
	 */
	public boolean move(String newPosition) {
		
		//TODO: Fix all this by maybe sending the new coordinates if they're the client (they're BLACK)
		
		char newX = newPosition.charAt(0);
		char newY = newPosition.charAt(1);
		
		int x = toX(newX);
		int y = toY(newY);
		
		if(board.getColor() == WHITE) {
			y = Board.ROWS - y - 1;
			
		} else if(board.getColor() == BLACK) {
			x = Board.COLUMNS - x - 1;
			
		}
		
		//at this point locals are new position and fields are old ones
		
		HashMap<String, BoardPiece> pieces = board.getPieces();
		
		char oldX = this.position.charAt(0);
		char oldY = this.position.charAt(1);
		
//		System.out.println("oldX: " + oldX + " oldY: " + oldY);
		
		boolean checkPerpendiculars = true;
		
		// && oldX != newX && oldY != newY
		if(this instanceof DiagnolI) {
			if(isMovingDiagnol(newX, newY)) {
				
				if(isBlockedDiagnol(oldX, oldY, newPosition)) {
					return false;
				} else {
					//we can move in the diagonal direction and we are allowed to do so; skip the other checking
					checkPerpendiculars = false;
				}
				
			} else if(!(this instanceof HorizontalI) && !(this instanceof VerticalI)) {//dang, I dislike that...
				return false;
			}
			
		}
		
		if(checkPerpendiculars) {
			// && oldY == newY
			if(this instanceof HorizontalI && oldX != newX) {
				
				if(isBlockedHorizontal(oldX, newPosition)) {
					return false;
				} else {
					
				}
				
			}
			
			// && oldX == newX
			if(this instanceof VerticalI && oldY != newY) {
				
				if(isBlockedVertical(oldY, newPosition)) {
					return false;
				}
				
			}
		}
		
		//call non-abstract, custom-movement, method here for pawn, knight, and king
		if(customBlock(newPosition)) {
			return false;
		}
		
		newPosition = String.valueOf(newX) + String.valueOf(newY);
		
		BoardPiece piece = pieces.get(newPosition);
		
		//so that we don't eat our own guy
		if(piece != null) {
			if(piece.getPosition().equals(newPosition) && piece.getColor() == getColor()) {
				return false;
			}
			
			if(piece instanceof King) {
				//king got eaten
				board.setKingDown(true);
			}
			
		}
		
		this.x = x;
		this.y = y;
		
		pieces.put(newPosition, this);
		pieces.put(this.position, null);
		
		this.position = newPosition;
		
		return true;
		
	}
	
	/**
	 * Since this method calculates horizontally, it only requires the old x-position and where the piece would like to move (for y-value and
	 * simplicity)
	 * 
	 * @param oldX The x-position this piece starts at
	 * @param newPosition The new position this pieces is intended to move to
	 * @return 
	 */
	public boolean isBlockedHorizontal(char oldX, String newPosition) {
		
		char newX = newPosition.charAt(0);
		
		int min = oldX;
		int max = newX;
		
		if(this.position.charAt(1) != newPosition.charAt(1) && !isMovingDiagnol(newX, newPosition.charAt(1))) {
			return true;
		}
		
		if(min > max) {//switch the two
			int temp = max;
			
			max = min - 1;//so it doesn't check itself on the way back
			min = temp;
			
			char tempr = oldX;
			
			oldX = newX;
			newX = tempr;
		} else {
			min += 1;//so it doesn't get stuck on itself before it even starts
		}
		
		for(int i = min; i <= max; i++) {
			
//			System.out.println(((int) oldX) + " " + oldY);
			
			String stringX = String.valueOf((char) i);
			String stringY = String.valueOf(newPosition.charAt(1));
			
//			System.out.println("getting piece at: " + stringX + ", " + stringY + " i: " + i + " min: " + min + " max: " + max);
			
			BoardPiece piece = board.getPieces().get(stringX + stringY);
			
			try {
				
				if(!piece.getPosition().equals(newPosition)) {
					//if it equaled new position, it would eat it; otherwise, it just blocks it
					return true;
				}
				
			} catch(NullPointerException ex) {
				continue;
			}
		}
		
		return false;
	}
	
	public boolean isBlockedVertical(char oldY, String newPosition) {
		
		char newY = newPosition.charAt(1);
		
		int min = oldY;
		int max = newY;
		
		if(this.position.charAt(0) != newPosition.charAt(0) && !isMovingDiagnol(newPosition.charAt(0), newY)) {
			return true;
		}
		
		if(min > max) {//switch the two
			int temp = max;
			
			max = min - 1;//so it doesn't check itself on the way back
			min = temp;
			
			char tempr = oldY;
			
			oldY = newY;
			newY = tempr;
		} else {
			min += 1;//so it doesn't get stuck on itself before it even starts
		}
		
		for(int i = min; i <= max; i++) {
			
//			System.out.println(((int) oldX) + " " + oldY);
			
			String stringX = String.valueOf(newPosition.charAt(0));
			String stringY = String.valueOf((char) i);
			
//			System.out.println("getting piece at: " + stringX + ", " + stringY + " i: " + i + " min: " + min + " max: " + max);
			
			BoardPiece piece = board.getPieces().get(stringX + stringY);
			
			try {
				
				if(!piece.getPosition().equals(newPosition)) {
					//if it equaled new position, it would eat it; otherwise, it just blocks it
					return true;
				}
				
			} catch(NullPointerException ex) {
				continue;
			}
		}
		
		return false;
		
	}
	
	public boolean isBlockedDiagnol(char oldX, char oldY, String newPosition) {
		
		char newX = newPosition.charAt(0);
		char newY = newPosition.charAt(1);
		
		int dy = newY - oldY;
		int dx = newX - oldX;
		
		int min = 1;
		int max = Math.abs(dx);
		
//		System.out.println("Going from: " + min + " to: " + max);
		
		//positive or negative one
		int xSign = 1;
		int ySign = 1;
		
		if(dx == -Math.abs(dx)) {
			xSign = -1;
		}
		
		if(dy == -Math.abs(dy)) {
			ySign = -1;
		}
		
//		System.out.print("Check: ");
		
		//i=1; skips the first one... <=; goes up to that spot
		for(int i = min; i <= max; i++) {
			
			String letter = String.valueOf((char) (oldX + (i*xSign)));
			String number = String.valueOf((char) (oldY + (i*ySign)));
			
			BoardPiece piece = board.getPieces().get(letter + number);
			
//			System.out.print(letter + "," + number + " |");
			
			try {
				if(!piece.getPosition().equals(newPosition)) {
					return true;
				}
				
			} catch(NullPointerException ex) {
				continue;
			}
			
		}
		
//		System.out.println();
		
		return false;
	}
	
	private boolean isMovingDiagnol(char newX, char newY) {
		
		int dx = newX - position.charAt(0);
		int dy = newY - position.charAt(1);
		
		if(Math.abs(dx) == Math.abs(dy)) {
			return true;
		}
		
		return false;
		
	}
	
	private void loadTexture(int type) {
		
		int row = type & COLOUR_MASK;//only looks ate the first bit
		int column = type >> 1;//shifts byte over to the right by 1; getting rid of the right-most bit
		
		texture = PICTURE_SHEET.getSubimage(column * LENGTH, row * LENGTH, LENGTH, LENGTH);
		
	}
	
	/**
	 * Should return true only when the "custom movement" is being hindered and can't
	 * be performed; the pieces gets blocked
	 * 
	 * @param newPosition
	 * @return
	 */
	protected boolean customBlock(String newPosition) {
		return false;
	}
	
	//Could use these two methods to change it to a whole bit thing
	public int toX(char x) {
		
		return x - A;
		
	}
	
	public int toY(char y) {
		
		return y - ONE;
		
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getColor() {
		return colour;
	}
	
	public void setPosition(String position) {
		
		this.x = toX(position.charAt(0));
		this.y = toY(position.charAt(1));
		
		if(board.getColor() == BLACK) {
			this.x = Board.ROWS - this.x - 1;
			
		} else {
			this.y = Board.COLUMNS - this.y - 1;
			
		}
		
		this.position = position;
	}

	public String getPosition() {
		return position;
	}
	
}
