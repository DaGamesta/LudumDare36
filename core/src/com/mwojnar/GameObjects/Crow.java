package com.mwojnar.GameObjects;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.mwojnar.Assets.AssetLoader;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.Mask;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameWorld;
import com.playgon.Utils.PlaygonMath;

public class Crow extends Enemy {
	
	public enum Mode { INITIAL, TURNING, ATTACKING }
	
	private float speed = 5.0f, acceleration = 0.5f, rotationAcceleration = 1.0f;
	private int pauseTimer = 90;
	private boolean hostile = true;
	private Mode mode = Mode.INITIAL;
	private Reaper reaper = null;
	private float xStop = 1100.0f;
	
	public Crow(GameWorld myWorld) {
		
		super(myWorld);
		setSprite(AssetLoader.spriteCrow1);
		setMask(new Mask(this, new Vector2(getSprite().getWidth() / 2.0f, getSprite().getHeight() / 2.0f), 8.0f));
		setPivot(getSprite().getWidth() / 2.0f, getSprite().getHeight() / 2.0f);
		setDepth(100);
		setGridVelocity(-speed, 0);
		setHP(2);
		
	}
	
	@Override
	public void update(float delta, List<TouchEvent> touchEventList, List<Character> charactersTyped, List<Integer> keysFirstDown, List<Integer> keysFirstUp, List<Integer> keysDown) {
		
		super.update(delta, touchEventList, charactersTyped, keysFirstDown, keysFirstUp, keysDown);
		
		switch (mode) {
		
		case INITIAL:
			if (getPos(false).x <= xStop) {
				
				setGridVelocity(getGridVelocity().x + acceleration, 0.0f);
				if (getGridVelocity().x >= 0.0f) {
					
					setGridVelocity(0.0f, 0.0f);
					mode = Mode.TURNING;
					setFrame(4);
					
				}
				
			}
			break;
		case TURNING:
			float direction = PlaygonMath.direction(getPos(true), getReaper().getPos(true));
			float rotation = PlaygonMath.fixAngle(PlaygonMath.toRadians(getRotation() + 180.0f));
			if (direction != rotation) {
				
				if (!PlaygonMath.withinRotationFromDirection(direction, rotation, PlaygonMath.toRadians(rotationAcceleration), PlaygonMath.toRadians(rotationAcceleration))) {
					
					if (direction < rotation) {
						
						if (rotation - direction > Math.PI) {
							
							setRotation(getRotation() + rotationAcceleration);
							
						} else {
							
							setRotation(getRotation() - rotationAcceleration);
							
						}
						
					} else {
						
						if (direction - rotation > Math.PI) {
							
							setRotation(getRotation() - rotationAcceleration);
							
						} else {
							
							setRotation(getRotation() + rotationAcceleration);
							
						}
						
					}
					
				} else {
					
					setRotation(PlaygonMath.toDegrees(direction) + 180.0f);
					
				}
				
			}
			if (pauseTimer > 0) {
				
				pauseTimer--;
				
			} else {
				
				mode = Mode.ATTACKING;
				setAnimationSpeed(0.0f);
				setRadialVelocity(0.0f, PlaygonMath.toRadians(getRotation() + 180.0f));
				
			}
			break;
		case ATTACKING:
			if (getRadialVelocity().x < speed * 2.0f) {
				
				setRadialVelocity(getRadialVelocity().x + acceleration, getRadialVelocity().y);
				
			} else {
				
				setRadialVelocity(speed * 2.0f, getRadialVelocity().y);
				
			}
			if (getPos(false).x < -50 || getPos(false).x > 1330 || getPos(false).y < -50 || getPos(false).y > 770) {
				
				destroy();
				
			}
		
		}
		moveByVelocity();
		if (getReaper().collisionsWith(this, getReaper().bullMask).size() > 0 && hostile && getReaper().hurt(2)) {
			
			AssetLoader.sndCrowHit.play(AssetLoader.soundVolume);
			hostile = false;
			
		}
		if (mode == Mode.ATTACKING) {
			
			for (Entity entity : getWorld().getActiveEntityList()) {
				
				if (entity instanceof PlantPickup) {
					
					if (!((PlantPickup)entity).isTrampled() && !collisionsWith(entity).isEmpty()) {
						
						AssetLoader.sndTrample.play(AssetLoader.soundVolume);
						((PlantPickup)entity).trample();
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public Reaper getReaper() {
		
		if (reaper == null) {
			
			for (Entity entity : getWorld().getActiveEntityList()) {
				
				if (entity instanceof Reaper) {
					
					reaper = (Reaper)entity;
					break;
					
				}
				
			}
			
		}
		return reaper;
		
	}
	
	public float getxStop() {
		
		return xStop;
		
	}
	
	public Crow setxStop(float xStop) {
		
		this.xStop = xStop;
		return this;
		
	}
	
}