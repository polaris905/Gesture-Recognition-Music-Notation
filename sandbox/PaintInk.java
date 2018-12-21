package sandbox;

import graphicslib.UC;
import graphicslib.G;
import graphicslib.Window;
import reaction.Ink;
import reaction.Shape;
import reaction.Shape.Prototype.List;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class PaintInk extends Window {

  public static Ink.inkList inkList = new Ink.inkList();
  public static List pList = new List();
  public static String recognized = "";

  public PaintInk() {
    super("PaintInk", UC.screenWidth, UC.screenHeight);
  }

  @Override
  public void paintComponent(Graphics g) {
    G.fillBackground(g, Color.WHITE);
    g.setColor(Color.RED);
//    g.drawString("points: " + Ink.BUFFER.n, 600, 30);

    // 可以把graphic改成graphic2D实现抗锯齿
//    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//    g2d.addRenderingHints(hint);
    pList.show(g);
    /*
    此处绘制的过去已经画好并保存在intList的图形。
     */
    inkList.show(g);
    /*
    此处绘制的是目前正在画（鼠标释放前）并实时保存在Buffer对象里的图形。
     */
    Ink.BUFFER.show(g);
    g.drawString(recognized, 700, 400);
//    G.VS vs = new G.VS(100, 100, 100,100);
//    G.V.T.set(Ink.BUFFER.bbox, vs);
//    G.PL pl = new G.PL(25);
//    Ink.BUFFER.subSample(pl);
//    pl.transform();
//    pl.draw(g);

//    if (Ink.BUFFER.n > 0) {
//      Ink.Norm norm = new Ink.Norm();
//      norm.drawAt(g, new G.VS(500, 30, 100, 100));
//      norm.drawAt(g, new G.VS(50, 200, 200, 200));
//    }
  }

  @Override
  public void mousePressed(MouseEvent me) {
    Ink.BUFFER.dn(me.getX(), me.getY());
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    Ink.BUFFER.drag(me.getX(), me.getY());
    repaint();
  }

  /*
  鼠标释放时，将当前Buffer的图形作为一个新的Int对象并保存到intList中。
   */
  @Override
  public void mouseReleased(MouseEvent me) {
    Ink ink = new Ink();
    Shape s = Shape.recognize(ink);
    recognized = "Recognized: " + ((s == null) ? "UNKNOWN" : s.name);
//    inkList.add(ink);
//    Shape.Prototype prototype;
//    if (pList.bestDist(ink.norm) < UC.noMatchDist) {
//      List.bestMatch.blend(ink.norm);
//      prototype = List.bestMatch;
//    } else {
//      prototype = new Shape.Prototype();
//      pList.add(prototype);
//    }
//    ink.norm = prototype;
    repaint();
  }
}
