package net.radioreg.radioPlayer;

import net.radioreg.radioPlayer.player.MusicPlayer;
import net.radioreg.radioPlayer.player.gson.Stream;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@SuppressWarnings("FieldMayBeFinal")
@ConfigName("settings")
public class Configuration extends AddonConfig {

  @SwitchSetting
  private ConfigProperty<Boolean> enabled = new ConfigProperty<>(true).addChangeListener(
      (property, prevValue, newValue) -> {
        Main main = Main.get();
        if(main == null) {
          return;
        }
        MusicPlayer musicPlayer = main.musicPlayer;
        if (musicPlayer == null) {
          return;
        }
        if (newValue) {
          main.getWebsocket().connect();
          musicPlayer.play(this.selectedStream());
        } else {
          main.getWebsocket().disconnect();
          musicPlayer.stop();
        }
      });

  @SliderSetting(max = 100, min = 0, steps = 1)
  private ConfigProperty<Integer> volumeSlider = new ConfigProperty<>(25).addChangeListener(
      (property, prevValue, newValue) -> {
        Main main = Main.get();
        if(main == null) {
          return;
        }
        MusicPlayer musicPlayer = main.musicPlayer;
        if (musicPlayer == null) {
          return;
        }
        Main.get().musicPlayer.setVolume((float) newValue / 100);
      });

  @KeyBindSetting()
  @SettingSection("keys")
  private ConfigProperty<Key> toggleKey = new ConfigProperty<>(Key.NONE);

  @KeyBindSetting()
  private ConfigProperty<Key> pauseKey = new ConfigProperty<>(Key.NONE);

  @KeyBindSetting()
  private ConfigProperty<Key> playKey = new ConfigProperty<>(Key.NONE);

  @Exclude
  private Stream selectedStream = new Stream(0, "Default", "",null, null);

  public ConfigProperty<Key> toggleKey() {
    return this.toggleKey;
  }

  public ConfigProperty<Key> pauseKey() {
    return this.pauseKey;
  }

  public ConfigProperty<Key> playKey() {
    return this.playKey;
  }

  public Stream selectedStream() {
    return selectedStream;
  }

  public void setSelectedStream(Stream selectedStream) {
    this.selectedStream = selectedStream;
  }

  public ConfigProperty<Integer> volumeSlider() {
    return this.volumeSlider;
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }
}