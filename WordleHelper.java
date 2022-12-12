import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class WordleHelper
{
    public static void main(String[] args) throws IOException
    {
        // Constant variables
        final int CHANCES = 6;
        final int WORD_LENGTH = 5;

        File path = new File("words.txt");

        // Must make sure that the files actually exist before we proceed
        if (!(new File("ListOfWords.java")).exists())
        {
            System.out.println("Error: ListOfWords.java does not exist.");
            return;
        }
        else if (!path.exists())
        {
            System.out.println("Error: words.txt does not exist.");
            return;
        }

        Scanner file = new Scanner(path);
        Scanner userInput = new Scanner(System.in);

        // Utilizes an Abstract Linked List to optimize runtime efficiency
        ListOfWords list = new ListOfWords();

        // Adds every word in "words.txt" to the list
        while (file.hasNext())
            list.add(file.next());

        // "Waiver" to help the player better understand the rules they must follow to not
        // cause any problems the program may run in to
        String waiver = "N";
        while (!waiver.equals("Y"))
        {
            System.out.println("Just to let you know, this is unfortunately not as perfect as intended.");
            System.out.println("It doesn't handle repeating letters too well, and there's one other tiny thing...");
            System.out.println("I need you to use a strict format for inputting your answers!");
            System.out.println("As in this way, everything needs to be FIVE characters long (with dashes).");
            System.out.println("===================================================================");
            System.out.println("Example: the correct answer will be TROPE and my answer is WROTE");
            System.out.println("===================================================================");
            System.out.println("The input for my green letters will be:   \"-RO-E\"");
            System.out.println("The input for my yellow letters will be:  \"---T-\"");
            System.out.println("The input for my black letters will be:   \"W----\"");
            System.out.println("===================================================================");
            System.out.println("The case (lowercase vs. uppercase) doesn\'t matter, so you\'re welcome.");
            System.out.print("Does all of that sound good? (Y/N): ");
            waiver = userInput.nextLine().toUpperCase();
            System.out.println();
            
            if (waiver.equals("Y"))
                System.out.println("Great! Let\'s start!");
            else if (waiver.equals("N"))
                System.out.println("Let me repeat that for you...");
            else
                System.out.println("That answer makes no sense. Just one letter! I'll try again...");

            System.out.println();
        }

        for (int i = 1; i <= CHANCES && list.size() > 0; i++)
        {
            // Give a recommended word based on the other words that remain in the list
            System.out.println("===================================================================");
            System.out.println("GUESS " + i);
            System.out.println("===================================================================");
            System.out.println("This is the recommended word: " + list.getRecommended());
            System.out.println("===================================================================");

            System.out.print("Enter the confirmed letters you received (green): ");
            String green = userInput.nextLine().toLowerCase();
            System.out.println();

            System.out.print("Enter what letters it has to be (yellow): ");
            String yellow = userInput.nextLine().toLowerCase();
            System.out.println();

            System.out.print("Enter what letters it couldn't be (black): ");
            String black = userInput.nextLine().toLowerCase();
            System.out.println();

            // The player needs to enter valid input for the program to continue
            if (green.length() != WORD_LENGTH || 
                yellow.length() != WORD_LENGTH || 
                black.length() != WORD_LENGTH)
            {
                System.out.println("Remember for next time, the input is important!");
                System.out.println("FIVE characters long!");
                return;
            }

            // Remove the words that can no longer be considered because of the response the player received
            list.removeInvalid(green, yellow, black);

            // Print a little message for the player updating the status of the remaining words
            if (list.size() <= 20)
            {
                switch(list.size())
                {
                    case 0:
                        System.out.println("There's no possible words left, which means one of us screwed up.");
                        System.out.println("I hope you had fun though! Appreciate ya!");
                        return;
                    case 1:
                        if (list.head().equalsIgnoreCase(green))
                            System.out.print("Hooray! Excellent job! You found the answer as...: ");
                        else
                            System.out.print("Only one more left! The answer must be...: ");
                        System.out.println(list.head());
                        return;
                    default:
                        System.out.println("You are getting so close, there are only " + list.size() + " possibilities!");
                        System.out.println("Here's the list:");
                        list.print();
                }
            }
            else
            {
                System.out.println("There are still " + list.size() + " words left. Good luck!");
            }
        }
    }
}