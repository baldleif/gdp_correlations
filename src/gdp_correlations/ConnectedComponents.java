package gdp_correlations;

import java.util.Arrays;
import java.util.Stack;

public class ConnectedComponents {

	public void calculateConnectedComponents(){
		//constant so code is more readable
		final int	NUMBER_OF_NODES = this.getCountryData().size();

		//constants to define for our array
		final int	UNDISCOVERED	= 0;
		final int 	DISCOVERED 		= 1;
		final int 	VISITED 		= 2;

		//stores the state of all edges in the graph
		int[] state = new int[NUMBER_OF_NODES];

		int count = 0;

		//initialize all edges to undiscovered
		Arrays.fill(state, UNDISCOVERED);

		//attempt to run DFS from every node
		for(int i = 0; i < NUMBER_OF_NODES; i++) {
			CountryBin sourceNode = fetch(i);

			//if the node isn't visited, we want to run DFS on it
			if(state[sourceNode.getID()] == UNDISCOVERED){
				//add to the number of connected components
				count++;

				//start DFS
				//DFS uses Stack data structure
				Stack<CountryBin> dfsStack = new Stack<CountryBin>();

				dfsStack.push(sourceNode);
				state[sourceNode.getID()] = VISITED;

				//while we have a stack
				while(dfsStack.isEmpty() == false) {

					CountryBin currentNode = (CountryBin)dfsStack.peek();
					state[currentNode.getID()] = DISCOVERED;

					//visit child
					CountryBin child = getUnvisitedChild(currentNode);
					//if it exists
					if(child != null) {
						state[child.getID()] = VISITED;
						dfsStack.push(child);
					}

					//if the node has no children of their own, discard it off the stack
					//get one off the pile of children
					else {
						dfsStack.pop();
					}
				}

			}

			//print the number of connected components
			System.out.println(count);

		}
	}

	//returns an undiscovered child node if one exists, null if it doesn't
	public CountryBin getUnvisitedChild(CountryBin parent)
	{
		//copies the parent node's edge list into an array
		Double[] edges = parent.getGDPlist().toArray(new Double[parent.getGDPlist().size()]);

		//for each edge in edges
		for (Double edge : edges)
		{
			//if the edge is undiscovered, return the node that edge refers to
			if (state[edge] == UNDISCOVERED)
				return scanner.bigGraphOfJustice.getNode(edge);
		}

		//the node has no edges that are undiscovered
		return null;
	}
}
