package de.optischa.radioPlayer.utils;

import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;

public class RadioRegTextures {
  public static class Common {

    public static final Icon ICON;

    static {
      ICON = Icon.texture(ResourceLocation.create("radioreg", "textures/icon.png"));
    }

  }
}
