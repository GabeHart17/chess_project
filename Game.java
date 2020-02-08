import piece_renderers.*;

import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Game extends Application {

  Board game = new Board();
  GeneralRenderer renderer = new GeneralRenderer(100);
  private Canvas board;
  private Canvas pieces;
  private Canvas highlight;
  private Color dark_square = Color.rgb(100, 100, 100);
  private Color light_square = Color.rgb(200, 200, 200);
  private Color black_piece = Color.rgb(255, 255, 255);
  private Color white_piece = Color.rgb(0, 0, 0);


  @Override
  public void start(Stage primary) {
    BorderPane bp = new BorderPane();
    StackPane sp = new StackPane();
    bp.setCenter(sp);
    board = new Canvas(800, 800);
    pieces = new Canvas(800, 800);
    highlight = new Canvas(800, 800);
    sp.getChildren().addAll(board, pieces, highlight); //.addAll(board, gp, highlight);
    drawBoard(board.getGraphicsContext2D());
    renderPieces();
    Scene scn = new Scene(bp, 900, 900);
    primary.setScene(scn);
    primary.show();
  }

  private void drawBoard(GraphicsContext gc) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        Color c = (i + j) % 2 == 0 ? light_square : dark_square;
        gc.setFill(c);
        gc.fillRect(100 * i, 100 * j, 100, 100);
      }
    }
  }


  private void renderPieces() {
    for (int f = 0; f < 8; f++) {
      for (int r = 0; r < 8; r++) {
        Piece p = game.getPiece(new Square(f, r));
        renderer.render(p, pieces.getGraphicsContext2D(), f * 100, (7 - r) * 100);
      }
    }
  }
}
