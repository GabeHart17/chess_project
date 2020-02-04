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
    public Move(Square s, Square f) {
      start = s;
      finish = f;
    }
    public boolean isLegal() {
      if (start.contents().color == PieceColor.E) return false;
      if (start.contents().color == finish.contents().color) return false;

      if (start.contents().type == PieceType.P) {
        if
      }
    }
  }

  private class Piece {
    public final PieceType type;
    public final PieceColor color;
    public Piece (PieceType t, PieceColor c) {
      type = t;
      color = c;
    }
  }

  public enum PieceType {E, K, Q, R, B, N, P};  // E is for empty squares
  public enum PieceColor {B, W, E};


  private Piece[][] board = new Piece[8][8];
  private boolean[] castlable = { true, true, true, true };  // white kingside, white queenside, black kingside, black queenside
  private Square en_passant = null;  // the square on which the capturing pawn would land, behind the captured pawn

  public Board() {
    for (int i = 0; i < 8; i++) {
      board[i][1] = new Piece(PieceType.P, PieceColor.W);
      board[i][6] = new Piece(PieceType.P, PieceColor.B);
    }
    board[4][0] = new Piece(PieceType.K, PieceColor.W);
    board[4][7] = new Piece(PieceType.K, PieceColor.B);
    board[3][0] = new Piece(PieceType.Q, PieceColor.W);
    board[3][7] = new Piece(PieceType.Q, PieceColor.B);
    for (int i = 0; i < 2; i++) {
      board[7 * i][0] = new Piece(PieceType.R, PieceColor.W);
      board[7 * i][7] = new Piece(PieceType.R, PieceColor.B);
      int s =  1 - (2 * i);
      board[7 * i + s][0] = new Piece(PieceType.N, PieceColor.W);
      board[7 * i + s][7] = new Piece(PieceType.N, PieceColor.B);
      board[7 * i + 2 * s][0] = new Piece(PieceType.B, PieceColor.W);
      board[7 * i + 2 * s][7] = new Piece(PieceType.B, PieceColor.B);
    }
    for (int i = 0; i < 8; i++) {
      for (int j = 2; j < 6; j++) {
        board[i][j] = new Piece(PieceType.E, PieceColor.E);
      }
    }
  }


  // return all squares that could potentially be accessible by this piece
  // assumes all possible pawn captures
  // checks castling rights but not obstruction
  // does not check move obstruction
  private ArrayList<Square> getRange(Square s) {
    ArrayList<Square> res = new ArrayList<>();
    switch (s.contents().type) {
      case E:
      return res;

      case K:
        if (s.contents().color == PieceColor.W && s == new Square(4, 0)) {
          if (castlable[0]) res.add(new Square(6, 0));
          if (castlable[1]) res.add(new Square(3, 0));
        }
        if (s.contents().color == PieceColor.B && s == new Square(4, 7)) {
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

      case Q:

      case R:
        for (int i = 0; i < 8; i++) {
          if (i != s.file) {
            res.add(new Square(i, s.rank));
          }
          if (i != s.rank) {
            res.add(new Square(s.file, i));
          }
        }
        if (s.contents() == PieceType.R) break;

      case B:
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

      case N:
        for (int i = -2; i < 3; i++) {
          for (int j = -2; j < 3; j++) {
            if (Math.abs(j) != Math.abs(i) && j != 0 && i != 0) {
              Square sq = new Square(s.file + i, s.rank + j);
              if (sq.isOnBoard()) res.add(sq);
            }
          }
        }
        break;

      case P:
        if (s.contents().color == PieceColor.W) {
          for (int i = -1; i < 2; i++) {
            Square sq = new Square(s.file + i, s.rank + 1);
            if (sq.isOnBoard()) res.add(sq);
          }
          if (s.rank = 1) res.add(new Square(s.file, 3));
        }
        else {
          for (int i = -1; i < 2; i++) {
            Square sq = new Square(s.file + i, s.rank - 1);
            if (sq.isOnBoard()) res.add(sq);
          }
          if (s.rank = 6) res.add(new Square(s.file, 4));
        }
        break;
    }
    return res;
  }

  // returns all squares attacked by the piece on a given square
  // refines accessible by checking for obstruction, attempts to capture same color
  private ArrayList<Square> getAccessible(Square s) {
    ArrayList<Square> res = getRange(s);
    res.removeIf(p -> {
      if (p.contents().color == s.contents().color) return true;
      switch (s.contents().type) {
        case P:
        if (Math.abs(p.rank - s.rank) > 1) {
          if (board[p.file][s.rank + (s.rank > p.rank ? -1 : 1)].type != PieceType.E) {
            return true;
          }
        }
        if (p == en_passant) return false;
        return s.file == p.file ? p.contents().type != PieceType.E : p.contents().type == PieceType.E;

        case B:
        int r_sign = p.rank < s.rank ? -1 : 1;
        int f_sign = p.file < s.file ? -1 : 1;
        boolean obstruction = false;
        for (int r = rsign; Math.abs(r) <= Math.abs(p.rank - s.rank); r += r_sign) {
          boolean o = obstruction;
          int f = f_sign * Math.abs(r);
          obstruction = board[f][r].type != PieceType.E;
          return o;
        }

        case R:
        int r_inc = p.rank == s.rank ? 0 : (p.rank < s.rank ? -1 : 1);
        int f_inc = p.file == s.file ? 0 : (p.file < s.file ? -1 : 1);
        if (r_inc != 0)
      }
    });
  }
}
