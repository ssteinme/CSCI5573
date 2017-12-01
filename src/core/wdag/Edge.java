package wdag;

public class Edge {
	
    public final Vertex destination;
    public final double edge_weight;
    
    
    // Edge of neighbor edge and weight - process and time 
    public Edge(Vertex d, double w) {
        destination = d;
        edge_weight = w;
    }
}
