import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author josefiina
 *
 */

public class Node {
    private double x;
    private double y;
    private List<Node> adjacent;
    private List<Edge> edgesToNode;
    private List<Edge> edgesFromNode;
    private boolean visited;
    
    public Node(double x, double y){
        this.x=x;
        this.y=y;
        this.adjacent = new ArrayList<Node>();
        this.edgesToNode = new ArrayList<Edge>();
        this.edgesFromNode = new ArrayList<Edge>();
        this.visited=false;
    }
    
    public void visit() { this.visited=true; }
    
    public void unvisit() { this.visited=false; }
    
    public boolean visited() { return this.visited; }
    
    public double gety() { return this.y; }
    
    public double getx() { return this.x; }
    
    public void addIncomingEdge(Edge e) { this.edgesToNode.add(e); }
    
    public void addLeavingEdge(Edge e) { this.edgesFromNode.add(e); }
    
    public void addAdjacent(Node a) { this.adjacent.add(a); }
    
    public List<Node> getAdjacent() { return this.adjacent; }
    
    //poistaa sekä noden että siihen liittyvän edgen
    public void removeAdjacent(int index) {
        Node n = this.adjacent.get(index);
        this.adjacent.remove(n);
        Edge e = this.edgesFromNode.get(index);
        this.edgesFromNode.remove(e);
    }
    
    public List getEdgesFromNode() { return this.edgesFromNode; }
    
    public int getInDegree() { return this.edgesToNode.size(); }
    
    public int getOutDegree() { return this.edgesFromNode.size(); }
    
    public void clearLists() {
        this.adjacent.clear();
        this.edgesFromNode.clear();
        this.edgesToNode.clear();
    }
    
    public void printAdj(){
        for(int i=0; i<this.adjacent.size(); i++){
            System.out.print(this.adjacent.get(i));
            System.out.println(" " + this.edgesFromNode.get(i));
        }
    }
    
    @Override
    public boolean equals(Object other) {
        Node n = (Node) other;
        return this.getx() == n.getx() && this.gety() == n.gety();
    }
    
    @Override
    public String toString(){
        return String.format("x: %s,\ty: %s",
                this.x, this.y);
    }
    
}
