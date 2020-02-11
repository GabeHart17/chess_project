public class Piece {
  public final PieceType type;
  public final PieceColor color;
  public Piece (PieceType t, PieceColor c) {
    type = t;
    color = c;
  }
  public Piece() {
    this(PieceType.E, PieceColor.E);
  }
  public boolean equals(Object other) {
    if (other instanceof Piece) {
      return type == ((Piece) other).type && color == ((Piece) other).color;
    }
    return false;
  }
  public String toString() {
    return String.format("piece(%s, %s)", type, color);
  }
}
