package core.wdag;



import java.util.Map;
import java.io.BufferedReader;
import java.util.PriorityQueue;
import java.util.List;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;


public class Dijkstra {
    
    /**
     * Get all nodes via a depth search.
     */
    public static List<Vertex> DFS(Vertex root) {
      HashMap<Vertex,Vertex> m = new HashMap<>();
      DFS(root,m);
      return new ArrayList<>(m.values());
      }
    
    /**
     * Depth search of the given graph root.
     */
    public static void DFS(Vertex root, HashMap<Vertex,Vertex> v) {
      if(v.containsKey(root)) return;
      if(root != null) v.put(root, root);
      
      for(int i=0;i<root.neighbors.size();i++)
        DFS(root.neighbors.get(i).destination,v);
      }
    
    private static void relax(Vertex v) {
      
      for(int i=0;i<v.neighbors.size();i++) {
        Edge e = v.neighbors.get(i);
        
        // If the difference between the current edge and the vertex edge is
        // smaller.
        double diff = Math.abs(e.edge_weight - e.destination.currentMinDist);
        
        // Give that vertex this edge as the current path to it.
        if(diff < e.destination.currentMinDist)
          e.destination.bestEdge = e;
        }
      }
    
    /**
     * Perform a bellman ford path optimization.
     */
    public static void bellmanFord(Vertex root) {
      List<Vertex> v = DFS(root);
      
      for(int i=0;i<v.size();i++) {
        for(int j=0;j<v.size();j++) 
          relax(v.get(j));
        }
      } 
      
    // Compute all of the paths / based off of the edges 
    public static void computePossiblePaths(Vertex source) {
    		// The source vertex should always have a distance of 0
    		// Add the source vertex into a priority queue
    	 	
        source.currentMinDist = 0.;
        PriorityQueue<Vertex> vQueue = new PriorityQueue<Vertex>();
        vQueue.add(source);
        
        while (!vQueue.isEmpty()) {
        		// Arbitrary vertex = the head of the queue
            Vertex arbV = vQueue.poll();
            // For each edge of the given vertex
            // get the neighboring edges id/weight
            // find the total distance from the source
           
            for (Edge e : arbV.neighbors) {
            		
                Vertex v = e.destination;
                double weight = e.edge_weight;
                double totalDistance = arbV.currentMinDist + weight;
                
                // If the distance of that node is less than the current
                // make it the new minimum distance 
                // label that vertex as the recent edge visited to help 
                // determine the path
                
                if (totalDistance < v.currentMinDist) {
                    vQueue.remove(v);
                    v.currentMinDist = totalDistance;
                    v.last = arbV;
                    v.bestEdge = e;
                    vQueue.add(v);
                }

            }
        }
    }
   
    public static Vertex getDestVert(Collection<Vertex> vertices) {
    		ArrayList<Vertex> list3 = new ArrayList<Vertex>(vertices);
    		 // This should call the index of the destination vertex from the list
        Vertex finalDestination = list3.get(grabIndexNum());
    		return finalDestination;
    }
    
    public static int grabIndexNum() {
    		// find out what the code really wants and fill that in 
    		return 2;
    }
    
    public static Vertex getSource(Collection<Vertex> vertices) {
    		Vertex source = vertices.iterator().next();
    		return source;
    }
    
    public static List<Vertex> getPID() {
    	
    	Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();
        BufferedReader in = null;
        
        // Read the file as input for processes and create the edges 
        try {
            in = new BufferedReader(new FileReader("/Users/crystinrodrick/git/CSCI5573-2/src/wdag/graph.txt"));
            String line;
            boolean sv = true;
            // Read the file, if before #, Store the connecting edges
            // or store the PID and Name
            
            while ((line = in.readLine()) != null) {
            	
                if (line.charAt(0) == '#') {
                    sv = false;
                    continue;
                }
                
                if (sv) {
                	
                    // Need to store all of the ID names 
                    int indexOfSpace = line.indexOf(' ');
                    String pID = line.substring(0, indexOfSpace);
                    String pName = line.substring(indexOfSpace + 1);
                    Vertex v = new Vertex(pName);
                    vertexMap.put(pID, v);
                } 
                
                else {
                	
                    //Need to store all of the connecting edges 
                    String[] g = line.split(" ");
                    String startEdge = g[0];
                    String endEdge = g[1];
                    double weight = Double.parseDouble(g[2]);
                    Vertex v = vertexMap.get(startEdge);
                    if (v != null) {
                        v.addEdge(new Edge(vertexMap.get(endEdge), weight));
                    }
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
        	
            if(in!= null)
                try {
                    in.close();
                } 
            		catch (IOException ignore) {
                }
        }
        
        // List of vertices 
        Collection<Vertex> vertices = vertexMap.values();
        //System.out.println(vertices);
        
        // Calls for current process 
        Vertex source = getSource(vertices);
        
        // Computes all the paths 
        computePossiblePaths(source);
        
        // Calls for destination process
        Vertex finalDestination = getDestVert(vertices);
       
        // This should call the index of the destination vertex from the list
        //Vertex finalDestination = list3.get(x);
        System.out.println("From : " + source + " to destination: " + finalDestination + " the distance = " + finalDestination.currentMinDist);
        List<Vertex> path = getShortestPathTo(finalDestination);
        getAmountOfTimeTaken(finalDestination.currentMinDist);
        System.out.println("The optimal path is: " + path);
        return path;
    }
    public static double getAmountOfTimeTaken(double currentMinDist) {
    		return currentMinDist;
    }
    public static List<Vertex> getShortestPathTo(Vertex destination) {
        List<Vertex> path = new ArrayList<Vertex>();
        // If the minimum distance applies to that vertex, 
        //then add that vertex to the path
        for (Vertex v = destination; v != null; v = v.last)
            path.add(v);

        Collections.reverse(path);
        return path;
    }
    
    public static String[] IDinfo() {
    		//List<String> listOfPIDS = new ArrayList();
    		// This whole method will change to return the list of processes in String form 
    		String[] listOfPIDS = {"vert0", "vert1", "vert2", "vert3"};
    		return listOfPIDS;
    }
    
    public static void postInfo() throws IOException {

    		WriteFile data = 
    				new WriteFile("/Users/crystinrodrick/git/CSCI5573-2/src/wdag/graph.txt", 
    						true);
    		// To to file
    		for (int i = 0; i < IDinfo().length; i++) {
    			data.writeToFile(IDinfo()[i]);
    		}
		
    }
   
    public static void main(String args[]) throws Exception {
    	  // testIt();
//        getPID();
//        postInfo();
        
    } 
    
    
}
