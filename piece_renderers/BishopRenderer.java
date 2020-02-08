package piece_renderers;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;


public class BishopRenderer extends PieceRenderer {
  private double[] xRatios = {0.25, 0.75, 0.5};
  private double[] yRatios = {0.5, 0.5, 0.25};
  double[] xRel = new double[yRatios.length];
  double[] yRel = new double[yRatios.length];
  public BishopRenderer (double size, Color color) {
    super(size, color);
    for (int i = 0; i < xRatios.length; i++) {
      xRel[i] = size * xRatios[i];
      yRel[i] = size * yRatios[i];
    }
  }
  public void render(GraphicsContext gc, double x, double y) {
    gc.clearRect(x, y, size, size);
    gc.setStroke(Color.TRANSPARENT);
    gc.setFill(color);
    gc.fillRect(x + size * 0.125, y + size * 0.75, size * 0.25, size * 0.125);
    gc.fillRect(x + size * 0.625, y + size * 0.75, size * 0.25, size * 0.125);
    double[] xAbs = new double[yRatios.length];
    double[] yAbs = new double[yRatios.length];
    for (int i = 0; i < xRel.length; i++) {
      xAbs[i] = xRel[i] + x;
      yAbs[i] = yRel[i] + y;
    }
    gc.fillPolygon(xAbs, yAbs, xAbs.length);
    gc.fillArc(x + size * 0.25, y + size * 0.25, size * 0.5, size * 0.5, 180, 180, ArcType.ROUND);
    gc.fillArc(x + size * 0.25, y + size * 0.625, size * 0.25, size * 0.25, 270, 90, ArcType.ROUND);
    gc.fillArc(x + size * 0.5, y + size * 0.625, size * 0.25, size * 0.25, 180, 90, ArcType.ROUND);
    gc.fillOval(x + size * 0.4375, y + size * 0.125, size * 0.125, size * 0.125);
  }
}
