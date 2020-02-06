import java.util.ArrayList;
import java.util.function.Predicate;


public class Board {

  public class Move {
    public final Square start;
    public final Square finish;
    public Move(Square s, Square f) {
      start = s;
      finish = f;
    }
    public boolean isLegal() {
      if (getPiece(start).color == PieceColor.E) return false;
      if (getPiece(start).color == getPiece(finish).color) return false;


      return false;
    }
  }


  private Piece[][] board = new Piece[8][8];
  private boolean[] castlable = { true, true, true, true };  // white kingside, white queenside, black kingside, black queenside
  private Square en_passant = null;  // the square on which the capturing pawn would land, behind the captured pawn
  private Square whiteKing = new Square(4, 0);
  private Square blackKing = new Square(4, 7);

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
  // does not include castling
  // assumes all possible pawn captures
  // does not check move obstruction
  private ArrayList<Square> getRange(Square s) {
    ArrayList<Square> res = new ArrayList<>();
    switch (getPiece(s).type) {
      case E:
      return res;

      case K:
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
        if (getPiece(s).type == PieceType.R) break;

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
        if (getPiece(s).color == PieceColor.W) {
          for (int i = -1; i < 2; i++) {
            Square sq = new Square(s.file + i, s.rank + 1);
            if (sq.isOnBoard()) res.add(sq);
          }
          if (s.rank == 1) res.add(new Square(s.file, 3));
        }
        else {
          for (int i = -1; i < 2; i++) {
            Square sq = new Square(s.file + i, s.rank - 1);
            if (sq.isOnBoard()) res.add(sq);
          }
          if (s.rank == 6) res.add(new Square(s.file, 4));
        }
        break;
    }
    return res;
  }

  // returns all squares attacked by the piece on a given square
  // refines accessible by checking for obstruction, attempts to capture same color, pawn captures
  // does not include castling
  private ArrayList<Square> getAccessible(Square s) {
    ArrayList<Square> res = getRange(s);
    res.removeIf((Square p) -> {
      if (getPiece(p).color == getPiece(s).color) return true;
      boolean q_bishop = false;
      switch (getPiece(s).type) {
        case P:
        if (Math.abs(p.rank - s.rank) > 1) {
          if (board[p.file][s.rank + (s.rank > p.rank ? -1 : 1)].type != PieceType.E) {
            return true;
          }
        }
        if (p == en_passant) return false;
        if (s.file == p.file) {
          return getPiece(p).type != PieceType.E;
        } else {
          return getPiece(p).type == PieceType.E;
        }

        case Q:
        q_bishop = p.rank != s.rank && p.file != s.file;

        case B:
        if (getPiece(s).type == PieceType.B || q_bishop) {
          int r_sign = p.rank < s.rank ? -1 : 1;
          int f_sign = p.file < s.file ? -1 : 1;
          boolean obstruction = false;
          for (int r = r_sign; Math.abs(r) <= Math.abs(p.rank - s.rank); r += r_sign) {
            int f = f_sign * Math.abs(r);
            obstruction = board[f][r].type != PieceType.E;
          }
          return obstruction;
        }

        case R:
        int r_inc = p.rank == s.rank ? 0 : (p.rank < s.rank ? -1 : 1);
        int f_inc = p.file == s.file ? 0 : (p.file < s.file ? -1 : 1);
        boolean obstruction = false;
        boolean last_obstruction = false;
        if (r_inc != 0) {
          for (int i = s.rank + r_inc; (p.rank - i) * r_inc > 0; i += r_inc) {
            last_obstruction = obstruction;
            if (board[p.file][i].type != PieceType.E) obstruction = true;
          }
        } else {
          for (int i = s.file + f_inc; (p.file - i) * f_inc > 0; i += f_inc) {
            last_obstruction = obstruction;
            if (board[i][p.rank].type != PieceType.E) obstruction = true;
          }
        }
        return obstruction;

        case N:
        case K:
        return false;
      }
      return true;
    });
    return res;
  }

  public Piece getPiece(Square s) {
    return board[s.file][s.rank];
  }
}
