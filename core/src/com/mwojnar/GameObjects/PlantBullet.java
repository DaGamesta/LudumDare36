package com.mwojnar.GameObjects;

import java.util.List;

import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameWorld.LudumDare36World;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.Mask;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameWorld;

public class PlantBullet extends Entity {
	
	private float speed = 8.0f;
	private LudumDare36World.Ammo type = LudumDare36World.Ammo.CORN;
	
	public PlantBullet(GameWorld myWorld) {
		
		super(myWorld);
		setSprite(AssetLoader.spriteCornStalkShot);
		setMask(new Mask(this, getSprite().getWidth(), getSprite().getHeight()));
		setPivot(getSprite().getWidth() / 2.0f, getSprite().getHeight() / 2.0f);
		setGridVelocity(speed, 0.0f);
		
	}
	
	@Override
	public void update(float delta, List<TouchEvent> touchEventList, List<Character> charactersTyped, List<Integer> keysFirstDown, List<Integer> keysFirstUp, List<Integer> keysDown) {
		
		super.update(delta, touchEventList, charactersTyped, keysFirstDown, keysFirstUp, keysDown);
		moveByVelocity();
		if (getPos(false).x > getWorld().getGameDimensions().x) {
			
			destroy();
			
		}
		
	}
	
	public LudumDare36World.Ammo getType() {
		
		return type;
		
	}
	
	public PlantBullet setType(LudumDare36World.Ammo type) {
		
		this.type = type;
		switch(type) {
		
		case CORN: setSprite(AssetLoader.spriteCornStalkShot); break;
		case WHEAT: setSprite(AssetLoader.spriteWheatShot); break;
		case RICE: setSprite(AssetLoader.spriteRiceShot); break;
		
		}
		
		return this;
		
	}
	
}