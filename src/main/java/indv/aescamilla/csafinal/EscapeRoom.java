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
import java.awt.event.KeyEvent;
import java.io.IOException;
import static indv.aescamilla.csafinal.EscapeRoom.*;
import static indv.aescamilla.csafinal.HelperFunctions.print;
import static indv.aescamilla.csafinal.HelperFunctions.sleep;
import static javax.sound.sampled.Clip.LOOP_CONTINUOUSLY;

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
  static int __ = 0;
  ;
  static AudioPlayer background;

  public static void updateScore(int score) { // This method updates the score by adding the score to the current score, and then updates the score label
    game.score += score;
    game.scoreLabel.setText("Score: " + game.score);
  }

  public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
    // welcome message
    print("Welcome to EscapeRoom!");
    print("Get to the other side of the room, avoiding walls and invisible traps,");
    print("pick up all the prizes.\n");

    print("Press 'H' for a list of commands.");
    

    game.createBoard();



    background = new AudioPlayer("resources/013 Underground.wav");
    background.play();
    background.clip.loop(LOOP_CONTINUOUSLY);
    // size of move

    game.addKeyListener(new MKeyListener());

    
    int score = 0;

    // set up game
    String play = "true";
    while (play.equals("true")) // while the play string is true, the game will continue
    {
      /* TODO: get all the commands working */
	  /* Your code here */



      __ ++;  // The requirement never said the increment operation had to impact the program, so it's just a loop counter.
    }


    score += game.endGame();

    System.out.println("score=" + score);
    System.out.println("steps=" + game.getSteps());
  }
}

class MKeyListener extends KeyAdapter {


  @Override
  public void keyPressed(KeyEvent event) {


    switch (event.getKeyCode()) {
      case KeyEvent.VK_Q -> { // if the key was q, quit the game
          int gameEnd = 0;

          background.clip.stop();

          try {
              gameEnd = game.endGame();
          } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
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
      case KeyEvent.VK_D -> {// if the key was d, move right

        if (event.isShiftDown()){ // if the shift key is down, dash
            try {
                updateScore(game.movePlayer(2 * m, 0));
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (event.isAltDown()) { // if the alt key is down, spring a trap

            try {
                updateScore(game.springTrap(m, 0));
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
            try {
                updateScore(game.movePlayer(m, 0));
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }

        }
        else { // otherwise, just move
            try {
                updateScore(game.movePlayer(m, 0));
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
        }
      }
      case KeyEvent.VK_A -> { // if the key was a, move left
            if (event.isShiftDown()){ // if the shift key is down, dash
                try {
                    updateScore(game.movePlayer(-2 * m, 0));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (event.isAltDown()) { // if the alt key is down, spring a trap
                try {
                    updateScore(game.springTrap(-m, 0));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    updateScore(game.movePlayer(-m, 0));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else { // otherwise, just move
                try {
                    updateScore(game.movePlayer(-m, 0));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
      }
      case KeyEvent.VK_W -> { // if the key was w, move up
            if (event.isShiftDown()){ // if the shift key is down, dash
                try {
                    updateScore(game.movePlayer(0, -2 * m));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (event.isAltDown()) { // if the alt key is down, spring a trap
                try {
                    updateScore(game.springTrap(0, -m));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    updateScore(game.movePlayer(0, -m));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else { // otherwise, just move
                try {
                    updateScore(game.movePlayer(0, -m));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
      }
      case KeyEvent.VK_S -> {   // if the key was s, move down
            if (event.isShiftDown()){ // if the shift key is down, dash
                try {
                    updateScore(game.movePlayer(0, 2 * m));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (event.isAltDown()) { // if the alt key is down, spring a trap
                try {
                    updateScore(game.springTrap(0, m));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    updateScore(game.movePlayer(0, m));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {  // otherwise, just move
                try {
                    updateScore(game.movePlayer(0, m));
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
      }
      case KeyEvent.VK_R -> { // if the key was r, reset the game
          try {
              updateScore(game.replay());
          } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
              throw new RuntimeException(e);
          }
          game.score = 0;
      }
      case KeyEvent.VK_SPACE -> { // if the key was space, pickup the prize
          try {
              updateScore(game.pickupPrize());
          } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
              throw new RuntimeException(e);
          }
      }

      case KeyEvent.VK_H -> { // if the key was h, display the help message
          JFrame frame = new JFrame("Help");
          JOptionPane.showMessageDialog(frame, "Use the WASD keys to move. Hold shift to move twice as far.  Hold alt and move to spring a trap. Press space to pick up a prize. Press R to replay the game. Press Q to quit.");
      }
    }
  }
}


        