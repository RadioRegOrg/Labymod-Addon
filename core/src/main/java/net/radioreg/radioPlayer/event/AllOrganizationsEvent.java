package net.radioreg.radioPlayer.event;

import net.radioreg.radioPlayer.player.gson.Organization;
import net.labymod.api.event.Event;

public record AllOrganizationsEvent(Organization[] organizations) implements Event {

}
