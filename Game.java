import java.util.HashMap;
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
import javafx.scene.image.Image;


public class Game extends Application {

  Board game = new Board();
  private HashMap<Piece, Image> piece_images = new HashMap<>();
  private Canvas board;
  private Canvas pieces;
  private Canvas highlight;
  private Color dark_square = Color.rgb(100, 100, 100);
  private Color light_square = Color.rgb(200, 200, 200);
  private Color black_piece = Color.rgb(255, 255, 255);
  private Color white_piece = Color.rgb(0, 0, 0);


  @Override
  public void start(Stage primary) {
    loadPieces("./piece_images");
    BorderPane bp = new BorderPane();
    StackPane sp = new StackPane();
    bp.setCenter(sp);
    board = new Canvas(800, 800);
    pieces = new Canvas(800, 800);
    highlight = new Canvas(800, 800);
    sp.getChildren().addAll(board, pieces, highlight);
    drawBoard(board.getGraphicsContext2D());
    Scene scn = new Scene(bp, 900, 900);
    renderPieces(pieces.getGraphicsContext2D());
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

  private String getPieceFileName(Piece p) {
    if (p.type == PieceType.E) return "";
    String c = p.color == PieceColor.W ? "l" : "d";
    String t = "";
    switch (p.type) {
      case K:
        t = "k";
        break;

      case Q:
        t = "q";
        break;

      case B:
        t = "b";
        break;

      case N:
        t = "n";
        break;

      case R:
        t = "r";
        break;

      case P:
        t = "p";
        break;
    }
    return String.format("Chess_%s%st60.png", t, c);
  }

  private void loadPieces(String dir) {
    for (PieceType t : PieceType.values()) {
      if (t != PieceType.E) {
        Piece b = new Piece(t, PieceColor.B);
        Piece w = new Piece(t, PieceColor.W);
        Image bi = new Image(String.format("%s/%s", dir, getPieceFileName(b)), true);
        Image wi = new Image(String.format("%s/%s", dir, getPieceFileName(w)), true);
        System.out.println(String.format("%s\t%s", bi.getWidth(), bi.getHeight()));
        // Image bi = null;
        // Image wi = null;
        // try {
        //   System.out.println(getPieceFileName(b));
        //   System.out.println(getPieceFileName(w));
        //   FileInputStream bf = new FileInputStream(new File(String.format("%s/%s", dir, getPieceFileName(b))));
        //   FileInputStream wf = new FileInputStream(new File(String.format("%s/%s", dir, getPieceFileName(w))));
        //   bi = new Image(bf);
        //   wi = new Image(wf);
        // } catch (IOException e) {
        //   e.printStackTrace();
        // }
        System.out.println(bi != null);
        System.out.println(wi != null);
        System.out.println();
        piece_images.put(b, bi);
        piece_images.put(w, wi);
      }
    }
  }

  private void renderPieces(GraphicsContext gc) {
    gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    for (int f = 0; f < 8; f++) {
      for (int r = 0; r < 8; r++) {
        Piece p = game.getPiece(new Square(f, r));
        if (p.type != PieceType.E) gc.drawImage(piece_images.get(p), 100 * f, 100 * (6 - r));//, 100, 100);
      }
    }
  }
}
