package net.radioreg.radioPlayer.player;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.ThreadLocalHttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.logging.Logging;
import net.radioreg.radioPlayer.Main;
import net.radioreg.radioPlayer.event.station.StreamChangeTrackEvent;
import net.radioreg.radioPlayer.player.gson.Stream;

public class MusicPlayer {

  private final Main addon;
  private int currentVolume;
  private Stream currentStream;
  private final Logging logger;
  public List<Stream> streams;
  private final AudioPlayerManager playerManager;
  private final AudioPlayer player;
  private final BlockingQueue<AudioTrack> trackQueue;
  private SourceDataLine outputLine;
  private boolean playing;


  public MusicPlayer(Logging logger) {
    this.logger = logger;
    this.addon = Main.get();

    this.currentStream = this.addon.configuration().selectedStream();
    this.currentVolume = this.addon.configuration().volumeSlider().get();

    this.logger.info("Loading the Musicplayer...");


    AudioDataFormat outputFormat = StandardAudioDataFormats.COMMON_PCM_S16_LE;
    this.logger.debug("Initializing AudioPlayerManager...");
    this.playerManager = new DefaultAudioPlayerManager();
    playerManager.getConfiguration().setOutputFormat(outputFormat);
    AudioSourceManagers.registerRemoteSources(playerManager);
    this.player = playerManager.createPlayer();
    this.trackQueue = new LinkedBlockingQueue<>();
    this.playing = false;
    this.logger.debug("AudioPlayerManager initialized.");

    AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
        outputFormat.sampleRate,
        16,
        outputFormat.channelCount,
        outputFormat.channelCount * 2,
        outputFormat.sampleRate,
        false);

    try {
      this.logger.debug("Initializing SourceDataLine...");
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
      if (!AudioSystem.isLineSupported(info)) {
        this.logger.debug("Line matching " + info + " is not supported.");
        return;
      }
      this.outputLine = (SourceDataLine) AudioSystem.getLine(info);
      this.outputLine.open(format);
      this.outputLine.start();
      this.logger.debug("Audio output line started with format: " + format);
    } catch (LineUnavailableException e) {
      this.logger.error("LineUnavailableException: " + e.getMessage());
      e.printStackTrace();
    }

    // Setup audio frame processing
    new Thread(this::processAudioFrames).start();
    this.logger.debug("Audio frame processing thread started.");

    this.changeUserAgent();

    this.logger.info("Finished initialization the Musicplayer...");
  }

  private void processAudioFrames() {
    this.logger.debug("Entering audio frame processing loop...");
    while (true) {
      if (playing) {
        AudioFrame frame = player.provide();
        if (frame != null) {
          try {
            byte[] data = frame.getData();
            outputLine.write(data, 0, data.length);
          } catch (Exception e) {
            this.logger.error("Exception in processing audio frames: " + e.getMessage());
          }
        }
      } else {
        try {
          Thread.sleep(1000);  // Wait for a second before checking again
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void updateCurrentStream() {
    if (!this.isPlaying()) {
      return;
    }
    Stream currentStreamRequest = this.addon.configuration().selectedStream();
    List<Stream> streams1 = streams.stream()
        .filter(stream -> stream.id == this.addon.configuration().selectedStream().id).toList();
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
    stop();
    play(stream);
  }

  public boolean isPlaying() {
    return !player.isPaused() && player.getPlayingTrack() != null;
  }

  public void setVolume(int volume) {
    this.logger.debug("Setting volume to: " + volume);
    player.setVolume(volume);
  }

  private void checkVolume() {
    if (this.currentVolume != (this.addon.configuration().volumeSlider().get())) {
      this.currentVolume = this.addon.configuration().volumeSlider().get();
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
        stop();
      }

      this.checkVolume();

      playerManager.loadItem(stream.url, new AudioLoadResultHandler() {
        @Override
        public void trackLoaded(AudioTrack track) {
          logger.debug("Track loaded: " + track.getInfo().title);
          player.startTrack(track, false);
          player.setVolume(currentVolume);
          trackQueue.add(track);
          playing = true;
          logger.debug("Started playing track: " + track.getInfo().title);
          currentStream = stream;
          addon.labyAPI().notificationController().push(
              PlayerNotification.sendInfoNotification("radioreg.notification.playing.name",
                  Component.translatable("radioreg.notification.playing.description",
                      Component.text(stream.name))));
          updateCurrentStream();
          addon.stationsActivity.updateStations();

          addon.getWebsocket().startCheckTimer();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
          logger.debug("Playlist loaded with " + playlist.getTracks().size() + " tracks.");
          for (AudioTrack track : playlist.getTracks()) {
            trackQueue.add(track);
          }
          player.startTrack(trackQueue.poll(), false);
          playing = true;
          logger.debug("Started playing playlist.");
        }

        @Override
        public void noMatches() {
          logger.debug("No matches found for: " + stream.name);
        }

        @Override
        public void loadFailed(FriendlyException e) {
          logger.debug("Failed to load track: " + stream.name);
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      logger.error("Failed to open the stream");
      logger.debug("", e);
    }
  }

  public void pause() {
    player.setPaused(true);
  }


  public void toggle() {
    if (!player.isPaused()) {
      this.logger.debug("Pausing playback.");
      stop();
    } else {
      this.logger.debug("Resuming playback.");
      play();
    }
  }

  public void stop() {
    this.logger.debug("Stopping playback.");
    player.stopTrack();
    playing = false;
    addon.getWebsocket().stopCheckTimer();
  }

  public void changeNewURL(Stream stream) {
    stop();
    addon.stationsActivity.updateStations();
    this.play(stream);
  }

  private void changeUserAgent() {
    try {
      final Field field = this.playerManager.getClass().getDeclaredField("sourceManagers");
      field.setAccessible(true);
      @SuppressWarnings("TypeMayBeWeakened")
      final List<AudioSourceManager> sourceManagers = (List<AudioSourceManager>) field.get(this.playerManager);
      for(final AudioSourceManager sourceManager : sourceManagers) {
        if(sourceManager instanceof HttpAudioSourceManager) {
          final ThreadLocalHttpInterfaceManager shim =
              new ThreadLocalHttpInterfaceManager(
                  HttpClientTools.createSharedCookiesHttpBuilder()
                      .setRedirectStrategy(new HttpClientTools.NoRedirectsStrategy())
                      .setUserAgent("RadioReg/LabyMod"),
                  HttpClientTools.DEFAULT_REQUEST_CONFIG);
          final Field him = sourceManager.getClass().getDeclaredField("httpInterfaceManager");
          him.setAccessible(true);
          him.set(sourceManager, shim);
        }
      }
    } catch(final NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public Stream getCurrentStream() {
    return currentStream;
  }
}