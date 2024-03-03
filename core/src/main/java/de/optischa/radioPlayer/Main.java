package de.optischa.radioPlayer;

import de.optischa.radioPlayer.hudwidgets.RadioPlayerHudWidget;
import de.optischa.radioPlayer.listener.HotkeyListener;
import de.optischa.radioPlayer.player.MusicPlayer;
import de.optischa.radioPlayer.player.basic.BasicPlayer;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class Main extends LabyAddon<Configuration> {

  private static Main instance;
  private BasicPlayer basicPlayer;
  public MusicPlayer musicPlayer;

  public Main() {
    Main.instance = this;
  }

  public static Main get() {
    return Main.instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    this.basicPlayer = new BasicPlayer();

    this.musicPlayer = new MusicPlayer(this.basicPlayer, this.logger());

    HudWidgetRegistry registry = this.labyAPI().hudWidgetRegistry();
    registry.register(new RadioPlayerHudWidget("stream_track"));

    this.registerListener(new HotkeyListener());

    this.logger().info("Enabled the Addon");
  }

  @Override
  protected Class<Configuration> configurationClass() {
    return Configuration.class;
  }
}