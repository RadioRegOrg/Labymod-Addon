package de.optischa.radioPlayer.player;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.event.StreamChangeTrackEvent;
import de.optischa.radioPlayer.player.basic.BasicPlayer;
import de.optischa.radioPlayer.player.basic.BasicPlayerException;
import de.optischa.radioPlayer.player.gson.Stream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.ThreadSafe;
import net.labymod.api.util.logging.Logging;

public class MusicPlayer {

  private Main addon;
  private final BasicPlayer basicPlayer;
  private double currentVolume = 0.01;
  private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors
      .newSingleThreadScheduledExecutor();
  private Stream currentStream;
  private Logging logger;
  public Stream[] streams;

  public MusicPlayer(BasicPlayer basicPlayer, Logging logger) {
    this.basicPlayer = basicPlayer;
    this.logger = logger;
    this.addon = Main.get();

    this.startUpdateTask();
    this.updateCurrentStreamTask();

    this.currentStream = this.addon.configuration().selectedStream();
    this.currentVolume = this.addon.configuration().volumeSlider().get();
  }

  private void startUpdateTask() {
    EXECUTOR_SERVICE.scheduleAtFixedRate(this::updateStream, 0L, 30L, TimeUnit.SECONDS);
  }

  private void updateCurrentStreamTask() {
    EXECUTOR_SERVICE.scheduleAtFixedRate(this::updateCurrentStream, 0L, 10L, TimeUnit.SECONDS);
  }

  private void updateCurrentStream() {
    if (!this.isPlaying()) {
      return;
    }
    Stream currentStreamRequest = this.addon.configuration().selectedStream();
    Stream updatetStream = Request.getStream(currentStreamRequest.id);
    if (currentStreamRequest.artist.equalsIgnoreCase(updatetStream.artist)
        && currentStreamRequest.title.equalsIgnoreCase(updatetStream.title)) {
      return;
    }
    this.addon.configuration().setSelectedStream(updatetStream);
    this.addon.labyAPI().eventBus().fire(new StreamChangeTrackEvent(updatetStream));
  }

  private void updateStream() {
    this.streams = Request.getStreams();
  }

  public Optional<Stream> currentStream() {
    return Optional.ofNullable(currentStream);
  }

  public void play() {
    if (this.isPlaying()) {
      return;
    }
    Stream stream = this.addon.configuration().selectedStream();
    if (stream == null || stream.id == 0) {
      this.addon.labyAPI().notificationController().push(
          PlayerNotification.sendInfoNotification("radioreg.notification.notSelected.name",
              "radioreg.notification.notSelected.description")
      );
      return;
    }
    try {
      basicPlayer.stop();
      play(stream);
      basicPlayer.resume();
    } catch (BasicPlayerException e) {
      logger.warn("Failed to resume the player", e);
    }
  }

  public boolean isPlaying() {
    return basicPlayer.getStatus() == BasicPlayer.PLAYING;
  }

  public float getVolume() {
    return (float) currentVolume;
  }

  public void setVolume(float volume) {
    if (!isPlaying()) {
      return;
    }
    this.currentVolume = volume;
    try {
      basicPlayer.setGain(volume);
    } catch (BasicPlayerException e) {
      logger.warn("Failed to set the volume", e);
    }
  }

  private void checkVolume() {
    this.addon.logger().info(((double) this.addon.configuration().volumeSlider().get() / 100) + "");
    if (this.currentVolume != ((double) this.addon.configuration().volumeSlider().get() / 100)) {
      this.currentVolume = (double) this.addon.configuration().volumeSlider().get() / 100;
    }
  }

  public void play(Stream stream) {
    if (currentStream.id == stream.id && this.isPlaying()) {
      this.addon.labyAPI().notificationController().push(
          PlayerNotification.sendInfoNotification("radioreg.notification.alreadyPlaying.name",
              Component.translatable("radioreg.notification.alreadyPlaying.description",
                  Component.text(stream.name)))
      );
      return;
    }
    ThreadSafe.executeOnRenderThread(() -> {
      try {
        if (isPlaying()) {
          basicPlayer.stop();
        }
        URL url = new URL(stream.url);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "LabyMod/Radio Player");

        this.checkVolume();

        basicPlayer.open(connection.getInputStream());
        basicPlayer.play();
        basicPlayer.setGain(this.currentVolume);
        currentStream = stream;
        this.addon.labyAPI().notificationController().push(
            PlayerNotification.sendInfoNotification("radioreg.notification.playing.name",
                Component.translatable("radioreg.notification.playing.description",
                    Component.text(stream.name)))
        );
        this.updateCurrentStream();
      } catch (BasicPlayerException e) {
        logger.warn("Failed to play the stream", e);
      } catch (Exception e) {
        logger.warn("Failed to open the stream", e);
      }
    });
  }

  public void pause() {
    try {
      basicPlayer.stop();
    } catch (BasicPlayerException e) {
      logger.warn("Failed to stop the player", e);
    }
  }

  public void toggle() {
    try {
      if (basicPlayer.getStatus() == BasicPlayer.PAUSED
          || basicPlayer.getStatus() == BasicPlayer.STOPPED) {
        basicPlayer.resume();
      } else if (basicPlayer.getStatus() == BasicPlayer.PLAYING) {
        basicPlayer.pause();
      }
    } catch (BasicPlayerException e) {
      logger.warn("Failed to toggle the player", e);
    }
  }

  public void stop() {
    try {
      basicPlayer.stop();
    } catch (BasicPlayerException e) {
      logger.warn("Failed to stop the player", e);
    }
  }
}