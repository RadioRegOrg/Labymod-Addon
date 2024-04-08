package de.optischa.radioPlayer.player;

import com.google.gson.Gson;
import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.event.StreamChangeTrackEvent;
import de.optischa.radioPlayer.player.gson.Song;
import de.optischa.radioPlayer.player.gson.Stream;
import de.optischa.radioPlayer.player.gson.UpdateStream;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;
import net.labymod.api.util.logging.Logging;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Websocket {

  private final Logging logger;
  private Socket socket;

  public Websocket(Logging logger, String serverUrl) {
    this.logger = logger;

    IO.Options options = new IO.Options();
    options.forceNew = true;
    options.reconnection = true;

    try {
      this.socket = IO.socket(serverUrl, options);
    } catch (URISyntaxException e) {
      this.logger.error("Fehler beim Socket erstellen");
    }

    this.logger.info("Initialisierung vom Websocket Abgeschlossen");

    this.onEvent("connect", objects -> {
      this.logger.info("Verbindung zum Websocket hergestellt");

      Main.get().getWebsocket().onEvent("all_stations",
          objects1 -> Main.get().musicPlayer.streams = new Gson().fromJson(
              Arrays.stream(objects1).toList().get(0).toString(), Stream[].class));

      Main.get().getWebsocket().onEvent("station_stream_content_update", objects1 -> {
        UpdateStream updateStream = new Gson().fromJson(
            Arrays.stream(objects1).toList().get(0).toString(), UpdateStream.class);

        List<Stream> newStreams = new ArrayList<>();

        Arrays.stream(Main.get().musicPlayer.streams).toList().forEach(stream -> {
          if (stream.id == updateStream.id) {
            Stream newStream = new Stream(stream.id, stream.name, stream.url,
                new Song(updateStream.title, updateStream.artist, updateStream.cover));
            newStreams.add(newStream);

            if (Main.get().musicPlayer.isPlaying()) {
              if (updateStream.id == Main.get().configuration().selectedStream().id) {
                Main.get().configuration().setSelectedStream(newStream);
                Main.get().labyAPI().eventBus().fire(new StreamChangeTrackEvent(newStream));
              }
            }
          } else {
            newStreams.add(stream);
          }
        });
        Main.get().musicPlayer.streams = newStreams.toArray(Stream[]::new);
      });
    });

  }

  public void connect() {
    this.socket.connect();
  }

  public void disconnect() {
    this.socket.disconnect();
  }

  public void sendMessage(String event, String message) {
    this.socket.emit(event, message);
  }

  public void sendMessage(String event) {
    this.socket.emit(event);
  }

  public void onEvent(String event, Listener listener) {
    this.socket.on(event, listener);
  }

  public void removeEventListener(String event) {
    this.socket.off(event);
  }
}
