package com.mwojnar.GameObjects;

import java.util.List;

import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameWorld.LudumDare36World;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.Mask;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameWorld;

public class CornStalk extends Entity {
	
	public CornStalk(GameWorld myWorld) {
		
		super(myWorld);
		setSprite(AssetLoader.spriteCornStalk);
		setMask(new Mask(this, getSprite().getWidth(), getSprite().getHeight()));
		
	}
	
	@Override
	public void update(float delta, List<TouchEvent> touchEventList, List<Character> charactersTyped, List<Integer> keysFirstDown, List<Integer> keysFirstUp, List<Integer> keysDown) {
		
		super.update(delta, touchEventList, charactersTyped, keysFirstDown, keysFirstUp, keysDown);
		setGridVelocity(((LudumDare36World)getWorld()).getScrollSpeed(), 0.0f);
		moveByVelocity();
		if (getPos(false).x + getSprite().getWidth() < 0.0f) {
			
			destroy();
			
		}
		
	}
	
}