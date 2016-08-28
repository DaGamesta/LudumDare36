package com.mwojnar.GameObjects;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameWorld.LudumDare36World;
import com.playgon.GameEngine.Collision;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.Mask;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameRenderer;
import com.playgon.GameWorld.GameWorld;
import com.playgon.Utils.PlaygonMath;

public class Reaper extends Entity {
	
	private LudumDare36World.Ammo ammo = LudumDare36World.Ammo.CORN;
	private float acceleration = 1.0f, maxSpeed = 4.0f, traction = 1.0f;
	private int cornAmmo = 0, maxCornAmmo = 25, wheatAmmo = 0, maxWheatAmmo = 25, riceAmmo = 0, maxRiceAmmo = 25, cooldown = 0, cooldownMax = 22, crossbowFrame = 0, crossbowAnimatingDelayTimer = 0, oxFrame = 0;
	private boolean crossbowAnimating = false, isSlowCrossbowAnimating = false;
	private Vector2[] stackOffsets = new Vector2[25];
	
	public Reaper(GameWorld myWorld) {
		
		super(myWorld);
		setSprite(AssetLoader.spriteReaper);
		setMask(new Mask(this, getSprite().getWidth(), getSprite().getHeight()));
		setDepth(50);
		Random rand = new Random();
		for (int i = 0; i < 25; i++) {
			
			stackOffsets[i] = new Vector2(rand.nextInt(10), rand.nextInt(10));
			
		}
		
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
		if (keysFirstDown.contains(com.badlogic.gdx.Input.Keys.SHIFT_LEFT) || keysFirstDown.contains(com.badlogic.gdx.Input.Keys.SHIFT_RIGHT)) {
			
			int ammoAmount = 0;
			switch (ammo) {
			
			case CORN: ammo = LudumDare36World.Ammo.WHEAT; ammoAmount = wheatAmmo; break;
			case WHEAT: ammo = LudumDare36World.Ammo.CORN; ammoAmount = cornAmmo; break;
			case RICE: ammo = LudumDare36World.Ammo.CORN; ammoAmount = cornAmmo; break;
			
			}
			if (ammoAmount <= 0) {

				crossbowAnimating = false;
				crossbowAnimatingDelayTimer = 0;
				crossbowFrame = 0;
				
			} else if (!crossbowAnimating) {
				
				crossbowFrame = 9;
				
			}
			
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
			
			if (entity instanceof PlantPickup) {
				
				List<Collision> collisions = collisionsWith(entity);
				if (!collisions.isEmpty() && entity.getPos(false).x + entity.getSprite().getWidth() > getPos(false).x + getSprite().getWidth() - 26) {
					
					entity.destroy();
					switch (((PlantPickup)entity).getType()) {
					
					case CORN: if (cornAmmo < maxCornAmmo) {
						
						cornAmmo++;
						if (ammo == LudumDare36World.Ammo.CORN && !crossbowAnimating && crossbowFrame == 0) {
							
							crossbowAnimating = true;
							crossbowAnimatingDelayTimer = 0;
							cooldown = 10;
							
						}
						
					} break;
					case WHEAT: if (wheatAmmo < maxWheatAmmo) {
						
						wheatAmmo++;
						if (ammo == LudumDare36World.Ammo.WHEAT && !crossbowAnimating && crossbowFrame == 0) {
							
							crossbowAnimating = true;
							crossbowAnimatingDelayTimer = 0;
							cooldown = 10;
							
						}
						
					} break;
					case RICE: if (riceAmmo < maxRiceAmmo) {
						
						riceAmmo++;
						if (ammo == LudumDare36World.Ammo.RICE && !crossbowAnimating && crossbowFrame == 0) {
							
							crossbowAnimating = true;
							crossbowAnimatingDelayTimer = 0;
							cooldown = 10;
							
						}
						
					} break;
					
					}
					
				}
				
			}
			
		}
		
		if (crossbowAnimating) {
			
			if (crossbowAnimatingDelayTimer > 0) {
				
				crossbowAnimatingDelayTimer--;
				
			} else if (!isSlowCrossbowAnimating || ((LudumDare36World)getWorld()).getFramesSinceLevelCreation() % 4 == 0) {
				
				crossbowFrame++;
				if (crossbowFrame > 9) {
					
					crossbowFrame = 9;
					crossbowAnimating = false;
					isSlowCrossbowAnimating = false;
					
				}
				
			}
			
		}
		if (cooldown > 0) {
			
			cooldown--;
			
		} else if (keysDown.contains(com.badlogic.gdx.Input.Keys.SPACE) && getAmmo() > 0) {

			shoot();
			
		}
		if (((LudumDare36World)getWorld()).getFramesSinceLevelCreation() % 8 == 0) {

			oxFrame++;
			
		}
		if (oxFrame > 7) {
			
			oxFrame = 0;
			
		}
		
	}
	
	private void shoot() {
		
		cooldown = cooldownMax;
		if (ammo == LudumDare36World.Ammo.WHEAT) {
			
			cooldown = cooldownMax * 4;
			
		}
		switch (ammo) {
		
		case CORN: getWorld().createEntity(new PlantBullet(getWorld()).setPos(getPos(false).x + getSprite().getWidth(), getPos(false).y + getSprite().getHeight() / 2.0f, true)); break;
		case WHEAT: Random rand = new Random();
		for (int i = 0; i < 8; i++) {
			
			int randAmount = rand.nextInt(11) - 5;
			getWorld().createEntity(new PlantBullet(getWorld()).setType(LudumDare36World.Ammo.WHEAT).setPos(getPos(false).x + getSprite().getWidth(), getPos(false).y + getSprite().getHeight() / 2.0f - 17.5f + i * 5.0f, true).setRotation(-22.0f + 1.0f / 3.0f + i * 10.0f * 2.0f / 3.0f + randAmount).setRadialVelocity(8.0f, PlaygonMath.toRadians(-22.0f + 1.0f / 3.0f + i * 10.0f * 2.0f / 3.0f + randAmount)));
			
		}break;
		case RICE: getWorld().createEntity(new PlantBullet(getWorld()).setType(LudumDare36World.Ammo.RICE).setPos(getPos(false).x + getSprite().getWidth(), getPos(false).y + getSprite().getHeight() / 2.0f, true)); break;
		
		}
		setAmmo(getAmmo() - 1);
		crossbowFrame = 0;
		if (getAmmo() > 0) {
			
			crossbowAnimating = true;
			crossbowAnimatingDelayTimer = 12;
			if (ammo == LudumDare36World.Ammo.WHEAT) {
				
				crossbowAnimatingDelayTimer = cooldownMax * 4 - 40;
				isSlowCrossbowAnimating = true;
				
			}
			
		}
		
	}

	private void setAmmo(int amount) {
		
		switch (ammo) {
		
		case CORN: cornAmmo = amount; break;
		case WHEAT: wheatAmmo = amount; break;
		case RICE: riceAmmo = amount; break;
		
		}
		
	}

	private int getAmmo() {
		
		switch (ammo) {
		
		case CORN: return cornAmmo;
		case WHEAT: return wheatAmmo;
		case RICE: return riceAmmo;
		
		}
		return 0;
		
	}

	@Override
	public void draw(GameRenderer renderer) {
		
		AssetLoader.spriteOx.draw(getPos(false).x, getPos(false).y, oxFrame, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, renderer);
		super.draw(renderer);
		AssetLoader.spriteCrossbow.draw(getPos(false).x, getPos(false).y, crossbowFrame, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, renderer);
		if (getAmmo() > 0 && !crossbowAnimating) {
			
			switch (ammo) {
			
			case CORN: AssetLoader.spriteCornStalkShot.draw(getPos(false).x + 80.0f, getPos(false).y + getSprite().getHeight() / 2.0f - AssetLoader.spriteCornStalkShot.getHeight() / 2.0f, 0, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, renderer); break;
			case WHEAT: AssetLoader.spriteWheatBunch.draw(getPos(false).x + 80.0f, getPos(false).y + getSprite().getHeight() / 2.0f - AssetLoader.spriteWheatBunch.getHeight() / 2.0f, 0, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, renderer); break;
			case RICE: AssetLoader.spriteRiceShot.draw(getPos(false).x + 80.0f, getPos(false).y + getSprite().getHeight() / 2.0f - AssetLoader.spriteRiceShot.getHeight() / 2.0f, 0, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, renderer); break;
			
			}
			
		}
		if (getAmmo() > 1) {
			
			for (int i = 0; i < getAmmo() - 1; i++) {
				
				switch (ammo) {
				
				case CORN: AssetLoader.spriteCornStalkShot.draw(getPos(false).x + 120.0f + stackOffsets[i].x, getPos(false).y + getSprite().getHeight() / 2.0f + AssetLoader.spriteCornStalkShot.getWidth() / 2.0f - 5 + stackOffsets[i].y, 0, 1.0f, 1.0f, -90.0f, 0.0f, 0.0f, renderer); break;
				case WHEAT: AssetLoader.spriteWheatBunch.draw(getPos(false).x + 120.0f + stackOffsets[i].x, getPos(false).y + getSprite().getHeight() / 2.0f + AssetLoader.spriteWheatBunch.getWidth() / 2.0f - 5 + stackOffsets[i].y, 0, 1.0f, 1.0f, -90.0f, 0.0f, 0.0f, renderer); break;
				case RICE: AssetLoader.spriteRiceShot.draw(getPos(false).x + 120.0f + stackOffsets[i].x, getPos(false).y + getSprite().getHeight() / 2.0f + AssetLoader.spriteRiceShot.getWidth() / 2.0f - 5 + stackOffsets[i].y, 0, 1.0f, 1.0f, -90.0f, 0.0f, 0.0f, renderer); break;
				
				}
				
			}
		}
		
		AssetLoader.debugFont.draw(renderer.getBatcher(), Integer.toString(getAmmo()), 0.0f, 0.0f);
		
	}
	
}
