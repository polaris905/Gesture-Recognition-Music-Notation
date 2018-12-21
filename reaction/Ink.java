package reaction;

import graphicslib.G;
import graphicslib.I;
import graphicslib.UC;

import java.io.Serializable;
import java.awt.*;
import java.util.ArrayList;

public class Ink implements I.Show {

  public Norm norm;
  public G.VS vs;

  public static Buffer BUFFER = new Buffer();

  /**
   * 构造器的作用在于每次画完一个图形鼠标释放时，把当前BUFFER中储存的图形保存为一个新的Ink对象
   */
  public Ink() {
    norm = new Norm(); // automatically loads from BUFFER
    vs = BUFFER.bbox.getNewVS(); // where the ink was on the screen
  }

  /**
   * 此方法调用继承自G.PL的draw方法，即把当前Int对象保存的所有点都画出来
   */
  public void show(Graphics g) {
    g.setColor(UC.inkColor);
    norm.drawAt(g, vs);
  }

  // ----- Buffer ------
  /**
   * Buffer类基本属性与Ink类一致，都继承自G.PL，并且实现I.Show接口。唯一却别在于Buffer类还实现了I.Area接口，这是因为Buffer类表达的是鼠标正在绘制的这个图形，需要响应一些动作。
   * 当鼠标点下去、释放前，所有drag经过的点都保存在Buffer对象里，并且实时显示在屏幕上。当鼠标释放时，Buffer对象中保存的所有点复制到一个新的Int对象里，并加到intList，即屏幕上显示的之前画过的线条图形。
   */
  public static class Buffer extends G.PL implements I.Show, I.Area {

    public static final int MAX = UC.inkBufferMax;
    public int n = 0; // tracks how many points are in the buffer.
    public G.BBox bbox = new G.BBox();

    private Buffer() {
      super(MAX);
    }

    /**
     * 此处的if条件是为了防止数组越界，因为当前Buffer对象保存的每一个点都存在points数组里，数组大小已声明为MAX常量。
     * 但是固定大小的数组会产生一个限制，就是当鼠标点下持续拖拽绘制图形时，图形的长度受制于MAX，有最大上限。 如果有必要，在Buffer中可以考虑用ArrayList代替数组来保存绘制的点的集合。
     */
    public void add(int x, int y) {
      if (n < MAX) {
        points[n].set(x, y);
        n++;
        // 鼠标每到一个新点时更新BBox边界
        bbox.add(x, y);
      }
    }

    public void clear() {
      n = 0;
    } // reset the buffer

//    /**
//     * 根据正在绘制并存在Buffer中的线条长度，按照k值进行采样，返回一个由k个采样点形成的线条
//     */
//    public G.PL subSample(int k) {
//      G.PL res = new G.PL(k);
//      for (int i = 0; i < k; i++) {
//        // 采用n-1和k-1而不是n和k，能够确保最后一个采样点位于原线条的末端，并且指针不越界
//        res.points[i].set(this.points[i * (n - 1) / (k - 1)]);
//      }
//      return res;
//    }

    public void subSample(G.PL pl) {
      int K = pl.points.length;
      for (int i = 0; i < K; i++) {
        // 采用n-1和k-1而不是n和k，能够确保最后一个采样点位于原线条的末端，并且指针不越界
        pl.points[i].set(points[i * (n - 1) / (K - 1)]);
      }
    }

    /**
     * week2-2课上最后一个bug（即每次画图形时都有冗余线条出现）出在这里。 老师之所以debug后drawN代替draw方法，是因为只需要在窗口画出Buffer对象中序号n之前保存的点。
     * 这是因为每次Buffer清零clear方法只是清零了序号n，也就是说大于序号n的位置其实保存有此前图形记录的。
     */
    @Override //--I.Show interface
    public void show(Graphics g) {
      this.drawN(g, n);
//      this.drawNDots(g, n);
//      if (n > 0) {
//        G.PL ss = subSample(UC.normSampleSize);
//        g.setColor(UC.inkColor);
//        ss.drawNDots(g, UC.normSampleSize);
//        ss.draw(g);
//        bbox.draw(g);
//      }
    }// draw the n points as a line.

    @Override //--I.Area interface
    public boolean hit(int x, int y) {
      return true;
    } // any point COULD go into ink

    /**
     * 此处clear的作用正是因为之前提到Buffer对象BUFFER变量是全局唯一的，每次鼠标点击时要把当前index即n归零。因为下面的show方法调用drawN时传递的正是这个n，如果第二次点鼠标画图形时，如果n不清零，则此前的BUFFER剩余线条会保留。
     */
    public void dn(int x, int y) {
      clear();
      // 每次鼠标点下去时重新设定BBox边界，初始大小为当前点位置
      bbox.set(x, y);
      add(x, y);
    } // add first point

    public void drag(int x, int y) {
      add(x, y);
    } // add each point as it comes is

    public void up(int x, int y) {
    }
  }

  // ---- Norm ----
  public static class Norm extends G.PL implements Serializable {

    public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
    public static final G.VS CS = new G.VS(0, 0, MAX, MAX); // the coordinate box for Transforms

    public Norm() {
      super(N); // creates the PL
      BUFFER.subSample(this);
      G.V.T.set(BUFFER.bbox, CS);
      this.transform();
    }

    public void drawAt(Graphics g, G.VS vs) { // expands Norm to fit in vs
      G.V.T.set(CS, vs); // prepare to move from normalized CS to vs
      for (int i = 1; i < N; i++) {
        g.drawLine(points[i - 1].tx(), points[i - 1].ty(), points[i].tx(), points[i].ty());
      }
    }

    public int dist(Norm n) {
      int res = 0;
      for (int i = 0; i < N; i++) {
        int dx = points[i].x - n.points[i].x, dy = points[i].y - n.points[i].y;
        res += dx * dx + dy * dy;
      }
      return res;
    }
  }

  // ---- List ------
  public static class inkList extends ArrayList<Ink> implements I.Show {

    /**
     * 即把inkList对象列表中储存的所有Ink对象（即线条图形）都画出来
     */
    public void show(Graphics g) {
      for (Ink ink : this) {
        ink.show(g);
      }
    }
  }
}