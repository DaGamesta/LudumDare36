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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
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
	public static Texture wojworksTexture;
	public static TextureRegion reaperTexture, cornStalkTexture, crossbowTexture, oxTexture,
					wheatTexture, riceTexture, cornStalkShotTexture, wheatShotTexture,
					riceShotTexture, wheatBunchTexture, crow1Texture, crow2Texture,
					crowEggTexture, crowEggParticleTexture, crowDeathParticleTexture,
					wheatParticleTexture, cornStalkParticleTexture, cornStalkTrampledTexture,
					wheatTrampledTexture, heartTexture;
	public static Sprite spriteReaper, spriteCornStalk, spriteCrossbow, spriteOx, spriteWheat,
					spriteRice, spriteCornStalkShot, spriteWheatShot, spriteRiceShot,
					spriteWheatBunch, spriteCrow1, spriteCrow2, spriteCrowEgg,
					spriteCrowEggParticle, spriteCrowDeathParticle, spriteWheatParticle,
					spriteCornStalkParticle, spriteCornStalkTrampled, spriteWheatTrampled,
					spriteHeart;
	public static TextureRegion grassTexture;
	public static BackgroundTemplate grassBackground;
	public static Sound sndCornSlice, sndCrossbowShoot, sndCrowDie, sndCrowHit, sndCrowSwoop,
					sndEggHit, sndEggShoot, sndTrample, sndWheatSlice;
	//public static SoundGroup ;
	public static MusicTemplate mainMusic;
	public static MusicHandler musicHandler;
	public static List<Class<? extends Entity>> classList = new ArrayList<Class<? extends Entity>>();
	public static List<Pair<String, Sprite>> spriteList = new ArrayList<Pair<String, Sprite>>();
	public static List<Pair<String, BackgroundTemplate>> backgroundList = new ArrayList<Pair<String, BackgroundTemplate>>();
	public static List<Pair<String, MusicTemplate>> musicList = new ArrayList<Pair<String, MusicTemplate>>();
	public static BitmapFont debugFont = new BitmapFont(true), titleFont = new BitmapFont(true);
	public static Color greenTextColor = new Color(52.0f / 255.0f, 217.0f / 255.0f, 34.0f / 255.0f, 1.0f),
			blueTextColor = new Color(77.0f / 255.0f, 207.0f / 255.0f, 228.0f / 255.0f, 1.0f);
	public static float musicVolume = 0.5f, soundVolume = 1.0f;
	public static float vlmCornSlice = 1.0f, vlmCrossbowShoot = 1.0f, vlmCrowDie = 1.0f, vlmCrowHit = 1.0f, vlmCrowSwoop = 1.0f,
	vlmEggHit = 1.0f, vlmEggShoot = 1.0f, vlmTrample = 1.0f, vlmWheatSlice = 1.0f;
	
	public static void load() {
		
		dispose();
		
		loaded = false;
		
		assetManager = new AssetManager();
		
		musicHandler = new MusicHandler();
		
		wojworksTexture = new Texture(Gdx.files.internal("data/Images/spr_wojworks.png"));
		
		assetManager.load("data/Images/LudumDare36Textures.pack", TextureAtlas.class);
		//atlas = new TextureAtlas(Gdx.files.internal("data/Images/DribbleTextures.pack"));
		
		Preferences preferences = Gdx.app.getPreferences("LudumDare36 Prefs");
		musicVolume = preferences.getFloat("musicVolume", 1.0f);
		soundVolume = preferences.getFloat("soundVolume", 0.5f);
		
		loadSoundsManager();
		
		debugFont.setUseIntegerPositions(false);
		titleFont.setUseIntegerPositions(false);
		
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
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/Fonts/CountryGold.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 36;
		parameter.flip = true;
		parameter.color = Color.BLACK;
		debugFont = generator.generateFont(parameter);
		generator.dispose();
		
		generator = new FreeTypeFontGenerator(Gdx.files.internal("data/Fonts/CountryGold.ttf"));
		parameter = new FreeTypeFontParameter();
		parameter.size = 144;
		parameter.flip = true;
		parameter.color = Color.BLACK;
		titleFont = generator.generateFont(parameter);
		generator.dispose();
		
	}
	
	private static void loadSoundsManager() {
		
		assetManager.load("data/Sounds/snd_corn_slice.mp3", com.badlogic.gdx.audio.Sound.class);
		assetManager.load("data/Sounds/snd_crossbow_shoot.mp3", com.badlogic.gdx.audio.Sound.class);
		assetManager.load("data/Sounds/snd_crow_call.mp3", com.badlogic.gdx.audio.Sound.class);
		assetManager.load("data/Sounds/snd_crow_hit.mp3", com.badlogic.gdx.audio.Sound.class);
		assetManager.load("data/Sounds/snd_crow_swoop.mp3", com.badlogic.gdx.audio.Sound.class);
		assetManager.load("data/Sounds/snd_egg_hit.mp3", com.badlogic.gdx.audio.Sound.class);
		assetManager.load("data/Sounds/snd_egg_shoot.mp3", com.badlogic.gdx.audio.Sound.class);
		assetManager.load("data/Sounds/snd_trample.mp3", com.badlogic.gdx.audio.Sound.class);
		assetManager.load("data/Sounds/snd_wheat_slice.mp3", com.badlogic.gdx.audio.Sound.class);
		
	}
	
	private static void loadSounds() {
		
		sndCornSlice = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_corn_slice.mp3", com.badlogic.gdx.audio.Sound.class));
		sndCrossbowShoot = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_crossbow_shoot.mp3", com.badlogic.gdx.audio.Sound.class));
		sndCrowDie = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_crow_call.mp3", com.badlogic.gdx.audio.Sound.class));
		sndCrowHit = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_crow_hit.mp3", com.badlogic.gdx.audio.Sound.class));
		sndCrowSwoop = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_crow_swoop.mp3", com.badlogic.gdx.audio.Sound.class));
		sndEggHit = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_egg_hit.mp3", com.badlogic.gdx.audio.Sound.class));
		sndEggShoot = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_egg_shoot.mp3", com.badlogic.gdx.audio.Sound.class));
		sndTrample = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_trample.mp3", com.badlogic.gdx.audio.Sound.class));
		sndWheatSlice = LudumDare36Game.createSound(assetManager.get("data/Sounds/snd_wheat_slice.mp3", com.badlogic.gdx.audio.Sound.class));
		
	}
	
	private static void loadMusic() {
		
		mainMusic = new MusicTemplate(Gdx.files.internal("data/Music/HM_-_Back_Mountain.mp3"));
		mainMusic.setLooping(true);
		musicHandler.addMusic(mainMusic);
		
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
		crow1Texture = atlas.findRegion("spr_crowA_strip6");
		crow2Texture = atlas.findRegion("spr_crowB_strip6");
		crowEggTexture = atlas.findRegion("spr_egg_shot");
		crowEggParticleTexture = atlas.findRegion("spr_egg_particles_strip3");
		crowDeathParticleTexture = atlas.findRegion("spr_crow_death_particles_strip3");
		wheatParticleTexture = atlas.findRegion("spr_wheat_particles_strip3");
		cornStalkParticleTexture = atlas.findRegion("spr_corn_particles_strip5");
		cornStalkTrampledTexture = atlas.findRegion("spr_corn_trampled");
		wheatTrampledTexture = atlas.findRegion("spr_wheat_trampled");
		heartTexture = atlas.findRegion("spr_health_strip2");
		
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
		spriteCrow1 = new Sprite(crow1Texture, 6);
		spriteCrow2 = new Sprite(crow2Texture, 6);
		spriteCrowEgg = new Sprite(crowEggTexture, 1);
		spriteCrowEggParticle = new Sprite(crowEggParticleTexture, 3);
		spriteCrowDeathParticle = new Sprite(crowDeathParticleTexture, 3);
		spriteWheatParticle = new Sprite(wheatParticleTexture, 3);
		spriteCornStalkParticle = new Sprite(cornStalkParticleTexture, 5);
		spriteCornStalkTrampled = new Sprite(cornStalkTrampledTexture, 1);
		spriteWheatTrampled = new Sprite(wheatTrampledTexture, 1);
		spriteHeart = new Sprite(heartTexture, 2);
		
		grassBackground = new BackgroundTemplate(grassTexture, 1);
		
	}
	
}