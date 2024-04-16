package de.optischa.radioPlayer.listener;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.event.AllStationEvent;
import de.optischa.radioPlayer.event.StationUpdateEvent;
import de.optischa.radioPlayer.event.StreamChangeTrackEvent;
import de.optischa.radioPlayer.player.gson.Song;
import de.optischa.radioPlayer.player.gson.Stream;
import de.optischa.radioPlayer.player.gson.UpdateStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.labymod.api.event.Subscribe;

public class StreamUpdateListener {

  private final Main addon;

  public StreamUpdateListener() {
    this.addon = Main.get();
  }

  @Subscribe
  public void onAllStationEvent(AllStationEvent event) {
    if (event.getStreams() != null) {
      this.addon.musicPlayer.streams = event.getStreams();
    }
  }

  @Subscribe
  public void onAllStationEvent(StationUpdateEvent event) {
    UpdateStream updateStream = event.getUpdateStream();
    List<Stream> newStreams = new ArrayList<>();

    Arrays.stream(Main.get().musicPlayer.streams).toList().forEach(stream -> {
      if (stream.id == updateStream.id) {
        Stream newStream = new Stream(stream.id, stream.name, stream.url,
            new Song(updateStream.title, updateStream.artist, updateStream.cover),
            stream.organization);
        newStreams.add(newStream);

        if (Main.get().musicPlayer.isPlaying()) {
          if (updateStream.id == addon.configuration().selectedStream().id) {
            addon.configuration().setSelectedStream(newStream);
            addon.labyAPI().eventBus().fire(new StreamChangeTrackEvent(newStream));
          }
        }
      } else {
        newStreams.add(stream);
      }
    });
    addon.musicPlayer.streams = newStreams.toArray(Stream[]::new);
    addon.stationsActivity.updateStations();
  }

  @Subscribe
  public void onChangeTrack(StreamChangeTrackEvent event) {
    addon.stationsActivity.updateStations();
  }
}
