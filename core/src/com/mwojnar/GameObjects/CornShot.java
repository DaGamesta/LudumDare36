package com.mwojnar.GameObjects;

import java.util.List;

import com.mwojnar.Assets.AssetLoader;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.Mask;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameWorld;

public class CornShot extends Entity {
	
	private float speed = 8.0f;
	
	public CornShot(GameWorld myWorld) {
		
		super(myWorld);
		setSprite(AssetLoader.spriteCornStalk);
		setMask(new Mask(this, getSprite().getWidth(), getSprite().getHeight()));
		setPivot(getSprite().getWidth() / 2.0f, getSprite().getHeight() / 2.0f);
		setRotation(90.0f);
		setGridVelocity(speed, 0.0f);
		
	}
	
	@Override
	public void update(float delta, List<TouchEvent> touchEventList, List<Character> charactersTyped, List<Integer> keysFirstDown, List<Integer> keysFirstUp, List<Integer> keysDown) {
		
		super.update(delta, touchEventList, charactersTyped, keysFirstDown, keysFirstUp, keysDown);
		moveByVelocity();
		
	}
	
}