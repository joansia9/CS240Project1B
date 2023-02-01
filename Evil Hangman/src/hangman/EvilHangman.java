package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner; //fixes the user input bug

public class EvilHangman {

    public static void main(String[] args) { //passing array of strings as arguments
        //main method of the function that implements a game of Evil Hangman

        //check length of arguments to make sure we have enough arguments
        if (args.length < 3) {
            System.out.println("not enough arguments");
            return;
        }
        if(args.length > 3 ) {
            System.out.println("too many arguments");
            return;
        }

        //3 command line arguments
        /*
            //1. the path to a file that contains a list of words
            //2. the desired length of the word to be guessed
            //3. the number of guesses the player is allowed
         */

        //initialize the 3 arguments
        File myFile = new File(args[0]);
            //the path to a file that contains a list of words
        int wordLength = Integer.parseInt(args[1]);
            //parsing the string value stored at index 1 of the args array as word length (turned to int)
        int numGuess = Integer.parseInt(args[2]);
            //parsing the string value stored at index 2 of the args array as # of guesses (turned to int)

        //DEBUG to test if files are loading in:
        /*
        System.out.println("arg1: " + wordLength);
        System.out.println("arg2: " + numGuess);
        System.out.println("file: " + myFile);
         */

        //check if the word length and number of guesses are valid and exits program if it is not valid
        if (wordLength < 2) { //if the word length is less
            System.out.println("Word length must be greater than 2");
            //throw new EmptyDictionaryException("Word length must be greater than 2");
            return;
        }
        if (numGuess < 1) { //if the num of guesses is less than 1
            System.out.println("Number of guesses must be greater than 1");
            return;
        }

        //create an instance of the EvilHangmanGame class
        EvilHangmanGame game = new EvilHangmanGame();

        //try to start the game and catch 2 exceptions! (IO and EmptyDictionary)
        try{
            //try to start the game
            game.startGame(myFile, wordLength);
        } catch (IOException error) {
            //IOException: is a standard Java exception that is thrown when an input/output operation fails
                //ex: file not being found, a network connection being lost, or permision issues
                //in the case of this code: blocks when the game is attempting to start with the dictionary given by the user and the word length
            System.out.println(error.getMessage()); //printing the error message
            //return;
        } catch (EmptyDictionaryException error) {
            //EmptyDictionaryException: a custom exception that is thrown when there are no words in the dictionary with the specified length
                //in the case of this code: the dictionary is empty
            System.out.println(error.getMessage()); //printing the error message
            //return;
        }


        // while the user still has guesses left, allow them to guess
        while(numGuess > 0 ) {
            // show game stats each time the player can make a guess
            Show_Game_Stats(numGuess, game);

            for (Character currChar : game.prevPattern.toCharArray()) {
                if (!game.prevPattern.contains(Character.toString('-'))){
                    System.out.println("you win! You guessed the word: " + game.dictionaryWords.first() + "\n");
                    return;
                }
            }

            try { // Try to make a guess

                //visual:
                char guess = Get_User_Input(); //helper function to get the users guess
                game.makeGuess(guess);

                numGuess--; // Decrement the number of guesses every time user guesses

                // CASE 1: if guess is not found in word, say "Sorry, there are no <letter>'s"
                if(!game.pattern.contains(Character.toString(guess))) {
                    System.out.printf("Sorry, there are no " + guess + "'s \n\n");

                //CASE 2: if the guess is found in the word, say "Yes, there is/are # <letter>"
                } else {
                    //A correct guess does not decrement the number of guesses you have left
                    numGuess++; //reverts the decrement

                    //GRAMMAR SOLUTION: is/are
                    int charCount = 0;
                    String areOrIs =  "are"; //plural: multiple spots in pattern
                    for(char currChar : game.pattern.toCharArray()) {
                        if(currChar == guess) {
                            charCount++;
                        }
                    }
                    //singular: only 1 spot in the pattern
                    if(charCount < 2){
                        areOrIs = "is";
                    }

                    //Yes, there 'are/is' '#' 'guessed character'
                    System.out.printf("Yes, there " + areOrIs + " " + charCount+ " " + guess + "\n\n");
                }
            } catch (GuessAlreadyMadeException error) {
                System.out.print("Guess already made! \n\n");
            }
            //losing when we ran out of guesses
            if (numGuess == 0 && game.prevPattern.contains(Character.toString('-'))) {
                System.out.println("You lose! The guessed word: " + game.dictionaryWords.first() + "\n");
                return;
            }
            //winning when we ran out of guesses
            if (numGuess == 0 && !game.prevPattern.contains(Character.toString('-'))) {
                System.out.println("you win! You guessed the word: " + game.dictionaryWords.first() + "\n");
                return;
            }
        }
    }

    //helper functions
    public static char Get_User_Input() {
        //tools
        char guess = ' ';
        String userLine;

        //we have to make sure the user's input is a letter and not a space!
        while (!Character.isAlphabetic(guess) || Character.isSpaceChar(guess)) {

            System.out.print("Enter guess: ");
            //reads the user's input using a Scanner object and stores it in userLine
                userLine = new Scanner(System.in).nextLine(); //syntax: system.in is used to input

                //if userInput is more than 1 and does not have whitespacs

                //if the user's input is empty or blank statement (invalid)
                if (userLine.isEmpty() || userLine.isBlank()) {
                    //throw new Exception("Invalid input");
                    System.out.println("Invalid input");
                } else { //valid arguments
                    guess = userLine.charAt(0);
                }
                //If userInput is not an alphabetical character, prints "Invalid input\n".
                if (!Character.isAlphabetic(guess)) {
                    //throw new Exception("Invalid input");
                    System.out.println("Invalid input");
                }
        }
        return Character.toLowerCase(guess);
    }

    public static void Show_Game_Stats(int numGuess, EvilHangmanGame game) {
        //STATS:
        //print remaining guesses
        //You have # guesses left
        System.out.println("You have " + numGuess + " guesses left");
        //print letters guessed in alphabetical order (in makeGuess)
        //Used letters: a b c etc.
        System.out.print("Used letters: ");

        //if you have guessed before, print the guessed letters
        if(!game.guessedLetters.isEmpty()) { //if guessed letters is not empty
            System.out.print("[");
            for(Character currLetter : game.guessedLetters) { //print each one
                System.out.printf(currLetter + " "); //REMEMBER: make sure there is a space in between them
            }
            System.out.print("]");
        } else {
            System.out.print("[]");
        }

        //WORD:
        System.out.println();
        //FIXME: why is the pattern not showing up correctly?
        //adding "game.prevPattern = game.Update_Pattern(game.prevPattern, game.pattern);" here is BAD programming because...
            //changing a value in a print function
            //this should happen in the evilHangmanGame not in the main function
        System.out.printf("Word: " + game.prevPattern + '\n');
    }

}
