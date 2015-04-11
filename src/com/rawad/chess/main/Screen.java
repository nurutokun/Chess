package com.rawad.chess.main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;

import com.Rawad.Input.KeyboardInput;
import com.Rawad.Input.MouseInput;

public class Screen extends JPanel implements MouseListener, MouseMotionListener, KeyListener, WindowListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8964403441344517420L;
	
	private Game game;
	
	public Screen(int width, int height) {
		
		this.setSize(new Dimension(width, height));
		this.setPreferredSize(new Dimension(width, height));
		this.setBounds(0, 0, width, height);
		this.setMinimumSize(new Dimension(width, height));
		
		setLayout(new CardLayout(10, 10));
		
		setBackground(Color.WHITE);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(game != null) {
			game.render((Graphics2D) g);
		}
		
		g.dispose();
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
		if(e.isAltDown()) {
		
		} else if(e.isControlDown()) {
			
		} else {
			MouseInput.leftMouse(true);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		MouseInput.leftMouse(false);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		MouseInput.setX(e.getX());
		MouseInput.setY(e.getY());
	}

	@Override
	public void windowOpened(WindowEvent e) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		Frame f = (Frame) e.getSource();
		
		f.dispose();
		System.exit(0);
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		KeyboardInput.keyDown(e.getKeyCode());
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		KeyboardInput.keyUp(e.getKeyCode());
		
	}
	
}
