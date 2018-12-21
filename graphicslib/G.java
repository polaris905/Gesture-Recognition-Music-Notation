package graphicslib;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class G {

  public static Random RND = new Random();

  public static int rnd(int max) {
    return RND.nextInt(max);
  }

  public static Color rndColor() {
    return new Color(rnd(256), rnd(256), rnd(256));
  }

  public static void fillBackground(Graphics g, Color c) {
    g.setColor(c);
    g.fillRect(0, 0, 3000, 3000);
  }

  /**
   * 以传进来的参数x和y为圆心，转换成swing坐标系的坐标来绘制圆形
   */
  public static void drawCircle(Graphics g, int x, int y, int r) {
    g.drawOval(x - r, y - r, r + r, r + r);
  }

  public static Polygon poly = new Polygon();

  public static void pSpline(int xA, int yA, int xB, int yB, int xC, int yC, int n) {
    if (n == 0) {
      poly.addPoint(xA, yA);
      poly.addPoint(xC, yC);
      return;
    }
    int xAB = (xA + xB) / 2, yAB = (yA + yB) / 2, xBC = (xB + xC) / 2, yBC = (yB + yC) / 2;
    int xABC = (xAB + xBC) / 2, yABC = (yAB + yBC) / 2;
    pSpline(xA, yA, xAB, yAB, xABC, yABC, n - 1);
    pSpline(xABC, yABC, xBC, yBC, xC, yC, n - 1);
  }

  /**
   * 表示一个含有x和y值的类
   */
  public static class V implements Serializable {

    public int x, y;

    public V(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public V(V v) {
      x = v.x;
      y = v.y;
    }

    public static Transform T = new Transform(); // the single isomorphic one that V will use for tx, ty and setT

    public void add(int dx, int dy) {
      x += dx;
      y += dy;
    }

    public void add(V v) {
      x += v.x;
      y += v.y;
    }

    public void set(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public void set(V v) {
      x = v.x;
      y = v.y;
    }

    public void blend(V v, int k) {
      set((k * x + v.x) / (k + 1), (k * y + v.y) / (k + 1));
    }

    public int tx() {
      return x * T.newScale / T.oldScale + T.dx;
    }

    public int ty() {
      return y * T.newScale / T.oldScale + T.dy;
    }

    public void setT(V v) {
      set(this.tx(), this.ty());
    } // sets this v to transform of v

    public static class Transform {

      private int dx = 0, dy = 0, oldScale = 1, newScale = 1; // the single scale multiplier is newScal/oldScale

      public void set(VS from, VS to) {
        setScale(from.size.x, from.size.y, to.size.x, to.size.y);
        dx = trans(from.loc.x, from.size.x, to.loc.x, to.size.x);
        dy = trans(from.loc.y, from.size.y, to.loc.y, to.size.y);
      }

      public void set(BBox from, VS to) {
        setScale(from.h.size(), from.v.size(), to.size.x, to.size.y);
        dx = trans(from.h.lo, from.h.size(), to.loc.x, to.size.x);
        dy = trans(from.v.lo, from.v.size(), to.loc.y, to.size.y);
      }

      private void setScale(int oldW, int oldH, int newW, int newH) {
        oldScale = (oldW > oldH) ? oldW : oldH;
        newScale = (newW > newH) ? newW : newH;
      }

      private int trans(int oldX, int oldW, int newX,
          int newW) { // assumes that scale has already been set
        return (-oldX - oldW / 2) * newScale / oldScale + (newX + newW / 2);
      }
    }
  }

  /**
   * 表示矩形形状的类，通过两个V类来表示，loc代表左上顶点坐标，size代表宽和高
   */
  public static class VS implements Serializable {

    public V loc, size;

    //public VS(V loc, V size){this.loc = new V(loc); this.size = new V(size);}
    public VS(int x, int y, int w, int h) {
      loc = new V(x, y);
      size = new V(w, h);
    }

    public void fill(Graphics g, Color c) {
      g.setColor(c);
      g.fillRect(loc.x, loc.y, size.x, size.y);
    }

    public boolean hit(int x, int y) {
      return loc.x <= x && loc.y <= y && x <= (loc.x + size.x) && y <= (loc.y + size.y);
    }

    public int xLow() {
      return loc.x;
    }

    public int xHi() {
      return loc.x + size.x;
    }

    public int xMid() {
      return (loc.x + loc.x + size.x) / 2;
    }

    public int yLow() {
      return loc.y;
    }

    public int yHi() {
      return loc.y + size.y;
    }

    public int yMid() {
      return (loc.y + loc.y + size.y) / 2;
    }

    public void resize(int x, int y) {
      size.x = x;
      size.y = y;
    }

    public void set(int x, int y, int w, int h) {
      loc.set(x, y);
      size.set(w, h);
    }
  }

  /**
   * 代表储存最小和最大值的类，里面有扩大和缩小两个方法，其原理是lo永远取最小，hi永远取最大
   */
  public static class LoHi {

    int lo, hi;

    public LoHi(int min, int max) {
      lo = min;
      hi = max;
    }

    public void set(int v) {
      lo = v;
      hi = v;
    } // first value into the box

    public void add(int v) {
      if (v < lo) {
        lo = v;
      }
      if (v > hi) {
        hi = v;
      }
    } // move bounds if necessary

    public int size() {
      return (hi - lo) > 0 ? hi - lo : 1;
    } // force size not zero

    public int constrain(int v) {
      if (v < lo) {
        return lo;
      } else {
        return (v < hi) ? v : hi;
      }
    }
  }

  public static class BBox { // Bounding Box

    LoHi h, v;  // horizontal and vertical ranges.

    public BBox() {
      h = new LoHi(0, 0);
      v = new LoHi(0, 0);
    }

    public void set(int x, int y) {
      h.set(x);
      v.set(y);
    } // sets it to a single point

    /**
     * 下面两个方法作用为，传入一个新的点，如果这个点在BBox界外，则扩充BBox的边界到这个点的位置
     */
    public void add(int x, int y) {
      h.add(x);
      v.add(y);
    }

    public void add(V v) {
      add(v.x, v.y);
    }

    /**
     * 以当前的BBox规格生成一个新的VS对象
     *
     * @return VS对象
     */
    public VS getNewVS() {
      return new VS(h.lo, v.lo, h.hi - h.lo, v.hi - v.lo);
    }

    public void draw(Graphics g) {
      g.drawRect(h.lo, v.lo, h.hi - h.lo, v.hi - v.lo);
    }
  }

  /**
   * 此类定义了线条图形的基本属性和方法。所谓线条，即把屏幕中每一个点的坐标保存起来，然后每两点画线连接
   */
  public static class PL implements Serializable { // Polyline

    public V[] points;

    public PL(int count) {
      points = new V[count];
      for (int i = 0; i < count; i++) {
        points[i] = new V(0, 0);
      }
    }

    public int size() {
      return points.length;
    }

    public void drawN(Graphics g, int n) {
      for (int i = 1; i < n; i++) {
        g.drawLine(points[i - 1].x, points[i - 1].y, points[i].x, points[i].y);
      }
//      drawNDots(g, n);
    }

    /**
     * 以points中n之前的所有点为圆心，分别画出小圆形
     */
    public void drawNDots(Graphics g, int n) {
//      g.setColor(Color.RED);
      for (int i = 1; i < n; i++) {
        drawCircle(g, points[i].x, points[i].y, UC.dotRadius);
      }
    }

    public void draw(Graphics g) {
      drawN(g, points.length);
    }

    public void transform() {
      for (int i = 0; i < points.length; i++) {
        points[i].setT(points[i]);
      }
    }
  }
}