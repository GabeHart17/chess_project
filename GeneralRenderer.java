import piece_renderers.*;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class GeneralRenderer {
  private double size;

  private PieceRenderer rPB;
  private PieceRenderer rRB;
  private PieceRenderer rNB;
  private PieceRenderer rBB;
  private PieceRenderer rQB;
  private PieceRenderer rKB;
  private PieceRenderer rPW;
  private PieceRenderer rRW;
  private PieceRenderer rNW;
  private PieceRenderer rBW;
  private PieceRenderer rQW;
  private PieceRenderer rKW;

  public GeneralRenderer(double size) {
    this.size = size;
    rPB = new PawnRenderer(size, Color.BLACK);
    rRB = new RookRenderer(size, Color.BLACK);
    rNB = new KnightRenderer(size, Color.BLACK);
    rBB = new BishopRenderer(size, Color.BLACK);
    rQB = new QueenRenderer(size, Color.BLACK);
    rKB = new KingRenderer(size, Color.BLACK);
    rPW = new PawnRenderer(size, Color.WHITE);
    rRW = new RookRenderer(size, Color.WHITE);
    rNW = new KnightRenderer(size, Color.WHITE);
    rBW = new BishopRenderer(size, Color.WHITE);
    rQW = new QueenRenderer(size, Color.WHITE);
    rKW = new KingRenderer(size, Color.WHITE);
  }

  public void render(Piece p, GraphicsContext gc, double x, double y) {
    switch (p.type) {
      case P:
      (p.color == PieceColor.B ? rPB : rPW).render(gc, x, y);
      break;
      case R:
      (p.color == PieceColor.B ? rRB : rRW).render(gc, x, y);
      break;
      case N:
      (p.color == PieceColor.B ? rNB : rNW).render(gc, x, y);
      break;
      case B:
      (p.color == PieceColor.B ? rBB : rBW).render(gc, x, y);
      break;
      case Q:
      (p.color == PieceColor.B ? rQB : rQW).render(gc, x, y);
      break;
      case K:
      (p.color == PieceColor.B ? rKB : rKW).render(gc, x, y);
      break;
      default:
      gc.clearRect(x, y, size, size);
    }
  }
}
