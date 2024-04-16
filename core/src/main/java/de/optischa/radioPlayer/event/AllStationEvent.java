package de.optischa.radioPlayer.event;

import de.optischa.radioPlayer.player.gson.Stream;
import net.labymod.api.event.Event;

public class AllStationEvent implements Event {
  private final Stream[] streams;

  public AllStationEvent(Stream[] streams) {
    this.streams = streams;
  }

  public Stream[] getStreams() {
    return this.streams;
  }
}
