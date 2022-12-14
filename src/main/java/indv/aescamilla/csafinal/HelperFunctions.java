package indv.aescamilla.csafinal;

import java.util.Scanner;

class HelperFunctions {

  public static void print(Object... args){ //I made my own print function.  Because java is special.
    for (Object obj : args){
      System.out.print(obj + " ");
    }
    System.out.print("\n");
  }

  public static String input(String question){ // I made my own input function.  Because java is special.
    print(question);
    Scanner readLn = new Scanner(System.in);
    String input = readLn.nextLine();
    // Intentionally didn't close the reader.  Because Java is special
    return input;
  }

  public static void sleep(int secondsToSleep){
    try {
    Thread.sleep(secondsToSleep * 1000L);
    }
    catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }

  public static void cls(){
    System.out.print("\033[H\033[2J");  
    System.out.flush(); 
  }
}