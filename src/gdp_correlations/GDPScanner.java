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
	
	final double T_VALUE = .40;
	
	ArrayList<CountryBin> countrydata;
	ArrayList<CountryCorrelation> countrycorrelations;
	Network G;
	Scanner fin;
	
	public static void main(String args[]) {
		
		@SuppressWarnings("unused")
		GDPScanner newStudy = new GDPScanner();
		
	}
	
	public GDPScanner() {
		
		try {
			
			Scanner fin = new Scanner(new File("weoreptc.aspx"));
			
			countrydata = new ArrayList<CountryBin>();
			countrycorrelations = new ArrayList<CountryCorrelation>();
			
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
				
				if (country.gdplist != null)
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
			
			//Compute r for each pair of countries
			for (CountryBin a : countrydata)
				for (CountryBin b : countrydata) {
					
					if (a == b)
						continue;
					
					countrycorrelations.add(new CountryCorrelation(a.weocode, b.weocode, a.calcCorrelation(b)));
				}
			
			PrintStream corfile = new PrintStream(new File("correlations.txt"));
			
			for (CountryCorrelation c : countrycorrelations)
				corfile.println(c.weo1 + " " + c.weo2 + " " + c.correlation);
			
			G = new Network();
			
			CorrelationBin rBin = new CorrelationBin(new Scanner(new File("correlations.txt")));
			
			rBin.G.printWeighted(new PrintStream(new File("weightedEdges.txt")));
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void populateNetwork() {
		
		while (fin.hasNextInt()) {
			
			int source = fin.nextInt();
			int dest = fin.nextInt();
			
			Double weight = fin.nextDouble();
			
			//FOR ALL EDGES (X, Y) IN |E|, IFF |C(X,Y)| > T (CHOOSE A VALUE T SUCH THAT THE EDGE DENSITY OF G IS BETWEEN 0.05 AND 0.50)
			if (Math.abs(weight) < T_VALUE) 
				continue; //omit this edge
			
			Vertex a = G.getVertex(source);
			Vertex b = G.getVertex(dest);
			
			if (a != null) a.connections.add(dest);
			
			else if (a == null) {
				
				a = new Vertex(source);
				a.connections.add(dest);
				G.vertices.add(a);
				
			}
			
			if (b == null) {
				
				b = new Vertex(dest);
				G.vertices.add(b);
				
			}
			
			G.insertDirectedEdge(a, b, weight);
			
		}
	}
	
	public CountryBin fetch(int weo) {
		
		for (CountryBin a : countrydata)
			if (a.weocode == weo)
				return a;
		
		return null;
	}
	
	public void printData(PrintStream outfile) {
		
		for (CountryBin b : countrydata)
			b.Print(outfile);
		
	}
}
