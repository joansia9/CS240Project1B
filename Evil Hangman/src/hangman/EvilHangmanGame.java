package hangman;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
    //REMEMBER: left hand is more general than the right hand
    //dictionary words
    SortedSet<String> dictionaryWords = new TreeSet<>();
    //guessed letters
    SortedSet<Character> guessedLetters = new TreeSet<>();
    //patterns
    String pattern;
    //patterns we used
    //TreeSet<String> patternsUsed = new TreeSet<>();
    String prevPattern;

    //constructor that takes no arguments
    public EvilHangmanGame() {}

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        // * Starts a new game of evil hangman using words from <code>dictionary</code>
        //	 * with length <code>wordLength</code>.
        //	 *	<p>
        //	 *	This method should set up everything required to play the game,
        //	 *	but should not actually play the game. (ie. There should not be
        //	 *	a loop to prompt for input from the user.)

        //studentGame.startGame(new File(DICTIONARY), 2);
        //wordlength = 2

        //Assertions.assertThrows(EmptyDictionaryException.class, ()-> studentGame.startGame(new File(SMALL_DICTIONARY), 15), "Failed to throw EmptyDictionaryException.");

        //RESET PREVIOUS GUESS:
        //clear the previously used variables of any values
        guessedLetters.clear();
        dictionaryWords.clear();

        //if first guess
        if (prevPattern == null || pattern == null) {
            pattern = new String(new char[wordLength]).replace('\0', '-'); //a-- ,-> ---
            prevPattern = new String(new char[wordLength]).replace('\0', '-');
        } else {
            prevPattern = Update_Pattern(prevPattern, pattern);
        }

        //make the pattern of the word be filled with dashes instead of the prev patterns
        pattern = new String(new char[wordLength]).replace('\0', '-');

        //is the dictionary empty? (use the largest subset from the last turn!)
        if (dictionary.length() > 0) { //if the dictionary is NOT empty
            //open dictionary, create a scanner, read it
            try (Scanner sc = new Scanner(new FileReader(dictionary))) {

                //CASE 2: if the wordlength is not a valid word length
                if (wordLength < 2) {
                    throw new EmptyDictionaryException("Word length must be greater than 2");
                    //SOLVES: 1 case 2: Assertions.assertThrows(EmptyDictionaryException.class, ()-> studentGame.startGame(new File(DICTIONARY), 1), "Failed to throw EmptyDictionaryException.");
                }

                //while there are more words
                while (sc.hasNext()) {

                    //initialize the currWord to be the next word in the scanner
                    String currWord = sc.next();

                    //assertEquals(68, possibleWords.size(), "Incorrect set size."); //problem right here
                    //if the currword length = the word length we want, add it to the dictionary words
                    if (currWord.length() == wordLength) {
                        dictionaryWords.add(currWord);
                    }

                }

                //CASE 3: empty after going through the dictionary
                if (dictionaryWords.size() == 0) {
                    // 1 case 3 exception: Assertions.assertThrows(EmptyDictionaryException.class, ()-> studentGame.startGame(new File(SMALL_DICTIONARY), 15), "Failed to throw EmptyDictionaryException.");
                    throw new EmptyDictionaryException("no words");
                }

            }
            //CASE 1: already given empty
            //1 case 1 exception: if the dictionary is really empty, throw this exception
        } else {
            throw new EmptyDictionaryException("There are no words in your dictionary");
        } //fixes error of test case 1

    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        try {
            //tools
            TreeMap<String, TreeSet<String>> subsets = new TreeMap<>();
            //this is the subsets (key,value)
            // (a_, <ah,ab,al etc!>) as 1 subset

            char lowerCaseGuess = Character.toLowerCase(guess);

            //if we have guessed before
            if (!guessedLetters.isEmpty()) {
                //CASE 1: if the letter has already been guessed, throw the exception
                if (guessedLetters.contains(Character.toLowerCase(guess))) {
                    throw new GuessAlreadyMadeException("you have already guessed this character\n");
                }
            }
            //we also want to add if it is our first time adding to guessed letters so not in an else statement
            //otherwise, add the letter to the guessed letters set
            guessedLetters.add(lowerCaseGuess);

            //guessing a
            //deleting a as possible answers bc it is the smallest subset
            //String[] correctPossibilities = {"be","bi","bo","by","de","do","ef","eh","el","em","en","er","es","et","ex","go","he","hi","hm","ho","id","if","in","is","it","jo","li","lo","me","mi","mm","mo","mu","my","ne","no","nu","od","oe","of","oh","om","on","op","or","os","ow","ox","oy","pe","pi","re","sh","si","so","ti","to","uh","um","un","up","us","ut","we","wo","xi","xu","ye"};

            //create the subsets
            for (String currWord : dictionaryWords) { //get each word
                //debug
                //System.out.println("dictionaryWords: ");
                //System.out.println(currWord);

                // Save the word as a set to be added as/or to the set in the map
                TreeSet<String> partitionedWords = new TreeSet<>();
                partitionedWords.add(currWord);

                // Get the pattern
                String pattern = Make_Pattern(currWord, lowerCaseGuess);

                //check if the pattern is in the map
                if (!subsets.containsKey(pattern)) { //if not add it
                    subsets.put(pattern, partitionedWords);
                } else{ //if yes, add the word to the corresponding group
                    //Solved problem: the ".addAll(partitionedWords)" syntax adds the words to the specific subsets
                    subsets.get(pattern).addAll(partitionedWords);
                }
            }

            //finding the largest subset and returning it as the new word set for the next round!
            Map.Entry<String, TreeSet<String>> largestSubset = null; //what will be inputted and updated to be outputed
            //ALGORITHM SOLUTION: use "Map.Entry<String, TreeSet<String>> largestSubset instead of TreeMap<String> because...
            //this gives us access to the key (pattern) -> <String> and its values (words in those subset) -> TreeSet<String>
            //a piece from map<String, treeSet<String>> subsets;

            //debugging
            //System.out.println("\n\ncurrent guess: " + lowerCaseGuess + "\n");

            // 8.@DisplayName("Largest Group Test")
            //CASE 1: choose the largest
            for(Map.Entry<String, TreeSet<String>> entry : subsets.entrySet()) { //go through the subset
                if(largestSubset == null || entry.getValue().size() > largestSubset.getValue().size()){
                    largestSubset = entry;
                    //debug
                    //System.out.println("largest subset is: " + largestSubset.getKey().toString() + " because it is the largest out of all of them (no checks needed)\n");
                }

                //TODO: tie breaker algorithm (if the sizes are the same)
                //debug
                //System.out.println("entering checks: \n");

                // CASE 2: If there are 2 of the same size subsets, apply the tiebreaker algorithm
                if(entry.getValue().size() == largestSubset.getValue().size()) { //if the one we are comparing = the largestSubset
                    //DEBUG
                    //System.out.println("comparing: " + entry.getKey() + entry.getValue().toString() + " and " + largestSubset.getKey() + largestSubset.getValue().toString() + " because they have the same size \n");

                    //9. @DisplayName("Letter Does Not Appear Test")
                    //CASE 2A: no instance of the character
                    if(!entry.getKey().contains(Character.toString(lowerCaseGuess))) { //if it does not contain guess char
                        largestSubset = entry;
                        //debug
                        //System.out.print(largestSubset.getKey().toString() + " is the largest bc guessed letter does not appear\n");

                    //CASE 2B: If every pattern contains that character
                    } else if(entry.getKey().contains(Character.toString(lowerCaseGuess))
                            && largestSubset.getKey().contains(Character.toString(lowerCaseGuess))) {

                        //10. DisplayName("Pattern With Fewest Instances Test")
                        // Case 2BA: least occurrences of that character
                        long countA = entry.getKey().chars().filter(ch -> ch == lowerCaseGuess).count();
                        long countB = largestSubset.getKey().chars().filter(ch -> ch == lowerCaseGuess).count();
                            //thanks elaine!
                            //SYNTAX: Calling filter(ch -> ch == lowerCaseGuess) on the IntStream to get a stream of characters that match the character represented by the lowerCaseGuess variable.
                            //SYNTAX: Calling count() on the resulting IntStream to get the number of characters that match the lowerCaseGuess character.
                            //advice: using long instead of int bc the code ensures that it can handle a large number of occurrences of the lowerCaseGuess character in the String keys, without running into integer overflow issues.
                        if (countA < countB){
                            largestSubset = entry;
                            //debug
                            //System.out.print(largestSubset.getKey().toString() + " is the largest bc it has the least occurrences of the guessed character\n");

                        //11.@DisplayName("Pattern With Rightmost Instances Test")
                        //12. @DisplayName("Rightmost Instance of Multiple Instances Test")
                        // CASE 2BB: If they have the same amount of occurrences
                        } else if (countA == countB) {
                            // Case 2BBA: Choose the one with the rightmost guessed letter
                            if (entry.getKey().lastIndexOf(lowerCaseGuess) > largestSubset.getKey().lastIndexOf(lowerCaseGuess))
                                largestSubset = entry;
                                //debug
                                //System.out.print(largestSubset.getKey().toString() + " is the largest bc it has the right most occurrence of the guessed character\n");
                        }
                    }
                    //debug:
                    //System.out.println("\nafter frequency and right occurrence checks, the largest subset is: " + largestSubset.getKey().toString() + largestSubset.getValue().toString() + "\n");
                }
            }
            //debugging (subsets made)
            /*
            System.out.println("Subsets made:\n");
            for (Map.Entry<String, TreeSet<String>> entry : subsets.entrySet()) {
                System.out.println("Pattern: " + entry.getKey());
                System.out.println("Words: " + entry.getValue());
            }
            System.out.println("\nlargest Subset is: " + largestSubset);
             */


            //dictionaryWords = largestSubset; //DEBUG: fixes the bug of saving the last largestSubset after second guess

            // Using the largest entry from our map
            // assert largestSubset != null;

            //update the dictionary to be the largest set
            dictionaryWords = largestSubset.getValue();
            pattern = largestSubset.getKey();

            //FIXME: this is causing my out of bounds error
            prevPattern = Update_Pattern(prevPattern, pattern);

            //debug
            //System.out.println("patternsUsed adding " + pattern.toString());

            //the clear function in the startGame function fixes it but it is assigned to dictionaryWords
            return dictionaryWords;

            /*
            error fixed! we must return everything that has been added! (before exception state bc exception returns)
            return largestSubset;
            */


        } catch (GuessAlreadyMadeException error) {
            throw new GuessAlreadyMadeException("you already guessed this character\n");
        }
    }


    @Override
    public SortedSet<Character> getGuessedLetters() {
        //* Returns the set of previously guessed letters, in alphabetical order.
        return guessedLetters;
    }

    //helper function

    //remember: makes the pattern
    public String Make_Pattern(String currWord, char guess) {
        //test
        //studentGame.startGame(new File(DICTIONARY), 2);
        //studentGame.makeGuess('a');
        //should make subsets: _,_ ; a,_ ; _,a; a,a

        char[] currWordCharArray = currWord.toCharArray(); //create an array of the letters in that word
        StringBuilder subsetPattern = new StringBuilder(); //output the subset pattern
            //syntax: we're using StringBuilder because we want to manipulate the string
                //remember String is immutable (cannot be changed once declared) but StringBuilder can be changed after created

        //4. @DisplayName("Guess Already Made Test")
        //studentGame.startGame(new File(DICTIONARY), 2);
        //studentGame.makeGuess('a');

        //iterate through the word as an array of characters
        for (char currChar : currWordCharArray) { //ab
            //if the character is not what is guessed, make it a dash
            if (currChar != guess) {
                currChar = '-'; //a,-
            }
            subsetPattern.append(currChar); //a,- goes into the subsetPattern
            //remember, StringBuilder needed here!
        }
        return subsetPattern.toString(); //a,- (array of chars) becomes a- (a String)
    }

    public String Get_Pattern() {
        return pattern;
    }

    public SortedSet<Character> Get_GuessedLetters(){
        return guessedLetters;
    }

    public String Update_Pattern(String prevPattern, String currPattern) { //st__ __y_
        if (prevPattern.length() == currPattern.length()) {
            char[] combinedPattern = new char[prevPattern.length()];
            char[] prevPatternArray = prevPattern.toCharArray();
            char[] currPatternArray = currPattern.toCharArray();
            int index = 0;

            for (int i = 0; i < prevPatternArray.length; i++) {
                char currPrevPatternChar = prevPatternArray[i];
                char currPatternArrayChar;

                if (i < currPatternArray.length) {
                    currPatternArrayChar = currPatternArray[i];
                } else {
                    currPatternArrayChar = '-';
                }

                if (currPrevPatternChar != '-') {
                    combinedPattern[index] = currPrevPatternChar;
                } else {
                    combinedPattern[index] = currPatternArrayChar;
                }
                index++;
            }

            for (int i = prevPatternArray.length; i < currPatternArray.length; i++) {
                char currPatternArrayChar = currPatternArray[i];

                if (currPatternArrayChar != '-') {
                    combinedPattern[index] = currPatternArrayChar;
                }
                index++;
            }
            return new String(combinedPattern);
        }
        return null;
    }

}



