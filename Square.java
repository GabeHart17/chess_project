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
}
