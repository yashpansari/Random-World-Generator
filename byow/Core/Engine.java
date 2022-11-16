package byow.Core;

import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static edu.princeton.cs.algs4.StdDraw.mouseX;
import static edu.princeton.cs.algs4.StdDraw.mouseY;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private boolean gameOn = true;
    private int seed;
    private World world;
    private String stage = "INITIAL";
    private boolean quitting;
    private ArrayList<Character> moves;
    private ArrayList<Character> moves2;
    private File saves;
    private String inp;
    private int players;
    public Engine() {
        moves = new ArrayList<>(Arrays.asList('w','a','s','d'));
        moves2 = new ArrayList<>(Arrays.asList('i','j','k','l'));
        saves = new File("./games.txt");
        inp = "";
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        startMenu();
        KeyboardInputSource input = new KeyboardInputSource();
        while (gameOn) {
            if (input.possibleNextInput()) {
                char k = input.getNextKey();
                analyzeK(Character.toLowerCase(k));
            }
            if (stage == "ON") {
                ter.renderFrame(world.matrix());
            }
        }
        end(stage);
    }

    private void end(String stage) {
        StdDraw.setCanvasSize(WIDTH*25, HEIGHT*25);
        Font style = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(style);
        StdDraw.enableDoubleBuffering();
        StdDraw.text(WIDTH / 2, HEIGHT*0.9, "GAME OVER!");
        Font sub = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(sub);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.75, "I WIN...");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.55, "PROGRESS SAVED!");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.45, "Press L(oser) to load this progress next time.");
        StdDraw.show();
        StdDraw.pause(1);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */


    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        for (char k: input.toCharArray()) {
            analyzeK(k);
        }
        TETile[][] finalWorldFrame = world.matrix();
        return finalWorldFrame;
    }

    // Create the CS61B Start Menu using StdDraw
    private void startMenu() {
        StdDraw.setCanvasSize(WIDTH*25, HEIGHT*25);
        Font style = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(style);
        StdDraw.enableDoubleBuffering();
        StdDraw.text(WIDTH / 2, HEIGHT*0.9, "Hide and Seek");
        Font sub = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(sub);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.75, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.7, "2 Player New Game (Y)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.65, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.6, "Quit (Q)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.5, "Use wasd to move H(ider), and for the multiplayer version, ijkl to move S(eeker).");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.45, "In-game, you can press T to toggle the line-of-dight feature.");
        StdDraw.show();
        StdDraw.pause(1);
    }

    private void seedUI() {

        Font sub = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(sub);
        StdDraw.clear(Color.BLACK);

        StdDraw.text(WIDTH / 2, HEIGHT * 0.8, "Input Seed...");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.7, Integer.toString(seed));
        StdDraw.text(WIDTH / 2, HEIGHT * 0.6, "S to start");
        StdDraw.show();
        StdDraw.pause(1);
    }
    private void analyzeK(char k) {
        inp += k;
        if (!gameOn) {
            return;
        }
        if (stage == "INITIAL") {
            if (k == 'n') {
                players = 1;
                quitting = false;
                stage = "SEED";
                seedUI();
                seed = 0;
            }
            if (k == 'y') {
                players = 2;
                quitting = false;
                stage = "SEED";
                seedUI();
                seed = 0;
            }
            if (k == 'l') {
                quitting = false;
                inp = "";
                load();
                stage = "ON";
            }
            if (k == 'q' && quitting) {
                gameOn = false;
                stage = "OVER";
                save();
            }
        } else if (stage == "SEED") {
            if (k == 's') {
                stage = "ON";
                gameStart();
            } else {
                seed *= 10;
                try {
                    seed += Integer.parseInt(String.valueOf(k));
                } catch (NumberFormatException e) {
                    return;
                }
                seedUI();
            }
        } else if (stage == "ON") {
            if (k == 't') {
                world.toggle();
            }
            if (k == ':') {
                quitting = true;
            }
            if (k == 'q' && quitting) {
                gameOn = false;
                stage = "OVER";
                save();
            }
            if (moves.contains(k)) {
                world.movePlayer(k);
            }
            if (moves2.contains(k)) {
                world.moveOpp(k);
            }
        }
    }

    private void load() {
        try {
            FileReader reader = new FileReader(saves);
            String temp = "";
            int c = 0;
            while (c != -1) {
                c = reader.read();
                temp += (char) c;
            }
            reader.close();
            interactWithInputString(temp.substring(0,temp.length()-3));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void gameStart() {
        world = new World(WIDTH, HEIGHT, new Random(seed));
        ter.initialize(WIDTH, HEIGHT);
        world.addPlayer(seed);
        if (players == 2) {
            world.addOpponent(seed);
        }
    }

    private void save(){
        try {
            saves.createNewFile();
            FileWriter writer = new FileWriter(saves);
            writer.write(inp);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private String headsUpDisplay(int X, int Y) {
//        if (gameOn && world.onBoard(X, Y)) {
//            return world.pointType(X, Y);
//        }
//        return "";
//    }

}
