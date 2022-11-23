import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Huffingman {

    public Node root;
    public PriorityQueue<Node> nodes;
    public ArrayList<Node> nodeArray = new ArrayList<Node>();;
    

    public Huffingman(String input, Boolean freqOrBin){

        try{
            File file = new File(input);
            Scanner scan = new Scanner(file);
            if(freqOrBin) {
                while (scan.hasNextLine()) {
                    this.nodeArray.add(new Node(scan.next().charAt(0), scan.nextDouble()));
                }
                this.nodes = new PriorityQueue<>(this.nodeArray);
                createTree();
            } else {
                while (scan.hasNextLine()) {
                    this.nodeArray.add(new Node(scan.next().charAt(0), scan.next()));
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
        while(this.root == null){
            first = nodes.poll();
            second = nodes.poll();
            combinedNode = new Node(first, second);
            nodes.add(combinedNode);
            if(combinedNode.letterFrequency == 100) this.root = combinedNode;
        }
        HuffingAlgorithm(this.root);
    }

    public void createBinaryTree(){
        root = new Node();
        System.out.println(nodeArray);
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

    public static void outputTree(FileWriter writer, Node node){
        if(node == null) return;
        try { 
            if(node.letter != (char)0) writer.write(node.letter + " " + node.binary + "\n");
        }
        catch (IOException e){ System.out.println(e);}
        outputTree(writer, node.leftChild);
        outputTree(writer, node.rightChild);
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
                currNode = root;
            }
        }
        return result;
    }

    public String encrypt(String plainText){
        String result = "";
        char[] text = plainText.toCharArray();
        for (int i = 0; i < text.length; i++) {
            result += encryptHelper(text[i], root);
        }
        return result;
    }

    private String encryptHelper(char currentChar, Node node){
        if(node != null) {
        if(node.letter == currentChar) return node.binary;
        return encryptHelper(currentChar, node.leftChild) + encryptHelper(currentChar, node.rightChild);
        } else return "";
    }

     public static void main(String[] args) {
        
        
        

        try{
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
            FileWriter writer= new FileWriter(outputFile);
            // Outputtree takes the writer for the file and the root node and writes the characters with the 
            // corresponding binary values
            Huffingman.outputTree(writer, potato.root);
            writer.close();


            System.out.println("Encrypt or Decrypt? (e/d)");
            scan.nextLine();
            answer = scan.next().charAt(0);
            if(answer == 'd'){
                System.out.println("Now enter the encrypted text:");
                System.out.println("Decrypted text:");
                while(scan.hasNext()){
                    System.out.println(potato.decrypt(scan.next()));
                }
            } else if (answer == 'e') {
                System.out.println("Now enter some plaintext:");
                while(scan.hasNext()){
                    System.out.println(potato.encrypt(scan.next()));
                }
            } else System.out.println("That's not what I asked for");
            scan.close();
        }
        catch(IOException e) {System.out.println(e);}

        // System.out.println("Encryption: " + potato.encrypt("bad"));
        // System.out.println(potato.decrypt("1001101"));
        // System.out.println(potato.decrypt("1101110011001101"));
        
     }
}