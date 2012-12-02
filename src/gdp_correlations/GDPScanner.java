package gdp_correlations;

import java.util.*;
import java.io.*;

/**
 * 
 * GDPScanner will scan an excel file of global GDP information gathered by the IMF into a GDP data storage class
 * 
 * @author Matthew
 *
 */
public class GDPScanner {

	private ArrayList<CountryBin> countrydata;
	private ArrayList<CountryCorrelation> countrycorrelations;

	public static void main(String args[]) {

		@SuppressWarnings("unused")
		GDPScanner newStudy = new GDPScanner();

	}

	public GDPScanner() {

		try {

			Scanner fin = new Scanner(new File("weoreptc.aspx"));

			countrydata = new ArrayList<CountryBin>();

			//Scan through the first line (Garbage) 
			fin.nextLine();

			//For the next x lines, int, str, str, str, int, int, int, int, int, int, (~Garbage int)
			//						weo, cty, typ, scl, '06, '07, '08, '09, '10, '11, (Estimates After yr. ####)


			//Stop when next != an integer
			// -- accounted for countries with compound names "Republics of ' ',' ' and ' ', United ' ', etc"
			// -- accounted for 'n/a' data vals, 

			while (fin.hasNext()) {

				CountryBin country = new CountryBin();

				if (!fin.hasNextInt())
					break;

				country.setCode(fin.nextInt());
				country.setName(fin.next());

				while (!fin.next().equals("National"))
					fin.next();

				country.setCurrency(fin.next());
				country.setScale(fin.next());

				for (int x = 1; x <= 6; x++) {

					String rawdata = fin.next();
					String strdata = "";
					double procdata = 0;

					//					System.out.println(rawdata);

					for (char y : rawdata.toCharArray()) {

						//						System.out.println(y-'0');

						if (y-'0' < 0 || y-'0' > 9)
							continue;


						strdata += y;
					}

					if (strdata.length() > 0) {

						procdata = Long.parseLong(strdata);
						country.add(procdata);

					}
				}

				if (country.getGDPList() != null)
					countrydata.add(country);

				fin.nextLine();
			}

			//Success so far:

			// still TODO for task 1:

			// Assign each country its standard deviation 

			//testing stdeviation calc
			for (CountryBin b : countrydata) {
				b.calcSigma();				
			}

			//Testing to see if the data has been stored properly

			PrintStream outfile = new PrintStream(new File("outputofcorrelation.txt"));
			printData(outfile);

			// Create a function to Compute the pearson coefficient for countries (X, Y)

			//Test if calcCorrelation(a, b) works:

			//			Scanner systemin = new Scanner(System.in);
			//			
			//			while (systemin.hasNext()) {
			//				
			//				CountryBin a = fetch(systemin.nextInt());
			//				CountryBin b = fetch(systemin.nextInt());
			//			
			//				System.out.println(a.calcCorrelation(b));
			//			}

			//Compute r for each pair of countries



		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CountryBin fetch(int weo) {

		for (CountryBin a : countrydata)
			if (a.getWEOcode() == weo)
				return a;

		return null;
	}

	public ArrayList<CountryBin> getCountryData()
	{
		return this.countrydata;
	}

	public void printData(PrintStream outfile) {

		for (CountryBin b : countrydata)
			b.Print(outfile);

	}

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
					CountryBin child = null;
					
					//copies the parent node's edge list into an array
					//TODO figure out how to do this in our implementation
					Integer[] edges = null; //currentNode.getGDPList().toArray(new Integer[currentNode.getGDPList().size()]);

					//for each edge in edges
					for (Integer edge : edges)
					{
						//if the edge is undiscovered, return the node that edge refers to
						//if this never runs, the node has no edges that are undiscovered
						if (state[edge] == UNDISCOVERED)
							child = this.getCountryData().get(edge);
					}
			
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
}
