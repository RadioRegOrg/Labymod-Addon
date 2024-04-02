package de.optischa.radioPlayer.player;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.event.StreamChangeTrackEvent;
import de.optischa.radioPlayer.player.basic.BasicPlayer;
import de.optischa.radioPlayer.player.basic.BasicPlayerException;
import de.optischa.radioPlayer.player.gson.Stream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.logging.Logging;

public class MusicPlayer {

  private final Main addon;
  private final BlockingQueue<Runnable> queue;
  private final BasicPlayer basicPlayer;
  private double currentVolume = 0.01;
  private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors
      .newSingleThreadScheduledExecutor();
  private Stream currentStream;
  private final Logging logger;
  public Stream[] streams;

  public MusicPlayer(BasicPlayer basicPlayer, Logging logger) {
    this.logger = logger;
    this.addon = Main.get();

    this.queue = new LinkedBlockingQueue<>();
    this.basicPlayer = basicPlayer;
    Thread playerThread = new Thread(() -> {
      while (true) {
        try {
          Runnable task = queue.take();
          task.run();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    playerThread.start();

    this.updateCurrentStreamTask();

    this.currentStream = this.addon.configuration().selectedStream();
    this.currentVolume = this.addon.configuration().volumeSlider().get();
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
    if (currentStreamRequest.song.artist.equalsIgnoreCase(updatetStream.song.artist)
        && currentStreamRequest.song.title.equalsIgnoreCase(updatetStream.song.title)) {
      return;
    }
    this.addon.configuration().setSelectedStream(updatetStream);
    this.addon.labyAPI().eventBus().fire(new StreamChangeTrackEvent(updatetStream));
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

  public boolean play(Stream stream) {
    if (currentStream.id == stream.id && this.isPlaying()) {
      this.addon.labyAPI().notificationController().push(
          PlayerNotification.sendInfoNotification("radioreg.notification.alreadyPlaying.name",
              Component.translatable("radioreg.notification.alreadyPlaying.description",
                  Component.text(stream.name)))
      );
      return false;
    }

    try {
      if (isPlaying()) {
        this.stop();
      }

      this.checkVolume();

      URL url = new URL(stream.url);
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent", "RadioReg/Labymod");

      return queue.offer(() -> {
        try {
          this.basicPlayer.open(connection.getInputStream());
          this.basicPlayer.play();
          basicPlayer.setGain(this.currentVolume);
          currentStream = stream;
          this.addon.labyAPI().notificationController().push(
              PlayerNotification.sendInfoNotification("radioreg.notification.playing.name",
                  Component.translatable("radioreg.notification.playing.description",
                      Component.text(stream.name)))
          );
          this.updateCurrentStream();
        } catch (BasicPlayerException | IOException e) {
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      logger.warn("Failed to open the stream", e);
    }

    return false;
  }

  public void pause() {
    queue.offer(() -> {
      try {
        basicPlayer.stop();
      } catch (BasicPlayerException e) {
        logger.warn("Failed to stop the player", e);
      }
    });
  }

  public void toggle() {
    queue.offer(() -> {
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
    });
  }

  public void stop() {
    queue.offer(() -> {
      try {
        this.basicPlayer.stop();
      } catch (BasicPlayerException e) {
        e.printStackTrace();
      }
    });
  }
}