package com.rawad.chess.animations;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class LoadingCircle {
	
	private static final String FILE_NAME_BASE = "res/loading icon/frame_";
	private static final String FILE_EXTENSION = ".gif";
	
	private static final long SCENE_TIME = 20;
	
	private Animation anim;
	
	public LoadingCircle() {
		anim = new Animation();
		
		loadFrames();
		
	}
	
	public void update(long timePassed) {
		anim.update(timePassed);
	}
	
	public BufferedImage getCurrentImage() {
		return anim.getImage();
	}
	
	private void loadFrames() {
		
		try {
			
			BufferedImage image;
			
			for(int i = 0; i <= 30; i++) {//there are 30 frames
				
				String number = createTripleDigit(i);
				
//				System.out.println(number);
				
				image = ImageIO.read(new File(FILE_NAME_BASE + number + FILE_EXTENSION));
				
				anim.addScene(image, SCENE_TIME);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private String createTripleDigit(int i) {
		
		String num = i + "";
		
		while(num.length() < 3) {
			num = "0" + num;
		}
		
		return num;
		
	}
	
}
