package de.jcing.jcingworld.main;

import de.jcing.jcingworld.engine.GraphicsLoader;
import de.jcing.jcingworld.engine.rendering.GUIRenderer;
import de.jcing.jcingworld.engine.rendering.MasterRenderer;
import de.jcing.jcingworld.game.GUIManager;
import de.jcing.jcingworld.game.Game;

public class Main {
	
	
	
	
	public static void main(String[] args) {
		
		
		
	}
	
	
	private static Game game;
	private static GraphicsLoader loader;
	private static MasterRenderer renderer;
	private static GUIRenderer guirenderer;
	private static GUIManager gui;
	
	
	public static Game game() {
		return game;
	}
	
	public static GraphicsLoader loader() {
		return loader;
	}
//	
//	public static Render render() {
//		return null;
//	}
//	
//	public static GUI gui() {
//		return null;
//	}
	
	
}
