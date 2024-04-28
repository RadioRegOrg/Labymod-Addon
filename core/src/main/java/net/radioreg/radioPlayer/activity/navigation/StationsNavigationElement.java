package net.radioreg.radioPlayer.activity.navigation;

import net.radioreg.radioPlayer.Main;
import net.radioreg.radioPlayer.utils.RadioRegTextures.Common;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.navigation.elements.ScreenNavigationElement;

public class StationsNavigationElement extends ScreenNavigationElement {

  private final Main addon;

  public StationsNavigationElement(Main addon) {
      super(addon.mainActivity);
      this.addon = addon;
  }

  @Override
  public String getWidgetId() {
    return "radioreg_navigation";
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("radioreg.navigation.title");
  }

  @Override
  public Icon getIcon() {
    return Common.ICON;
  }

  @Override
  public boolean isVisible() {
    return this.addon.configuration().enabled().get();
  }
}
