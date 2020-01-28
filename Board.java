import java.util.ArrayList;


public class Board {
  public class Square {
    public final int rank; // rank
    public final int file; // file
    public Square(int r, int f) {
      rank = r;
      file = f;
    }
  }
  public class Move {
    public final Square start;
    public final Square finish;
    public Move (Square s, Square f) {
      start = s;
      finish = f;
    }
  }

  public enum Piece {E, KW, QW, RW, BW, NW, PW, KB, QB, RB, BB, NB, PB};  // E is for empty squares

  private Piece[][] board = new Piece[8][8];
  private boolean castlable = { true, true, true, true };
  private Square en_passant = null;  // the square on which the capturing pawn would land, behind the captured pawn

  public Board() {
    for (int i = 0; i < 8; i++) {
      board[i][1] = PW;
      board[i][6] = PB;
    }
    board[4][0] = KW;
    board[4][7] = KB;
    board[3][0] = QW;
    board[3][7] = QB;
    for (int i = 0; i < 1; i++) {
      board[7 * i][0] = RW;
      board[7 * i][7] = RB;
      int s =  1 - (2 * i);
      board[7 * i + s][0] = NW;
      board[7 * i + s][7] = NB;
      board[7 * i + 2 * s][0] = BW;
      board[7 * i + 2 * s][7] = BB;
    }
  }

  private ArrayList
}
