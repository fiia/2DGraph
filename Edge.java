/*
*
* Josefiina
*
*/

public class Edge {
    private double len;
    
    public Edge(double len) {
        this.len=len;
    }
    
    public double getLen() { return this.len; }
    
    @Override
    public String toString() {
        return String.format("distance: %f", this.len);
    }
    
}