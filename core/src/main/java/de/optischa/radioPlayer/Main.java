package de.optischa.radioPlayer;

import de.optischa.radioPlayer.activity.MainActivity;
import de.optischa.radioPlayer.activity.StationsActivity;
import de.optischa.radioPlayer.activity.navigation.StationsNavigationElement;
import de.optischa.radioPlayer.hudwidgets.RadioPlayerHudWidget;
import de.optischa.radioPlayer.listener.HotkeyListener;
import de.optischa.radioPlayer.player.MusicPlayer;
import de.optischa.radioPlayer.player.Websocket;
import de.optischa.radioPlayer.player.basic.BasicPlayer;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class Main extends LabyAddon<Configuration> {

  private static Main instance;
  public MusicPlayer musicPlayer;
  public StationsActivity stationsActivity;
  public MainActivity mainActivity;
  private Websocket websocket;

  public Main() {
    Main.instance = this;
  }

  public static Main get() {
    return Main.instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    BasicPlayer basicPlayer = new BasicPlayer();

    this.musicPlayer = new MusicPlayer(basicPlayer, this.logger());

    this.stationsActivity = new StationsActivity();
    this.mainActivity = new MainActivity();

    this.websocket = new Websocket(this.logger(), "http://localhost:3001/labymod");

    HudWidgetRegistry registry = this.labyAPI().hudWidgetRegistry();
    registry.register(new RadioPlayerHudWidget("stream_track"));

    this.registerListener(new HotkeyListener());

    labyAPI().navigationService().register("radioreg_main_ui", new StationsNavigationElement(this));

    this.websocket.connect();

    this.logger().info("Enabled the Addon");
  }

  @Override
  protected Class<Configuration> configurationClass() {
    return Configuration.class;
  }

  public Websocket getWebsocket() {
    return websocket;
  }
}