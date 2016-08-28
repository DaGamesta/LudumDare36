package com.mwojnar.Assets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mwojnar.Game.LudumDare36Game;
import com.playgon.GameEngine.BackgroundTemplate;
import com.playgon.GameEngine.Entity;
import com.playgon.GameEngine.MaskSurface;
import com.playgon.GameEngine.MusicTemplate;
import com.playgon.GameEngine.Sound;
import com.playgon.GameEngine.SoundGroup;
import com.playgon.GameEngine.Sprite;
import com.playgon.Helpers.MusicHandler;
import com.playgon.Utils.Pair;

public class AssetLoader {

	public static boolean loaded = false;
	public static AssetManager assetManager;
	private static TextureAtlas atlas;
	public static TextureRegion reaperTexture, cornStalkTexture, crossbowTexture, oxTexture,
					wheatTexture, riceTexture, cornStalkShotTexture, wheatShotTexture,
					riceShotTexture, wheatBunchTexture;
	public static Sprite spriteReaper, spriteCornStalk, spriteCrossbow, spriteOx, spriteWheat,
					spriteRice, spriteCornStalkShot, spriteWheatShot, spriteRiceShot,
					spriteWheatBunch;
	public static TextureRegion grassTexture;
	public static BackgroundTemplate grassBackground;
	//public static Sound ;
	//public static SoundGroup ;
	//public static MusicTemplate ;
	public static MusicHandler musicHandler;
	public static List<Class<? extends Entity>> classList = new ArrayList<Class<? extends Entity>>();
	public static List<Pair<String, Sprite>> spriteList = new ArrayList<Pair<String, Sprite>>();
	public static List<Pair<String, BackgroundTemplate>> backgroundList = new ArrayList<Pair<String, BackgroundTemplate>>();
	public static List<Pair<String, MusicTemplate>> musicList = new ArrayList<Pair<String, MusicTemplate>>();
	public static BitmapFont debugFont = new BitmapFont(true);
	public static Color greenTextColor = new Color(52.0f / 255.0f, 217.0f / 255.0f, 34.0f / 255.0f, 1.0f),
			blueTextColor = new Color(77.0f / 255.0f, 207.0f / 255.0f, 228.0f / 255.0f, 1.0f);
	public static float musicVolume = 0.5f, soundVolume = 1.0f;
	//public static float uniqueSoundVolume;
	
	public static void load() {
		
		dispose();
		
		loaded = false;
		
		assetManager = new AssetManager();
		
		musicHandler = new MusicHandler();
		
		assetManager.load("data/Images/LudumDare36Textures.pack", TextureAtlas.class);
		//atlas = new TextureAtlas(Gdx.files.internal("data/Images/DribbleTextures.pack"));
		
		Preferences preferences = Gdx.app.getPreferences("LudumDare36 Prefs");
		musicVolume = preferences.getFloat("musicVolume", 0.5f);
		soundVolume = preferences.getFloat("soundVolume", 1.0f);
		
		loadSoundsManager();
		
		debugFont.setColor(Color.RED);
		debugFont.setUseIntegerPositions(false);
		debugFont.getData().setScale(3.0f);
		
	}
	
	public static void postload() {
		
		atlas = assetManager.get("data/Images/LudumDare36Textures.pack", TextureAtlas.class);
		
		loadMusic();
		loadSounds();
		loadTextures();
		loadSprites();
		loadMisc();
		
	}
	
	private static void loadMisc() {
		
		
		
	}
	
	private static void loadSoundsManager() {
		
		//assetManager.load("data/Sounds/BALLOON_POPPING_1.mp3", com.badlogic.gdx.audio.Sound.class);
		
	}
	
	private static void loadSounds() {
		
		/*sndGrpBalloonPopping = new SoundGroup();
		sndGrpBalloonPopping.add(LudumDare36Game.createSound(assetManager.get("data/Sounds/BALLOON_POPPING_1.mp3", com.badlogic.gdx.audio.Sound.class)));
		sndGrpBalloonPopping.add(LudumDare36Game.createSound(assetManager.get("data/Sounds/BALLOON_POPPING_2.mp3", com.badlogic.gdx.audio.Sound.class)));*/
		
		//soundUICancel = LudumDare36Game.createSound(assetManager.get("data/Sounds/UI_CANCEL.mp3", com.badlogic.gdx.audio.Sound.class));
		
	}
	
	private static void loadMusic() {
		
		//musicPaintForest = new MusicTemplate(Gdx.files.internal("data/Music/msc_paintforest.mp3"));
		//musicPaintForest.setLooping(true);
		
		//musicHandler.addMusic(musicPaintForest);
		//musicList.add(new Pair<String, MusicTemplate>("Paint Forest", musicPaintForest));
		
	}
	
	public static void setMusicVolume(float musicVolume) {
		
		AssetLoader.musicVolume = musicVolume;
		//musicPaintForest.setVolume(musicVolume);
		
	}
	
	public static void dispose() {}
	
	private static void loadTextures() {
		
		reaperTexture = atlas.findRegion("spr_reaper_strip4");
		crossbowTexture = atlas.findRegion("spr_crossbow_strip10");
		oxTexture = atlas.findRegion("spr_ox_strip8");
		cornStalkTexture = atlas.findRegion("spr_corn_collectible");
		wheatTexture = atlas.findRegion("spr_wheat_collectible");
		riceTexture = atlas.findRegion("RicePickup");
		cornStalkShotTexture = atlas.findRegion("spr_corn_shot");
		wheatShotTexture = atlas.findRegion("spr_wheat_shot");
		riceShotTexture = atlas.findRegion("RiceBullet");
		wheatBunchTexture = atlas.findRegion("spr_wheat_bunch");
		
		grassTexture = atlas.findRegion("bg_grass");
		
	}
	
	private static void loadSprites() {
		
		spriteReaper = new Sprite(reaperTexture, 4);
		spriteCrossbow = new Sprite(crossbowTexture, 10);
		spriteOx = new Sprite(oxTexture, 8);
		spriteCornStalk = new Sprite(cornStalkTexture, 1);
		spriteWheat = new Sprite(wheatTexture, 1);
		spriteRice = new Sprite(riceTexture, 1);
		spriteCornStalkShot = new Sprite(cornStalkShotTexture, 1);
		spriteWheatShot = new Sprite(wheatShotTexture, 1);
		spriteRiceShot = new Sprite(riceShotTexture, 1);
		spriteWheatBunch = new Sprite(wheatBunchTexture, 1);
		
		grassBackground = new BackgroundTemplate(grassTexture, 1);
		
	}
	
}