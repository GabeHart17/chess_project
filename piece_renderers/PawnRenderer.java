package piece_renderers;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class PawnRenderer extends PieceRenderer {
  private double[] xRatios = {0.25, 0.75, 0.75, 0.625, 0.5, 0.375, 0.25};
  private double[] yRatios = {0.875, 0.875, 0.75, 0.75, 0.25, 0.75, 0.75};
  double[] xRel = new double[yRatios.length];
  double[] yRel = new double[yRatios.length];
  public PawnRenderer (double size, Color color) {
    super(size, color);
    for (int i = 0; i < xRatios.length; i++) {
      xRel[i] = size * xRatios[i];
      yRel[i] = size * yRatios[i];
    }
  }
  public void render(GraphicsContext gc, double x, double y)  {
    gc.clearRect(x, y, size, size);
    gc.setStroke(Color.TRANSPARENT);
    gc.setFill(color);
    double[] xAbs = new double[yRatios.length];
    double[] yAbs = new double[yRatios.length];
    for (int i = 0; i < xRel.length; i++) {
      xAbs[i] = xRel[i] + x;
      yAbs[i] = yRel[i] + y;
    }
    gc.fillPolygon(xAbs, yAbs, xAbs.length);
    gc.fillOval(x + size * 0.375, y + size * 0.125, size * 0.25, size * 0.25);
  }
}
