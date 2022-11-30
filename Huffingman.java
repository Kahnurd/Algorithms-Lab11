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
 * 
 * @param input
 * @param freqOrBin
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

    public static void HuffingAlgorithm(Node node){
        if(node == null) return;
        if(node.leftChild !=null) node.leftChild.binary = node.binary+"0";
        if(node.rightChild !=null) node.rightChild.binary = node.binary+"1";
        HuffingAlgorithm(node.leftChild);
        HuffingAlgorithm(node.rightChild);
    }

    public void outputList(String outFile){
        try{
        FileWriter writer= new FileWriter(outFile);
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(new compareBinary());
        nodeQueue.addAll(nodeArray);
        Node node;
        for (nodeQueue.addAll(nodeArray); 0 < nodeQueue.size(); node = nodeQueue.poll()) {
            node = nodeQueue.poll();
            writer.write(node.letter + " " + node.binary + "\n");
        }
        writer.close();
    } catch (IOException e) { System.out.println("IOException in the outputList method");}
    }

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

    private String encryptHelper(char currentChar, Node node){
        if(node != null) {
        if(node.letter == currentChar) return node.binary;
        return encryptHelper(currentChar, node.leftChild) + encryptHelper(currentChar, node.rightChild);
        } else return "";
    }

    private String averageLength(){
        double total = 0;
        for (Node node : nodeArray) {
            total += (node.letterFrequency/100) * node.binary.length();
        }
        // Still need this to return a fraction instead of an integer
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