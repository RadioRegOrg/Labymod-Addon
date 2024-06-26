package net.radioreg.radioPlayer.player;

import com.google.gson.Gson;
import net.radioreg.radioPlayer.Main;
import net.radioreg.radioPlayer.event.AllOrganizationsEvent;
import net.radioreg.radioPlayer.event.UpdateOrganizationEvent;
import net.radioreg.radioPlayer.event.station.AllStationEvent;
import net.radioreg.radioPlayer.event.station.AddStationEvent;
import net.radioreg.radioPlayer.event.station.RemoveStationEvent;
import net.radioreg.radioPlayer.event.station.StationUpdateEvent;
import net.radioreg.radioPlayer.event.station.UpdateStreamEvent;
import net.radioreg.radioPlayer.player.gson.Organization;
import net.radioreg.radioPlayer.player.gson.Stream;
import net.radioreg.radioPlayer.player.gson.UpdateStream;
import net.radioreg.radioPlayer.player.gson.UpdateStreamContent;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import net.labymod.api.Laby;
import net.labymod.api.util.logging.Logging;

public class Websocket {

  private final Logging logger;
  private Socket socket;
  private Timer checkTimer;

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
              Arrays.stream(objects1).toList().getFirst().toString(), UpdateStreamContent.class))));

      Main.get().getWebsocket().onEvent("add_station", objects1 ->
          Laby.labyAPI().eventBus().fire(new AddStationEvent(new Gson().fromJson(
              Arrays.stream(objects1).toList().getFirst().toString(), Stream.class))));

      Main.get().getWebsocket().onEvent("update_station", objects1 ->
          Laby.labyAPI().eventBus().fire(new UpdateStreamEvent(new Gson().fromJson(
              Arrays.stream(objects1).toList().getFirst().toString(), UpdateStream.class))));

      Main.get().getWebsocket().onEvent("remove_station", objects1 ->
          Laby.labyAPI().eventBus().fire(new RemoveStationEvent(new Gson().fromJson(
              Arrays.stream(objects1).toList().getFirst().toString(), Stream.class))));

      Main.get().getWebsocket().onEvent("all_organizations",
          objects1 -> Laby.labyAPI().eventBus().fire(new AllOrganizationsEvent(new Gson().fromJson(
              Arrays.stream(objects1).toList().getFirst().toString(), Organization[].class))));

      Main.get().getWebsocket().onEvent("update_organization",
          objects1 -> Laby.labyAPI().eventBus().fire(new UpdateOrganizationEvent(new Gson().fromJson(
              Arrays.stream(objects1).toList().getFirst().toString(), Organization.class))));
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

  public void startCheckTimer() {
    if (checkTimer == null) {
      checkTimer = new Timer();
      checkTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          System.out.println("eee");
          if (Main.get().musicPlayer.isPlaying()) {
            sendMessage("check_stream_status", Integer.toString(Main.get().musicPlayer.getCurrentStream().id));
          }
        }
      }, 0, 60000); // Check every minute (60000 milliseconds)
    }
  }

  public void stopCheckTimer() {
    if (checkTimer != null) {
      checkTimer.cancel();
      checkTimer = null;
    }
  }
}
