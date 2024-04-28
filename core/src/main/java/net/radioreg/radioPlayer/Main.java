package net.radioreg.radioPlayer;

import net.radioreg.radioPlayer.activity.MainActivity;
import net.radioreg.radioPlayer.activity.StationsActivity;
import net.radioreg.radioPlayer.activity.navigation.StationsNavigationElement;
import net.radioreg.radioPlayer.hudwidgets.RadioPlayerHudWidget;
import net.radioreg.radioPlayer.listener.HotkeyListener;
import net.radioreg.radioPlayer.listener.StreamUpdateListener;
import net.radioreg.radioPlayer.player.MusicPlayer;
import net.radioreg.radioPlayer.player.Websocket;
import net.radioreg.radioPlayer.player.gson.Organization;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.models.addon.annotation.AddonMain;
import java.util.List;

@AddonMain
public class Main extends LabyAddon<Configuration> {

  private static Main instance;
  public MusicPlayer musicPlayer;
  public StationsActivity stationsActivity;
  public MainActivity mainActivity;
  private Websocket websocket;
  private List<Organization> organizations;

  public Main() {
    Main.instance = this;
  }

  public static Main get() {
    return Main.instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    this.musicPlayer = new MusicPlayer(this.logger());

    this.stationsActivity = new StationsActivity();
    this.mainActivity = new MainActivity();

    this.websocket = new Websocket(this.logger(), "https://ws.radioreg.net/labymod");

    HudWidgetRegistry registry = this.labyAPI().hudWidgetRegistry();
    registry.register(new RadioPlayerHudWidget("stream_track"));

    this.registerListener(new HotkeyListener());
    this.registerListener(new StreamUpdateListener());

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

  public List<Organization> getOrganizations() {
    return organizations;
  }

  public void setOrganizations(List<Organization> organizations) {
    this.organizations = organizations;
  }
}