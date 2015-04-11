package com.rawad.chess.guis;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Button {
	
	public static final int BUTTON_WIDTH = 100;
	public static final int BUTTON_HEIGHT = 30;
	
	public static final int MAX_SELECTED_BORDER = 3;
	
	private String text;
	
	private int x;
	private int y;
	
	private int width;
	private int height;
	
	private boolean highlighted;
	
	public Button(String text, int width, int height) {
		
		this.text = text;
		
		//initial x and y won't matter because they'll be updated almost immediately
		this.x = 0;
		this.y = 0;
		
		this.width = width;
		this.height = height;
		
		highlighted = true;
		
	}
	
	public void render(Graphics2D g) {
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y, width, height);
		
		FontMetrics metrics = g.getFontMetrics();
		
		int height = metrics.getHeight();
		int width = metrics.stringWidth(text);
		
		int x = this.x + (this.width - width)/2;
		int y = this.y + (this.height/2) + (height/2);
		
		g.setColor(Color.CYAN);
		g.drawString(text, x, y);
		
		if(highlighted) {
			
			g.setColor(Color.RED);
			
			for(int i = 0; i < MAX_SELECTED_BORDER; i++) {
				g.drawRect(this.x + i, this.y + i, this.width - (i*2), this.height - (i*2));
			}
			
		}
		
	}
	
	public boolean intersects(int x, int y) {
		
		if(	(x >= this.x && x <= (this.x + width)) &&
			(y >= this.y && y <= (this.y + height))) {
			
			return true;
			
		}
		
		return false;
		
	}

	public void setHighlighted(boolean highlighted) {
		
		this.highlighted = highlighted;
	}
	
	public boolean isHighlighted() {
		return this.highlighted;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setX(int x) {
		this.x = x - (width/2);
	}
	
	public int getX() {
		return this.x;
	}

	public void setY(int y) {
		this.y = y - (height/2);
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
}
