package sandbox;

import graphicslib.I;
import graphicslib.UC;
import graphicslib.G;
import graphicslib.Window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Timer;

public class Squares extends Window implements ActionListener {

//  public static VS theVS = new VS(100, 100, 200, 300);
//  public static Color theColor = G.rndColor();
  public static G.V defaultSize = new G.V(100, 100);
  public static Square theSquare = new Square(200, 320);
  public static Square.SquareList theList = new Square.SquareList();
  public static Square backgroundSquare = new Square(0, 0) {
    @Override
    public void dn(int x, int y) {
      theList.add(new Square(x, y));
      firstPressed.set(x, y);
    }

    @Override
    public void drag(int x, int y) {
      Square s = theList.get(theList.size() - 1);
      int w = Math.abs(x - firstPressed.x);
      int h = Math.abs(y - firstPressed.y);
      if (x < firstPressed.x) {
        s.loc.x = x;
      }
      if (y < firstPressed.y) {
        s.loc.y = y;
      }
      s.resize(w, h);
    }

    @Override
    public void up(int x, int y) {
      Square s = theList.get(theList.size() - 1);
      if (s.size.x < defaultSize.x) {
        s.size.x = defaultSize.x;
        if (x < firstPressed.x) {
          s.loc.x = firstPressed.x - defaultSize.x;
        }
      }
      if (s.size.y < defaultSize.y) {
        s.size.y = defaultSize.y;
        if (y < firstPressed.y) {
          s.loc.y = firstPressed.y - defaultSize.y;
        }
      }
      s.dv.set(G.rnd(20) - 10, G.rnd(20) - 10);
    }
  };

  static {
    theList.add(backgroundSquare);
    backgroundSquare.size.set(3000, 3000);
    backgroundSquare.c = Color.WHITE;
  }

  public static G.V mousePosition = new G.V(0, 0);
  public static G.V firstPressed = new G.V(0, 0);
  public static Timer timer;
  public static I.Area currentArea;

  public Squares() {
    super("square", UC.screenWidth, UC.screenHeight);
    timer = new Timer(33, this);
    timer.setInitialDelay(5000);
    timer.start();
  }

  @Override
  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.WHITE); // It solves my problem in Windows system.
    theList.draw(g);
  }

  @Override
  public void mousePressed(MouseEvent me) {
    int x = me.getX();
    int y = me.getY();
    firstPressed.set(x, y);
    theSquare = theList.hit(x, y);
    currentArea = theSquare;
    currentArea.dn(x, y);
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    int x = me.getX();
    int y = me.getY();
    currentArea.drag(x, y);
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    currentArea.up(me.getX(), me.getY());
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  public static class Square extends G.VS implements I.Area {

    public Color c = G.rndColor();
    public G.V dv = new G.V(0,0);

    /**
     * A constructor that create a square shape with top-left coordinate (xStem, y) and fixed weight
     * 100, height 100.
     */
    public Square(int x, int y) {
      super(x, y, 0, 0);
    }

    public void moveAndBounce() {

      loc.add(dv);
      if (xLow() < 0 && dv.x < 0) {
        dv.x = -dv.x;
      }
      if (yLow() < 0 && dv.y < 0) {
        dv.y = -dv.y;
      }
      if (xHi() > UC.screenWidth && dv.x > 0) {
        dv.x = -dv.x;
      }
      if (yHi() > UC.screenHeight && dv.y > 0) {
        dv.y = -dv.y;
      }
    }

    public void draw(Graphics g) {
      this.fill(g, c);
      moveAndBounce();
    }

    @Override
    public void dn(int x, int y) {
      theSquare.dv.set(0, 0);
      mousePosition.x = x - theSquare.loc.x;
      mousePosition.y = y - theSquare.loc.y;
    }

    @Override
    public void drag(int x, int y) {
      theSquare.loc.x = x - mousePosition.x;
      theSquare.loc.y = y - mousePosition.y;
    }

    @Override
    public void up(int x, int y) {
      theSquare.dv.set((x - firstPressed.x) / 20, (y - firstPressed.y) / 20);
    }

    public static class SquareList extends ArrayList<Square> {

      public void draw(Graphics g) {
        for (Square s : this) {
          s.draw(g);
        }
      }

      public Square hit(int x, int y) {
        Square result = null;
        for (int i = this.size() - 1; i >= 0; i--) { // I think loop from backward is more efficient
          if (this.get(i).hit(x, y)) {
            result = this.get(i);
            break;
          }
        }
        return result;
      }
    }
  }
}