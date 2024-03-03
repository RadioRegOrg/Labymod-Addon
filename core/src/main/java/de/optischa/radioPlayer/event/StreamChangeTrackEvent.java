package de.optischa.radioPlayer.event;

import de.optischa.radioPlayer.player.gson.Stream;
import net.labymod.api.event.Event;

public class StreamChangeTrackEvent implements Event {
  private final Stream stream;

  public StreamChangeTrackEvent(Stream stream) {
    this.stream = stream;
  }

  public Stream getStream() {
    return this.stream;
  }
}
