package net.radioreg.radioPlayer.listener;

import net.radioreg.radioPlayer.Configuration;
import net.radioreg.radioPlayer.Main;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;

public class HotkeyListener {

  private final Main addon;

  public HotkeyListener() {
    this.addon = Main.get();
  }

  @Subscribe
  public void onHotkey(KeyEvent event) {
    if (event.state() != State.PRESS
        || Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
      return;
    }

    Configuration config = this.addon.configuration();

    if (config.playKey().get() != Key.NONE && config.playKey().get() == event.key()) {
      this.addon.musicPlayer.play();
    } else if (config.playKey().get() != Key.NONE && config.toggleKey().get() == event.key()) {
      this.addon.musicPlayer.toggle();
    } else if (config.playKey().get() != Key.NONE && config.pauseKey().get() == event.key()) {
      this.addon.musicPlayer.stop();
    }
  }

}
