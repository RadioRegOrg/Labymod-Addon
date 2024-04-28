package net.radioreg.radioPlayer.event.station;

import net.radioreg.radioPlayer.player.gson.UpdateStreamContent;
import net.labymod.api.event.Event;

public record StationUpdateEvent(UpdateStreamContent updateStream) implements Event {

}
