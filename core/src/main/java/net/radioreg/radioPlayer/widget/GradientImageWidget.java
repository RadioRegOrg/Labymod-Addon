package net.radioreg.radioPlayer.widget;

import net.labymod.api.client.gui.lss.property.LssProperty;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.attributes.BorderRadius;
import net.labymod.api.client.render.draw.RectangleRenderer;
import net.labymod.api.client.render.matrix.Stack;

@AutoWidget
public class GradientImageWidget extends SimpleWidget {
  private final LssProperty<Direction> direction = new LssProperty((Object)null);
  private final LssProperty<Integer> colorStart = new LssProperty<>(0);
  private final LssProperty<Integer> colorEnd = new LssProperty<>(0);

  public GradientImageWidget() {
  }

  public void render(Stack stack, MutableMouse mouse, float tickDelta) {
    RectangleRenderer rectangleRenderer = (RectangleRenderer)this.labyAPI.renderPipeline().rectangleRenderer().pos(this.bounds());
    BorderRadius borderRadius = this.getBorderRadius();
    if (borderRadius != null) {
      rectangleRenderer.rounded(borderRadius.getLeftTop(), borderRadius.getRightTop(), borderRadius.getLeftBottom(), borderRadius.getRightBottom());
    }

    Integer topColor = (Integer)this.colorStart.get();
    Direction direction = (Direction)this.direction.get();
    if (direction == null) {
      ((RectangleRenderer)rectangleRenderer.color(topColor)).render(stack);
    } else {
      Integer bottomColor = (Integer)this.colorEnd.get();
      switch (direction.ordinal()) {
        case 0 -> rectangleRenderer.gradientVertical(topColor, bottomColor);
        case 1 -> rectangleRenderer.gradientVertical(bottomColor, topColor);
        case 2 -> rectangleRenderer.gradientHorizontal(topColor, bottomColor);
        case 3 -> rectangleRenderer.gradientHorizontal(bottomColor, topColor);
      }

      rectangleRenderer.render(stack);
    }
  }

  public LssProperty<Direction> direction() {
    return this.direction;
  }

  public LssProperty<Integer> colorStart() {
    return this.colorStart;
  }

  public LssProperty<Integer> colorEnd() {
    return this.colorEnd;
  }

  public static enum Direction {
    VERTICAL_TOP_BOTTOM,
    VERTICAL_BOTTOM_TOP,
    HORIZONTAL_LEFT_RIGHT,
    HORIZONTAL_RIGHT_LEFT;

    private Direction() {
    }
  }
}
