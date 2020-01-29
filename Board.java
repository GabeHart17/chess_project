import java.util.ArrayList;


public class Board {

  public class Square {
    public final int file; // file
    public final int rank; // rank
    public Square(int f, int r) {
      rank = r;
      file = f;
    }
    public boolean equals(Object other) {
      if (other instanceof Square) {
        return rank == ((Square) other).rank && file == ((Square) other).file;
      }
      return false;
    }
    public boolean isOnBoard() {
      return 0 <= rank && rank <= 7 && 0 <= file && file <= 7;
    }
    public Piece contents() {
      return board[file][rank];
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
  private boolean[] castlable = { true, true, true, true };  // white kingside, white queenside, black kingside, black queenside
  private Square en_passant = null;  // the square on which the capturing pawn would land, behind the captured pawn

  public Board() {
    for (int i = 0; i < 8; i++) {
      board[i][1] = Piece.PW;
      board[i][6] = Piece.PB;
    }
    board[4][0] = Piece.KW;
    board[4][7] = Piece.KB;
    board[3][0] = Piece.QW;
    board[3][7] = Piece.QB;
    for (int i = 0; i < 1; i++) {
      board[7 * i][0] = Piece.RW;
      board[7 * i][7] = Piece.RB;
      int s =  1 - (2 * i);
      board[7 * i + s][0] = Piece.NW;
      board[7 * i + s][7] = Piece.NB;
      board[7 * i + 2 * s][0] = Piece.BW;
      board[7 * i + 2 * s][7] = Piece.BB;
    }
  }


  // return all squares that would be accessible by piece on square on empty board
  private ArrayList<Square> getAccessible(Square s) {
    ArrayList<Square> res = new ArrayList<>();
    switch (s.contents()) {
      case E:
      return res;

      case KW:
        if (s == new Square(4, 0)) {
          if (castlable[0]) res.add(new Square(6, 0));
          if (castlable[1]) res.add(new Square(3, 0));
        }
      case KB:
        if (s.contents() == Piece.KB && s == new Square(4, 7)) {
          if (castlable[2]) res.add(new Square(6, 7));
          if (castlable[3]) res.add(new Square(3, 7));
        }
        for (int i = -1; i < 2; i++) {
          for (int j = -1; i < 2; i++) {
            Square sq = new Square(s.file + i, s.rank + j);
            if (s != sq && sq.isOnBoard()) {
              res.add(sq);
            }
          }
        }
        break;

      case QW:
      case QB:

      case RW:
      case RB:
        for (int i = 0; i < 8; i++) {
          if (i != s.file) {
            res.add(new Square(i, s.rank));
          }
          if (i != s.rank) {
            res.add(new Square(s.file, i));
          }
        }
        if (s.contents() == Piece.RW || s.contents() == Piece.RB) break;

      case BW:
      case BB:
        for (int i = -7; i < 8; i++) {
          if (i != 0) {
            Square s0 = new Square(s.file + i, s.rank + i);
            Square s1 = new Square(s.file + i, s.rank - i);
            Square s2 = new Square(s.file - i, s.rank + i);
            Square s3 = new Square(s.file - i, s.rank - i);
            if (s0.isOnBoard()) res.add(s0);
            if (s1.isOnBoard()) res.add(s1);
            if (s2.isOnBoard()) res.add(s2);
            if (s3.isOnBoard()) res.add(s3);
          }
        }
      break;

      case NW:
      case NB:
        for (int i = -2; i < 3; i++) {
          for (int j = -2; j < 3; j++) {
            if (Math.abs(j) != Math.abs(i) && j != 0 && i != 0) {
              Square sq = new Square(s.file + i, s.rank + j);
              if (sq.isOnBoard()) res.add(sq);
            }
          }
        }
        break;

      case PW:
        for (int i = -1; i < 2; i++) {
          Square sq = new Square(s.file + i, s.rank + 1);
          if (sq.isOnBoard()) res.add(sq);
        }
        break;
      case PB:
        for (int i = -1; i < 2; i++) {
          Square sq = new Square(s.file + i, s.rank - 1);
          if (sq.isOnBoard()) res.add(sq);
        }
        break;
    }
    return res;
  }
}
