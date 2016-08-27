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
	//public static Texture ;
	//public static TextureRegion ;
	//public static Sprite ;
	//public static BackgroundTemplate ;
	//public static Sound ;
	//public static SoundGroup ;
	//public static MusicTemplate ;
	public static MusicHandler musicHandler;
	public static List<Class<? extends Entity>> classList = new ArrayList<Class<? extends Entity>>();
	public static List<Class<? extends MaskSurface>> surfaceClassList = new ArrayList<Class<? extends MaskSurface>>();
	public static List<Pair<String, Sprite>> spriteList = new ArrayList<Pair<String, Sprite>>();
	public static List<Pair<String, ?>> spriteWallBackgroundList = new ArrayList<Pair<String, ?>>();
	public static List<Pair<String, ?>> spriteWallEdgeList = new ArrayList<Pair<String, ?>>();
	public static List<Pair<String, ?>> spriteGumballList = new ArrayList<Pair<String, ?>>();
	public static List<Pair<String, ?>> surfaceTypeList = new ArrayList<Pair<String, ?>>();
	public static List<Pair<String, ?>> cannonTypeList = new ArrayList<Pair<String, ?>>();
	public static List<Pair<String, BackgroundTemplate>> backgroundList = new ArrayList<Pair<String, BackgroundTemplate>>();
	public static List<Pair<String, MusicTemplate>> musicList = new ArrayList<Pair<String, MusicTemplate>>();
	public static List<Pair<String, ?>> paintColorList = new ArrayList<Pair<String, ?>>();
	public static List<Pair<String, ?>> dribbleSkinList = new ArrayList<Pair<String, ?>>();
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
		
		//assetManager.load("data/Images/DribbleTextures.pack", TextureAtlas.class);
		//atlas = new TextureAtlas(Gdx.files.internal("data/Images/DribbleTextures.pack"));
		
		Preferences preferences = Gdx.app.getPreferences("Dribble Prefs");
		musicVolume = preferences.getFloat("musicVolume", 0.5f);
		soundVolume = preferences.getFloat("soundVolume", 1.0f);
		
		loadSoundsManager();
		
		debugFont.setColor(Color.RED);
		debugFont.setUseIntegerPositions(false);
		
	}
	
	public static void postload() {
		
		//atlas = assetManager.get("data/Images/DribbleTextures.pack", TextureAtlas.class);
		
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
		
		//greenTexture = atlas.findRegion("Colors/green");
		
	}
	
	private static void loadSprites() {
		
		//spriteDribble = new Sprite(textureDribble, 18);
		//spriteList.add(new Pair<String, Sprite>("Dribble", spriteDribble));
		
	}
	
}