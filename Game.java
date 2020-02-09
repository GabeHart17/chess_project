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
import javafx.scene.input.MouseEvent;


public class Game extends Application {

  private int squareSize = 100;
  private Board game = new Board();
  private GeneralRenderer renderer = new GeneralRenderer(squareSize);
  private Canvas board;
  private Canvas pieces;
  private Canvas highlight;
  private Color darkSquare = Color.rgb(100, 100, 100);
  private Color lightSqare = Color.rgb(200, 200, 200);
  private Color blackPiece = Color.rgb(255, 255, 255);
  private Color whitePiece = Color.rgb(0, 0, 0);
  private Color checkColor = Color.RED;  // color for checks and checkmates
  private Color checkHighlight = Color.rgb(255, 0, 0, 0.3);
  private Color lastHighlight = Color.rgb(238, 204, 0, 0.3);  // color for last move
  private Color currentColor = Color.GREEN;  // color for current move
  private Color currentHighlight = Color.rgb(142, 218, 64, 0.3);
  private Square[] lastMove = new Square[2];
  private Square selectedSquare = null;
  private ArrayList<Square> possibleMoves = null;


  @Override
  public void start(Stage primary) {
    BorderPane bp = new BorderPane();
    StackPane sp = new StackPane();
    bp.setCenter(sp);
    board = new Canvas(8 * squareSize, 8 * squareSize);
    pieces = new Canvas(8 * squareSize, 8 * squareSize);
    highlight = new Canvas(8 * squareSize, 8 * squareSize);
    highlight.setOnMouseClicked(e -> {
      handleClick(getClickSquare(e));
      renderPieces();
      updateHighlight();
    });
    sp.getChildren().addAll(board, pieces, highlight);
    drawBoard(board.getGraphicsContext2D());
    renderPieces();
    Scene scn = new Scene(bp, 8 * squareSize + 100, 8 * squareSize + 100);
    primary.setScene(scn);
    primary.show();
  }

  private Square getClickSquare(MouseEvent e) {
    int file = (int) (e.getX() / squareSize);
    int rank = 7 - (int) (e.getY() / squareSize);
    return new Square(file, rank);
  }

  private void handleClick(Square s) {
    if (selectedSquare == null) {
      selectedSquare = s;
      possibleMoves = game.getLegal(s);
    } else {
      if (possibleMoves.contains(s)) {
        game.makeMove(selectedSquare, s);
      }
      selectedSquare = null;
    }
  }

  private void drawBoard(GraphicsContext gc) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        Color c = (i + j) % 2 == 0 ? lightSqare : darkSquare;
        gc.setFill(c);
        gc.fillRect(squareSize * i, squareSize * j, squareSize, squareSize);
      }
    }
  }

  private void renderPieces() {
    for (int f = 0; f < 8; f++) {
      for (int r = 0; r < 8; r++) {
        Piece p = game.getPiece(new Square(f, r));
        renderer.render(p, pieces.getGraphicsContext2D(), f * squareSize, (7 - r) * squareSize);
      }
    }
  }

  private void clearHighlight() {
    highlight.getGraphicsContext2D().clearRect(0, 0, highlight.getWidth(), highlight.getHeight());
  }

  private void highlightSquare(Color c, Square s) {
    GraphicsContext gc = highlight.getGraphicsContext2D();
    gc.setFill(c);
    int x = squareSize * s.file;
    int y = squareSize * (7 - s.rank);
    gc.fillRect(x, y, squareSize, squareSize);
  }

  private void boxSquare(Color c, Square s) {
    GraphicsContext gc = highlight.getGraphicsContext2D();
    gc.setStroke(c);
    gc.setLineWidth(squareSize / 16);
    int x = squareSize * s.file;
    int y = squareSize * (7 - s.rank);
    gc.strokeRect(x, y, squareSize, squareSize);
  }

  private void updateHighlight() {
    clearHighlight();
    if (lastMove[0] != null) highlightSquare(lastHighlight, lastMove[0]);
    if (lastMove[1] != null) highlightSquare(lastHighlight, lastMove[1]);
    if (selectedSquare != null) {
      boxSquare(currentColor, selectedSquare);
      for (Square s : possibleMoves) {
        highlightSquare(currentHighlight, s);
      }
    }
  }
}
