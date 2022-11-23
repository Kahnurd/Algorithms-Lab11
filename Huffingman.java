import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Huffingman {

    public Node root;

    public void getInput(String input){
        ArrayList<Node> nodes = new ArrayList<Node>();
        try{
            File file = new File(input);
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                nodes.add(new Node(scan.next().charAt(0), scan.nextDouble()));
              }
            scan.close();
            System.out.println(nodes.toString());
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred");
        }
    }


    /*
     * Each node needs: 
     *  parent node
     *  two child nodes
     *  letter frequency
     *  letter (optional)
     *  binary rep. of node
     */

     public static void main(String[] args) {

        Huffingman potato = new Huffingman();
        potato.getInput("file.txt");
     }
}