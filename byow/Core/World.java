package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.*;

public class World implements Serializable {
    private TETile[][] grid;
    private boolean lights;
    private int W;
    private Pair Opponent;
    private int H;
    private Pair Player;
    private HashSet<Pair> Empties;
    private ArrayList<Pair> EmptiesOrdered;
    private int emptyCount;
    private Random R;
    public World(int width, int height, Random seed) {
        lights = false;
        grid = new TETile[height][width];
        W = width-4;
        H = height-4;
        R = seed;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = Tileset.NOTHING;
            }
        }
        fillRandomly();
        addWalls();
    }
    private void addWalls() {
        HashSet<TETile> temp = new HashSet<>();
        temp.add(Tileset.WALL);
        temp.add(Tileset.NOTHING);
        for (int i = 1; i < H+3; i++) {
            for (int j = 1; j < W + 3; j++) {
                if (grid[i][j] == Tileset.NOTHING) {
                    if (!(temp.contains(grid[i - 1][j - 1])
                            && temp.contains(grid[i - 1][j])
                            && temp.contains(grid[i - 1][j + 1])
                            && temp.contains(grid[i + 1][j - 1])
                            && temp.contains(grid[i + 1][j + 1])
                            && temp.contains(grid[i + 1][j])
                            && temp.contains(grid[i][j - 1])
                            && temp.contains(grid[i][j + 1]))) {
                        grid[i][j] = Tileset.WALL;
                    }
                }
            }
        }
    }

    private void fillRandomly() {

        Empties  = new HashSet<>();
        EmptiesOrdered = new ArrayList<>();
        int num_rooms = RandomUtils.uniform(R, 10, 20);
        ArrayList<Pair> temp = new ArrayList<>();
        Pair p;
        Pair last;
        int x = RandomUtils.uniform(R, 7, W-6);
        int y = RandomUtils.uniform(R, 7, H-6);
        int w = RandomUtils.uniform(R, 3, 6);
        int h = RandomUtils.uniform(R, 3, 6);
        for (int i = y; i < y + h; i++) {
            for (int j = x; j < x + w; j++) {
                if (grid[i][j] != Tileset.FLOOR) {
                    grid[i][j] = Tileset.FLOOR;
                    emptyCount++;
                    p = new Pair(j, i);
                    Empties.add(p);
                    EmptiesOrdered.add(p);
                    temp.add(p);
                }
            }
        }
        int t = RandomUtils.uniform(R, 0, temp.size());
        last = temp.get(t);
        temp.clear();
        for (int c = 1; c < num_rooms; c++) {
            x = RandomUtils.uniform(R, 10, W-6);
            y = RandomUtils.uniform(R, 10, H-6);
            w = RandomUtils.uniform(R, 3, 8);
            h = RandomUtils.uniform(R, 3, 8);
            for (int i = y; i < y + h; i++) {
                for (int j = x; j < x + w; j++) {
                    if (grid[i][j] != Tileset.FLOOR) {
                        grid[i][j] = Tileset.FLOOR;
                        emptyCount++;
                        p = new Pair(j, i);
                        Empties.add(p);
                        EmptiesOrdered.add(p);
                        temp.add(p);
                    }
                }
            }
            if (temp.size() > 0) {
                t = RandomUtils.uniform(R, 0, temp.size());
                connect(last, temp.get(t));
                last = temp.get(t);
            }
            temp.clear();
        }
    }

    private void connect(Pair last, Pair next) {
        int startX = Math.min(last.getX(), next.getX());
        int endX = Math.max(last.getX(), next.getX());
        int startY = Math.min(last.getY(), next.getY());
        int endY = Math.max(last.getY(), next.getY());
        int j1 = last.getX();
        for (int i = startY; i <= endY; i++) {
            if (grid[i][j1] != Tileset.FLOOR) {
                grid[i][j1] = Tileset.FLOOR;
                emptyCount++;
                Empties.add(new Pair(j1, i));
                EmptiesOrdered.add(new Pair(j1, i));
            }
        }
        int i = next.getY();
        for (int j = startX; j <= endX; j++) {
            if (grid[i][j] != Tileset.FLOOR) {
                grid[i][j] = Tileset.FLOOR;
                emptyCount++;
                Empties.add(new Pair(j, i));
                EmptiesOrdered.add(new Pair(j, i));
            }
        }
    }

    public TETile[][] matrix() {
        if (lights) {
            return grid;
        }
        TETile[][] res = new TETile[H+4][W+4];
        for (int i = 0; i < H+4; i++) {
            for (int j = 0; j < W+4; j++) {
                res[i][j] = Tileset.NOTHING;
            }
        }
        int x = Player.getX();
        int y = Player.getY();
        for (int i = Math.max(0, y - 4); i < Math.min(y + 5, H + 4); i++) {
            for (int j = Math.max(0, x - 4); j < Math.min(x + 5, W + 4); j++) {
                res[i][j] = grid[i][j];
            }
        }
        if (Opponent != null) {
            x = Opponent.getX();
            y = Opponent.getY();
            for (int i = Math.max(0, y - 4); i < Math.min(y + 5, H + 4); i++) {
                for (int j = Math.max(0, x - 4); j < Math.min(x + 5, W + 4); j++) {
                    res[i][j] = grid[i][j];
                }
            }
        }
        return res;
    }
    public void toggle() {
        if (lights) {
            lights = false;
            return;
        }
        lights = true;
    }

    public boolean onBoard(int x, int y) {
        return false;
    }

    public String pointType(int x, int y) {
        return "empty";
    }

    public void movePlayer(char k) {
        int x = Player.getX();
        int y = Player.getY();
        switch (k) {
            case 'w' -> x ++;
            case 'a' -> y --;
            case 's' -> x --;
            case 'd' -> y ++;
        }
        try {
            if (grid[y][x] == Tileset.FLOOR) {
                grid[Player.getY()][Player.getX()] = Tileset.FLOOR;
                Player = new Pair(x, y);
                grid[y][x] = Tileset.AVATAR;
            }
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void moveOpp(char k) {
        int x = Opponent.getX();
        int y = Opponent.getY();
        switch (k) {
            case 'i' -> x ++;
            case 'j' -> y --;
            case 'k' -> x --;
            case 'l' -> y ++;
        }
        try {
            if (grid[y][x] == Tileset.FLOOR) {
                grid[Opponent.getY()][Opponent.getX()] = Tileset.FLOOR;
                Opponent = new Pair(x, y);
                grid[y][x] = Tileset.OPPONENT;
            }
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void addPlayer(int seed) {
        int ind = RandomUtils.uniform(R, 0, EmptiesOrdered.size());
        Pair p = EmptiesOrdered.get(ind);
        Player = p;
        grid[p.getY()][p.getX()] = Tileset.AVATAR;
    }
    public void addOpponent(int seed) {
        int ind = RandomUtils.uniform(R, 0, EmptiesOrdered.size());
        Pair p = EmptiesOrdered.get(ind);
        Opponent = p;
        grid[p.getY()][p.getX()] = Tileset.OPPONENT;
    }
}
