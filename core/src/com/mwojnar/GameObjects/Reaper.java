package com.mwojnar.GameObjects;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameWorld.LudumDare36World;
import com.playgon.GameEngine.Collision;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.Mask;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameRenderer;
import com.playgon.GameWorld.GameWorld;

public class Reaper extends Entity {
	
	private float acceleration = 1.0f, maxSpeed = 4.0f, traction = 1.0f;
	private int cornAmmo = 0, maxCornAmmo = 25, cooldown = 0, cooldownMax = 15;
	
	public Reaper(GameWorld myWorld) {
		
		super(myWorld);
		setSprite(AssetLoader.spriteReaper);
		setMask(new Mask(this, getSprite().getWidth(), getSprite().getHeight()));
		
	}
	
	@Override
	public void update(float delta, List<TouchEvent> touchEventList, List<Character> charactersTyped, List<Integer> keysFirstDown, List<Integer> keysFirstUp, List<Integer> keysDown) {
		
		super.update(delta, touchEventList, charactersTyped, keysFirstDown, keysFirstUp, keysDown);
		
		boolean verPressed = false, horPressed = false;
		if (keysDown.contains(com.badlogic.gdx.Input.Keys.UP)) {
			
			setGridVelocity(getGridVelocity().x, getGridVelocity().y - acceleration);
			verPressed = true;
			
		}
		if (keysDown.contains(com.badlogic.gdx.Input.Keys.DOWN)) {
			
			setGridVelocity(getGridVelocity().x, getGridVelocity().y + acceleration);
			verPressed = true;
			
		}
		if (keysDown.contains(com.badlogic.gdx.Input.Keys.LEFT)) {
			
			setGridVelocity(getGridVelocity().x - acceleration, getGridVelocity().y);
			horPressed = true;
			
		}
		if (keysDown.contains(com.badlogic.gdx.Input.Keys.RIGHT)) {
			
			setGridVelocity(getGridVelocity().x + acceleration, getGridVelocity().y);
			horPressed = true;
			
		}
		if (!verPressed) {
			
			if (getGridVelocity().y >= traction) {
				
				setGridVelocity(getGridVelocity().x, getGridVelocity().y - traction);
				
			} else if (getGridVelocity().y <= -traction) {
				
				setGridVelocity(getGridVelocity().x, getGridVelocity().y + traction);
				
			} else {
				
				setGridVelocity(getGridVelocity().x, 0.0f);
				
			}
			
		}
		if (!horPressed) {
			
			if (getGridVelocity().x >= traction) {
				
				setGridVelocity(getGridVelocity().x - traction, getGridVelocity().y);
				
			} else if (getGridVelocity().x <= -traction) {
				
				setGridVelocity(getGridVelocity().x + traction, getGridVelocity().y);
				
			} else {
				
				setGridVelocity(0.0f, getGridVelocity().y);
				
			}
			
		}
		
		if (getGridVelocity().x > maxSpeed) {
			
			setGridVelocity(maxSpeed, getGridVelocity().y);
			
		}
		if (getGridVelocity().x < -maxSpeed) {
			
			setGridVelocity(-maxSpeed, getGridVelocity().y);
			
		}
		if (getGridVelocity().y > maxSpeed) {
			
			setGridVelocity(getGridVelocity().x, maxSpeed);
			
		}
		if (getGridVelocity().y < -maxSpeed) {
			
			setGridVelocity(getGridVelocity().x, -maxSpeed);
			
		}
		
		moveByVelocity();
		
		if (getPos(false).x < 0) {
			
			setPos(0.0f, getPos(false).y, false);
			
		}
		if (getPos(false).x + getSprite().getWidth() > getWorld().getGameDimensions().x) {
			
			setPos(getWorld().getGameDimensions().x - getSprite().getWidth(), getPos(false).y, false);
			
		}
		if (getPos(false).y < 0) {
			
			setPos(getPos(false).x, 0.0f, false);
			
		}
		if (getPos(false).y + getSprite().getHeight() > getWorld().getGameDimensions().y) {
			
			setPos(getPos(false).x, getWorld().getGameDimensions().y - getSprite().getHeight(), false);
			
		}
		
		for (Entity entity : getWorld().getActiveEntityList()) {
			
			if (entity instanceof CornStalk) {
				
				List<Collision> collisions = collisionsWith(entity);
				if (!collisions.isEmpty()) {
					
					entity.destroy();
					if (cornAmmo < maxCornAmmo) {
						
						cornAmmo++;
						
					}
					
				}
				
			}
			
		}
		
		if (cooldown > 0) {
			
			cooldown--;
			
		} else if (keysDown.contains(com.badlogic.gdx.Input.Keys.SPACE) && cornAmmo > 0) {
			
			cooldown = cooldownMax;
			getWorld().createEntity(new CornShot(getWorld()).setPos(getPos(false).x + getSprite().getWidth(), getPos(false).y + getSprite().getHeight() / 2.0f, true));
			cornAmmo--;
			
		}
		
	}
	
	@Override
	public void draw(GameRenderer renderer) {
		
		super.draw(renderer);
		
		AssetLoader.debugFont.draw(renderer.getBatcher(), Integer.toString(cornAmmo), 0.0f, 0.0f);
		
	}
	
}
