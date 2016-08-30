package com.mwojnar.GameObjects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Align;
import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameWorld.LudumDare36World;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameRenderer;
import com.playgon.GameWorld.GameWorld;

public class ScoreBoard extends Entity {
	
	private int score = 0;
	private List<Integer> highScores = new ArrayList<Integer>();
	
	public ScoreBoard(GameWorld myWorld) {
		
		super(myWorld);
		setDepth(1000);
		score = ((LudumDare36World)myWorld).getScore();
		highScores.clear();
		highScores = ((LudumDare36World)myWorld).getHighScores();
		for (int i = 0; i < 5; i++) {
			
			if (highScores.get(i) < score) {
				
				highScores.remove(4);
				highScores.add(i, score);
				break;
				
			}
			
		}
		Preferences preferences = Gdx.app.getPreferences("LudumDare36 Prefs");
		for (int i = 0; i < 5; i++) {
			
			preferences.putInteger("score" + Integer.toString(i + 1), highScores.get(i));
			preferences.flush();
			
		}
		
	}
	
	@Override
	public void draw(GameRenderer renderer) {
		
		AssetLoader.debugFont.draw(renderer.getBatcher(), "Score: " + Integer.toString(score), 0, 100, 1280.0f, Align.center, false);
		AssetLoader.debugFont.draw(renderer.getBatcher(), "High Scores", 0, 200, 1280.0f, Align.center, false);
		AssetLoader.debugFont.draw(renderer.getBatcher(), "___________", 0, 250, 1280.0f, Align.center, false);
		for (int i = 0; i < 5; i++) {
			
			AssetLoader.debugFont.draw(renderer.getBatcher(), "#" + Integer.toString(i + 1) + ". " + highScores.get(i), 0, 300 + 50 * i, 1280.0f, Align.center, false);
			
		}
		
	}
	
	@Override
	public void update(float delta, List<TouchEvent> touchEventList, List<Character> charactersTyped, List<Integer> keysFirstDown, List<Integer> keysFirstUp, List<Integer> keysDown) {
		
		super.update(delta, touchEventList, charactersTyped, keysFirstDown, keysFirstUp, keysDown);
		
		if (keysFirstDown.contains(com.badlogic.gdx.Input.Keys.ENTER )) {
			
			((LudumDare36World)getWorld()).startMenu();
			
		}
		
	}
	
}