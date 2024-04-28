package net.radioreg.radioPlayer.event.station;

import net.radioreg.radioPlayer.player.gson.Stream;
import net.labymod.api.event.Event;

public record StreamChangeTrackEvent(Stream stream) implements Event {

}
