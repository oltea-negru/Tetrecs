package uk.ac.soton.comp1206.network;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Multimedia {

    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    /**
     * A boolean property for the audio being enabled or not
     */
    private static BooleanProperty audioEnabledProperty = new SimpleBooleanProperty(true);

    /**
     * The media player for the sounds
     */
    private static MediaPlayer musicPlayer;
    private static MediaPlayer audioPlayer;


    /**
     * Play an audio file
     *
     * @param file filename to play from resources
     */
    public static void playAudio(String file) {
        if (!getAudioEnabled()) return;

        String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
        logger.info("Playing audio: " + toPlay);

        try {
            Media play = new Media(toPlay);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            setAudioEnabled(false);
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    /**
     * Play an audio file for the whole duration of the game
     *
     * @param file filename to play from resources
     */
    public static void playBackgroundMusic(String file) {
        if (!getAudioEnabled()) return;

        String toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
        logger.info("Playing audio: " + toPlay);
        try {
            Media play = new Media(toPlay);
            musicPlayer = new MediaPlayer(play);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.play();
        } catch (Exception e) {
            setAudioEnabled(false);
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    /**
     * Get the audio property
     * @return
     */
    public static BooleanProperty audioEnabledProperty() {
        return audioEnabledProperty;
    }

    /**
     * Get the audio status
     * @return
     */
    public static boolean getAudioEnabled() {
        return audioEnabledProperty().get();
    }

    /**
     * Set a status for the audio
     * @param enabled
     */
    public static void setAudioEnabled(boolean enabled) {
        logger.info("Audio enabled set to: " + enabled);
        audioEnabledProperty().set(enabled);
    }

    /**
     * Changes the audio's status so that it will be silent
     */
    public static void stop() {
        musicPlayer.stop();
    }
}
