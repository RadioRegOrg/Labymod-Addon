package net.radioreg.radioPlayer.event.station;

import net.radioreg.radioPlayer.player.gson.UpdateStream;
import net.labymod.api.event.Event;

public record UpdateStreamEvent (UpdateStream stream) implements Event {

}
