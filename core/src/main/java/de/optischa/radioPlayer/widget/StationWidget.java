package de.optischa.radioPlayer.widget;

import de.optischa.radioPlayer.player.gson.Stream;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.resources.ResourceLocation;

@AutoWidget
public class StationWidget extends SimpleWidget {
  private final Stream stream;
  public StationWidget(Stream stream) {
    this.stream = stream;
  }
  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget itemContainerWidget = new FlexibleContentWidget();
    itemContainerWidget.addId("item-container");

    IconWidget iconWidget = new IconWidget(this.getIconWidget(this.stream.song.cover));
    iconWidget.addId("avatar");
    itemContainerWidget.addContent(iconWidget);

    FlexibleContentWidget itemTextContainerWidget = new FlexibleContentWidget();
    itemTextContainerWidget.addId("item-text-container");

    ComponentWidget nameWidget = ComponentWidget.component(Component.text(this.stream.name));
    nameWidget.addId("name");
    itemTextContainerWidget.addContent(nameWidget);

    ComponentWidget titleWidget = ComponentWidget.component(Component.text(this.stream.song.title != null ? this.stream.song.title : ""));
    titleWidget.addId("title");
    itemTextContainerWidget.addContent(titleWidget);

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
