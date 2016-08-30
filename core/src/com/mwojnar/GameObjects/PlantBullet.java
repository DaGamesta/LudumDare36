package com.mwojnar.GameObjects;

import java.util.List;
import java.util.Random;

import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameObjects.Particle.FadeEffect;
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
		for (Entity entity : getWorld().getActiveEntityList()) {
			
			if (entity instanceof Enemy && !collisionsWith(entity).isEmpty()) {
				
				int hurtAmount = 0;
				switch (type) {
				
				case CORN: hurtAmount = 4;
				Random rand = new Random();
				int[] frames = new int[4];
				frames[0] = 1;
				frames[1] = 2;
				frames[2] = 3;
				frames[3] = 4;
				for (int i = 0; i < 10; i++) {
					
					int index1 = rand.nextInt(4), index2 = rand.nextInt(4);
					int temp = frames[index1];
					frames[index1] = frames[index2];
					frames[index2] = temp;
					
				}
				for (int i = 0; i < 4; i++) {
					
					Particle particle = new Particle(getWorld());
					particle.setSprite(AssetLoader.spriteCornStalkParticle);
					particle.setPos(getPos(false).x + i * 9.0f + AssetLoader.spriteCornStalkParticle.getWidth() / 2.0f, getPos(true).y, true);
					particle.setPivot(AssetLoader.spriteCornStalkParticle.getWidth() / 2.0f, AssetLoader.spriteCornStalkParticle.getHeight() / 2.0f);
					particle.setFrame(frames[i]);
					particle.setAnimationSpeed(0.0f);
					particle.setFading(true);
					particle.setMaxTime(60);
					particle.setTimed(true);
					particle.setRotates(true);
					particle.setRotationSpeed((rand.nextFloat() * 10.0f - 5.0f) * (float)Math.PI / 180.0f);
					particle.setRotation(rand.nextFloat() * 360.0f);
					particle.setGridVelocity(((LudumDare36World)getWorld()).getScrollSpeed(), 0.0f);
					getWorld().createEntity(particle);
					
				}
				Particle particle = new Particle(getWorld());
				particle.setSprite(AssetLoader.spriteWheatParticle);
				particle.setPos(getPos(false).x + 4.0f * 9.0f + AssetLoader.spriteWheatParticle.getWidth() / 2.0f, getPos(true).y, true);
				particle.setPivot(AssetLoader.spriteWheatParticle.getWidth() / 2.0f, AssetLoader.spriteWheatParticle.getHeight() / 2.0f);
				particle.setFrame(0);
				particle.setAnimationSpeed(0.0f);
				particle.setFading(true);
				particle.setMaxTime(60);
				particle.setTimed(true);
				particle.setRotates(true);
				particle.setRotationSpeed((rand.nextFloat() * 10.0f - 5.0f) * (float)Math.PI / 180.0f);
				particle.setRotation(rand.nextFloat() * 360.0f);
				particle.setRadialVelocity(rand.nextFloat() * 0.5f + 0.5f, 0.0f);
				getWorld().createEntity(particle);
				break;
				case WHEAT: hurtAmount = 4;
				rand = new Random();
				for (int i = 0; i < 4; i++) {
					
					particle = new Particle(getWorld());
					particle.setPos(getPos(true), true);
					particle.setSprite(AssetLoader.spriteWheatParticle);
					particle.setPivot(AssetLoader.spriteWheatParticle.getWidth() / 2.0f, AssetLoader.spriteCrowEggParticle.getHeight() / 2.0f);
					particle.setFrame(rand.nextInt(3));
					particle.setAnimationSpeed(0.0f);
					particle.setShrinking(true);
					particle.setMaxTime(60);
					particle.setTimed(true);
					particle.setRotation(rand.nextFloat() * 360.0f);
					particle.setRadialVelocity(rand.nextFloat() * .5f + .5f, rand.nextFloat() * (float)Math.PI * 2.0f);
					getWorld().createEntity(particle);
					
				}
				break;
				
				}
				((Enemy)entity).hurt(hurtAmount);
				destroy();
				
			}
			
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
		setMask(new Mask(this, getSprite().getWidth(), getSprite().getHeight()));
		
		return this;
		
	}
	
}