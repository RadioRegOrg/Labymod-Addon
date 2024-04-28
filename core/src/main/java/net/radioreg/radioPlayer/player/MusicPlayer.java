package net.radioreg.radioPlayer.player;

import net.radioreg.radioPlayer.Main;
import net.radioreg.radioPlayer.event.station.StreamChangeTrackEvent;
import net.radioreg.radioPlayer.player.basic.BasicPlayer;
import net.radioreg.radioPlayer.player.basic.BasicPlayerException;
import net.radioreg.radioPlayer.player.gson.Stream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.logging.Logging;

public class MusicPlayer extends BasicPlayer {

  private final Main addon;
  private final BlockingQueue<Runnable> queue;
  private double currentVolume;
  private Stream currentStream;
  private final Logging logger;
  public List<Stream> streams;


  public MusicPlayer(Logging logger) {
    this.logger = logger;
    this.addon = Main.get();

    this.queue = new LinkedBlockingQueue<>();
    Thread playerThread = new Thread(() -> {
      while (true) {
        try {
          Runnable task = queue.take();
          task.run();
        } catch (InterruptedException e) {
          this.logger.error(e.getMessage());
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
    List<Stream> streams1 = streams.stream().filter(stream -> stream.id == this.addon.configuration().selectedStream().id).toList();
    if (streams1.isEmpty()) {
      return;
    }
    if (currentStreamRequest.song == null || currentStreamRequest.song.artist == null
        || currentStreamRequest.song.title == null) {
      return;
    }
    if (currentStreamRequest.song.artist.equalsIgnoreCase(streams1.getFirst().song.artist)
        && currentStreamRequest.song.title.equalsIgnoreCase(streams1.getFirst().song.title)) {
      return;
    }
    this.addon.configuration().setSelectedStream(streams1.getFirst());
    this.addon.labyAPI().eventBus().fire(new StreamChangeTrackEvent(streams1.getFirst()));
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
      super.stop();
      play(stream);
      super.resume();
    } catch (BasicPlayerException e) {
      logger.warn("Failed to resume the player", e);
    }
  }

  public boolean isPlaying() {
    return super.getStatus() == BasicPlayer.PLAYING;
  }

  public void setVolume(float volume) {
    if (!isPlaying()) {
      return;
    }
    this.currentVolume = volume;
    try {
      super.setGain(volume);
    } catch (BasicPlayerException e) {
      logger.warn("Failed to set the volume");
      logger.debug("", e);
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
        super.stop();
      }

      this.checkVolume();

      URL url = new URL(stream.url);
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent", "RadioReg/Labymod");

      queue.offer(() -> {
        try {
          super.open(connection.getInputStream());
          super.play();
          super.setGain(this.currentVolume);
          currentStream = stream;
          this.addon.labyAPI().notificationController().push(
              PlayerNotification.sendInfoNotification("radioreg.notification.playing.name",
                  Component.translatable("radioreg.notification.playing.description",
                      Component.text(stream.name)))
          );
          this.updateCurrentStream();
          addon.stationsActivity.updateStations();
        } catch (BasicPlayerException | IOException e) {
          logger.error("Failed to play the stream", e);
          logger.debug("", e);
        }
      });
    } catch (Exception e) {
      logger.error("Failed to open the stream");
      logger.debug("", e);
    }

  }

  public void pause() {
    queue.offer(() -> {
      try {
        super.stop();
      } catch (BasicPlayerException e) {
        logger.error("Failed to stop the player");
        logger.debug("", e);
      }
    });
  }

  public void toggle() {
    queue.offer(() -> {
      try {
        if (super.getStatus() == BasicPlayer.PAUSED
            || super.getStatus() == BasicPlayer.STOPPED) {
          super.resume();
        } else if (super.getStatus() == BasicPlayer.PLAYING) {
          super.pause();
        }
      } catch (BasicPlayerException e) {
        logger.error("Failed to toggle the player");
        logger.debug("", e);
      }
    });
  }

  public void stop() {
    queue.offer(() -> {
      try {
        super.stop();
        addon.stationsActivity.updateStations();
      } catch (BasicPlayerException e) {
        logger.error("Failed to stop the player");
        logger.debug("", e);
      }
    });
  }

  public void changeNewURL(Stream stream) {
    queue.offer(() -> {
      try {
        super.stop();
        addon.stationsActivity.updateStations();
        this.play(stream);
      } catch (BasicPlayerException e) {
        logger.error("Failed to stop the player");
        logger.debug("", e);
      }
    });
  }
}