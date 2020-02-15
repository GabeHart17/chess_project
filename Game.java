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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;



public class Game extends Application {

  private enum BoardOrientation {UP, DOWN, RIGHT, LEFT};  // side of board on which white pieces are

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
  private Color checkmateColor = Color.RED;  // color for checkmates
  private Color checkHighlight = Color.rgb(255, 0, 0, 0.3);
  private Color lastHighlight = Color.rgb(255, 200, 0, 0.3);  // color for last move
  private Color currentColor = Color.GREEN;  // color for current move
  private Color currentHighlight = Color.rgb(142, 218, 64, 0.3);
  private Square[] lastMove = new Square[2];
  private Square selectedSquare = null;
  private ArrayList<Square> possibleMoves = null;
  private Text status = new Text("White to move");
  private boolean move = true;  // true for white, false for black
  private boolean hasEnded = false;
  private BoardOrientation orientation = BoardOrientation.DOWN;
  private boolean flipOrientation = false;  // whether or not to flip the board after each turn


  @Override
  public void start(Stage primary) {
    BorderPane bp = new BorderPane();
    StackPane sp = new StackPane();
    bp.setTop(makeMenus());
    bp.setBottom(status);
    bp.setCenter(sp);
    board = new Canvas(8 * squareSize, 8 * squareSize);
    pieces = new Canvas(8 * squareSize, 8 * squareSize);
    highlight = new Canvas(8 * squareSize, 8 * squareSize);
    highlight.setOnMouseClicked(e -> {
      if (!hasEnded) {
        handleClick(getClickSquare(e));
        renderAll();
        checkEnds();
      }
    });
    sp.getChildren().addAll(board, pieces, highlight);
    drawBoard();
    renderPieces();
    Scene scn = new Scene(bp, 8 * squareSize + 100, 8 * squareSize + 100);
    primary.setScene(scn);
    primary.show();
  }

  private void renderAll() {
    drawBoard();
    renderPieces();
    updateHighlight();
  }

  private MenuBar makeMenus() {
    MenuBar mb = new MenuBar();
    mb.getMenus().addAll(makeOrientationMenu(), makeFlipMenu());
    return mb;
  }

  private Menu makeOrientationMenu() {
    Menu m = new Menu("Set Orientation");
    MenuItem upItem = new MenuItem("Up");
    upItem.setOnAction(e -> {
      orientation = BoardOrientation.UP;
      renderAll();
    });
    MenuItem downItem = new MenuItem("Down");
    downItem.setOnAction(e -> {
      orientation = BoardOrientation.DOWN;
      renderAll();
    });
    MenuItem leftItem = new MenuItem("Left");
    leftItem.setOnAction(e -> {
      orientation = BoardOrientation.LEFT;
      renderAll();
    });
    MenuItem rightItem = new MenuItem("Right");
    rightItem.setOnAction(e -> {
      orientation = BoardOrientation.RIGHT;
      renderAll();
    });
    m.getItems().addAll(upItem, downItem, leftItem, rightItem);
    return m;
  }

  private Menu makeFlipMenu() {
    Menu m = new Menu("Flip Orientation");
    ToggleGroup tg = new ToggleGroup();
    RadioMenuItem onItem = new RadioMenuItem("On");
    onItem.setToggleGroup(tg);
    onItem.setOnAction(e -> {
      flipOrientation = true;
    });
    RadioMenuItem offItem = new RadioMenuItem("Off");
    offItem.setToggleGroup(tg);
    offItem.setOnAction(e -> {
      flipOrientation = false;
    });
    m.getItems().addAll(onItem, offItem);
    return m;
  }

  private BoardOrientation inverseOrientation(BoardOrientation b) {
    BoardOrientation res = BoardOrientation.UP;
    switch (b) {
      case UP:
      res = BoardOrientation.DOWN;
      break;
      case DOWN:
      res = BoardOrientation.UP;
      break;
      case RIGHT:
      res = BoardOrientation.LEFT;
      break;
      case LEFT:
      res = BoardOrientation.RIGHT;
      break;
    }
    return res;
  }

  private Square rotateCW(Square s) {
    return new Square(s.rank, 7 - s.file);
  }

  private Square rotateCCW(Square s) {
    return new Square(7 - s.rank, s.file);
  }

  private Square renderSquare(Square s) {  // applies board orientation to a square
    int iter = 0;
    switch (orientation) {
      case RIGHT:
      iter += 1;
      case UP:
      iter += 1;
      case LEFT:
      iter += 1;
      case DOWN:
      break;
    }
    Square res = s;
    for (int i = 0; i < iter; i++) {
      res = rotateCW(res);
    }
    return new Square(res.file, 7 - res.rank);
  }

  private Square boardSquare(Square s) {  // inverse of renderSquare()
    int iter = 0;
    switch (orientation) {
      case RIGHT:
      iter += 1;
      case UP:
      iter += 1;
      case LEFT:
      iter += 1;
      case DOWN:
      break;
    }
    Square res = new Square(s.file, 7 - s.rank);
    for (int i = 0; i < iter; i++) {
      res = rotateCCW(res);
    }
    return res;
  }

  private Square getClickSquare(MouseEvent e) {
    int file = (int) (e.getX() / squareSize);
    int rank = (int) (e.getY() / squareSize);
    return boardSquare(new Square(file, rank));
  }

  private void handleClick(Square s) {
    if (selectedSquare == null) {
      if (game.getPiece(s).color == (move ? PieceColor.W : PieceColor.B)) {
        selectedSquare = s;
        possibleMoves = game.getLegal(s);
      }
    } else {
      if (possibleMoves.contains(s)) {
        lastMove[0] = selectedSquare;
        lastMove[1] = s;
        boolean success = game.makeMove(selectedSquare, s);
        if (success) {
          status.setText(String.format("%s to move", move ? "Black" : "White"));
          move = !move;
          if (flipOrientation) {
            orientation = inverseOrientation(orientation);
          }
          if (game.getPiece(s).type == PieceType.P && (s.rank == 0 || s.rank == 7)) {
            Stage promotionStage = new Stage();
            BorderPane bp = new BorderPane();
            MenuBar mb = new MenuBar();
            bp.setTop(mb);
            Menu mu = new Menu("Promote to...");
            mb.getMenus().add(mu);
            MenuItem qItem = new MenuItem("Queen");
            qItem.setOnAction(e -> {
              game.promotePawn(s, PieceType.Q);
              renderPieces();
              promotionStage.close();
            });
            MenuItem rItem = new MenuItem("Rook");
            rItem.setOnAction(e -> {
              game.promotePawn(s, PieceType.R);
              renderPieces();
              promotionStage.close();
            });
            MenuItem bItem = new MenuItem("Bishop");
            bItem.setOnAction(e -> {
              game.promotePawn(s, PieceType.B);
              renderPieces();
              promotionStage.close();
            });
            MenuItem nItem = new MenuItem("Knight");
            nItem.setOnAction(e -> {
              game.promotePawn(s, PieceType.N);
              renderPieces();
              promotionStage.close();
            });
            mu.getItems().addAll(qItem, rItem, bItem, nItem);
            promotionStage.setOnCloseRequest(e -> {
              game.promotePawn(s, PieceType.Q);
              renderPieces();
            });
            Scene ps = new Scene(bp, 200, 100);
            promotionStage.setScene(ps);
            promotionStage.show();
          }
        }
      }
      selectedSquare = null;
    }
  }

  private void drawBoard() {
    GraphicsContext gc = board.getGraphicsContext2D();
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        Color c = (i + j) % 2 == 0 ? lightSqare : darkSquare;
        gc.setFill(c);
        Square s = renderSquare(new Square(i, j));
        gc.fillRect(squareSize * s.file, squareSize * s.rank, squareSize, squareSize);
      }
    }
  }

  private void renderPieces() {
    for (int f = 0; f < 8; f++) {
      for (int r = 0; r < 8; r++) {
        Piece p = game.getPiece(new Square(f, r));
        Square s = renderSquare(new Square(f, r));
        renderer.render(p, pieces.getGraphicsContext2D(), s.file * squareSize, s.rank * squareSize);
      }
    }
  }

  private void clearHighlight() {
    highlight.getGraphicsContext2D().clearRect(0, 0, highlight.getWidth(), highlight.getHeight());
  }

  private void highlightSquare(Color c, Square s) {
    GraphicsContext gc = highlight.getGraphicsContext2D();
    gc.setFill(c);
    Square hs = renderSquare(s);
    int x = squareSize * hs.file;
    int y = squareSize * hs.rank;
    gc.fillRect(x, y, squareSize, squareSize);
  }

  private void boxSquare(Color c, Square s) {
    GraphicsContext gc = highlight.getGraphicsContext2D();
    gc.setStroke(c);
    gc.setLineWidth(squareSize / 16);
    Square hs = renderSquare(s);
    int x = squareSize * hs.file;
    int y = squareSize * hs.rank;
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
    Square wk = game.getWhiteKing();
    if (!game.getAttackers(wk, PieceColor.B).isEmpty()) {
      highlightSquare(checkHighlight, wk);
    }
    Square bk = game.getBlackKing();
    if (!game.getAttackers(bk, PieceColor.W).isEmpty()) {
      highlightSquare(checkHighlight, bk);
    }
  }

  private void checkEnds() {
    PieceColor c = move ? PieceColor.W : PieceColor.B;
    boolean cm = game.isCheckmated(c);
    boolean sm = game.isStalemated(c);
    if (sm || cm) {
      hasEnded = true;
      clearHighlight();
      if (cm) {
        Square sq = move ? game.getWhiteKing() : game.getBlackKing();
        highlightSquare(checkHighlight, sq);
        boxSquare(checkmateColor, sq);
        status.setText(String.format("Checkmate. %s wins.", move ? "Black" : "White"));
      } else {
        status.setText("Stalemate. Game is drawn.");
      }
    }
  }
}
