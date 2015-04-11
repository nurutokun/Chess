package com.rawad.chess.animations;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {

	private ArrayList<Scene> scenes;
	
	private int sceneIndex;
	private long movieTime;
	private long totalTime;
	
	public Animation() {
		scenes = new ArrayList<Scene>();
		totalTime = 0;
		start();
	}
	
	public synchronized void addScene(BufferedImage i, long t) {
		totalTime += t;
		scenes.add(new Scene(i, totalTime));
	}
	
	public synchronized void start() {
		movieTime = 0;
		sceneIndex = 0;
	}
	
	public synchronized void update(long timePassed) {
		
		if (scenes.size() > 1) {
			movieTime += timePassed;
			
			if (movieTime >= totalTime) {
				movieTime = 0;
				sceneIndex = 0;
			}
			
			while (movieTime > getScene(sceneIndex).endTime) {
				sceneIndex++;
			}
		}
	}
	
	public synchronized BufferedImage getImage() {
		if (scenes.size() == 0) {
			return null;
		} else {
			return getScene(sceneIndex).pic;
		}
	}

	// get scene
	private Scene getScene(int i) {
		return scenes.get(i);
	}
	
	/////// Private inner class ///////
	
	/**
	 * Represents a single frame in this animation
	 * 
	 * @author TheNewBoston
	 *
	 */
	private class Scene {
		BufferedImage pic;
		long endTime;
		
		public Scene(BufferedImage pic, long endTime) {
			this.pic = pic;
			this.endTime = endTime;
		}
	}

}
