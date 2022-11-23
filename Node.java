public class Node {
    public Node leftChild;
    public Node rightChild;
    public Node parent;

    public double letterFrequency;
    public char letter;
    public String binary;

    public Node(){
        letterFrequency = 100;
    }

    public Node(char character, double freq){
        this.letterFrequency = freq;
        this.letter = character;
    }

    public Node(Node left, Node right){
        this.letterFrequency = left.letterFrequency + right.letterFrequency;
        this.leftChild = left;
        this.rightChild = right;
        left.parent = this;
        right.parent = this;
    }

    public String toString(){
        return this.letter + " " + this.letterFrequency;
    }

}
