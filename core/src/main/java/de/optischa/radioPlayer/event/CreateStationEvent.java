package de.optischa.radioPlayer.event;

import de.optischa.radioPlayer.player.gson.Stream;
import net.labymod.api.event.Event;

public class CreateStationEvent implements Event {
  private final Stream stream;

  public CreateStationEvent(Stream stream) {
    this.stream = stream;
  }

  public Stream getStream() {
    return this.stream;
  }
}
