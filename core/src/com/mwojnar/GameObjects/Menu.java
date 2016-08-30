package com.mwojnar.GameObjects;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameWorld.LudumDare36World;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameRenderer;
import com.playgon.GameWorld.GameWorld;

public class Menu extends Entity {
	
	private boolean blink = false;
	private int blinkTimer = 60, blinkTimerMax = 60;
	
	public Menu(GameWorld myWorld) {
		
		super(myWorld);
		
	}
	
	@Override
	public void update(float delta, List<TouchEvent> touchEventList, List<Character> charactersTyped, List<Integer> keysFirstDown, List<Integer> keysFirstUp, List<Integer> keysDown) {
		
		super.update(delta, touchEventList, charactersTyped, keysFirstDown, keysFirstUp, keysDown);
		
		if (keysFirstDown.contains(com.badlogic.gdx.Input.Keys.ENTER)) {
			
			((LudumDare36World)getWorld()).startGame();
			
		}
		blinkTimer--;
		if (blinkTimer <= 0) {
			
			blinkTimer = blinkTimerMax;
			blink = !blink;
			
		}
		
	}
	
	@Override
	public void draw(GameRenderer renderer) {
		
		AssetLoader.titleFont.draw(renderer.getBatcher(), "Reaper Rampage", getWorld().getGameDimensions().x / 4.0f, getWorld().getGameDimensions().y / 6.0f, getWorld().getGameDimensions().x / 2.0f, Align.center, true);
		if (!blink) {
			
			AssetLoader.debugFont.draw(renderer.getBatcher(), "Press Enter", 0.0f, getWorld().getGameDimensions().y * 3.0f / 4.0f, getWorld().getGameDimensions().x, Align.center, false);
			
		}
		
	}
	
}