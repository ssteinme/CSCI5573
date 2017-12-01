package shortestPath;


public class Vertex implements Comparable<Vertex> {
    private String name;
    public Edge[] neighbors;
    double distance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public Vertex(String argName) { name = argName; }
    public String toString() {    	
    		return name; 
    	}
    public int compareTo(Vertex neighboring_vertices) {  	
        return Double.compare(distance, neighboring_vertices.distance);
    }
    public String getName() {
    		return name;
    }
    public Edge[] getNeighborEdge() {
    		return neighbors;
    }
    public Vertex getPrevious() {
    		return previous;
    }
	public double getDistance() {
		// TODO Auto-generated method stub
		return distance;
	}
	public void setPrevious(Vertex previous) {
		this.previous = previous;
	}
	public Edge[] getNeighbors() {
        return neighbors;
    }
}
