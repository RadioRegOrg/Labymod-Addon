package de.optischa.radioPlayer.player;

import com.google.gson.Gson;
import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.event.AllStationEvent;
import de.optischa.radioPlayer.event.StationUpdateEvent;
import de.optischa.radioPlayer.player.gson.Stream;
import de.optischa.radioPlayer.player.gson.UpdateStream;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;
import java.net.URISyntaxException;
import java.util.Arrays;
import net.labymod.api.Laby;
import net.labymod.api.util.logging.Logging;

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
          objects1 -> Laby.labyAPI().eventBus().fire(new AllStationEvent(new Gson().fromJson(
              Arrays.stream(objects1).toList().getFirst().toString(), Stream[].class))));

      Main.get().getWebsocket().onEvent("station_stream_content_update", objects1 ->
          Laby.labyAPI().eventBus().fire(new StationUpdateEvent(new Gson().fromJson(
              Arrays.stream(objects1).toList().getFirst().toString(), UpdateStream.class))));
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
