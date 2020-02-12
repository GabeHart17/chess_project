import java.util.ArrayList;
import java.util.function.Predicate;


public class Board {

  private class Move {
    public final Square start;
    public final Square finish;
    private final Piece startPiece;
    private final Piece finishPiece;
    private final boolean isCastling;
    public final Square ep;
    private boolean made = false;
    public Move(Square s, Square f) {
      start = s;
      finish = f;
      ep = en_passant;
      startPiece = getPiece(s);
      finishPiece = getPiece(f);
      if (startPiece.type == PieceType.K) {
        if (!(getAccessible(start).contains(finish))) {
          //isCastling = start.rank == (startPiece.color == PieceColor.W ? 0 : 7);
          isCastling = start.equals(new Square(4, startPiece.color == PieceColor.W ? 0 : 7));
        } else {
          isCastling = false;
        }
      } else {
        isCastling = false;
      }
    }
    private void make() {
      board[finish.file][finish.rank] = getPiece(start);
      board[start.file][start.rank] = new Piece();
      if (isCastling) {
        int side = finish.file - start.file > 0 ? 1 : -1;  // 1 for kingside, -1 for queenside
        board[finish.file - side][start.rank] = board[side > 0 ? 7 : 0][start.rank];
        board[side > 0 ? 7 : 0][start.rank] = new Piece();
      }
      if (startPiece.type == PieceType.K) {
        int index = startPiece.color == PieceColor.W ? 0 : 1;
        kings[index] = finish;
      }
      if (finish.equals(en_passant)) {
        board[finish.file][startPiece.color == PieceColor.W ? 4:3] = new Piece();
      }
      if ((startPiece.type == PieceType.P) && (Math.abs(start.rank - finish.rank) > 1)) {
        en_passant = new Square(start.file, start.rank + (startPiece.color == PieceColor.W ? 1 : -1));
      } else {
        en_passant = null;
      }
      made = true;
    }
    private void unMake() {
      board[finish.file][finish.rank] = finishPiece;
      board[start.file][start.rank] = startPiece;
      if (isCastling) {
        int side = finish.file - start.file > 0 ? 1 : -1;  // 1 for kingside, -1 for queenside
        board[side > 0 ? 7 : 0][start.rank] = board[side > 0 ? 7 : 0][start.rank];
        board[finish.file - side][start.rank] = new Piece();
      }
      if (startPiece.type == PieceType.K) {
        int index = startPiece.color == PieceColor.W ? 0 : 1;
        kings[index] = start;
      }
      en_passant = ep;
      if (finish.equals(ep)) {
        board[finish.file]
             [startPiece.color == PieceColor.W ? 4 : 3] = new Piece(PieceType.P,
                                                                    startPiece.color == PieceColor.W ? PieceColor.B : PieceColor.W);
      }
      made = false;
    }
    public boolean isLegal() {
      if (made) return false;
      if (getPiece(start).color == PieceColor.E) return false;
      if (getPiece(start).color == getPiece(finish).color) return false;
      if (isCastling) {
        for (int i = start.file; i != 0 && i != 7; i += Math.copySign(1, finish.file - start.file)) {
          Square s = new Square(i, start.rank);
          if (Math.abs(start.file - s.file) < 4) {
            if (!getAttackers(s, startPiece.color == PieceColor.W ? PieceColor.B : PieceColor.W).isEmpty()) {
              return false;
            }
          }
          if (i != start.file) {
            if (getPiece(s).type != PieceType.E) return false;
          }
        }
        int index = 0;
        index += startPiece.color == PieceColor.W ? 0 : 2;
        index += start.file - finish.file < 0 ? 0 : 1;
        return castlable[index];
      }
      if (!getAccessible(start).contains(finish)) return false;
      make();
      boolean res = true;
      ArrayList<Square> checks = getAttackers(kings[startPiece.color == PieceColor.W ? 0 : 1],
                                              startPiece.color == PieceColor.W ? PieceColor.B : PieceColor.W);
      if (!checks.isEmpty()) res = false;
      unMake();
      return res;
    }
    public boolean attempt() {
      if (made) return false;
      boolean res = isLegal();
      if (res) {
        make();
      }
      return res;
    }
    public boolean isMade() {
      return made;
    }
  }


  private Piece[][] board = new Piece[8][8];  // indexed board[file][rank]
  private boolean[] castlable = { true, true, true, true };  // white kingside, white queenside, black kingside, black queenside
  private Square en_passant = null;  // the square on which the capturing pawn would land, behind the captured pawn
  private Square whiteKing = new Square(4, 0);
  private Square blackKing = new Square(4, 7);
  private Square[] kings = new Square[2];  // 0 = white king, 1 = black king

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
    kings[0] = new Square(4, 0);
    kings[1] = new Square(4, 7);
  }

  public Piece getPiece(Square s) {
    return board[s.file][s.rank];
  }

  public boolean makeMove(Square start, Square finish) {
    Move m = new Move(start, finish);
    int kIndex = getPiece(start).color == PieceColor.W ? 0 : 2;
    int rIndex = start.file > 0 ? 0 : 1;
    PieceType startType = getPiece(start).type;
    boolean res = m.attempt();
    if (res) {
      if (startType == PieceType.K && start.file == 4) {
        castlable[kIndex] = false;
        castlable[kIndex + 1] = false;
      } else if (startType == PieceType.R && (start.file == 0 || start.file == 7)) {
        castlable[kIndex + rIndex] = false;
      }
    }
    return res;
  }

  public Square getWhiteKing() { return kings[0]; }
  public Square getBlackKing() { return kings[1]; }

  // return all squares that could potentially be accessible by this piece
  // does not include castling
  // assumes all possible pawn captures
  // does not check move obstruction
  public ArrayList<Square> getRange(Square s) {
    ArrayList<Square> res = new ArrayList<>();
    switch (getPiece(s).type) {
      case E:
      return res;

      case K:
        int[] r = {-1, 0, 1};
        int[] f = {-1, 0, 1};
        for (int i : f) {
          for (int j : r) {
            Square sq = new Square(s.file + i, s.rank + j);
            if (s != sq && sq.isOnBoard()) res.add(sq);
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
  public ArrayList<Square> getAccessible(Square s) {
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
        if (p.equals(en_passant)) return false;
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
          boolean last_obstruction = false;
          for (int r = r_sign; Math.abs(r) <= Math.abs(p.rank - s.rank); r += r_sign) {
            int f = f_sign * Math.abs(r);
            // System.out.printf("%s, %s\n", f, r);
            Piece target = board[s.file + f][s.rank + r];
            last_obstruction = obstruction;
            obstruction = obstruction || target.type != PieceType.E;
          }
          return last_obstruction;
        }

        case R:
        int f_inc = p.file == s.file ? 0 : (p.file < s.file ? -1 : 1);
        int r_inc = p.rank == s.rank ? 0 : (p.rank < s.rank ? -1 : 1);
        int file = s.file;
        int rank = s.rank;
        Square sq = new Square(file, rank);
        while (!sq.equals(p)) {
          if (!sq.equals(s)) {
            if (getPiece(sq).type != PieceType.E) return true;
          }
          sq = new Square(file += f_inc, rank += r_inc);
        }
        return false;

        case N:
        case K:
        return false;
      }
      return true;
    });
    return res;
  }

  // return all attackers of a given color of a given square
  // used for checking for checks
  // public to allow other classes to check for attackers
  public ArrayList<Square> getAttackers(Square s, PieceColor c) {
    ArrayList<Square> res = new ArrayList<>();
    for (int f = 0; f < 8; f++) {
      for (int r = 0; r < 8; r++) {
        Square sq = new Square(f, r);
        if (getAccessible(sq).contains(s) && getPiece(sq).color == c) {
          res.add(sq);
        }
      }
    }
    return res;
  }

  public ArrayList<Square> getLegal(Square s) {
    ArrayList<Square> res = getAccessible(s);
    if (getPiece(s).type == PieceType.K) {
      res.add(new Square(s.file + 2, s.rank));
      res.add(new Square(s.file - 2, s.rank));
    }
    res.removeIf((Square p) -> {
      if (!p.isOnBoard()) return true;
      Move m = new Move(s, p);
      return !m.isLegal();
    });
    return res;
  }
}
