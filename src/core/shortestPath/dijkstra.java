package shortestPath;




import org.codehaus.jackson.map.ObjectMapper;
import java.util.PriorityQueue;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class dijkstra {
	
    public static void computePaths(Vertex source) {
    	
        source.distance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.neighbors) {
                Vertex v = e.destination;
                double edge_weight = e.edge_weight;
                double distanceThroughU = u.distance + edge_weight;
                
                if (distanceThroughU < v.distance) {
                		vertexQueue.remove(v);
                		v.distance = distanceThroughU ;
                		v.previous = u;
                		vertexQueue.add(v);
                }
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex destination) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = destination; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
    	
    		ObjectMapper objectMapper = new ObjectMapper();
    	
    		try {
            Graph graph = objectMapper.readValue(new File("/Users/crystinrodrick/eclipse-workspace/objectPass/src/practice/sampleJSONFile.json"), Graph.class);
            grabInfo(graph);
            
    		} 
    		catch (IOException e) {
            e.printStackTrace();
    		}
    		// Here is where we need it to read the json file
    		Vertex vert2 = new Vertex("A:1"); 
    		Vertex vert3 = new Vertex("B:2");
    		Vertex vert4 = new Vertex("C:3");
    		Vertex vert5 = new Vertex("D:4");
    		Vertex vert6 = new Vertex("E:5");
    		Vertex vert1 = new Vertex("F:6");
     
    		vert1.neighbors = new Edge[]{ new Edge(vert3, 1.1),
                                  new Edge(vert6,  8.13) };
    		vert2.neighbors = new Edge[]{ new Edge(vert5,  7.7),
                                  new Edge(vert3,  311.42),
                                  new Edge(vert6, 10.00) };
    		vert3.neighbors = new Edge[]{ new Edge(vert2,  38.65) };
    		vert4.neighbors = new Edge[]{ new Edge(vert3, 102.53),
                                  new Edge(vert6,  621.44)};
    		vert5.neighbors = new Edge[]{ new Edge(vert6, 133.4) };
    		vert6.neighbors = new Edge[]{ new Edge(vert1,  11.77),
                                  new Edge(vert4,  62.65),
                                  new Edge(vert5, 14.47)};
     
    		Vertex[] vertices = {vert1, vert2, vert3, vert4, vert5, vert6};

    		for (Vertex v : vertices) {
        	
            computePaths(vert1);
            System.out.println("\nDistance to " + v + ": " + v.distance);
            List<Vertex> path = getShortestPathTo(v);
            System.out.println("Path: " + path);
    		}
        

        
    }
    private static void grabInfo(Graph graph) {
    		grabvertexInfo(graph.getvertexInfo());
        printEdges(graph.getEdge());
    }
    private static void printEdges(Edge[] edges) {
        
        for(Edge e : edges) {
            getEdges(e);
        }
    }
    private static void grabvertexInfo(Vertex nodes) {
    	
    		System.out.println("\tVertex : " + nodes.getName());
    }
    private static void getEdges(Edge edges) {
    	
    		String[] v = edges.getNeighbors();
    		System.out.println(v);
    		double w = edges.getWeights();
    		System.out.println(w);
    }
}
