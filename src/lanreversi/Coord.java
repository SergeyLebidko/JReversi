package lanreversi;

public class Coord {

    public int x;
    public int y;

    public Coord(int y, int x) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null)return false;
        if(!(obj instanceof Coord))return false;
        Coord c=(Coord)obj;
        return ((x==c.x) & (y==c.y));
    }

}
