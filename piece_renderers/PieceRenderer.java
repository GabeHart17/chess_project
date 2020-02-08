package piece_renderers;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public abstract class PieceRenderer {
  protected double size;  // size of square in which piece is to be rendered
  protected Color color;
  public PieceRenderer (double size, Color color) {
    this.size = size;
    this.color = color;
  }
  public abstract void render(GraphicsContext gc, double x, double y);  // x, y are coords of upper left of render square
}
