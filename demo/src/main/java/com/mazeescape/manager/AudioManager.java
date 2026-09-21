package com.mazeescape.manager;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioManager {

    private MediaPlayer backgroundMusic;

    private double masterVolume = 1.0;
    private double musicVolume = 1.0;
    private double sfxVolume = 1.0;
    private Path ambientTrack;
    private Path keyEffect;
    private Path unlockEffect;
    private Path completeEffect;
    private Path gameOverEffect;
    private Path clickEffect;
    private final List<MediaPlayer> activeSoundEffects = new ArrayList<>();

    public AudioManager() {
        try {
            ensureHauntedAudio();
        } catch (IOException exception) {
            System.err.println("Unable to prepare UI audio: " + exception.getMessage());
        }
    }

    public void syncVolumes(double master, double music, double sfx) {
        masterVolume = clamp(master);
        musicVolume = clamp(music);
        sfxVolume = clamp(sfx);
        updateMusicVolume();
    }

    public void playHauntedMusic() {
        try {
            ensureHauntedAudio();
            playBackgroundMusic(ambientTrack.toString());
        } catch (IOException exception) {
            System.err.println("Unable to prepare haunted music: " + exception.getMessage());
        }
    }

    public void playMainMenuMusic() {
        try {
            Path menuTrack = copyResourceToTemp("/sounds/Main_Menu_Sound.wav", "maze-escape-main-menu.wav");
            playBackgroundMusic(menuTrack.toString());
        } catch (IOException exception) {
            System.err.println("Unable to prepare main menu music: " + exception.getMessage());
        }
    }

    public void playKeyPickup() {
        playGeneratedEffect(1);
    }

    public void playExitUnlock() {
        playGeneratedEffect(2);
    }

    public void playLevelComplete() {
        playGeneratedEffect(3);
    }

    public void playGameOver() {
        playGeneratedEffect(4);
    }

    public void playClick() {
        playGeneratedEffect(5);
    }

    // =========================
    // VOLUME
    // =========================
    public void setMasterVolume(double volume) {
        masterVolume = clamp(volume);
        updateMusicVolume();
    }

    public void setMusicVolume(double volume) {
        musicVolume = clamp(volume);
        updateMusicVolume();
    }

    public void setSfxVolume(double volume) {
        sfxVolume = clamp(volume);
    }

    public double getMasterVolume() {
        return masterVolume;
    }

    public double getMusicVolume() {
        return musicVolume;
    }

    public double getSfxVolume() {
        return sfxVolume;
    }

    // =========================
    // BACKGROUND MUSIC
    // =========================
    public void playBackgroundMusic(String filePath) {

        stopBackgroundMusic();

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println(
                    "Background music not found: " + filePath
            );
            return;
        }

        try {
            Media media = new Media(file.toURI().toString());

            backgroundMusic = new MediaPlayer(media);

            backgroundMusic.setCycleCount(
                    MediaPlayer.INDEFINITE
            );

            backgroundMusic.setVolume(
                    masterVolume * musicVolume
            );

            backgroundMusic.play();

        } catch (Exception e) {
            System.err.println(
                    "Unable to play background music: "
                    + e.getMessage()
            );
        }
    }

    public void stopBackgroundMusic() {

        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }

    public void pauseBackgroundMusic() {

        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
    }

    public void resumeBackgroundMusic() {

        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
    }

    // =========================
    // SOUND EFFECT
    // =========================
    public void playSoundEffect(String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println(
                    "Sound effect not found: " + filePath
            );
            return;
        }

        try {
            Media media = new Media(file.toURI().toString());

            MediaPlayer soundEffect
                    = new MediaPlayer(media);

            soundEffect.setVolume(
                    masterVolume * sfxVolume
            );

            soundEffect.setOnEndOfMedia(
                    () -> {
                        soundEffect.stop();
                        soundEffect.dispose();
                        activeSoundEffects.remove(soundEffect);
                    }
            );

            activeSoundEffects.add(soundEffect);
            soundEffect.play();

        } catch (Exception e) {
            System.err.println(
                    "Unable to play sound effect: "
                    + e.getMessage()
            );
        }
    }

    private void playGeneratedEffect(int effectType) {
        try {
            ensureHauntedAudio();
            Path effect = switch (effectType) {
                case 1 -> keyEffect;
                case 2 -> unlockEffect;
                case 3 -> completeEffect;
                case 4 -> gameOverEffect;
                case 5 -> clickEffect;
                default -> throw new IllegalArgumentException("Unknown audio effect: " + effectType);
            };
            playSoundEffect(effect.toString());
        } catch (IOException exception) {
            System.err.println("Unable to prepare haunted sound effect: " + exception.getMessage());
        }

    }

    private void setClipVolume(Clip clip, double volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (volume <= 0) {
                gain.setValue(gain.getMinimum());
            } else {
                double decibels = 20 * Math.log10(volume);
                gain.setValue((float) Math.max(gain.getMinimum(),
                        Math.min(gain.getMaximum(), decibels)));
            }
        } else if (clip.isControlSupported(BooleanControl.Type.MUTE)) {
            BooleanControl mute = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
            mute.setValue(volume <= 0);
        }
    }

    private Path copyResourceToTemp(String resourcePath, String fileName) throws IOException {
        Path target = Files.createTempFile("maze-escape-", "-" + fileName);
        try (InputStream stream = AudioManager.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IOException("Missing audio resource: " + resourcePath);
            }
            Files.copy(stream, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        target.toFile().deleteOnExit();
        return target;
    }

    private void ensureHauntedAudio() throws IOException {
        if (ambientTrack != null) {
            return;
        }
        Path directory = Files.createTempDirectory("maze-escape-audio-");
        ambientTrack = writeTone(directory.resolve("haunted-ambient.wav"), 12.0, 0);
        keyEffect = writeTone(directory.resolve("key-pickup.wav"), 0.45, 1);
        unlockEffect = writeTone(directory.resolve("exit-unlock.wav"), 0.7, 2);
        completeEffect = writeTone(directory.resolve("level-complete.wav"), 0.9, 3);
        gameOverEffect = writeTone(directory.resolve("game-over.wav"), 0.8, 4);
        clickEffect = writeTone(directory.resolve("ui-click.wav"), 0.18, 5);
    }

    private Path writeTone(Path path, double seconds, int type) throws IOException {
        int sampleRate = 44100;
        int sampleCount = (int) (sampleRate * seconds);
        byte[] audio = new byte[sampleCount * 2];
        for (int sample = 0; sample < sampleCount; sample++) {
            double time = sample / (double) sampleRate;
            double value;
            if (type == 0) {
                double pulse = 0.55 + 0.45 * Math.sin(2 * Math.PI * 0.18 * time);
                value = (Math.sin(2 * Math.PI * 55 * time)
                        + 0.5 * Math.sin(2 * Math.PI * 73 * time)
                        + 0.25 * Math.sin(2 * Math.PI * 110 * time)) * 0.16 * pulse;
            } else {
                double[] notes = switch (type) {
                    case 1 -> new double[]{523, 659};
                    case 2 -> new double[]{220, 330, 440};
                    case 3 -> new double[]{392, 494, 659, 784};
                    case 5 -> new double[]{880};
                    default -> new double[]{220, 165, 110};
                };
                int note = Math.min(notes.length - 1, (int) (time / seconds * notes.length));
                double envelope = Math.max(0, 1 - time / seconds);
                value = Math.sin(2 * Math.PI * notes[note] * time) * 0.28 * envelope;
            }
            short pcm = (short) Math.max(-32767, Math.min(32767, value * 32767));
            audio[sample * 2] = (byte) (pcm & 0xff);
            audio[sample * 2 + 1] = (byte) ((pcm >> 8) & 0xff);
        }
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        try (AudioInputStream stream = new AudioInputStream(
                new java.io.ByteArrayInputStream(audio), format, sampleCount)) {
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, path.toFile());
        }
        return path;
    }

    // =========================
    // UPDATE MUSIC VOLUME
    // =========================
    private void updateMusicVolume() {

        if (backgroundMusic != null) {
            backgroundMusic.setVolume(
                    masterVolume * musicVolume
            );
        }
    }

    // =========================
    // RESET
    // =========================
    public void resetVolumes() {

        masterVolume = 1.0;
        musicVolume = 1.0;
        sfxVolume = 1.0;

        updateMusicVolume();
    }

    // =========================
    // HELPER
    // =========================
    private double clamp(double value) {

        return Math.max(
                0.0,
                Math.min(1.0, value)
        );
    }
}
