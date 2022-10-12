import java.awt.*;
import java.util.*;

public class SandLab
{
  public static void main(String[] args)
  {
    SandLab lab = new SandLab(120, 80);
    lab.run();
  }
  
  //add constants for particle types here
  public static final int EMPTY = 0;
  public static final int STONE = 1;
  public static final int SAND = 2;
  public static final int WATER = 3;
  public static final int ICE = 4;
  public static final int OBSIDIAN = 5;
  public static final int LAVA = 6;
  public static final int CLEAR = 8;
  
  //do not add any more fields
  private int[][] grid;
  private SandDisplay display;
  private int timer = 0;
  
  public SandLab(int numRows, int numCols)
  {
    String[] names;
    names = new String[8];
    names[EMPTY] = "Empty";
    names[STONE] = "Stone";
    names[SAND] = "Sand";
    names[WATER] = "Water";
    names[ICE] = "Ice";
    names[OBSIDIAN] = "Obsidian";
    names[LAVA] = "Lava";
    names[CLEAR] = "Clear";
    display = new SandDisplay("Falling Sand", numRows, numCols, names);
    grid = new int[numRows][numCols];
  }
  
  //called when the user clicks on a location using the given tool
  private void locationClicked(int row, int col, int tool)
  {
      grid[row][col] = tool;
  }

  //copies each element of grid into the display
  public void updateDisplay()
  {
      for(int r = 0; r < grid.length; r++)
      {
          for(int c = 0; c < grid[r].length; c++)
          {
              if(grid[r][c] == EMPTY)
              {
                  display.setColor(r, c, new Color(0, 0, 0));
              }
              if(grid[r][c] == STONE)
              {
                  display.setColor(r, c, new Color(128, 128, 128));
              }
              if(grid[r][c] == SAND)
              {
                  display.setColor(r, c, new Color(255, 255, 102));
              }
              if(grid[r][c] == WATER)
              {
                  display.setColor(r, c, new Color(0, 128, 255));
              }
              if(grid[r][c] == ICE)
              {
                  display.setColor(r, c, new Color(153, 255, 255));
              }
              if(grid[r][c] == OBSIDIAN)
              {
                  display.setColor(r, c, new Color(48, 0, 104));
              }
              if(grid[r][c] == LAVA)
              {
                  int colorChange = (int)(Math.random() * 6);
                  if(colorChange == 0)
                  {
                      //Yellow
                      display.setColor(r, c, new Color(255, 255, 51));
                  }
                  else
                  {
                      //Orange
                      display.setColor(r, c, new Color(255, 128, 0));
                  }
              }
          }
      }
  }

  //called repeatedly.
  //causes one random particle to maybe do something.
  //steps 100000 times per second
  public void step()
  {
      int randRow = (int)(Math.random() * grid.length);
      int randCol = (int)(Math.random() * grid[0].length);
      timer++;
      
      //Logic for sand
      if(grid[randRow][randCol] == SAND)
      {
          //Must not go out of bounds
          if(randRow + 1 < grid.length)
          {
              //Sand through empty space
              if(grid[randRow + 1][randCol] == EMPTY)
              {
                  //Turns what was sand into empty and space below it to sand
                  grid[randRow][randCol] = EMPTY;
                  grid[randRow + 1][randCol] = SAND;
              }
              else if(grid[randRow + 1][randCol] == WATER)
              {
                  //Sand displaces water
                  grid[randRow][randCol] = WATER;
                  grid[randRow + 1][randCol] = SAND;
              }
          }
      }
      
      //Logic for water
      if(grid[randRow][randCol] == WATER)
      {
          //Makese water flow
          fluidFlow(WATER, randRow, randCol);
          
          //Turns water into obsidian if it touches lava
          if(randRow + 1 < grid.length && grid[randRow + 1][randCol] == LAVA)
          {
              //Turns what was water into empty and space below it to water
              grid[randRow][randCol] = EMPTY;
              grid[randRow + 1][randCol] = OBSIDIAN;
          }
          if(randCol - 1 > 0 && grid[randRow][randCol - 1] == LAVA)
          {
              grid[randRow][randCol] = EMPTY;
              grid[randRow][randCol - 1] = OBSIDIAN;
          }
          if(randCol + 1 < grid[randRow].length && grid[randRow][randCol + 1] == LAVA)
          {
              grid[randRow][randCol] = EMPTY;
              grid[randRow][randCol + 1] = OBSIDIAN;
          }
      }
      
      //Logic for lava
      if(grid[randRow][randCol] == LAVA)
      {
          fluidFlow(LAVA, randRow, randCol);
          
          //Turns water into stone
          if(randRow + 1 < grid.length && grid[randRow + 1][randCol] == WATER)
          {
              //Turns what was water into empty and space below it to water
              grid[randRow][randCol] = EMPTY;
              grid[randRow + 1][randCol] = STONE;
          }
          if(randCol - 1 > 0 && grid[randRow][randCol - 1] == WATER)
          {
              grid[randRow][randCol] = EMPTY;
              grid[randRow][randCol - 1] = STONE;
          }
          if(randCol + 1 < grid[randRow].length && grid[randRow][randCol + 1] == WATER)
          {
              grid[randRow][randCol] = EMPTY;
              grid[randRow][randCol + 1] = STONE;
          }
      }
      
      //Logic for Ice
      if(grid[randRow][randCol] == ICE)
      {
          //steps 100000 times per second
          int distance = 20;
          int delay = 1000000;
          
          //Checks if lava is nearby
          for(int n = 0; n < distance; n++)
          {
              if(randRow + n < grid.length && grid[randRow + n][randCol] == LAVA && timer % delay < 1000)
              {
                  grid[randRow][randCol] = WATER;
              }
              //Fixes for lava right on top
              if(randRow - 1 > 0 && grid[randRow - n][randCol] == LAVA && timer % delay < 1000)
              {
                  grid[randRow][randCol] = WATER;
              }
              //Detects lava near it horizontally
              if(randCol + n < grid[randRow].length && grid[randRow][randCol + n] == LAVA && timer % delay < 1000)
              {
                  grid[randRow][randCol] = WATER;
              }
              if(randCol - n > 0 && grid[randRow][randCol - n] == LAVA && timer % delay < 1000)
              {
                  grid[randRow][randCol] = WATER;
              }
          }
      }
      
      //Logic for clear
      if(grid[randRow][randCol] == CLEAR)
      {
        for(int r = 0; r < grid.length; r++)
        {
            for(int c = 0; c < grid[r].length; c++)
            {
                grid[r][c] = EMPTY;
            }
        }
      }
      
      //System.out.println(timer);
  }
  
  public void fluidFlow(int element, int r, int c)
  {
      //0 is down, 1 is left, 2 is right
      int flowDirection = (int)(Math.random() * 3);
          
      //Flow logic
      if(flowDirection == 0 && r + 1 < grid.length && grid[r + 1][c] == EMPTY)
      {
          //Turns what was water into empty and space below it to water
          grid[r][c] = EMPTY;
          grid[r + 1][c] = element;
      }
      if(flowDirection == 1 && c - 1 > 0 && grid[r][c - 1] == EMPTY)
      {
          grid[r][c] = EMPTY;
          grid[r][c - 1] = element;
      }
      if(flowDirection == 2 && c + 1 < grid[r].length && grid[r][c + 1] == EMPTY)
      {
          grid[r][c] = EMPTY;
          grid[r][c + 1] = element;
      }
  }
  
  //do not modify
  public void run()
  {
    while (true)
    {
      for (int i = 0; i < display.getSpeed(); i++)
        step();
      updateDisplay();
      display.repaint();
      display.pause(1);  //wait for redrawing and for mouse
      int[] mouseLoc = display.getMouseLocation();
      if (mouseLoc != null)  //test if mouse clicked
        locationClicked(mouseLoc[0], mouseLoc[1], display.getTool());
    }
  }
}
