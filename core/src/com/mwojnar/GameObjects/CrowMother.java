package com.mwojnar.GameObjects;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameObjects.Crow.Mode;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.Mask;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameWorld;
import com.playgon.Utils.PlaygonMath;

public class CrowMother extends Enemy {
	
	public enum Mode { INITIAL, TURNING, ATTACKING, FLEEING }
	
	private float speed = 5.0f, acceleration = 0.5f, rotationAcceleration = 5.0f;
	private int pauseTimer = 90, eggTimer = 90 * 5;
	private boolean hostile = true;
	private Mode mode = Mode.INITIAL;
	private Reaper reaper = null;
	private float xStop = 1100.0f;
	
	public CrowMother(GameWorld myWorld) {
		
		super(myWorld);
		setSprite(AssetLoader.spriteCrow2);
		setMask(new Mask(this, new Vector2(getSprite().getWidth() / 2.0f, getSprite().getHeight() / 2.0f), 12.0f));
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
			float rotation = PlaygonMath.fixAngle(PlaygonMath.toRadians(getRotation()));
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
					
					setRotation(PlaygonMath.toDegrees(direction));
					
				}
				
			}
			if (pauseTimer > 0) {
				
				pauseTimer--;
				
			} else {
				
				mode = Mode.ATTACKING;
				
			}
			break;
		case ATTACKING:
			direction = PlaygonMath.direction(getPos(true), getReaper().getPos(true));
			rotation = PlaygonMath.fixAngle(PlaygonMath.toRadians(getRotation()));
			if (direction != rotation) {
				
				if (!PlaygonMath.withinRotationFromDirection(direction, rotation, PlaygonMath.toRadians(rotationAcceleration / 5.0f), PlaygonMath.toRadians(rotationAcceleration / 5.0f))) {
					
					if (direction < rotation) {
						
						if (rotation - direction > Math.PI) {
							
							setRotation(getRotation() + rotationAcceleration / 5.0f);
							
						} else {
							
							setRotation(getRotation() - rotationAcceleration / 5.0f);
							
						}
						
					} else {
						
						if (direction - rotation > Math.PI) {
							
							setRotation(getRotation() - rotationAcceleration / 5.0f);
							
						} else {
							
							setRotation(getRotation() + rotationAcceleration / 5.0f);
							
						}
						
					}
					
				} else {
					
					setRotation(PlaygonMath.toDegrees(direction));
					
				}
				
			}
			if (eggTimer > 0) {
				
				if (eggTimer % 90 == 0) {
					
					shoot();
					
				}
				eggTimer--;
				
			} else {
				
				mode = Mode.FLEEING;
				setRadialVelocity(0.0f, PlaygonMath.toRadians(getRotation() + 180.0f));
				
			}
			break;
		case FLEEING:
			if (getRadialVelocity().x < speed) {
				
				setRadialVelocity(getRadialVelocity().x + acceleration, getRadialVelocity().y);
				
			} else {
				
				setRadialVelocity(speed, getRadialVelocity().y);
				
			}
			if (getPos(false).x < -50 || getPos(false).x > 1330 || getPos(false).y < -50 || getPos(false).y > 770) {
				
				destroy();
				
			}
			break;
		
		}
		moveByVelocity();
		if (getReaper().collisionsWith(this, getReaper().bullMask).size() > 0 && hostile && getReaper().hurt(2)) {
			
			AssetLoader.sndCrowHit.play(AssetLoader.soundVolume);
			hostile = false;
			mode = Mode.FLEEING;
			setRadialVelocity(0.0f, PlaygonMath.toRadians(getRotation() + 180.0f));
			
		}
		
	}
	
	private void shoot() {
		
		AssetLoader.sndEggShoot.play(AssetLoader.soundVolume);
		getWorld().createEntity(new CrowEgg(getWorld()).setPos(getPos(true), true).setRotation(getRotation() + 90.0f).setRadialVelocity(speed * 2.0f, PlaygonMath.toRadians(getRotation())));
		
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
	
	public CrowMother setxStop(float xStop) {
		
		this.xStop = xStop;
		return this;
		
	}

}
