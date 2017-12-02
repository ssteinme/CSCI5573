package core.wdag;

import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex> {
    public List<Edge> neighbors;
    public final String name;
    public Vertex last;
    public double currentMinDist = Double.POSITIVE_INFINITY;
    public Edge bestEdge = null;
    
    public Vertex(String a) {
        name = a;
        neighbors = new ArrayList<Edge>();
    }

    public void addEdge(Edge e) {
        neighbors.add(e);
    }

    public String toString() {
        return name;
    }

    public int compareTo(Vertex otherVertexes) {
        return Double.compare(currentMinDist, otherVertexes.currentMinDist);
    }

}
