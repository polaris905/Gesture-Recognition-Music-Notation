package sandbox;

import graphicslib.G;
import graphicslib.UC;
import graphicslib.Window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class Splines extends Window {

  public Splines() {
    super("splines", UC.screenWidth, UC.screenHeight);
  }

  public static int xA = 100, yA = 100, xB = 100, yB = 200, xC = 300, yC = 300;
  public static Point[] points = {new Point(100, 100), new Point(200, 200), new Point(300, 300)};
  public static int cPoint = 0;

  @Override
  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.WHITE);
    g.setColor(Color.RED);
    G.poly.reset();
    G.pSpline(points[0].x, points[0].y, points[1].x, points[1].y, points[2].x, points[2].y, 4);
    g.fillPolygon(G.poly);
  }

  @Override
  public void mousePressed(MouseEvent me) {
    cPoint = closestPoint(me.getX(), me.getY());
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    points[cPoint].x = me.getX();
    points[cPoint].y = me.getY();
    repaint();
  }

  public int closestPoint(int x, int y) {
    int result = 0, closestDistance = Integer.MAX_VALUE;
    for (int i = 0; i < points.length; i++) {
      Point p = points[i];
      int d = (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
      if (d < closestDistance) {
        closestDistance = d;
        result = i;
      }
    }
    return result;
  }
}
