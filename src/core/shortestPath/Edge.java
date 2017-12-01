package shortestPath;


public class Edge
{
    public Vertex destination;
    public double edge_weight;
    public  Edge(Vertex argTarget, double argWeight) { 
    		destination = argTarget; 
    		edge_weight = argWeight; 
    	}
    private String[] neighbors;
    public Vertex get_destination() {
    		return destination;
    }
    public void set_destination(Vertex destination) {
    		this.destination = destination;
    }
    
    public void setWeights(double edge_weight) {
		this.edge_weight = edge_weight;
    }
	
	public double getWeights() {
		// TODO Auto-generated method stub
		return edge_weight;
	}
	
	public String[] getNeighbors() {
        return neighbors;
    }
	public void setNeighbors(String[] neighbors) {
        this.neighbors = neighbors;
    }
}
