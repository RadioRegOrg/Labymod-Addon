package de.optischa.radioPlayer.event;

import de.optischa.radioPlayer.player.gson.UpdateStream;
import net.labymod.api.event.Event;

public class StationUpdateEvent implements Event {
  private final UpdateStream updateStream;

  public StationUpdateEvent(UpdateStream updateStream) {
    this.updateStream = updateStream;
  }

  public UpdateStream getUpdateStream() {
    return this.updateStream;
  }
}
