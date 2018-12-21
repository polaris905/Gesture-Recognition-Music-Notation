package graphicslib;

import java.awt.Graphics;
import java.util.ArrayList;
import marlin.music.Sys;
import marlin.reaction.Gesture;

public interface I {

  interface Area {

    boolean hit(int x, int y);

    void dn(int x, int y);

    void drag(int x, int y);

    void up(int x, int y);
  }

  interface Show {

    void show(Graphics g);
  }

  interface Act {

    void act(Gesture g);
  }

  interface React extends Act {

    int bid(Gesture g);
  }

  interface Margin {

    int top();

    int bot();

    int left();

    int right();
  }

  interface Page extends Margin {

    Sys.Fmt sysFmt();

    ArrayList<Sys> systems();
  }

  interface MusicApp {

    ArrayList<Page> pages();

    Sys.Fmt sysfmt(Page page);

    ArrayList<Sys> systems(Page page);
  }
}
