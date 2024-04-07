package de.optischa.radioPlayer.player;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.event.StreamChangeTrackEvent;
import de.optischa.radioPlayer.player.basic.BasicPlayer;
import de.optischa.radioPlayer.player.basic.BasicPlayerException;
import de.optischa.radioPlayer.player.gson.Stream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.logging.Logging;

public class MusicPlayer {

  private final Main addon;
  private final BlockingQueue<Runnable> queue;
  private final BasicPlayer basicPlayer;
  private double currentVolume;
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

    this.currentStream = this.addon.configuration().selectedStream();
    this.currentVolume = this.addon.configuration().volumeSlider().get();
  }

  private void updateCurrentStream() {
    if (!this.isPlaying()) {
      return;
    }
    Stream currentStreamRequest = this.addon.configuration().selectedStream();
    List<Stream> streams1 = Arrays.stream(streams).filter(stream -> stream.id == this.addon.configuration().selectedStream().id).toList();
    if(streams1.isEmpty())
      return;
    if (currentStreamRequest.song.artist.equalsIgnoreCase(streams1.get(0).song.artist)
        && currentStreamRequest.song.title.equalsIgnoreCase(streams1.get(0).song.title)) {
      return;
    }
    this.addon.configuration().setSelectedStream(streams1.get(0));
    this.addon.labyAPI().eventBus().fire(new StreamChangeTrackEvent(streams1.get(0)));
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

    try {
      if (isPlaying()) {
        this.stop();
      }

      this.checkVolume();

      URL url = new URL(stream.url);
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent", "RadioReg/Labymod");
      connection.setConnectTimeout(1000);

      queue.offer(() -> {
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
          logger.error("Failed to play the stream", e);
        }
      });
    } catch (Exception e) {
      logger.error("Failed to open the stream", e);
    }

  }

  public void pause() {
    queue.offer(() -> {
      try {
        basicPlayer.stop();
      } catch (BasicPlayerException e) {
        logger.error("Failed to stop the player", e);
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
        logger.error("Failed to toggle the player", e);
      }
    });
  }

  public void stop() {
    queue.offer(() -> {
      try {
        this.basicPlayer.stop();
      } catch (BasicPlayerException e) {
        logger.error("Failed to stop the player", e);
      }
    });
  }
}