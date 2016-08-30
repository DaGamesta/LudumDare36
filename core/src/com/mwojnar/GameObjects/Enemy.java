package com.mwojnar.GameObjects;

import java.util.Random;

import com.mwojnar.Assets.AssetLoader;
import com.mwojnar.GameWorld.LudumDare36World;
import com.playgon.GameEngine.Entity;
import com.playgon.GameWorld.GameWorld;

public class Enemy extends Entity {
	
	private int HP = 0;
	
	public Enemy(GameWorld myWorld) {
		
		super(myWorld);
		
	}

	public int getHP() {
		
		return HP;
		
	}
	
	protected void setHP(int HP) {
		
		this.HP = HP;
		
	}

	public void hurt(int amount) {
		
		HP -= amount;
		if (HP <= 0) {
			
			die();
			
		}
		
	}
	
	protected void die() {
		
		Random rand = new Random();
		for (int i = 0; i < 8; i++) {
			
			Particle particle = new Particle(getWorld());
			particle.setPos(getPos(true), true);
			particle.setSprite(AssetLoader.spriteCrowDeathParticle);
			particle.setPivot(AssetLoader.spriteCrowDeathParticle.getWidth() / 2.0f, AssetLoader.spriteCrowEggParticle.getHeight() / 2.0f);
			particle.setFrame(rand.nextInt(3));
			particle.setAnimationSpeed(0.0f);
			particle.setSlowing(true);
			particle.setMaxSpeed((rand.nextFloat() * .5f + .5f) * 2.0f);
			particle.setShrinking(true);
			particle.setMaxTime(90);
			particle.setTimed(true);
			particle.setRotation(rand.nextFloat() * 360.0f);
			particle.setRadialVelocity(0.0f, rand.nextFloat() * (float)Math.PI * 2.0f);
			getWorld().createEntity(particle);
			
		}
		((LudumDare36World)getWorld()).addScore(10);
		AssetLoader.sndCrowDie.play(AssetLoader.soundVolume);
		destroy();
		
	}
	
}
