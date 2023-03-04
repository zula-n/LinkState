// A Java program for Dijkstra's single source shortest path algorithm.
// The program is for adjacency matrix representation of the graph
import java.util.*;
import java.lang.*;
import java.io.*;
 
class ShortestPath
{
    // A utility function to find the vertex with minimum distance value,
    // from the set of vertices not yet included in shortest path tree
    public static int V = 4;
    
    public static final int INFINITY = 9999;
    
    int minDistance(int dist[][], Boolean sptSet[])
    {
        // Initialize min value
        int min = INFINITY, min_index=-1;
 
        //FILL IN THE CODE HERE
        for (int v = 0; v < V; v++){
            if (sptSet[v] == false && dist[v][0] <= min) {
                min = dist[v][0];
                min_index = v;
            
            }
        }
        return min_index;
    }


    // A utility function to print the constructed distance array
    void printSolution(int dist[][])
    {
        System.out.println("Vertex Distance from Source");
        System.out.println("V" + V);
        for (int i = 0; i < V; i++)
            System.out.println("Dest:"+i+" Cost:"+dist[i][0]+" Path:"+dist[i][1]);
            
    }

    // Function that implements Dijkstra's single source shortest path
    // algorithm for a graph represented using adjacency matrix
    // representation
    int[][] dijkstra(int graph[][], int src)
    {
        int output[][] = new int[4][2];
        
        // FILL IN THE CODE HERE
        Boolean sptSet[] = new Boolean[V];

        //initialize output and set all nodes as unvisited
        for (int v = 0; v < V; v++) {
            if(graph[src][v] != INFINITY){
                output[v][0] = graph[src][v];
            }
            else{
                output[v][0] = INFINITY;
            }
            sptSet[v] = false;
            output[v][1] = src;
        }


        for (int count = 0; count < V; count++) {
            int w = minDistance(output, sptSet);    //w = current node 
            sptSet[w] = true;   //change to visited 
        
            for (int v = 0; v < V; v++){
                if (graph[w][v] != INFINITY //if neighbors
                    && graph [w][v] != 0 //if it's to itself?
                    && output[w][0] + graph[w][v] < output[v][0]){
                    output[v][0] = graph[w][v] + output[w][0];
                    output[v][1] = w;
                    }
                   
                }
        }
        
        return output;
        
    }
    

 
    // Driver method - example of object creation
    public static void main (String[] args)
    {
    	int graph[][]= new int[][]{{0, 1, 1, 9999},
            					   {1, 0,10, 7},
            					   {1,10, 0, 2},
            					   {9999, 7, 2, 0},
    							   };
    							   
        ShortestPath t = new ShortestPath();
        graph = t.dijkstra(graph, 3);
        t.printSolution(graph);
    }
}
