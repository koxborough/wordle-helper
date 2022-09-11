import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class WordleHelper
{
    // Variables to be referenced for later
    public static final int NUM_WORDS = 14855;
    public static final int WORD_LENGTH = 5;
    public static final int ALPHABET_LENGTH = 26;

    public static void main(String [] args) throws IOException
    {
        BufferedReader file;
        Scanner userInput = new Scanner(System.in);
        String [] wordList = new String[NUM_WORDS];
        int numWords = NUM_WORDS;

        // Checks to make sure the word list is in the correct folder
        // So as to not throw an Exception and break the program
        try
        {
            file = new BufferedReader(new FileReader("words.txt"));
        }
        catch (Exception e)
        {
            System.out.println("Hey! You forgot to put \"words.txt\" file in the same folder!");
            return;
        }

        // Read in the entire list of words
        for (int i = 0; i < NUM_WORDS; i++)
        {
            wordList[i] = file.readLine();
        }
        
        // "Waiver" for the user to complete (all in jest)
        String waiver = "N";
        while (!waiver.equals("Y"))
        {
            System.out.println("Just to let you know, this is unfortunately not as perfect as intended.");
            System.out.println("It doesn't handle repeating letters too well, and there's one other tiny thing...");
            System.out.println("I need you to use a strict format for inputting your answers!");
            System.out.println("As in this way, everything needs to be FIVE characters long (with dashes).");
            System.out.println("For example, if I'm inputting my green letters for");
            System.out.println("WROTE and the answer was TROPE, my input should be \"-RO-E\"");
            System.out.println("The case (lowercase vs. uppercase) doesn\'t matter, so you\'re welcome.");
            System.out.print("Does all of that sound good? (Y/N): ");
            waiver = userInput.nextLine().toUpperCase();
            System.out.println();
            
            if (waiver.equals("N"))
            {
                System.out.println("Let me repeat that for you...");
            }
            else if (!waiver.equals("Y"))
            {
                System.out.println("That answer makes no sense. Just one letter! I'll try again...");
            }
            else
            {
                System.out.println("Great! Let\'s start!");
            }
            System.out.println();
        }

        // The loop of each guess (6 in total)
        int iterations = 1;
        do
        {
            System.out.println("===================================================================");
            System.out.println("GUESS " + iterations);
            System.out.println("===================================================================");

            // Gets the recommended word for the guess
            int [] frequencyList = getFrequencyList(wordList);
            String recommended = getRecommended(wordList, frequencyList);

            System.out.println("This is the recommended word: " + recommended.toUpperCase());
            System.out.println("===================================================================");

            // Accepts the response the user got with the green, yellow, 
            // And black letter tiles
            System.out.print("Enter the confirmed letters you received (green): ");
            String confirmed = userInput.nextLine().toLowerCase();
            System.out.println();

            System.out.print("Enter what letters it has to be (yellow): ");
            String alive = userInput.nextLine().toLowerCase();
            System.out.println();

            System.out.print("Enter what letters it couldn't be (black): ");
            String dead = userInput.nextLine().toLowerCase();
            System.out.println();

            // Checks to make sure the input was correctly entered
            if (confirmed.length() != WORD_LENGTH || 
                alive.length() != WORD_LENGTH || 
                dead.length() != WORD_LENGTH)
            {
                System.out.println("Remember for next time, the input is important!");
                System.out.println("FIVE characters long!");
                return;
            }

            // Deletes the words that are now impossible after the guess
            for (int i = 0; i < NUM_WORDS; i++)
            {
                if (wordList[i] == null)
                {
                    continue;
                }
                
                String checking = wordList[i];
                for (int j = 0; j < WORD_LENGTH; j++)
                {
                    // Run checks for each green, yellow, and black letter
                    if (confirmed.charAt(j) != '-')
                    {
                        if (confirmedLetters(checking, j, confirmed.charAt(j), numWords))
                        {
                            wordList[i] = null;
                            numWords--;
                            break;
                        }
                    }
                    if (alive.charAt(j) != '-')
                    {
                        if (aliveLetters(checking, j, alive.charAt(j), numWords))
                        {
                            wordList[i] = null;
                            numWords--;
                            break;
                        }
                    }
                    if (dead.charAt(j) != '-')
                    {
                        if (deadLetters(checking, dead.charAt(j), numWords))
                        {
                            wordList[i] = null;
                            numWords--;
                            break;
                        }
                    }
                }
            }

            // Prints the list of the possible words
            if (numWords <= 20)
            {
                if (numWords == 0)
                {
                    System.out.println("One of us screwed up I guess, but nobody\'s perfect!");
                }
                else if (numWords == 1)
                {
                    if (findLast(wordList).equalsIgnoreCase(confirmed))
                    {
                        System.out.print("Hooray! Excellent job! You found the answer as...: ");
                    }
                    else
                    {
                        System.out.print("Only one more left! The answer must be...: ");
                    }
                    System.out.println(findLast(wordList));
                }
                else
                {
                    System.out.println("You are getting so close, there are only " + numWords + " possibilities!");
                    System.out.println("Here's the list:");
                    printList(wordList);
                }
            }
            else
            {
                System.out.println("There are still " + numWords + " words left. Good luck!");
            }
            iterations++;
        } while (numWords > 1 && iterations < 7);
    }

    // Defines a Frequency List of the entire available words
    // Which counts every single letter in the list
    public static int[] getFrequencyList(String [] wordList)
    {
        int [] frequencyList = new int [ALPHABET_LENGTH];
        for (int i = 0; i < NUM_WORDS; i++)
        {
            if (wordList[i] == null)
            {
                continue;
            }

            String word = wordList[i];
            for (int j = 0; j < WORD_LENGTH; j++)
            {
                frequencyList[(int)(word.charAt(j)) - 97]++;
            }
        }
        return frequencyList;
    }

    // Finds the best available word by using "frequencyList"
    // and adding up the most common letters in each word
    // Returns the String of recommended word 
    public static String getRecommended(String [] wordList, int [] frequencyList)
    {
        String recommended = "";
        int max = 0;

        for (int i = 0; i < NUM_WORDS; i++)
        {
            if (wordList[i] == null)
            {
                continue;
            }

            int sum = 0;
            String word = wordList[i];

            // Makes sure that a repeating letter is not added twice
            int [] checkRepeating = new int [ALPHABET_LENGTH];
            for (int j = 0; j < WORD_LENGTH; j++)
            {
                if (checkRepeating[(int)(word.charAt(j)) - 97] == 0)
                {
                    sum += frequencyList[(int)(word.charAt(j)) - 97];
                    checkRepeating[(int)(word.charAt(j)) - 97] = 1;
                }
            }

            if (sum > max)
            {
                recommended = word;
                max = sum;
            }
        }
        return recommended;
    }

    // Checks to make sure the "green" letter(s) is in the right spot
    // TRUE if there is a problem, FALSE if fine to continue
    public static boolean confirmedLetters(String word, int index, char testChar, int numWords)
    {
        return (word.charAt(index) != testChar);
    }

    // Checks to make sure the "yellow" letters(s) are in the word and not in that spot
    // TRUE if there is a problem, FALSE if fine to continue
    public static boolean aliveLetters(String word, int index, char testChar, int numWords)
    {
        return (!word.contains(String.valueOf(testChar))) || (word.charAt(index) == testChar);
    }

    // Checks to make sure the "black" letter(s) are not in the word
    // TRUE if there is a problem, FALSE if fine to continue
    public static boolean deadLetters(String word, char testChar, int numWords)
    {
        return (word.contains(String.valueOf(testChar)));
    }

    // Prints entire list of words (only is called once there are <= 20 words)
    public static void printList(String [] wordList)
    {
        for (int i = 0; i < NUM_WORDS; i++)
        {
            if (wordList[i] != null)
            {
                System.out.println(wordList[i].toUpperCase());
            }
        }
    }

    // If the user has only one possible guess left (guaranteed a non-null answer)
    // Return String of last word
    public static String findLast(String [] wordList)
    {
        for (int i = 0; i < NUM_WORDS; i++)
        {
            if (wordList[i] != null)
            {
                return wordList[i].toUpperCase();
            }
        }
        return null;
    }
}