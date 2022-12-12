public class ListOfWords
{
    private Node head;
    private Node tail;
    private int size;

    // Constant variables
    private static final int WORD_LENGTH = 5;
    private static final int ALPHABET_SIZE = 26;

    // The Node class should only relate to the list, and therefore
    // it should remain nested in the ListOfWords class
    private class Node
    {
        public String word;
        public Node next;

        public Node(String word)
        {
            this.word = word;
            this.next = null;
        }
    }

    public ListOfWords()
    {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Returns the size of the list
    public int size()
    {
        return this.size;
    }

    // Returns the first word in the list
    public String head()
    {
        return this.head.word.toUpperCase();
    }

    // Adds the word to the back of the list using a tail pointer
    public void add(String word)
    {
        Node tempNode = new Node(word);
        if (this.size == 0)
        {
            this.head = tempNode;
            this.tail = tempNode;
        }
        else
        {
            this.tail.next = tempNode;
            this.tail = tempNode;
        }
        this.size++;
    }

    // Prints the remaining list of valid words
    public void print()
    {
        Node tempNode = this.head;
        while (tempNode != null)
        {
            System.out.println(tempNode.word.toUpperCase());
            tempNode = tempNode.next;
        }
    }

    // Returns the recommended word for the guess with the remaining list of valid words
    public String getRecommended()
    {
        int[] frequencyList = this.getFrequencyList();
        String recommended = null;
        int score = 0;
        Node tempNode = this.head;

        while (tempNode != null)
        {
            int checkRepeating = 0, tempScore = 0;
            for (int i = 0; i < tempNode.word.length(); i++)
            {
                // We can use an integer and bitwise operators to act as an array
                // to make sure no repeated letters are added to the score of the word
                int letterIndex = tempNode.word.charAt(i) - 'a';
                int newLetterIndex = 1 << letterIndex;
                if ((checkRepeating & newLetterIndex) == 0)
                {
                    tempScore += frequencyList[letterIndex];
                    checkRepeating ^= newLetterIndex;
                }
            }

            if (tempScore > score)
            {
                recommended = tempNode.word;
                score = tempScore;
            }

            tempNode = tempNode.next;
        }

        return recommended.toUpperCase();
    }

    // Gets the number of occurences for every letter still in the list to
    // assist in getting the recommended word
    public int[] getFrequencyList()
    {
        int[] frequencyList = new int[ALPHABET_SIZE];
        Node tempNode = this.head;

        while (tempNode != null)
        {
            for (int i = 0; i < tempNode.word.length(); i++)
                frequencyList[(int)(tempNode.word.charAt(i) - 'a')]++;

            tempNode = tempNode.next;
        }

        return frequencyList;
    }

    // Traverses list to remove any words that are now invalid
    // due to the output the player received
    public void removeInvalid(String green, String yellow, String black)
    {
        while (!testWord(head.word, green, yellow, black))
        {
            this.head = this.head.next;
            this.size--;
        }

        Node tempNode = this.head;

        while (tempNode.next != null)
        {
            if (!testWord(tempNode.next.word, green, yellow, black))
            {
                tempNode.next = tempNode.next.next;
                this.size--;
            }
            else
            {
                tempNode = tempNode.next;
            }
        }
    }

    // Tests a word to see if it passes the output the player received
    // TRUE: passes the test, keep in the list
    // FALSE: fails the test, must be removed from list
    private static boolean testWord(String word, String green, String yellow, String black)
    {
        for (int i = 0; i < WORD_LENGTH; i++)
        {
            char wordChar = word.charAt(i);
            char tempChar = green.charAt(i);

            if ((tempChar != '-') && (wordChar != tempChar))
                return false;

            tempChar = yellow.charAt(i);
            if ((tempChar != '-') && ((word.indexOf(tempChar) == -1) || (wordChar == tempChar)))
                return false;
                
            tempChar = black.charAt(i);
            if ((tempChar != '-') && (word.indexOf(tempChar) != -1))
                return false;
        }
        return true;
    }
}