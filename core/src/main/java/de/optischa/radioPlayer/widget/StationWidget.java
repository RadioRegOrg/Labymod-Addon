package de.optischa.radioPlayer.widget;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.player.gson.Stream;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.action.Pressable;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.resources.ResourceLocation;

@AutoWidget
public class StationWidget extends SimpleWidget {
  private final Stream stream;
  private final Pressable pressable;
  private final Main addon;
  public StationWidget(Stream stream, Pressable pressable) {
    this.addon = Main.get();
    this.stream = stream;
    this.pressable = pressable;
  }
  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget itemContainerWidget = new FlexibleContentWidget();
    itemContainerWidget.addId("item-container");

    IconWidget iconWidget = new IconWidget(this.getIconWidget(this.stream.song.cover));
    iconWidget.addId("avatar");

    IconWidget musicControlIcon;
    if(this.isActive() && this.addon.musicPlayer.isPlaying()) {
      musicControlIcon = new IconWidget(
          Icon.texture(ResourceLocation.create("radioreg", "textures/pause-outline.png")));
      musicControlIcon.addId("pause-button");
      musicControlIcon.setPressable(() -> {
        addon.musicPlayer.stop();
        addon.stationsActivity.updateStations();
      });
    } else {
      musicControlIcon = new IconWidget(
          Icon.texture(ResourceLocation.create("radioreg", "textures/play-outline.png")));
      musicControlIcon.addId("play-button");
      musicControlIcon.setPressable(() -> {
        if(!this.isActive()) {
          addon.configuration().setSelectedStream(stream);
        }
        addon.musicPlayer.play(stream);
      });
    }
    iconWidget.addChild(musicControlIcon);
    itemContainerWidget.addContent(iconWidget);


    FlexibleContentWidget itemTextContainerWidget = new FlexibleContentWidget();
    itemTextContainerWidget.addId("item-text-container");

    // STATION NAME
    ComponentWidget nameWidget = ComponentWidget.component(Component.text(this.stream.name));
    nameWidget.addId("name");
    itemTextContainerWidget.addContent(nameWidget);

    // ORGANIZATION NAME
    ComponentWidget organizationWidget = ComponentWidget.component(Component.text(this.stream.organization.name));
    organizationWidget.addId("organization");
    organizationWidget.setPressable(pressable);
    itemTextContainerWidget.addContent(organizationWidget);

    // SONG TITLE
    ComponentWidget titleWidget = ComponentWidget.component(Component.text(this.stream.song.title != null ? this.stream.song.title : ""));
    titleWidget.addId("title");
    itemTextContainerWidget.addContent(titleWidget);

    // SONG ARTIST
    ComponentWidget artistWidget = ComponentWidget.component(Component.text(this.stream.song.artist != null ? this.stream.song.artist : ""));
    artistWidget.addId("artist");
    itemTextContainerWidget.addContent(artistWidget);

    itemContainerWidget.addContent(itemTextContainerWidget);

    this.addChild(itemContainerWidget);
  }

  public Stream getStream() {
    return stream;
  }

  public Icon getIconWidget(String url) {
    if(url == null) {
      return Icon.texture(ResourceLocation.create("radioreg", "textures/icon.png"));
    } else {
      return Icon.url(url);
    }
  }
}
