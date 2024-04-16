package de.optischa.radioPlayer.hudwidgets;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.event.StreamChangeTrackEvent;
import de.optischa.radioPlayer.hudwidgets.RadioPlayerHudWidget.RadioRegHudWidgetConfig;
import de.optischa.radioPlayer.widget.IngameWidget;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.widget.WidgetHudWidget;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.hud.HudWidgetWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.ThreadSafe;
import net.labymod.api.util.bounds.area.RectangleAreaPosition;

@Link("ingame-widget.lss")
public class RadioPlayerHudWidget extends WidgetHudWidget<RadioRegHudWidgetConfig> {
  public static final String TRACK_CHANGE_REASON = "track_change";
  public static final String COVER_VISIBILITY_REASON = "cover_visibility";
  public static final String CHANGE_STATION_SHOWING_REASON = "station_visibility";

  private final Main addon;

  public RadioPlayerHudWidget(String id) {
    super(id, RadioRegHudWidgetConfig.class);
    this.addon = Main.get();

    setIcon(Icon.texture(ResourceLocation.create("radioreg", "textures/icon.png")));
  }

  @Override
  public void initializePreConfigured(RadioRegHudWidgetConfig config) {
    super.initializePreConfigured(config);

    config.setEnabled(false);
    config.setAreaIdentifier(RectangleAreaPosition.TOP_RIGHT);
    config.setX(-2);
    config.setY(2);
    config.setParentToTailOfChainIn(RectangleAreaPosition.TOP_RIGHT);
  }

  @Override
  public void initialize(HudWidgetWidget widget) {
    super.initialize(widget);

    IngameWidget ingameWidget = new IngameWidget(this);
    ingameWidget.backgroundColor().set(this.config.backgroundColor().get());
    widget.addChild(ingameWidget);
    widget.addId("radio");

    this.config.backgroundColor().addChangeListener((property, prevValue, newValue) -> ThreadSafe.executeOnRenderThread(() -> {
      if (!this.isEnabled()) {
        return;
      }
      ingameWidget.backgroundColor().set(newValue);
    }));

    this.config.showCover().addChangeListener((property, prevValue, newValue) -> ThreadSafe.executeOnRenderThread(() -> {
      if (!this.isEnabled()) {
        return;
      }
      this.requestUpdate(COVER_VISIBILITY_REASON);
    }));

    this.config.showStation().addChangeListener((property, prevValue, newValue) -> ThreadSafe.executeOnRenderThread(() -> {
      if (!this.isEnabled()) {
        return;
      }
      this.requestUpdate(CHANGE_STATION_SHOWING_REASON);
    }));
  }

  @Subscribe
  public void onStreamChangeTrackEvent(StreamChangeTrackEvent event) {
    ThreadSafe.executeOnRenderThread(() -> {
      if (!this.isEnabled()) {
        return;
      }

      this.requestUpdate(TRACK_CHANGE_REASON);
    });
  }

  @Override
  public boolean isVisibleInGame() {
    return this.addon.musicPlayer.isPlaying();
  }

  public static class RadioRegHudWidgetConfig extends HudWidgetConfig {
    @SwitchSetting
    private final ConfigProperty<Boolean> showCover = ConfigProperty.create(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> showStation = ConfigProperty.create(false);

    @ColorPickerSetting(alpha = true)
    private final ConfigProperty<Integer> backgroundColor = ConfigProperty.create(0x000000);

    public ConfigProperty<Boolean> showCover() {
      return this.showCover;
    }

    public ConfigProperty<Integer> backgroundColor() {
      return this.backgroundColor;
    }

    public ConfigProperty<Boolean> showStation() {
      return this.showStation;
    }
  }
}
