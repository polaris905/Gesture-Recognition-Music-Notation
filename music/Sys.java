package music;

import graphicslib.I;
import graphicslib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.Graphics;
import java.util.ArrayList;

public class Sys extends Mass {

  public ArrayList<Staff> staffs = new ArrayList<>();
  public Time.List times;
  public Stem.List stems = new Stem.List();
  public int ndx;
  public I.Page page;

  public Sys(I.Page page) {
    super("BACK");
    this.page = page;
    ndx = page.systems().size();
    page.systems().add(this);
    times = new Time.List(this);
    makeStaffsMatchSysFmt();
    addReaction(new Reaction("E-E") {
      @Override
      public int bid(Gesture g) {
        int x1 = g.vs.xLow(), y1 = g.vs.yLow(), x2 = g.vs.xHi(), y2 = g.vs.yHi();
        if (stems.fastReject(y1, y2)) {
          return UC.noBid;
        }
        ArrayList<Stem> tempArr = stems.allIntersectors(x1, y1, x2, y2);
        if (tempArr.size() < 2) {
          return UC.noBid;
        }
        Beam b = tempArr.get(0).beam;
        for (Stem s : tempArr) {
          if (s.beam != b) {
            return UC.noBid;
          }
        }
        if (b == null && tempArr.size() != 2) {
          return UC.noBid;
        }
        if (b == null && (tempArr.get(0).nFlag != 0 || tempArr.get(1).nFlag != 0)) {
          return UC.noBid;
        }
        return 50;
      }

      @Override
      public void act(Gesture g) {
        int x1 = g.vs.xLow(), y1 = g.vs.yLow(), x2 = g.vs.xHi(), y2 = g.vs.yHi();
        ArrayList<Stem> tempArr = stems.allIntersectors(x1, y1, x2, y2);
        Beam b = tempArr.get(0).beam;
        if (b == null) {
          new Beam(tempArr.get(0), tempArr.get(1));
        } else {
          for (Stem s : tempArr) {
            s.incFlag();
          }
        }
      }
    });
  }

  public Time getTime(int x) {
    return times.getTime(x);
  }

  public int yTop() {
    return page.top() + ndx * (page.sysFmt().height() + page.sysFmt().sysGap);
  }

  public int yBot() {
    return yTop() + page.sysFmt().height();
  }

  @Override
  public void show(Graphics g) {
    page.sysFmt().showAt(g, yTop(), page);
    g.drawLine(page.left(), yTop(), page.left(), yBot());
  }

  public void makeStaffsMatchSysFmt() {
    while (staffs.size() < page.systems().size()) {
      new Staff(this);
    }
  }

  public void addTime(Time time) {
    times.add(time);
  }

  public void addStaff(Staff staff) {
    staffs.add(staff);
  }

  public int getStaffSize() {
    return staffs.size();
  }

  public Staff getStaff(int ndx) {
    return staffs.get(ndx);
  }

  public void addStem(Stem stem) {
    stems.addStem(stem);
  }

  public void removeStem(Stem stem) {
    stems.remove(stem);
  }

  public static class Fmt extends ArrayList<Staff.Fmt> {

    public int maxH = UC.defaultStaffLineSpace;
    public int sysGap = 0;

    public int height() {
      Staff.Fmt last = get(size() - 1);
      return last.dy + last.height();
    }

    public void addNewStaff(int y, I.Page page) {
      new Staff.Fmt(y - page.top(), page);
      for (I.Page p : APP.get.pages()) {
        for (Sys s : page.systems()) {
          s.makeStaffsMatchSysFmt();
        }
      }
    }

    public void showAt(Graphics g, int y, I.Page page) {
      for (Staff.Fmt sf : this) {
        sf.showAt(g, y + sf.dy, page);
      }
    }
  }
}