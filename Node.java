import java.io.*;
import java.util.*;

/**
 * This is the class that students need to implement. The code skeleton is provided.
 * Students need to implement rtinit(), rtupdate() and linkhandler().
 * printdt() is provided to pretty print a table of the current costs for reaching
 * other nodes in the network.
 */ 
public class Node { 

	public static final int INFINITY = 9999;

	int[] lkcost;				/*The link cost between node 0 and other nodes*/
	int nodename;           	/*Name of this node*/
	int[][] costs = new int[4][2];				/*forwarding table, where index is destination node, [i][0] is cost to destination node and
  	  							  [i][1] is the next hop towards the destination node */

	int[][] graph = new int[4][4];				/*Adjacency metric for the network, where (i,j) is cost to go from node i to j */
	ShortestPath t;             /*Have Dijkstra's implementation */
	int seqNo = 0;
	List<Integer> neighbors = new ArrayList<>();
	List<Integer> broadcasted = new ArrayList<>();
	int numPacketSent;
	 

	/* Class constructor */
	public Node() { }


	void getNeighbors() {
		neighbors = new ArrayList<>();
		for (int i = 0; i < 4; i ++) {
			if (i != this.nodename && this.lkcost[i] != 9999) {
				this.neighbors.add(i);
			}
		}
	}

	/* students to write the following two routines, and maybe some others */
	void rtinit(int nodename, int[] initial_lkcost) {

		this.nodename = nodename;
		this.lkcost = initial_lkcost;

		for(int i=0; i<4; i++){
			costs[i][0] = this.lkcost[i];
			if (costs[i][0] == 9999){
				costs[i][1] = -1;
			}
			else{
				costs[i][1] = i;
			}
		}

		for (int i = 0; i < 4; i ++) {
			graph[this.nodename][i] = costs[i][0];
		}

		//send to neighbors
		for (int i=0; i<4; i++){
			if(this.lkcost[i]!= 9999 && i != this.nodename){
				Packet packet = new Packet();
				packet.sourceid = this.nodename;
				packet.destid = i;
				packet.nodename = this.nodename;
				packet.mincost = this.lkcost;
				packet.seqNo = seqNo;
				NetworkSimulator.tolayer2(packet);
				numPacketSent ++;
			}
		}
		
	}


	void rtupdate(Packet rcvdpkt) {

		getNeighbors();

		//updates dest node's graph with source node's link costs 
		for (int i = 0; i < 4; i ++) {
				graph[rcvdpkt.nodename][i] = rcvdpkt.mincost[i];
				graph[i][rcvdpkt.nodename] = rcvdpkt.mincost[i];
		}

		//print the graph after this update
		// System.out.println();
		// System.out.println("Updated Graph: " + Arrays.deepToString(graph));
		// System.out.println();


		//call dijsktra's on the graph 
		ShortestPath t = new ShortestPath();
        int output[][] = new int[4][2];					   
		output = t.dijkstra(graph,this.nodename);

		//print output from dijkstra's
		// System.out.println("Output table: " + Arrays.deepToString(output));
		// System.out.println();
		

		//update costs from graph after dijkstra's
		for(int i=0; i<4; i++){
			costs[i][0] = output[i][0];	
		}

	
		for (int i = 0; i < 4; i ++) {

			if (i == this.nodename) { //if it's to itself, next-hop is itself
				costs[i][1] = this.nodename;
				
			}

			else if(output[i][0] == graph[this.nodename][i]){
				costs[i][1] = i;
			}

			else if(output[i][0] != graph[this.nodename][i] && graph[output[i][1]][i] + graph[output[i][1]][output[output[i][1]][1]] != output[i][0]){
				costs[i][1] = output[output[i][1]][1];
			}

			else{
				costs[i][1] = output[i][1];
			}
		}

		//forward initial received packet to neighbors
		// System.out.println(this.nodename + "'s neighbors:" + this.neighbors);
		for (int i = 0; i < this.neighbors.size(); i ++) {

			if (this.neighbors.get(i) != rcvdpkt.nodename	//not itself
				&& this.neighbors.contains(i)	//is a neighbor
				&& !(this.broadcasted.contains(rcvdpkt.seqNo))){ //has not received a copy
					rcvdpkt.destid = this.neighbors.get(i);
					rcvdpkt.sourceid = this.nodename;
					// System.out.println("Dest ID: " + rcvdpkt.destid);
					NetworkSimulator.tolayer2(rcvdpkt);
					this.broadcasted.add(rcvdpkt.seqNo);
					numPacketSent ++;
			}
		}
		
	}

	/* called when cost from the node to linkid changes from current value to newcost*/
	void linkhandler(int linkid, int newcost) {
		boolean costChanged = false;
		
		graph[linkid][this.nodename] = newcost;
		this.lkcost[linkid] = newcost;
		costChanged = true;
		
		if (costChanged) {
		  for (int i = 0; i < this.neighbors.size(); i ++) {
				Packet packet = new Packet();
				packet.sourceid = this.nodename;
				packet.destid = this.neighbors.get(i);
				packet.nodename = this.nodename;
				packet.mincost = this.lkcost;
				packet.seqNo = seqNo;
				

				NetworkSimulator.tolayer2(packet);
				numPacketSent ++;
			}
		}

		
	 }  

	/* Prints the current costs to reaching other nodes in the network */
	void printdt() {

		System.out.printf("                    \n");
		System.out.printf("   D%d |   cost  next-hop \n", nodename);
		System.out.printf("  ----|-----------------------\n");
		System.out.printf("     0|  %3d   %3d\n",costs[0][0],costs[0][1]);
		System.out.printf("dest 1|  %3d   %3d\n",costs[1][0],costs[1][1]);
		System.out.printf("     2|  %3d   %3d\n",costs[2][0],costs[2][1]);
		System.out.printf("     3|  %3d   %3d\n",costs[3][0],costs[3][1]);
		System.out.printf("                    \n");
		System.out.println("Number of Routing Messages Sent: " + numPacketSent);
	}
		
	


}
