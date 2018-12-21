package sandbox;

import graphicslib.G;
import graphicslib.I;
import graphicslib.I.MusicApp;
import graphicslib.UC;
import graphicslib.Window;
import music.APP;
import music.Beam;
import music.Sys;
import music.Sys.Fmt;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Reaction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class Music2 extends Window implements MusicApp {

  static {
    new Layer("BACK");
    new Layer("NOTE");
    new Layer("FORE");
  }

  public static I.Page PAGE = new M2Page();
  public static ArrayList<I.Page> PAGES = new ArrayList<>();

  static {
    PAGES.add(PAGE);
  }

  public static Fmt SYSFMT = null;
  public static ArrayList<Sys> SYSTEMS = new ArrayList<>();

  public Sys.Fmt sysfmt(I.Page page) {
    return SYSFMT;
  }

  public ArrayList<Sys> systems(I.Page page) {
    return SYSTEMS;
  }

  public ArrayList<I.Page> pages() {
    return PAGES;
  }

  public Music2() {
    super("Music1", UC.screenWidth, UC.screenHeight);
    APP.get = this;
    Reaction.initialAction = new I.Act() {
      public void act(Gesture gesture) {
        SYSFMT = null;
      }
    };
    Reaction.initialReactions.addReaction(new Reaction("E-E") {
      public int bid(Gesture g) {
        if (SYSFMT == null) {
          return 0;
        }
        int y = g.vs.yMid();
        if (y > PAGE.top() + SYSFMT.height() + 15) { // 15 or 10
          return 100;
        } else {
          return UC.noBid;
        }
      }

      public void act(Gesture g) {
        int y = g.vs.yMid();
        if (SYSFMT == null) {
          ((M2Page) PAGE).top = y;
          SYSFMT = new Fmt();
          SYSTEMS.clear();
          new Sys(PAGE);
        }
        SYSFMT.addNewStaff(y, PAGE);
      }
    });
    Reaction.initialReactions.addReaction(new Reaction("E-W") {
      @Override
      public int bid(Gesture g) {
        if (SYSFMT == null) {
          return UC.noBid;
        }
        int y = g.vs.yMid();
        if (y > SYSTEMS.get(SYSTEMS.size() - 1).yBot() + 15) {
          return 100;
        }
        return UC.noBid;
      }

      @Override
      public void act(Gesture g) {
        int y = g.vs.yMid();
        if (SYSTEMS.size() == 1) {
          PAGE.sysFmt().sysGap = y - (PAGE.top() + SYSFMT.height());
        }
        new Sys(PAGE);
      }
    });
  }

  static int[] xPoly = {100, 200, 200, 100};
  static int[] yPoly = {50, 70, 80, 60};
  static Polygon poly = new Polygon(xPoly, yPoly, 4);

  public void brace(int y1, int y2, int x, int h) {
    // draw the brace
    G.poly.reset();
    int yM = (y1 + y2) / 2;
    int yH = 2 * h;
    G.pSpline(x, y1 + yH, x, y1, x + h, y1, 4);
    G.pSpline(x + h + h, y1, x + h, y1, x + h, y1 + yH, 4);
    G.pSpline(x + h, yM - yH, x + h, yM, x, yM, 4);
    G.pSpline(x, yM, x + h, yM, x + h, yM + yH, 4);
    G.pSpline(x + h, y2 - yH, x + h, y2, x + h + h, y2, 4);
    G.pSpline(x + h, y2, x, y2, x, y2 - yH, 4);
    G.pSpline(x, yM + yH, x, yM, x - h, yM, 4);
    G.pSpline(x - h, yM, x, yM, x, yM - yH, 4);
  }

  public void tie(int x1, int x2, int y, int h, int b) { // draw the tie between notes
    // b : bend
    G.poly.reset();
    int xM = (x1 + x2) / 2;
    G.pSpline(x1, y, xM, y + b + h, x2, y, 4); // top
    G.pSpline(x2, y, xM, y + b, x1, y, 4); // bot
  }

  @Override
  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.WHITE);
    g.setColor(Color.BLACK);
    Ink.BUFFER.show(g);
    Layer.ALL.show(g);
    int h = 8, x1 = 100, x2 = 200;
    Beam.setMasterBeam(x1, G.rnd(50) + 100, x2, G.rnd(50) + 100);
    Beam.drawBeamStack(g, 0, 1, x1, x2, h);
    g.setColor(Color.RED);
    Beam.drawBeamStack(g, 1, 3, x1 + 20, x2 - 20, h);

//    // test polygon
//    int xA = 100, yA = 500, xB = 300, yB = G.rnd(1000), xC = 500, yC = 500;
//    G.poly.reset();
//    G.pSpline(xA, yA, xB, yB, xC, yC, 5);
//    g.setColor(Color.BLACK);
//    g.fillPolygon(G.poly);
//    // test brace
//    brace(200, 400, 500, 20);
//    g.fillPolygon(G.poly);

    // test tie
    tie(500, 600, 500, 10, -20);
    g.fillPolygon(G.poly);
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

  public static class M2Page implements I.Page {

    private int top = 50;

    @Override
    public int top() {
      return top;
    }

    @Override
    public int bot() {
      return UC.screenHeight - 50;
    }

    @Override
    public int left() {
      return 50;
    }

    @Override
    public int right() {
      return UC.screenWidth - 50;
    }

    @Override
    public Fmt sysFmt() {
      return SYSFMT;
    }

    @Override
    public ArrayList<Sys> systems() {
      return SYSTEMS;
    }
  }
}