package com.mwojnar.GameObjects;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.mwojnar.Assets.AssetLoader;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.Mask;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameWorld;

public class CrowEgg extends Entity {
	
	private Reaper reaper = null;
	
	public CrowEgg(GameWorld myWorld) {
		
		super(myWorld);
		setSprite(AssetLoader.spriteCrowEgg);
		setMask(new Mask(this, new Vector2(getSprite().getWidth() / 2.0f, getSprite().getHeight() / 2.0f), getSprite().getWidth() / 2.0f));
		setPivot(getSprite().getWidth() / 2.0f, getSprite().getHeight() / 2.0f);
		setDepth(99);
		
	}
	
	@Override
	public void update(float delta, List<TouchEvent> touchEventList, List<Character> charactersTyped, List<Integer> keysFirstDown, List<Integer> keysFirstUp, List<Integer> keysDown) {
		
		super.update(delta, touchEventList, charactersTyped, keysFirstDown, keysFirstUp, keysDown);
		moveByVelocity();
		if (getPos(false).x < -50 || getPos(false).x > 1330 || getPos(false).y < -50 || getPos(false).y > 770) {
			
			destroy();
			
		}
		if (getReaper().collisionsWith(this, getReaper().bullMask).size() > 0 && getReaper().hurt(1)) {
			
			Random rand = new Random();
			for (int i = 0; i < 4; i++) {
				
				Particle particle = new Particle(getWorld());
				particle.setPos(getPos(true), true);
				particle.setSprite(AssetLoader.spriteCrowEggParticle);
				particle.setPivot(AssetLoader.spriteCrowEggParticle.getWidth() / 2.0f, AssetLoader.spriteCrowEggParticle.getHeight() / 2.0f);
				particle.setFrame(rand.nextInt(3));
				particle.setAnimationSpeed(0.0f);
				particle.setShrinking(true);
				particle.setMaxTime(60);
				particle.setTimed(true);
				particle.setRotation(rand.nextFloat() * 360.0f);
				particle.setRadialVelocity(rand.nextFloat() * .5f + .5f, rand.nextFloat() * (float)Math.PI * 2.0f);
				getWorld().createEntity(particle);
				
			}
			AssetLoader.sndEggHit.play(AssetLoader.soundVolume);
			destroy();
			
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

}
