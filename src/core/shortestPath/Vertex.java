package core.shortestPath;

import java.util.ArrayList;
import java.util.List;


public class Vertex implements Comparable<Vertex> {
    private String name;
    public List<Edge> neighbors = new ArrayList<>();
    public double distance = Double.POSITIVE_INFINITY;
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
    public List<Edge> getNeighborEdge() {
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
    public List<Edge> getNeighbors() {
        return neighbors;
    }
}
