package shortestPath;


public class Graph {
	private Vertex nodes;
    private Edge[] edges;

    public Vertex getvertexInfo() {
        return nodes;
    }

    public void setvertexInfo(Vertex nodes) {
        this.nodes = nodes;
    }

    public Edge[] getEdge() {
        return edges;
    }

    public void setEdge(Edge[] edges) {
        this.edges = edges;
    }
}
