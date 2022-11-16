package byow.Core;

import java.io.Serializable;

public class Pair implements Serializable {
    private int x;
    private int y;
    public Pair(int i, int j) {
        x = i;
        y = j;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
