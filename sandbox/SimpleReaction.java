package sandbox;

import graphicslib.G;
import graphicslib.I;
import graphicslib.UC;
import graphicslib.Window;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Mass;
import reaction.Reaction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Random;

public class SimpleReaction extends Window {

  static {
    new Layer("BACK");
    new Layer("FORE");
  }

  public static int SEED = 1234;

  public SimpleReaction() {
    super("SimpleReaction", UC.screenWidth, UC.screenHeight);
    Reaction.initialReactions.addReaction(new Reaction("SW-SW") {
      @Override
      public void act(Gesture gesture) {
        new Box(gesture.vs);
      }

      @Override
      public int bid(Gesture gesture) {
        return 0;
      }
    });
    //
    G.RND = new Random(SEED);
    Reaction.initialAction = new I.Act() {
      @Override
      public void act(Gesture gesture) {
        G.RND = new Random(SEED);
      }
    };
    //
  }

  @Override
  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.WHITE);
    g.setColor(Color.BLUE);
    Ink.BUFFER.show(g);
    Layer.ALL.show(g);
  }

  @Override
  public void mousePressed(MouseEvent me) {
    Gesture.AREA.dn(me.getX(), me.getY());
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    Gesture.AREA.drag(me.getX(), me.getY());
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    Gesture.AREA.up(me.getX(), me.getY());
    repaint();
  }

  public static class Box extends Mass {

    public G.VS vs;
    public Color c = G.rndColor();

    public Box(G.VS vs) {
      super("BACK");
      this.vs = vs;
      addReaction(new Reaction("S-S") {
        @Override
        public int bid(Gesture gesture) {
          int x = gesture.vs.xMid();
          int y = gesture.vs.yLow();
          if (Box.this.vs.hit(x, y)) {
            return Math.abs(x - Box.this.vs.xMid());
          } else {
            return UC.noBid;
          }
        }

        @Override
        public void act(Gesture gesture) {
          Box.this.deleteMass();
        }
      });

      addReaction(new Reaction("DOT") {
        @Override
        public int bid(Gesture gesture) {
          int x = gesture.vs.xMid();
          int y = gesture.vs.yLow();
          if (Box.this.vs.hit(x, y)) {
            return Math.abs(x - Box.this.vs.xMid());
          } else {
            return UC.noBid;
          }
        }

        @Override
        public void act(Gesture gesture) {
          c = G.rndColor();
        }
      });
    }

    @Override
    public void show(Graphics g) {
      vs.fill(g, c);
    }
  }
}
