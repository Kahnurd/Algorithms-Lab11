import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Huffingman {

    public Node root;
    public PriorityQueue<Node> nodes;
    

    public void getInput(String input){
        ArrayList<Node> nodeArray = new ArrayList<Node>();

        try{
            File file = new File(input);
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                nodeArray.add(new Node(scan.next().charAt(0), scan.nextDouble()));
              }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred");
        }
        this.nodes = new PriorityQueue<>(nodeArray);
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

     public static void main(String[] args) {
        Huffingman potato = new Huffingman();
        
        

        try{
            System.out.println("Give the name of the input file and then the name of the outputfile, separated by a space.");
            // Scanner for system in
            Scanner scan = new Scanner(System.in);
            // First input from the scanner is given to the method getInput
            potato.getInput(scan.next());
            // Second input from the scanner is given to the fileWriter
            FileWriter writer= new FileWriter(scan.next());
            // createTree builds a tree, using the algorithm given from the assignment writeup
            // So it starts with the smallest leaf nodes and slowly combines them until it reaches 100 frequency
            potato.createTree();
            // Outputtree takes the writer for the file and the root node and writes the characters with the 
            // corresponding binary values
            Huffingman.outputTree(writer, potato.root);
            scan.close();
            writer.close();
        }
        catch(IOException e) {System.out.println(e);}
        
     }
}