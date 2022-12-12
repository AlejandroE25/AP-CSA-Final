package indv.aescamilla.csafinal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Point;

import javax.sound.sampled.*;
import javax.swing.*;

import java.io.File;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.util.Random;

import static indv.aescamilla.csafinal.HelperFunctions.sleep;

/**
 * A Game board on which to place and move players.
 * 
 * @author PLTW
 * @version 1.0
 */
public class GameGUI extends JComponent
{
  static final long serialVersionUID = 141L; // problem 1.4.1

  private static final int WIDTH = 510;
  private static final int HEIGHT = 360;
  private static final int SPACE_SIZE = 60;
  private static final int GRID_W = 8;
  private static final int GRID_H = 5;
  private static final int START_LOC_X = 15;
  private static final int START_LOC_Y = 15;
  
  // initial placement of player
  int x = START_LOC_X; 
  int y = START_LOC_Y;

  // grid image to show in background
  private Image bgImage;

  // player image and info
  private Image player;
  private Point playerLoc;
  private int playerSteps;

  // walls, prizes, traps
  private int totalWalls;
  private Rectangle[] walls; 
  private Image prizeImage;
  private int totalPrizes;
  private Rectangle[] prizes;
  private int totalTraps;
  private Rectangle[] traps;

  // scores, sometimes awarded as (negative) penalties
  private int prizeVal = 10;
  private int trapVal = 5;
  private int endVal = 10;
  private int offGridVal = 5; // penalty only
  private int hitWallVal = 5;  // penalty only

  // game frame
  private JFrame frame;
  public JLabel scoreLabel;
  public int score = 0;



  /**
   * Constructor for the GameGUI class.
   * Creates a frame with a background image and a player that will move around the board.
   */
  public GameGUI() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    
    try {
      bgImage = ImageIO.read(new File("src/main/resources/grid.png"));
    } catch (Exception e) {
      System.err.println("Could not open file grid.png");
    }      
    try {
      prizeImage = ImageIO.read(new File("src/main/resources/coin.png"));
    } catch (Exception e) {
      System.err.println("Could not open file coin.png");
    }
  
    // player image, student can customize this image by changing file on disk
    try {
      player = ImageIO.read(new File("src/main/resources/player.png"));
    } catch (Exception e) {
     System.err.println("Could not open file player.png");
    }
    // save player location
    playerLoc = new Point(x,y);

    // create the game frame
    frame = new JFrame();
    frame.setTitle("EscapeRoom");
    frame.setSize(WIDTH, HEIGHT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.setVisible(true);
    frame.setResizable(false);

    frame.addKeyListener(new MKeyListener());

    scoreLabel = new JLabel("Score: " + score);
    frame.add(scoreLabel, "North");

    // set default config
    totalWalls = 20;
    totalPrizes = 3;
    totalTraps = 5;
  }

 /**
  * After a GameGUI object is created, this method adds the walls, prizes, and traps to the gameboard.
  * Note that traps and prizes may occupy the same location.
  */
  public void createBoard()
  {
    traps = new Rectangle[totalTraps];
    createTraps();
    
    prizes = new Rectangle[totalPrizes];
    createPrizes();

    walls = new Rectangle[totalWalls];
    createWalls();
  }

  /**
   * Increment/decrement the player location by the amount designated.
   * This method checks for bumping into walls and going off the grid,
   * both of which result in a penalty.
   * <P>
   * precondition: amount to move is not larger than the board, otherwise player may appear to disappear
   * postcondition: increases number of steps even if the player did not actually move (e.g. bumping into a wall)
   * <P>
   * @param incrx amount to move player in x direction
   * @param incry amount to move player in y direction
   * @return penalty score for hitting a wall or potentially going off the grid, 0 otherwise
   */
  public int movePlayer(int incrx, int incry) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
      int newX = x + incrx;
      int newY = y + incry;
      AudioPlayer bonk = new AudioPlayer("src/main/resources/invalid.wav");
      
      // increment regardless of whether player really moves
      playerSteps++;

      // check if off grid horizontally and vertically
      if ( (newX < 0 || newX > WIDTH-SPACE_SIZE) || (newY < 0 || newY > HEIGHT-SPACE_SIZE) )
      {
        bonk.play();
        System.out.println ("OFF THE GRID!");
        return -offGridVal;
      }

      // determine if a wall is in the way
      for (Rectangle r: walls)
      {
        // this rect. location
        int startX =  (int)r.getX();
        int endX  =  (int)r.getX() + (int)r.getWidth();
        int startY =  (int)r.getY();
        int endY = (int) r.getY() + (int)r.getHeight();

        // (Note: the following if statements could be written as huge conditional but who wants to look at that!?)
        // moving RIGHT, check to the right
        if ((incrx > 0) && (x <= startX) && (startX <= newX) && (y >= startY) && (y <= endY))
        {
            bonk.play();
          System.out.println("A WALL IS IN THE WAY");
          return -hitWallVal;
        }
        // moving LEFT, check to the left
        else if ((incrx < 0) && (x >= startX) && (startX >= newX) && (y >= startY) && (y <= endY))
        {
            bonk.play();
          System.out.println("A WALL IS IN THE WAY");
          return -hitWallVal;
        }
        // moving DOWN check below
        else if ((incry > 0) && (y <= startY && startY <= newY && x >= startX && x <= endX))
        {
            bonk.play();
          System.out.println("A WALL IS IN THE WAY");
          return -hitWallVal;
        }
        // moving UP check above
        else if ((incry < 0) && (y >= startY) && (startY >= newY) && (x >= startX) && (x <= endX))
        {
            bonk.play();
          System.out.println("A WALL IS IN THE WAY");
          return -hitWallVal;
        }     
      }

      // all is well, move player
      x += incrx;
      y += incry;
      repaint();   
      return 0;   
  }

  /**
   * Check the space adjacent to the player for a trap. The adjacent location is one space away from the player, 
   * designated by newx, newy.
   * <P>
   * precondition: newx and newy must be the amount a player regularly moves, otherwise an existing trap may go undetected
   * <P>
   * @param newx a location indicating the space to the right or left of the player
   * @param newy a location indicating the space above or below the player
   * @return true if the new location has a trap that has not been sprung, false otherwise
   */
  public boolean isTrap(int newx, int newy)
  {
    double px = playerLoc.getX() + newx;
    double py = playerLoc.getY() + newy;


    for (Rectangle r: traps)
    {
      // DEBUG: System.out.println("trapx:" + r.getX() + " trapy:" + r.getY() + "\npx: " + px + " py:" + py);
      // zero size traps have already been sprung, ignore
      if (r.getWidth() > 0)
      {
        // if new location of player has a trap, return true
        if (r.contains(px, py))
        {
          System.out.println("A TRAP IS AHEAD");
          return true;
        }
      }
    }
    // there is no trap where player wants to go
    return false;
  }

  /**
   * Spring the trap. Traps can only be sprung once and attempts to spring
   * a sprung task results in a penalty.
   * <P>
   * precondition: newx and newy must be the amount a player regularly moves, otherwise an existing trap may go unsprung
   * <P>
   * @param newx a location indicating the space to the right or left of the player
   * @param newy a location indicating the space above or below the player
   * @return a positive score if a trap is sprung, otherwise a negative penalty for trying to spring a non-existent trap
   */
  public int springTrap(int newx, int newy)
  {
    double px = playerLoc.getX() + newx;
    double py = playerLoc.getY() + newy;

    // check all traps, some of which may be already sprung
    for (Rectangle r: traps)
    {
      // DEBUG: System.out.println("trapx:" + r.getX() + " trapy:" + r.getY() + "\npx: " + px + " py:" + py);
      if (r.contains(px, py))
      {
        // zero size traps indicate it has been sprung, cannot spring again, so ignore
        if (r.getWidth() > 0)
        {
          r.setSize(0,0);
          System.out.println("TRAP IS SPRUNG!");
          return trapVal;
        }
      }
    }
    // no trap here, penalty
    System.out.println("THERE IS NO TRAP HERE TO SPRING");
    return -trapVal;
  }

  /**
   * Pickup a prize and score points. If no prize is in that location, this results in a penalty.
   * <P>
   * @return positive score if a location had a prize to be picked up, otherwise a negative penalty
   */
  public int pickupPrize() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    double px = playerLoc.getX();
    double py = playerLoc.getY();

    for (Rectangle p: prizes)
    {
      // DEBUG: System.out.println("prizex:" + p.getX() + " prizey:" + p.getY() + "\npx: " + px + " py:" + py);
      // if location has a prize, pick it up
      if (p.getWidth() > 0 && p.contains(px, py))
      {
        AudioPlayer coin = new AudioPlayer("src/main/resources/coin.wav");
        coin.play();

        System.out.println("YOU PICKED UP A PRIZE!");
        p.setSize(0,0);
        repaint();


        return prizeVal;
      }
    }
    System.out.println("OOPS, NO PRIZE HERE");
    return -prizeVal;  
  }

  /**
   * Return the numbers of steps the player has taken.
   * <P>
   * @return the number of steps
   */
  public int getSteps()
  {
    return playerSteps;
  }
  
  /**
   * Set the designated number of prizes in the game.  This can be used to customize the gameboard configuration.
   * <P>
   * precondition p must be a positive, non-zero integer
   * <P>
   * @param p number of prizes to create
   */
  public void setPrizes(int p) 
  {
    totalPrizes = p;
  }
  
  /**
   * Set the designated number of traps in the game. This can be used to customize the gameboard configuration.
   * <P>
   * precondition t must be a positive, non-zero integer
   * <P>
   * @param t number of traps to create
   */
  public void setTraps(int t) 
  {
    totalTraps = t;
  }
  
  /**
   * Set the designated number of walls in the game. This can be used to customize the gameboard configuration.
   * <P>
   * precondition t must be a positive, non-zero integer
   * <P>
   * @param w number of walls to create
   */
  public void setWalls(int w) 
  {
    totalWalls = w;
  }

  /**
   * Reset the board to replay existing game. The method can be called at any time but results in a penalty if called
   * before the player reaches the far right wall.
   * <P>
   * @return positive score for reaching the far right wall, penalty otherwise
   */
  public int replay() throws UnsupportedAudioFileException, LineUnavailableException, IOException {

    int win = playerAtEnd();

    AudioPlayer replay = new AudioPlayer("src/main/resources/restart.wav");
    replay.play();
  
    // resize prizes and traps to "reactivate" them
    for (Rectangle p: prizes)
      p.setSize(SPACE_SIZE/3, SPACE_SIZE/3);
    for (Rectangle t: traps)
      t.setSize(SPACE_SIZE/3, SPACE_SIZE/3);

    // move player to start of board
    x = START_LOC_X;
    y = START_LOC_Y;
    playerSteps = 0;
    score = 0;
    repaint();
    return win;
  }

 /**
  * End the game, checking if the player made it to the far right wall.
  * <P>
  * @return positive score for reaching the far right wall, penalty otherwise
  */
  public int endGame() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    int win = playerAtEnd();
  
    setVisible(false);
    frame.dispose();
    return win;
  }

  /*------------------- public methods not to be called as part of API -------------------*/

  /** 
   * For internal use and should not be called directly: Users graphics buffer to paint board elements.
   */
  public void paintComponent(Graphics g) {

    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;

    // draw grid
    g.drawImage(bgImage, 0, 0, null);
    // add (invisible) traps
    for (Rectangle t : traps)
    {
      g2.setPaint(Color.WHITE); 
      g2.fill(t);
    }

    // add prizes
    for (Rectangle p : prizes)
    {
      // picked up prizes are 0 size so don't render
      if (p.getWidth() > 0) 
      {
      int px = (int)p.getX() - 6;
      int py = (int)p.getY() - 10;
      g.drawImage(prizeImage, px, py, null);
      }
    }

    // add walls
    for (Rectangle r : walls) 
    {
      g2.setPaint(Color.BLACK);
      g2.fill(r);
    }
   
    // draw player, saving its location
    g.drawImage(player, x, y, 40,40, null);
    playerLoc.setLocation(x,y);
  }

  /*------------------- private methods -------------------*/

  /*
   * Add randomly placed prizes to be picked up.
   * Note:  prizes and traps may occupy the same location, with traps hiding prizes
   */
  private void createPrizes()
  {
    int s = SPACE_SIZE; 
    Random rand = new Random();
     for (int numPrizes = 0; numPrizes < totalPrizes; numPrizes++)
     {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);

      Rectangle r;
      r = new Rectangle((w*s + 15),(h*s + 15), 15, 15);
      prizes[numPrizes] = r;
     }
  }

  /*
   * Add randomly placed traps to the board. They will be painted white and appear invisible.
   * Note:  prizes and traps may occupy the same location, with traps hiding prizes
   */
  private void createTraps()
  {
    int s = SPACE_SIZE; 
    Random rand = new Random();
     for (int numTraps = 0; numTraps < totalTraps; numTraps++)
     {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);

      Rectangle r;
      r = new Rectangle((w*s + 15),(h*s + 15), 15, 15);
      traps[numTraps] = r;
     }
  }

  /*
   * Add walls to the board in random locations 
   */
  private void createWalls()
  {
     int s = SPACE_SIZE; 

     Random rand = new Random();
     for (int numWalls = 0; numWalls < totalWalls; numWalls++)
     {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);

      Rectangle r;
       if (rand.nextInt(2) == 0) 
       {
         // vertical wall
         r = new Rectangle((w*s + s - 5),h*s, 8,s);
       }
       else
       {
         /// horizontal
         r = new Rectangle(w*s,(h*s + s - 5), s, 8);
       }
       walls[numWalls] = r;
     }
  }

  /**
   * Checks if player as at the far right of the board 
   * @return positive score for reaching the far right wall, penalty otherwise
   */
  private int playerAtEnd() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    int score;

    double px = playerLoc.getX();
    if (px > (WIDTH - 2*SPACE_SIZE))
    {
        AudioPlayer goal = new AudioPlayer("src/main/resources/goal.wav");
        goal.play();

        try {
            AudioPlayer finalScore = new AudioPlayer("src/main/resources/finalScore.wav");
            finalScore.play();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }


        AudioPlayer win = new AudioPlayer("src/main/resources/012 Course Clear.wav");
        win.play();

      System.out.println("YOU MADE IT!");
      score = endVal;
    }
    else
    {
        AudioPlayer lose = new AudioPlayer("src/main/resources/lose.wav");
        lose.play();
      System.out.println("OOPS, YOU QUIT TOO SOON!");
      score = -endVal;
    }
    return score;
  
  }
}

class AudioPlayer {

    // to store current position
    Long currentFrame;
    Clip clip;

    // current status of clip
    String status;

    AudioInputStream audioInputStream;
    static String filePath;

    // constructor to initialize streams and clip
    public AudioPlayer(String filePath)
            throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        // create AudioInputStream object
        audioInputStream =
                AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());

        // create clip reference
        clip = AudioSystem.getClip();

        // open audioInputStream to the clip
        clip.open(audioInputStream);


    }
    public void play()
    {
        //start the clip
        clip.start();

        status = "play";
    }
}