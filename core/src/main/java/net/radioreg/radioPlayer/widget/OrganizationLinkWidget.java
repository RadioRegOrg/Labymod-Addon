package net.radioreg.radioPlayer.widget;

import net.radioreg.radioPlayer.player.gson.OrganizationLink;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.resources.ResourceLocation;

@AutoWidget
public class OrganizationLinkWidget extends FlexibleContentWidget {

  private final OrganizationLink organizationLink;

  public OrganizationLinkWidget(OrganizationLink organizationLink) {
    this.organizationLink = organizationLink;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget contentWidget = new FlexibleContentWidget();
    contentWidget.addId("content-widget");

    IconWidget internetIcon = new IconWidget(
        Icon.texture(ResourceLocation.create("radioreg", "textures/globe-outline.png")));
    internetIcon.addId("internet-icon");

    contentWidget.addContent(internetIcon);

    ComponentWidget nameWidget = ComponentWidget.component(Component.text(organizationLink.name));
    nameWidget.addId("name");
    contentWidget.addContent(nameWidget);


    this.addContent(contentWidget);
  }
}
