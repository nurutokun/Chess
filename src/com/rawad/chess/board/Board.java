package com.rawad.chess.board;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.rawad.chess.piecegeneration.GenerationAlgorithm;
import com.rawad.chess.pieces.BoardPiece;

/**
 * This holds the pieces then displayed to the player(s)
 * 
 * @author Rawad
 *
 */
public class Board {
	//		A	B	C	D	E	F	G	H
	//	8
	//	7
	//	6
	//	5
	//	4
	//	3
	//	2
	//	1
	//		A	B	C	D	E	F	G	H
	//	If this player has chosen to be white, this is what his board
	//	will look like; otherwise, it'll be reversed. Also, the server will
	//	keep a copy of this from white's perspective
	
	public static final int COLUMNS = 8;
	public static final int ROWS = 8;
	
	public static final int COLUMNS_BIT_LENGTH = Integer.toBinaryString(COLUMNS).length();
	public static final int ROWS_BIT_LENGTH = Integer.toBinaryString(ROWS).length();
	
	private static final String BOARD_TEXTURE_PATHNAME = "res/board.png";
	
	private static BufferedImage boardTexture;
	
	//String will look something like "A1" or "A2"
	private HashMap<String, BoardPiece> pieces;
	
	private int x;
	private int y;
	
	private int squareWidth;
	private int squareHeight;
	
	private int colorOnBottom;
	
	//could use int[][] for multiple selected states
	private boolean[][] selected;
	
	//previously selected square; so we can move pieces
	private int prevX;
	private int prevY;
	
	private String[] lastMoved;
	
	private boolean kingDown;
	
	//add parameter game which tells the board which
	//color the current player is
	public Board() {
		
		x = 40;
		y = 40;
		
		squareWidth = BoardPiece.LENGTH;
		squareHeight = BoardPiece.LENGTH;
		
		lastMoved = new String[2];
		
		selected = new boolean[ROWS][COLUMNS];
		
		loadTexture();
		
		kingDown = false;
		
	}
	
	/**
	 * Contains algorithm for creating the current player's display
	 * in terms of board pieces
	 * 
	 * @param color The color of the current player's pieces as specified by the {@code BoardPiece} Class
	 */
	public void createBoardPieces(int color, GenerationAlgorithm gen) {
		
		if(color != BoardPiece.WHITE && color != BoardPiece.BLACK) {
			return;
			
		} else {
			
			this.colorOnBottom = color;
			
			this.pieces = gen.generatePieces(this);
			
			kingDown = false;
		}
		
	}
	
	public void render(Graphics2D g) {
		
		Image pic = boardTexture.getScaledInstance(squareWidth * COLUMNS, squareHeight * ROWS, BufferedImage.SCALE_SMOOTH);
		
		//render actual board
		g.drawImage(pic, x, y, null);
		
		Color color = g.getColor();
		
		//render potential overlay; shows player where they can move current selected piece
		//maybe have an int[][] array to hold whether or not it's active to be colored in
		for(int y = 0; y < selected.length; y++) {
			for(int x = 0; x < selected[y].length; x++) {
				
				if(selected[y][x]) {//could also draw a binding frame of sorts
					g.setColor(new Color(200, 0, 0, 100));
					g.fillRect((x*squareWidth) + this.x, (y*squareHeight) + this.y, squareWidth, squareHeight);
				}
				
			}
		}
		
		g.setColor(color);
		
		drawNumbersAndLetters(g);
		
		//render pieces
		if(pieces != null) {
			for(int i = 0; i < ROWS; i++) {
				for(int j = 0; j <= COLUMNS; j++) {
					
					char x = (char) (i+BoardPiece.A);
					char y = (char) (j+BoardPiece.ONE);
					
					String pos = String.valueOf(x) + String.valueOf(y);
					
					BoardPiece p = pieces.get(pos);
					
					if(p != null) {
						p.render(g);
						
					}
					
				}
			}
		}
		
	}
	
	/**
	 * Used to draw the letters and numbers on the left/bottom based on 
	 * the orientation of the player
	 * 
	 * @param g
	 */
	private void drawNumbersAndLetters(Graphics2D g) {
		
		int max = (ROWS+COLUMNS)/2;
		
		FontMetrics metrics = g.getFontMetrics();
		
		//'A' = 65 'H' = 72
		for(int i = max + 57; i <= max + 64; i++) {
			
			int number = i-(max+56);
			char letter = (char) i;
			
			int displayNum = colorOnBottom == BoardPiece.BLACK? (max+65)-i:number;
			
			//x and y for number
			int x = this.x - metrics.stringWidth("" + displayNum);//slight offset
			int y = this.y + (squareHeight * ROWS) - (number * squareHeight) + metrics.getHeight();//started from the bottom
			
			g.drawString("" + displayNum, x, y);
			
			//x and y for letter
			x = this.x + ((number - 1) * squareWidth) + 3;
			y = this.y + (squareHeight * ROWS) + metrics.getHeight() - 3;
			
			g.drawString("" + (colorOnBottom == BoardPiece.BLACK? (char)((max+64)-(number-1)):letter), x, y);
			
		}
		
	}
	
	public boolean handleMouse(int x, int y) {
		
		if(	x > getX() && x < (getX() + getWidth()) &&
			y > getY() && y < (getY() + getHeight())) {
			
			x -= getX();
			y -= getY();
			
			for(int i = 0; i < COLUMNS; i++) {
				for(int j = 0; j < ROWS; j++) {
					
					if(	x > i * getSquareWidth() && (x) < (i * getSquareWidth()) + getSquareWidth() &&
						y > j * getSquareHeight() && (y) < (j * getSquareHeight()) + getSquareHeight()) {
						
						x /= getSquareWidth();//gets indexed positions
						y /= getSquareHeight();
						
						int numberX = invertX(x);
						int letterY = invertY(y);
						
						String letter = String.valueOf((char) (BoardPiece.A + numberX));
						String number = letterY + "";
						
						String position = letter + number;
						
						BoardPiece piece = pieces.get(position);
						
						if(selected[y][x]) {
							setSquare(x, y, false);
							
							return false;
							
						} else if(selected[prevY][prevX]) {
							
							numberX = invertX(prevX);
							letterY = invertY(prevY);
							
//							System.out.println("Previously cliked on: " + prevX + ", " + prevY);
							
							letter = String.valueOf((char) (BoardPiece.A + numberX));
							number = letterY + "";
							
							String oldPosition = pieces.get(letter + number).getPosition();
							
//							System.out.println("You moved a piece from: " + oldPosition + " to: " + position);
							
							if(movePiece(oldPosition, position)) {
								lastMoved[0] = oldPosition;
								lastMoved[1] = position;
								
							} else {
								return false;
							}
							
							deselectAllSquares();
							
							return true;
							
						} else if(piece != null && piece.getColor() == getColor()) {
							deselectAllSquares();
							
							prevX = x;
							prevY = y;
							
							setSquare(x, y, true);
							
							return false;
						}
						
					}
				}
			}
		}
		
		return false;
		
	}
	
	public void setKingDown(boolean kingDown) {
		this.kingDown = kingDown;
	}
	
	public boolean isKingDown() {
		return this.kingDown;
	}
	
	public int invertY(int y) {
		
		int re = y;
		
		if(getColor() == BoardPiece.BLACK) {
			re += 1;
		} else if(getColor() == BoardPiece.WHITE) {
			re = ROWS - y;
		}
		
		return re;
		
	}
	
	public int invertX(int x) {
		
		int re = x;
		
		if(getColor() == BoardPiece.BLACK) {
			re = COLUMNS - x - 1;
		}
		
		return re;
		
	}
	
	public boolean movePiece(String oldPosition, String newPosition) {
		
		boolean re = false;
		
		BoardPiece piece = pieces.get(oldPosition);
		
		if(piece != null) {
			re = piece.move(newPosition);
		}
		
		return re;
	}
	
	/**
	 * Optional to the {@code movePiece()} method. This directly moves the piece as a result of its new position having already been calculated
	 * from the server/client by the {@code movePiece()} method
	 * 
	 * @param oldPosition
	 * @param newPosition
	 */
	public void setNewPiecePosition(String oldPosition, String newPosition) {
		
		BoardPiece piece = pieces.get(oldPosition);
		
		if(piece != null) {
			piece.setPosition(newPosition);
			
			pieces.put(oldPosition, null);
			pieces.put(newPosition, piece);
			
//			System.out.println(pieces.get(newPosition) + " " + pieces.get(oldPosition));
			
//			System.out.println("moved: " + oldPosition + " to: " + newPosition);
		}
		
	}
	
	private void loadTexture() {
		
		try {
			boardTexture = ImageIO.read(new File(BOARD_TEXTURE_PATHNAME));
			
		} catch(IOException ex) {
			System.out.println("no board image found; creating new board image");
			
			boardTexture = createBoardTexture();
			
		}
		
	}
	
	private BufferedImage createBoardTexture() {
		
		BufferedImage temp = new BufferedImage(COLUMNS * squareWidth, ROWS * squareHeight, BufferedImage.TYPE_INT_ARGB);
		
		final Color greenSquare = new Color(204, 255, 204);//light green
		
		for(int i = 0; i < temp.getHeight(); i++) {
			for(int j = 0; j < temp.getWidth(); j++) {
				
				//creates pattern
				if(((i/squareHeight)%2 == 0) && ((j/squareWidth)%2 == 0) || ((i/squareHeight)%2 == 1) && ((j/squareWidth)%2 == 1)) {
					temp.setRGB(j, i, greenSquare.getRGB());
					
				} else {
					temp.setRGB(j, i, 0x0F00FF00);//second byte is for green so we'll give it a light green tint
					
				}
			}
		}
		
		try {
			ImageIO.write(temp, "png", new File(BOARD_TEXTURE_PATHNAME));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return temp;
		
	}
	
	/**
	 * Should take indexes based on calculations made involving
	 * all the movement interfaces
	 * 
	 * @param x 
	 * @param y 
	 */
	private void setSquare(int x, int y, boolean value) {
		
		try {
			selected[y][x] = value;
		} catch(IndexOutOfBoundsException ex) {
			System.out.println("IndexOutOfBonds in the select square method");
		}
		
	}
	
	private void deselectAllSquares() {
		
		for(int i = 0; i < selected.length; i++) {
			for(int j = 0; j < selected[i].length; j++) {
				setSquare(j, i, false);
			}
		}
		
	}
	
	public HashMap<String, BoardPiece> getPieces() {
		return pieces;
	}
	
	public String[] getLastMoved() {
		return lastMoved;
	}
	
	public int getColor() {
		return colorOnBottom;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getSquareWidth() {
		return squareWidth;
	}
	
	public int getWidth() {
		return squareWidth * COLUMNS;
	}
	
	public void setWidth(int width) {
		this.squareWidth = width/COLUMNS;
	}
	
	public int getSquareHeight() {
		return squareHeight;
	}
	
	public int getHeight() {
		return ROWS * squareHeight;
	}
	
	public void setHeight(int height) {
		this.squareHeight = height/ROWS;
	}
	
}
