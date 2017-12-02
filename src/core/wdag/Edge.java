package core.wdag;

public class Edge {
	
    public final Vertex destination;
    public final double edge_weight;
    public String name;
        
    // Edge of neighbor edge and weight - process and time 
    public Edge(Vertex d, double w) {
      destination = d;
      edge_weight = w;
      }
    
    public Edge(String n, Vertex d, double w) {
      this(d,w);
      name = n;
      }
}
