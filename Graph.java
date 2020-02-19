import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author josefiina
 * 
 * 
 */

public class Graph {
    private List<Node> list;
    private List<Node> backup;
    private Scanner reader;
    private File data;
    
    public Graph(String filename){
        this.list = new ArrayList<Node>();
        this.backup = new ArrayList<Node>();
        this.data = new File(filename);

        try {
            create(filename);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void create(String file) throws Exception {
        try {
            this.reader = new Scanner(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        while(this.reader.hasNext()) {
            String line=this.reader.next();
            String parts[]=line.split(",");
            addNode(new Node(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
        }
    }
    
    public void addNode(Node n) { this.list.add(n); }
    
    public void printNodes() { this.list.stream().forEach(n -> System.out.println(n)); }
    
    public void unvisitAll() {
        this.list.stream().forEach(n -> n.unvisit());
    }
    
    //finds node from list by coordinates
    public Node getNode(double x, double y) {
        for(Node n : this.list) {
            if(n.getx()==x && n.gety()==y) { return n; }
        }
        return null;
    }
    
    //removes node from this.list and from individual lists in nodes that point to it
    public void removeNode(Node node) {
        this.list.stream().forEach(n -> {
            int index = n.getAdjacent().indexOf(node);
            if(index!=-1) {
                n.getAdjacent().remove(index);
                n.getEdgesFromNode().remove(index);
            }
        });
        this.list.remove(node);
    }
    
    //asks coordinates of node and calls removeNode-function
    public void removeOne() {
        this.reader = new Scanner(System.in);
        System.out.println("Anna poistettavan solmun koordinaatit muodossa x,y:");
        String line = reader.nextLine();
        String parts[] = line.split(",");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        Node found = getNode(x, y);
        
        if(found==null) { System.out.println("Solmua ei löydy"); }
        else {
            System.out.println("Removing " + found.toString());
            removeNode(found); }
    }
    
    //prints in and out degrees to a file
    //suomenkielinen tehtävänanto ei määritellyt solmujen toivottua järjestystä
    //joten ne on degrees.txt tiedostossa samassa järjestyksessä kuin tdata.txt:ssa
    public void inOutDegrees(String filename){
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            this.list.stream().forEach(n -> {
                String line = String.format("node: %s\tin-degree: %d, out-degree: %d",
                        n.toString(), n.getInDegree(), n.getOutDegree());
                writer.println(line);
            });
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    //run depth first search for each sub graph
    //sub graphs can be separated from each other by removing comments from "writer.println("")"
    public void runDFSR(String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            this.list.stream().filter(n -> n.visited()==false).forEach(node -> {
                //writer.println("");
                DFSR(node, writer);
            });
            writer.close();
            unvisitAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    //recursive depth first search
    public void DFSR(Node node, PrintWriter writer) {
        writer.println(node);
        node.visit();
        List<Node> nodes = node.getAdjacent();
        for(Node n : nodes) {
            if(!n.visited()) {
                DFSR(n, writer);
            }
        }
    }
    
    //run breadth first search for each sub graph
    //sub graphs can be separated from each other by removing comments from "writer.println("")"
    public void runBFS(String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            this.list.stream().filter(n -> n.visited()==false).forEach(node -> {
                //writer.println("");
                BFS(node, writer);
            });
            writer.close();
            unvisitAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    //breadth first search
    public void BFS(Node node, PrintWriter writer) {
        writer.println(node);
        node.visit();
        List<Node> nodes = node.getAdjacent();
        List<Node> queue = new ArrayList<Node>();
        nodes.stream().filter(t -> t.visited()==false).forEach(te -> queue.add(te));
        int i=0;
        while(i<queue.size()) {
            Node n = queue.get(i);
            if(!n.visited()) {
                writer.println(n);
                List<Node> temp = n.getAdjacent();
                temp.stream().filter(t -> t.visited()==false).forEach(te -> queue.add(te));
                n.visit();
            }
            i++;
        }
    }
    
    //euklidean distance
    public double eukDist(Node node, Node node2) {
        return Math.sqrt(
                Math.pow(node.getx()-node2.getx(), 2) +
                        Math.pow(node.gety()-node2.gety(), 2));
    }
    
    //adds the closest neighbour that has not yet been added
    public void connectToNearest(){
        this.list.stream().forEach(node ->{
            Node node2 = (this.list.get(0) == node) ? this.list.get(1) : this.list.get(0);
            if (node.getAdjacent().contains(node2)){
                for(Node n : list) {
                    if(!node.getAdjacent().contains(n) && n!=node) {
                        node2=n;
                        break;
                    }
                }
            }
            double min = eukDist(node, node2);
            for(Node other : this.list) {
                if(node!=other && !(node.getAdjacent().contains(other))) {
                    if(eukDist(node, other) < min) {
                        min = eukDist(node, other);
                        node2=other;
                    }
                }
            }
            node.addAdjacent(node2);
            node.addLeavingEdge(new Edge(min));
            node2.addIncomingEdge(new Edge(min));
        });
    }
    
    public void connectToAll() {
        this.list.stream().forEach(node -> connectFast(node));
    }
    
    //creates a fully connected graph faster than connectToNearest
    public void connectFast(Node node) {
        HashMap<Double, Node>map = new HashMap<Double, Node>();
        ArrayList<Node>neighbours = new ArrayList<Node>();
        
        this.list.stream()
                .filter(n -> !n.equals(node))
                .filter(n -> !node.getAdjacent().contains(n))
                .forEach(n -> {
                    map.put(eukDist(node, n), n);
        });
        
        map.keySet().stream().sorted().forEach(key -> {
            neighbours.add(map.get(key));
            node.addLeavingEdge(new Edge(key));
            map.get(key).addIncomingEdge(new Edge(key));
        });
        
        neighbours.stream().forEach(n -> node.addAdjacent(n));
    }
    
    //tekee uuden listan reuna-arvoista ja säilöö sen, siirtää alkuperäisen backupiin
    public void connectOutliers() {
        ArrayList<Node> newlist = new ArrayList<Node>();
        this.list.stream()
                .filter(n -> (n.getx()>2.5 || n.getx()<-2.5) || (n.gety() <-2.5 || n.gety()>2.5))
                .forEach(n -> {
                    newlist.add(n);
                    n.clearLists();
                });
        this.backup = this.list;
        this.list = newlist;
    }
    
    //fully connected, find shorted distances
    public void minimumSpanningTree(String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
        
            HashMap<Double, Node>shortestDistances = new HashMap<Double, Node>();
            Node first = this.list.get(0);
            shortestDistances.put(Double.NaN, first);
            first.visit();
            writer.println(first);

            int linksMade=1;
            //double weight=0;
            
            while(linksMade<this.list.size()) {
                HashMap<Double, Node>temp = new HashMap<Double, Node>();
                shortestDistances.values().stream().forEach(node -> {
                    for(int i=0; i<node.getAdjacent().size(); i++) {
                        Node n = (Node) node.getAdjacent().get(i);
                        if(!n.visited()) {
                            Edge e = (Edge) node.getEdgesFromNode().get(i);
                            double distance = e.getLen();
                            temp.put(distance, n);
                            break;
                        }
                    }
                });
                temp.entrySet().stream().forEach(entry -> {
                    shortestDistances.putIfAbsent(entry.getKey(), entry.getValue());
                });
        
                ArrayList <Double> distances = new ArrayList<Double>(shortestDistances.keySet());
                Collections.sort(distances);
                for(Double dist : distances) {
                    Node closest = shortestDistances.get(dist);
                    if(!closest.visited()) {
                        closest.visit();
                        writer.println(closest.toString() + ",\tdistance: " + dist);
                        linksMade++;
                        //weight+=dist;
                        break;
                    }
                }
            }
        
        //writer.println("weight: " + weight);
        writer.close();
        unvisitAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
