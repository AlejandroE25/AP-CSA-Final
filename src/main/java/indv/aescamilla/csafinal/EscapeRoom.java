package indv.aescamilla.csafinal;
/*
* Problem 1: Escape Room
* 
* V1.0
* 10/10/2019
* Copyright(c) 2019 PLTW to present. All rights reserved
*/
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.util.Scanner;
import java.awt.event.KeyEvent;


import static indv.aescamilla.csafinal.EscapeRoom.*;
import static indv.aescamilla.csafinal.HelperFunctions.*;

/**
 * Create an escape room game where the player must navigate
 * to the other side of the screen in the fewest steps, while
 * avoiding obstacles and collecting prizes.
 */
public class EscapeRoom
{

      // describe the game with brief welcome message
      // determine the size (length and width) a player must move to stay within the grid markings
      // Allow game commands:
      //    right, left, up, down: if you try to go off grid or bump into wall, score decreases
      //    jump over 1 space: you cannot jump over walls
      //    if you land on a trap, spring a trap to increase score: you must first check if there is a trap, if none exists, penalty
      //    pick up prize: score increases, if there is no prize, penalty
      //    help: display all possible commands
      //    end: reach the far right wall, score increase, game ends, if game ended without reaching far right wall, penalty
      //    replay: shows number of player steps and resets the board, you or another player can play the same board
      // Note that you must adjust the score with any method that returns a score
      // Optional: create a custom image for your player use the file player.png on disk
    
      /**** provided code:
      // set up the game
      boolean play = true;
      while (play)
      {
        // get user input and call game methods to play 
        play = false;
      }
      */
      static GameGUI game;

    static {
        try {
            game = new GameGUI();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    static int m = 60;
  // individual player moves
  static int px = 0;
  static int py = 0;

  static AudioPlayer background;

  public static void updateScore(int score) {
    game.score += score;
    game.scoreLabel.setText("Score: " + game.score);
  }

  public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    // welcome message
    System.out.println("Welcome to EscapeRoom!");
    System.out.println("Get to the other side of the room, avoiding walls and invisible traps,");
    System.out.println("pick up all the prizes.\n");
    

    game.createBoard();

    background = new AudioPlayer("src/main/resources/013 Underground.wav");
    background.play();
    // size of move

    game.addKeyListener(new MKeyListener());

    
    int score = 0;

    Scanner in = new Scanner(System.in);
    String[] validCommands = { "right", "left", "up", "down", "r", "l", "u", "d",
    "jump", "jr", "jumpleft", "jl", "jumpup", "ju", "jumpdown", "jd",
    "pickup", "p", "quit", "q", "replay", "help", "?"};
  


    // set up game
    boolean play = true;
    while (play)
    {
      /* TODO: get all the commands working */
	  /* Your code here */



      
    }

  

    score += game.endGame();

    System.out.println("score=" + score);
    System.out.println("steps=" + game.getSteps());
  }
}

class MKeyListener extends KeyAdapter {

  @Override
  public void keyPressed(KeyEvent event) {

    char ch = event.getKeyChar();

    switch (event.getKeyCode()) {
      case KeyEvent.VK_Q -> {
          int gameEnd = 0;

          background.clip.stop();

          try {
              gameEnd = game.endGame();
          } catch (UnsupportedAudioFileException e) {
              throw new RuntimeException(e);
          } catch (LineUnavailableException e) {
              throw new RuntimeException(e);
          } catch (IOException e) {
              throw new RuntimeException(e);
          }
          updateScore(gameEnd);
        JFrame frame = new JFrame("Final Score");
        String endStatement;
        if (gameEnd > 0) {
            endStatement = "You won! Your final score is " + game.score;
            } else {
            endStatement = "You lost! Your final score is " + game.score;
        }

          JOptionPane.showMessageDialog(frame, endStatement);
        sleep(1);
        System.exit(0);
      }
      case KeyEvent.VK_D -> {
        if (event.isShiftDown()){
            try {
                updateScore(game.movePlayer(2 * m, 0));
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                updateScore(game.movePlayer(m, 0));
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
      }
      case KeyEvent.VK_A -> {
            if (event.isShiftDown()){
                try {
                    updateScore(game.movePlayer(-2 * m, 0));
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    updateScore(game.movePlayer(-m, 0));
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
      }
      case KeyEvent.VK_W -> {
            if (event.isShiftDown()){
                try {
                    updateScore(game.movePlayer(0, -2 * m));
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    updateScore(game.movePlayer(0, -m));
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
      }
      case KeyEvent.VK_S -> {
            if (event.isShiftDown()){
                try {
                    updateScore(game.movePlayer(0, 2 * m));
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    updateScore(game.movePlayer(0, m));
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
      }
      case KeyEvent.VK_R -> {
          try {
              updateScore(game.replay());
          } catch (UnsupportedAudioFileException e) {
              throw new RuntimeException(e);
          } catch (LineUnavailableException e) {
              throw new RuntimeException(e);
          } catch (IOException e) {
              throw new RuntimeException(e);
          }
      }
      case KeyEvent.VK_SPACE -> {
          try {
              updateScore(game.pickupPrize());
          } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
              throw new RuntimeException(e);
          }
      }
    }

    if (event.getKeyCode() == KeyEvent.VK_HOME) {

      System.out.println("Key codes: " + event.getKeyCode());

    }
  }
}


        