import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Huffingman {

    public Node root;
    public ArrayList<Node> nodeArray = new ArrayList<Node>();;
    
/**
 * This function is responsible for reading the input file and creating a node array that gets used
 * in the function createTree and createBinaryTree. After creating the ArrayList of nodes, it calls
 * createTree() or createBinaryTree() depending on the boolean value.
 * @param input Takes the name of a file
 * @param freqOrBin Takes a boolean value, true means the input has the frequencies of the letters
 * false means the input has the binary representations.
 */
    public Huffingman(String input, Boolean freqOrBin){
        try{
            File file = new File(input);  
            Scanner scan = new Scanner(file);
            if(freqOrBin) {
                while (scan.hasNextLine()) {
                    this.nodeArray.add(new Node(scan.next().charAt(0), scan.nextDouble()));
                }
                createTree();
            } else {
                while (scan.hasNextLine()) {
                    this.nodeArray.add(new Node(scan.next().charAt(0), scan.next()));
                    scan.nextLine();
                }
                createBinaryTree();
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred");
        }
    }

    /**
     * createTree has no input, but uses the ArrayList of nodes made by the Huffingman constructor.
     * It takes the ArrayList and creates a PriorityQueue of the nodes. Then it loops through taking
     * the two letters with the lowest frequencies (first two in queue) and combines them by making a parent
     * node with a frequency equal to the sum of the two given frequencies, then it makes the two given nodes children
     * of the new node. It does this over and over again until it creates a node with a frequency value of 100%, then
     * it makes that node the root node. Then it sends the root node to the HuffingAlgorithm method
     */
    public void createTree(){
        Node first;
        Node second;
        Node combinedNode;
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(new compareFrequency());
        nodeQueue.addAll(nodeArray);
        while(this.root == null){
            first = nodeQueue.poll();
            second = nodeQueue.poll();
            combinedNode = new Node(first, second);
            nodeQueue.add(combinedNode);
            if(combinedNode.letterFrequency == 100) this.root = combinedNode;
        }
        HuffingAlgorithm(this.root);
    }

    public void createBinaryTree(){
        root = new Node();
        for (Node node : nodeArray) {
            Node current = root;
            char[] binArray = node.binary.toCharArray();
            for (int i = 0; i < binArray.length; i++) {
                if(binArray[i] == '0') {
                    if(i == binArray.length-1) {
                        current.leftChild = node;
                    } else {
                        if(current.leftChild == null)current.leftChild = new Node();
                        current = current.leftChild;
                    }
                } else {
                    if(i == binArray.length-1) {
                        current.rightChild = node;
                    } else {
                        if(current.rightChild == null)current.rightChild = new Node();
                        current = current.rightChild;
                    }
                }
            }

        }

    }

    /**
     * This function takes the root node of the tree created by createTree(). It recursively
     * explores the tree adding a string value to each node's binary representation. It adds the string
     * 0 to every left child's binary representation and 1 to every right child's binary representation.
     * This hits every node in the tree and accurately gives each letter their binary representation according to
     * the Huffing Algorithm
     * @param node
     */
    public static void HuffingAlgorithm(Node node){
        if(node == null) return;
        if(node.leftChild !=null) node.leftChild.binary = node.binary+"0";
        if(node.rightChild !=null) node.rightChild.binary = node.binary+"1";
        HuffingAlgorithm(node.leftChild);
        HuffingAlgorithm(node.rightChild);
    }

    /**
     * This function takes the name of the output file and outputs each letter with its binary representation.
     * It throws the ArrayList of nodes into a priority queue that organizes it by length of binary representation.
     * Then it writes each node letter next to its binary represention in that order.
     * @param outFile Takes the name of the output file
     */
    public void outputList(String outFile){
        try{
        FileWriter writer= new FileWriter(outFile);
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(new compareBinary());
        Node node;
        nodeQueue.addAll(nodeArray);
        for (int i = nodeQueue.size(); 0 < i; i--) {
            System.out.println(nodeQueue);
            System.out.println(nodeQueue.size());
            node = nodeQueue.poll();
            writer.write(node.letter + " " + node.binary + "\n");
        }
        writer.close();
    } catch (IOException e) { System.out.println("IOException in the outputList method");}
    }

    /**
     * This function takes the encrypted text and turns it into the original characters.
     * It starts at the root node, and iterates through by following the binary given.
     * If the current character is a 1 it goes to the right and if it is a 0 it goes to the left.
     * It does this until it hits a leaf node (a node that has a character value). Once it finds one, it adds
     * it to the result string and starts back at the root continuing the process until it runs out of characters in the given string.
     * At the end it outputs the accumulated result string.
     * @param cryptText Takes text encrypted by the same Huffing tree as this instance
     * @return Returns the decrypted text with no spaces or puncuation.
     */
    public String decrypt(String cryptText){
        char[] text = cryptText.toCharArray();
        Node currNode = this.root;
        String result = "";
        for (int i = 0; i < text.length; i++) {
            if(text[i] == '1') {
                currNode = currNode.rightChild;
            } else {
                currNode = currNode.leftChild;
            }
            if(currNode.letter != (char)0) {
                result += currNode.letter; 
                if(result.length()%70 == 69) result += "\n";
                currNode = root;
            }
        }
        return result;
    }

    /**
     * This function takes some plaintext that is assumed to have the characters in the Huffing Tree and
     * converts it to their binary represntation without any spaces. Every 70 characters it will also
     * add a \n to make it more readable. The function ends when it runs out of characters to convert.
     * 
     * @param plainText Takes some plaintext without any spaces or special characters.
     * If a character in the plaintext doesn't exist in the Huffing Tree it won't print anything
     * for that character
     * @return Returns the encrypted text in 1's and 0's
     */
    public String encrypt(String plainText){
        String result = "";
        int counter = 0;
        char[] text = plainText.toCharArray();
        for (int i = 0; i < text.length; i++) {
            String encryptedChar = encryptHelper(text[i], root);
            result += encryptedChar;
            counter += encryptedChar.length();
            if(counter > 70){
                counter = 0;
                result += "\n";
            }
        }
        return result;
    }

    /**
     * This function takes information from the encrypt() method, like the letter to be encrypted
     * and the root node of the Huffing Tree. It visits every node in the tree and adds all the returned
     * strings together. The only time the function returns an actual string is when it finds the node with
     * the matching character. So at the end it should only have the correct binary. 
     * @param currentChar Takes the character that is to be encrypted
     * @param node Takes the root node of the tree used to encrypt the text
     * @return returns the binary representation of the given character
     */
    private String encryptHelper(char currentChar, Node node){
        if(node != null) {
        if(node.letter == currentChar) return node.binary;
        return encryptHelper(currentChar, node.leftChild) + encryptHelper(currentChar, node.rightChild);
        } else return "";
    }

    /**
     * This function finds the average binary length of the current tree in this instance. 
     * It goes through every node in the ArrayList of nodes and sums their binary length multiplied
     * by their frequency. 
     * @return returns the average binary length
     */
    private String averageLength(){
        double total = 0;
        for (Node node : nodeArray) {
            total += (node.letterFrequency/100) * node.binary.length();
        }
        return total + "";
    }

     public static void main(String[] args) {
            System.out.println("Give the name of the input file and then the name of the outputfile, separated by a space.");
            // Scanner for system in
            Scanner scan = new Scanner(System.in);
            String inputFile = scan.next();
            String outputFile = scan.next();

            System.out.println("Specify if the file has the binary or frequency of given characters,");
            System.out.println("write b for binary or f for frequency.");
            scan.nextLine();
            char answer = scan.next().charAt(0);
            Boolean freqOrBin = true;
            if(answer == 'b') freqOrBin = false;
            if(answer == 'f') freqOrBin = true;


            // Construct the Huffingman object with the file input
            Huffingman potato = new Huffingman(inputFile, freqOrBin);
            // Second input from the scanner is given to the fileWriter
            
            // Takes a given writer, and uses it to output 
            potato.outputList(outputFile);
            if(answer == 'f') System.out.println("Average length of binary representations: " + potato.averageLength());


            System.out.println("Encrypt or Decrypt? (e/d)");
            scan.nextLine();
            answer = scan.next().charAt(0);
            if(answer == 'd'){
                System.out.println("Now enter the encrypted text:");
                System.out.println("Decrypted text:");
                String encryptedText = "";
                while(scan.hasNext()){
                    encryptedText += scan.next();
                }
                System.out.println(potato.decrypt(encryptedText));
            } else if (answer == 'e') {
                System.out.println("Now enter some plaintext:");
                String plainText = "";
                while(scan.hasNext()){
                    plainText += scan.next();
                }
                String cleanText = plainText.replaceAll("\\W|[0-9]|_", "");
                System.out.println(potato.encrypt(cleanText.toLowerCase()));
            } else System.out.println("That's not what I asked for");
            scan.close();
     }
}