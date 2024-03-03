package de.optischa.radioPlayer.widget;

import de.optischa.radioPlayer.Main;
import de.optischa.radioPlayer.player.gson.Stream;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

@AutoWidget
public class StationWidget extends SimpleWidget {
  private final Stream stream;
  public StationWidget(Stream stream) {
    this.stream = stream;
  }
  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    IconWidget backgroundIcon = new IconWidget(this.getIconWidget(this.stream.thumbnail));
    backgroundIcon.addId("background-cover");
    this.addChild(backgroundIcon);

    IconWidget iconWidget = new IconWidget(this.getIconWidget(this.stream.cover));
    iconWidget.addId("avatar");
    this.addChild(iconWidget);

    ComponentWidget nameWidget = ComponentWidget.component(Component.text(this.stream.name));
    nameWidget.addId("name");
    this.addChild(nameWidget);

    ComponentWidget customNameWidget = ComponentWidget.component(Component.text(this.stream.title + " - " + this.stream.artist));
    customNameWidget.addId("song");
    this.addChild(customNameWidget);
  }

  public Stream getStream() {
    return stream;
  }

  public Icon getIconWidget(String url) {
    return Icon.url(url);
  }
}
