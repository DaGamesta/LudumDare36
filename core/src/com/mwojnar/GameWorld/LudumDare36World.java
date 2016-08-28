package com.mwojnar.GameWorld;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mwojnar.Game.LudumDare36Game;
import com.mwojnar.GameObjects.PlantPickup;
import com.mwojnar.GameObjects.Reaper;
import com.mwojnar.Assets.AssetLoader;
import com.playgon.GameEngine.Background;
import com.playgon.GameEngine.BackgroundTemplate;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.MusicTemplate;
import com.playgon.GameEngine.Sprite;
import com.playgon.GameEngine.TouchEvent;
import com.playgon.GameWorld.GameWorld;
import com.playgon.Utils.LoadingThread;
import com.playgon.Utils.Pair;

public class LudumDare36World extends GameWorld {

	public enum Ammo { CORN, WHEAT, RICE }
	
	private LoadingThread loadingThread = null;
	private boolean showFPS = true, paused = false;
	private long framesSinceLevelCreation = 0;
	private FileHandle levelToLoad = null;
	private Random rand = new Random();
	private float scrollSpeed = -1.0f;
	private Background mainBackground = null;
	
	public LudumDare36World() {
		
		super();
		setUsingRegions(false);
		
	}
	
	@Override
	public void initialize() {
		
		setFPS(60);
		initializeLevelEditorLists();
		getRenderer().setUsingIntegerViewPosition(false);
		getRenderer().setClearColor(Color.BLACK);
		
		boolean loadMenus = true;
		if (LudumDare36Game.args != null) {
			
			for (int i = 0; i < LudumDare36Game.args.length; i++) {
				
				if (LudumDare36Game.args[i].equals("-loadLevel")) {
					
					if (i + 1 < LudumDare36Game.args.length) {
						
						clearWorld();
						loadLevel(Gdx.files.absolute(LudumDare36Game.args[i + 1]));
						loadMenus = false;
						break;
						
					}
					
				}
				
			}
			
		}
		if (loadMenus) {
			
			createEntity(new Reaper(this).setPos(getGameDimensions().x / 2.0f - AssetLoader.spriteReaper.getWidth() / 2.0f, getGameDimensions().y / 2.0f - AssetLoader.spriteReaper.getHeight() / 2.0f, false));
			mainBackground = new Background(AssetLoader.grassBackground);
			mainBackground.setTilingX(true);
			mainBackground.setTilingY(true);
			addBackground(mainBackground);
			
		}
		
	}
	
	public static void initializeLevelEditorLists() {
		
		levelEditorLists.clear();
		levelEditorListClasses.clear();
		addToLevelEditorLists(new ArrayList<Pair<String, ?>>(AssetLoader.spriteList), Sprite.class);
		addToLevelEditorLists(new ArrayList<Pair<String, ?>>(AssetLoader.backgroundList), BackgroundTemplate.class);
		addToLevelEditorLists(new ArrayList<Pair<String, ?>>(AssetLoader.musicList), MusicTemplate.class);
		
	}
	
	@Override
	public void update(float delta) {
		
		if (AssetLoader.loaded) {
			
			if (!paused) {
				
				framesSinceLevelCreation++;
				
			}
			super.update(delta);
			
		}
		
	}
	
	
	@Override
	protected void updateMain(float delta) {
		
		if (paused) {
			
			float scale =  getGameDimensions().y / 240.0f;
			for (TouchEvent touchEvent : getCurrentTouchEventList()) {
				
				Rectangle rect2 = new Rectangle(getGameDimensions().x - 10.0f - 9.0f * 15.0f, getGameDimensions().y - 30.0f, 9.0f * 15.0f, 20.0f);
				if (touchEvent.type == TouchEvent.Type.TOUCH_UP) {
					
					setPaused(false);
					//AssetLoader.soundUIUnpausing.play(AssetLoader.soundVolume);
					return;
					
				}
				
			}
			
		} else if (levelToLoad == null) {
			
			super.updateMain(delta);
			mainBackground.setOffset(new Vector2(mainBackground.getOffset().x - scrollSpeed, 0.0f));
			if (rand.nextFloat() < 1.0f / 60.0f) {
				
				Ammo type = Ammo.CORN;
				switch (rand.nextInt(2)) {
				
				case 1: type = Ammo.WHEAT; break;
				case 2: type = Ammo.RICE; break;
				
				}
				spawnPlant(rand.nextInt(10), type);
				
			}
			
		} else {
			
			trueLoadLevel(levelToLoad);
			levelToLoad = null;
			
		}
		
	}
	
	public boolean isPaused() {
		
		return paused;
		
	}
	
	public void setPaused(boolean paused) {
		
		this.paused = paused;
		if (paused) {
			
			AssetLoader.musicHandler.setVolume(AssetLoader.musicVolume * 0.25f);
			
		} else {
			
			AssetLoader.musicHandler.setVolume(AssetLoader.musicVolume);
			
		}
		
	}
	
	public void loadLevel(FileHandle file) {
		
		levelToLoad = file;
		
	}
	
	public void trueLoadLevel(FileHandle file) {
		
		AssetLoader.musicHandler.unload();
		AssetLoader.musicHandler.stopMusic();
		
//		if (loadingThread == null) {
//			
//			loadingThread = new LoadingThread(this, file, levelEditorListClasses, levelEditorLists);
//			loadingThread.start();
//			
//		}
		
		load(file, levelEditorListClasses, levelEditorLists);
		
		addEntities();
		
		/*BackgroundShape backgroundShape = new BackgroundShape(this, getCamPos(false));
		backgroundShape.setPos(dribbleEntity.getPos(true), false);
		backgroundShape.width = 200.0f;
		backgroundShape.height = 100.0f;
		createEntity(backgroundShape);*/
		
		framesSinceLevelCreation = 0;
		
	}
	
	@Override
	protected void loadFromMultiThread() {
		
		super.loadFromMultiThread();
		
		addEntities();
		
		/*BackgroundShape backgroundShape = new BackgroundShape(this, getCamPos(false));
		backgroundShape.setPos(dribbleEntity.getPos(true), false);
		backgroundShape.width = 200.0f;
		backgroundShape.height = 100.0f;
		createEntity(backgroundShape);*/
		
		framesSinceLevelCreation = 0;
		loadingThread = null;
		
	}
	
	public void clearWorld() {
		
		for (Entity entity : getEntityList()) {
			
			entity.destroy();
			
		}
		releaseEntities();
		resetBackgrounds();
		
	}

	public boolean isLoading() {
		
		return (loadingThread != null);
		
	}

	public boolean isShowFPS() {
		
		return showFPS;
		
	}

	public void setShowFPS(boolean showFPS) {
		
		this.showFPS = showFPS;
		
	}
	
	@Override
	public void restartLevel() {
		
		super.restartLevel();
		
	}
	
	public long getFramesSinceLevelCreation() {
		
		return framesSinceLevelCreation;
		
	}
	
	public void endLevel() {
		
		clear();
		
	}
	
	public void setLudumDare36View() {
		
		setViewOffset(new Vector2(0.0f, 0.0f));
		setViewSpeed(0.0f);
		setViewYield(new Vector2(0.0f, 0.0f));
		toggleViewPredictPath(true);
		toggleViewAccelerateToPoint(true);
		
	}

	public FileHandle getLevelToLoad() {
		
		return levelToLoad;
		
	}
	
	public Random getRandom() {
		
		return rand;
		
	}
	
	public float getScrollSpeed() {
		
		return scrollSpeed;
		
	}
	
	public void setScrollSpeed(float scrollSpeed) {
		
		this.scrollSpeed = scrollSpeed;
		
	}
	
	public void spawnPlant(int row, Ammo type) {
		
		float baseX = 1500.0f;
		float baseY = row * 72.0f;
		createEntity(new PlantPickup(this).setType(type).setPos(baseX, baseY, false));
		createEntity(new PlantPickup(this).setType(type).setPos(baseX + 72 - AssetLoader.spriteCornStalk.getWidth(), baseY, false));
		createEntity(new PlantPickup(this).setType(type).setPos(baseX + 36 - AssetLoader.spriteCornStalk.getWidth() / 2.0f, baseY + 36 - AssetLoader.spriteCornStalk.getHeight() / 2.0f, false));
		createEntity(new PlantPickup(this).setType(type).setPos(baseX, baseY + 72 - AssetLoader.spriteCornStalk.getHeight(), false));
		createEntity(new PlantPickup(this).setType(type).setPos(baseX + 72 - AssetLoader.spriteCornStalk.getWidth(), baseY + 72 - AssetLoader.spriteCornStalk.getHeight(), false));
		
	}
	
}