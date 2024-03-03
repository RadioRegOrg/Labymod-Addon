package de.optischa.radioPlayer.widget;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.hudwidgets.RadioPlayerHudWidget;
import de.optischa.radioPlayer.player.gson.Stream;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.HudWidget.Updatable;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

@AutoWidget
public class IngameWidget extends FlexibleContentWidget implements Updatable {

  private final RadioPlayerHudWidget hudWidget;

  private ComponentWidget trackWidget;
  private ComponentWidget artistWidget;
  private ComponentWidget stationWidget;
  private IconWidget coverWidget;
  private final Main addon;

  public IngameWidget(RadioPlayerHudWidget hudWidget) {
    this.hudWidget = hudWidget;
    this.addon = Main.get();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.children.clear();

    if (!this.hudWidget.getConfig().showCover().get()) {
      this.addId("no-cover");
    }

    if (!this.hudWidget.getConfig().showStation().get()) {
      this.addId("no-station");
    }

    boolean leftAligned = this.hudWidget.anchor().isLeft();
    this.addId(leftAligned ? "left" : "right");

    this.coverWidget = new IconWidget(this.hudWidget.getIcon());
    this.coverWidget.setCleanupOnDispose(true);
    this.coverWidget.addId("cover");

    if (leftAligned) {
      this.addContent(this.coverWidget);
    }

    FlexibleContentWidget player = new FlexibleContentWidget();
    player.addId("player");

    FlexibleContentWidget textContent = new FlexibleContentWidget();
    textContent.addId("text-content");

    VerticalListWidget<ComponentWidget> text = new VerticalListWidget<>();
    text.addId("text");

    this.trackWidget = ComponentWidget.empty();
    this.trackWidget.addId("track-name");
    text.addChild(this.trackWidget);

    this.artistWidget = ComponentWidget.empty();
    this.artistWidget.addId("artist-name");
    text.addChild(this.artistWidget);

    this.stationWidget = ComponentWidget.empty();
    this.stationWidget.addId("station-name");
    text.addChild(this.stationWidget);

    if (leftAligned) {
      textContent.addFlexibleContent(text);
    } else {
      textContent.addFlexibleContent(text);
    }

    player.addFlexibleContent(textContent);

    this.addContent(player);
    
    if (!leftAligned) {
      this.addContent(this.coverWidget);
    }

    this.updateTrack(addon.configuration().selectedStream());
  }

  @Override
  public void update(String reason) {
    if (reason == null) {
      this.reInitialize();
      return;
    }

    if (reason.equals(RadioPlayerHudWidget.TRACK_CHANGE_REASON)) {
      this.updateTrack(addon.configuration().selectedStream());
    }

    if (reason.equals(RadioPlayerHudWidget.COVER_VISIBILITY_REASON)) {
      if (this.hudWidget.getConfig().showCover().get()) {
        this.removeId("no-cover");
      } else {
        this.addId("no-cover");
      }
    }

    if (reason.equals(RadioPlayerHudWidget.CHANGE_STATION_SHOWING_REASON)) {
      if (this.hudWidget.getConfig().showStation().get()) {
        this.removeId("no-station");
      } else {
        this.addId("no-station");
      }
    }
  }

  private void updateTrack(Stream stream) {
    if(stream.title == null || stream.artist == null || stream.cover == null || this.artistWidget == null
        || this.trackWidget == null) {
      return;
    }
    this.trackWidget.setComponent(Component.text(stream.title));
    this.artistWidget.setComponent(Component.text(stream.artist));
    this.stationWidget.setComponent(Component.text(stream.name));

    this.coverWidget.icon().set(Icon.url(stream.cover));
  }
}
