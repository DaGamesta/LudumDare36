package com.mwojnar.GameWorld;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mwojnar.Game.LudumDare36Game;
import com.mwojnar.GameObjects.Crow;
import com.mwojnar.GameObjects.CrowMother;
import com.mwojnar.GameObjects.Menu;
import com.mwojnar.GameObjects.PlantPickup;
import com.mwojnar.GameObjects.Reaper;
import com.mwojnar.GameWorld.LudumDare36World.Mode;
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
	public enum Mode { MENU, GAME, HIGHSCORE }
	public enum TutorialMode { MOVEMENT, RESTOCK, SHOOTING, SWITCHING, DONE }
	
	private TutorialMode tutorialMode = TutorialMode.MOVEMENT;
	private Mode mode = Mode.MENU;
	private LoadingThread loadingThread = null;
	private boolean showFPS = true, paused = false;
	private long framesSinceLevelCreation = 0, lastDifficultyNotch = 0, lastTimedNotch;
	private FileHandle levelToLoad = null;
	private Random rand = new Random();
	private float scrollSpeed = -1.0f, xStop = 1100.0f;
	private Background mainBackground = null;
	private int difficulty = 0, maxDifficulty = 10, initialWait = 300, difficultyTab = 0, score = 0;
	private List<List<Entity>> enemiesToLoad = new ArrayList<List<Entity>>();
	private List<Integer> framesUntilLoaded = new ArrayList<Integer>();
	private List<Integer> highScores = new ArrayList<Integer>();
	
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
		highScores.clear();
		Preferences preferences = Gdx.app.getPreferences("LudumDare36 Prefs");
		highScores.add(preferences.getInteger("score1", 0));
		highScores.add(preferences.getInteger("score2", 0));
		highScores.add(preferences.getInteger("score3", 0));
		highScores.add(preferences.getInteger("score4", 0));
		highScores.add(preferences.getInteger("score5", 0));
		AssetLoader.musicHandler.startMusic(AssetLoader.mainMusic);
		
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
			
			startMenu();
			
		}
		
	}
	
	public void startMenu() {
		
		clearWorld();
		mainBackground = new Background(AssetLoader.grassBackground);
		mainBackground.setTilingX(true);
		mainBackground.setTilingY(true);
		addBackground(mainBackground);
		mode = Mode.MENU;
		createEntity(new Menu(this));
		
	}

	public void startGame() {
		
		clearWorld();
		mode = Mode.GAME;
		score = 0;
		createEntity(new Reaper(this).setPos(getGameDimensions().x / 2.0f - AssetLoader.spriteReaper.getWidth() / 2.0f, getGameDimensions().y / 2.0f - AssetLoader.spriteReaper.getHeight() / 2.0f, false));
		spawnPlant(2, Ammo.CORN);
		spawnPlant(3, Ammo.WHEAT);
		spawnPlant(4, Ammo.CORN);
		spawnPlant(5, Ammo.CORN);
		spawnPlant(6, Ammo.WHEAT);
		spawnPlant(7, Ammo.CORN);
		framesSinceLevelCreation = 1;
		difficulty = 0;
		lastDifficultyNotch = 0;
		lastTimedNotch = 0;
		
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
		
		if (getKeysFirstDown().contains(com.badlogic.gdx.Input.Keys.F4)) {
			
			if (Gdx.graphics.isFullscreen()) {
				
				Gdx.graphics.setWindowedMode(1280, 720);
				
			} else {
				
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
				
			}
			
		}
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
			if (mode == Mode.GAME) {
				
				updateEnemyQueue();
				float cornMultiplier = 0;
				cornMultiplier = difficulty;
				if (cornMultiplier > 5) {
					
					cornMultiplier = 5;
					
				}
				cornMultiplier /= 2.0f;
				if ((getFramesSinceLevelCreation() - lastTimedNotch) % (300 + (int)(cornMultiplier * 60)) == 0) {
					
					Ammo type = Ammo.CORN;
					if (rand.nextFloat() < 0.2f) {
					
						type = Ammo.WHEAT;
					
					}
					spawnPlant(rand.nextInt(10), type);
					difficultyTab++;
					if (difficultyTab > 3) {
						
						difficultyTab = 0;
						lastTimedNotch = getFramesSinceLevelCreation() - ((getFramesSinceLevelCreation() - lastTimedNotch) % (300 + (int)(cornMultiplier * 60)));
						difficulty++;
						if (difficulty > maxDifficulty) {
							
							difficulty = maxDifficulty;
							
						}
						
					}
					
				}
				/*if ((getFramesSinceLevelCreation() - lastDifficultyNotch) % 1200 == 0) {
					
					lastTimedNotch = getFramesSinceLevelCreation() - ((getFramesSinceLevelCreation() - lastTimedNotch) % (300 + difficulty * 60));
					difficulty++;
					if (difficulty > maxDifficulty) {
						
						difficulty = maxDifficulty;
						
					} else {
						
						if (difficulty == 6 || difficulty == 8 || difficulty == 10 || difficulty == 12 || difficulty == 14) {
							
							difficulty++;
							
						}
						lastDifficultyNotch = getFramesSinceLevelCreation() - 1;
						
					}
					
				}*/
				int enemySpawnRate = 150;
				switch (difficulty) {
				
				case 1: enemySpawnRate = 120; break;
				case 2: enemySpawnRate = 210; break;
				case 3: case 8: case 10: enemySpawnRate = 240; break;
				case 4: enemySpawnRate = 180; break;
				case 5: case 6: case 7: case 9: enemySpawnRate = 300; break;
				
				}
				if (initialWait > 0) {
					
					initialWait--;
					
				} else if ((getFramesSinceLevelCreation() - lastDifficultyNotch) % enemySpawnRate == 0) {
					
					spawnEnemies();
					
				}
				
			}
			
		} else {
			
			trueLoadLevel(levelToLoad);
			levelToLoad = null;
			
		}
		
	}
	
	private void spawnEnemies() {
		
		List<List<Entity>> enemiesToAdd = new ArrayList<List<Entity>>();
		List<Integer> timers = new ArrayList<Integer>();
		Random rand = new Random();
		if (xStop == 1000.0f) {
			
			xStop = 1100.0f;
			
		} else if (xStop == 1100.0f) {
			
			xStop = 1200.0f;
			
		} else {
			
			xStop = 1000.0f;
			
		}
		switch (difficulty) {
		
		case 0: enemiesToAdd.add(new ArrayList<Entity>());
			enemiesToAdd.get(0).add(new Crow(this).setPos(1600.0f, rand.nextFloat() * getGameDimensions().y, true));
			timers.add(0);
			break;
			
		case 1: 
			if (rand.nextBoolean()) {
				
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, rand.nextFloat() * (getGameDimensions().y - 100.0f) + 50.0f, true));
				timers.add(0);
				
			} else {
				
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, rand.nextFloat() * (getGameDimensions().y - 100.0f) + 50.0f, true));
				timers.add(0);
				
			}
			break;
			
		case 2: enemiesToAdd.add(new ArrayList<Entity>());
			enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, rand.nextFloat() * (getGameDimensions().y - 100.0f) + 50.0f, true));
			enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, rand.nextFloat() * (getGameDimensions().y - 100.0f) + 50.0f, true));
			timers.add(0);
			break;
			
		case 3: case 4:	int randInt = rand.nextInt(3);
			switch (randInt) {
			
			case 0: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 5.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 5.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 5.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 5.0f, true));
				timers.add(0);
				break;
				
			case 1: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 5.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 5.0f, true));
				timers.add(60);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 5.0f, true));
				timers.add(120);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(3).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 5.0f, true));
				timers.add(180);
				break;
				
			case 2: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 3.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 3.0f, true));
				timers.add(0);
				break;
				
			}
			break;
			
		case 5: randInt = rand.nextInt(3);
			switch (randInt) {
			
			case 0: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 5.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 5.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 5.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 5.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 10.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 10.0f, true));
				timers.add(120);
				break;
				
			case 1: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 7.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 7.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 7.0f, true));
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 7.0f, true));
				timers.add(60);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 7.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 7.0f, true));
				timers.add(120);
				break;
				
			case 2: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 5.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 5.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 5.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 5.0f, true));
				timers.add(120);
				break;
				
			}
			break;
		
		case 6: randInt = rand.nextInt(3);
			switch (randInt) {
			
			case 0: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 9.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 18.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 18.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 11.0f / 18.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 15.0f / 18.0f, true));
				timers.add(120);
				break;
				
			case 1: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 13.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 13.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 13.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 13.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 9.0f / 13.0f, true));
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 11.0f / 13.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 13.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 13.0f, true));
				timers.add(120);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 13.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 13.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 10.0f / 13.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 12.0f / 13.0f, true));
				timers.add(240);
				break;
				
			case 2: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 9.0f, true));
				timers.add(0);
				break;
				
			}
			break;
			
		case 7: case 8: randInt = rand.nextInt(4);
			switch (randInt) {
			
			case 0: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 9.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 18.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 18.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 11.0f / 18.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 15.0f / 18.0f, true));
				timers.add(120);
				break;
				
			case 1: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 13.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 13.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 13.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 13.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 9.0f / 13.0f, true));
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 11.0f / 13.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 13.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 13.0f, true));
				timers.add(120);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 13.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 13.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 10.0f / 13.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 12.0f / 13.0f, true));
				timers.add(240);
				break;
				
			case 2: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 9.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 9.0f, true));
				timers.add(0);
				break;
				
			case 3: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 7.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 7.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 7.0f, true));
				timers.add(60);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 7.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 7.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 7.0f, true));
				timers.add(120);
				break;
				
			}
			break;
			
		default: randInt = rand.nextInt(4);
			switch (randInt) {
			
			case 0: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 9.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 10.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 13.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 14.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 11.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 12.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 15.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 16.0f / 17.0f, true));
				timers.add(0);
				break;
				
			case 1: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 9.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 10.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 11.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 12.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 13.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 14.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 15.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 16.0f / 17.0f, true));
				timers.add(0);
				break;
				
			case 2: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 9.0f / 17.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 17.0f, true));
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 17.0f, true));
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 10.0f / 17.0f, true));
				enemiesToAdd.get(1).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 11.0f / 17.0f, true));
				timers.add(60);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 1.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 12.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 13.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 14.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 15.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new Crow(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 16.0f / 17.0f, true));
				timers.add(120);
				break;
				
			case 3: enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 8.0f / 17.0f, true));
				enemiesToAdd.get(0).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 9.0f / 17.0f, true));
				timers.add(0);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 6.0f / 17.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 7.0f / 17.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 10.0f / 17.0f, true));
				enemiesToAdd.get(1).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 11.0f / 17.0f, true));
				timers.add(60);
				enemiesToAdd.add(new ArrayList<Entity>());
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 1.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 2.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 3.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 4.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 5.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 12.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 13.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 14.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 15.0f / 17.0f, true));
				enemiesToAdd.get(2).add(new CrowMother(this).setxStop(xStop).setPos(1600.0f, getGameDimensions().y * 16.0f / 17.0f, true));
				timers.add(120);
				break;
				
			}
			break;
			
		}
		for (int i = 0; i < enemiesToAdd.size(); i++) {
			
			queueEnemies(enemiesToAdd.get(i), timers.get(i));
			
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
		resetEntities();
		releaseEntities();
		getEntityList().clear();
		getActiveEntityList().clear();
		enemiesToLoad.clear();
		framesUntilLoaded.clear();
		initialWait = 300;
		
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
		
		float baseX = getGameDimensions().x;
		float baseY = row * 72.0f;
		for (int x = -1; x < 2; x++) {
			
			for (int y = -1; y < 2; y++) {
				
				createEntity(new PlantPickup(this).setType(type).setPos(baseX + 36 - AssetLoader.spriteCornStalk.getWidth() / 2.0f + x * 16, baseY + 36 - AssetLoader.spriteCornStalk.getHeight() / 2.0f + y * 16, false));
				
			}
			
		}
		/*createEntity(new PlantPickup(this).setType(type).setPos(baseX, baseY, false));
		createEntity(new PlantPickup(this).setType(type).setPos(baseX + 72 - AssetLoader.spriteCornStalk.getWidth(), baseY, false));
		createEntity(new PlantPickup(this).setType(type).setPos(baseX + 36 - AssetLoader.spriteCornStalk.getWidth() / 2.0f, baseY + 36 - AssetLoader.spriteCornStalk.getHeight() / 2.0f, false));
		createEntity(new PlantPickup(this).setType(type).setPos(baseX, baseY + 72 - AssetLoader.spriteCornStalk.getHeight(), false));
		createEntity(new PlantPickup(this).setType(type).setPos(baseX + 72 - AssetLoader.spriteCornStalk.getWidth(), baseY + 72 - AssetLoader.spriteCornStalk.getHeight(), false));*/
		
	}
	
	private void queueEnemies(List<Entity> enemies, int delay) {
		
		enemiesToLoad.add(enemies);
		framesUntilLoaded.add(delay);
		
	}
	
	private void updateEnemyQueue() {
		
		for (int i = 0; i < enemiesToLoad.size(); i++) {
			
			framesUntilLoaded.set(i, framesUntilLoaded.get(i) - 1);
			if (framesUntilLoaded.get(i) <= 0) {
				
				for (Entity entity : enemiesToLoad.get(i)) {
					
					createEntity(entity);
					
				}
				enemiesToLoad.remove(i);
				framesUntilLoaded.remove(i);
				
			}
			
		}
		
	}

	public int getDifficulty() {
		
		return difficulty;
		
	}
	
	public int getScore() {
		
		return score;
		
	}
	
	public void setScore(int score) {
		
		this.score = score;
		
	}
	
	public void addScore(int amount) {
		
		this.score += amount;
		
	}
	
	public List<Integer> getHighScores() {
		
		return highScores;
		
	}

	public void setMode(Mode mode) {
		
		this.mode = mode;
		
	}
	
	public TutorialMode getTutorialMode() {
		
		return tutorialMode;
		
	}
	
	public void setTutorialMode(TutorialMode tutorialMode) {
		
		this.tutorialMode = tutorialMode;
		
	}
	
}