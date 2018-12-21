import graphicslib.Window;
import sandbox.Music1;
import sandbox.Music2;
import sandbox.PaintInk;
import sandbox.ShapeTrainer;
import sandbox.SimpleReaction;
import sandbox.Splines;
import sandbox.Squares;

import java.awt.EventQueue;

public class Main {

  public static void main(String[] args) {

//    Window.PANEL = new Squares();
//    Window.PANEL = new PaintInk();
//    Window.PANEL = new ShapeTrainer();
//    Window.PANEL = new SimpleReaction();
    Window.PANEL = new Music2();
//    Window.PANEL = new Splines();
    Window.PANEL.launch();
  }
}
