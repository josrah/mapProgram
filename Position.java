import java.util.Objects;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int hashCode(){
        return Objects.hash(x,y);
    }

    public boolean equals(Object other){
        if(!(other instanceof Position)){
            return false;
        }
        Position o = (Position) other;
        return this.x == o.x && this.y == o.y;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
