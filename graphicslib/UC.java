package graphicslib;

import java.awt.Color;

public class UC {

  public static final int screenHeight = 800;
  public static final int screenWidth = 1000;
  public static final int inkBufferMax = 800;
  public static final int normSampleSize = 25;
  public static final int normCoordMax = 500;
  public static Color inkColor = Color.BLUE;
  public static final int dotRadius = 4;
  public static final int noMatchDist = 500000; // Based on normSampleSize of 200 and normCoordMax of 500
  public static final int dotThreshold = 6;
  public static final String shapeDBFileName = "C:/Users/LiC/Documents/java/GestureBasedUIProject/Marlin/ShapeDB.bin";
  public static int noBid = Integer.MAX_VALUE; // or 10000
  public static int defaultStaffLineSpace = 8;
  public static String FontName = "sinfonia";
  public static int barToMarginSnap = 20;
  public static int defaultStaffH = 8;
  public static int snapTime = 40;
  public static int restFirstDot = 40;
  public static int dotSpace = 10;
}
