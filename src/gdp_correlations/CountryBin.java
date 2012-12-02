package gdp_correlations;

import java.util.*;
import java.io.*;


public class CountryBin {

	private int 	weocode;
	private Double 	stdev;
	private Double 	average;
	private String 	countryname;
	private	String 	currency;
	private	String 	scale;
	private	ArrayList<Double> gdplist;
	private	int		ID;
	
	//counter to generate IDs
	private static int idCounter = 0;
	
	
	public CountryBin() {
		
		this.setWEOcode(0);
		this.countryname = null;
		this.currency = null;
		this.scale = null;
		this.setGDPList(new ArrayList<Double>());
		this.ID = idCounter++;
	}
	
	public Double calcCorrelation(CountryBin b) {
		
		int i = 0;
		Double sum = new Double(0);
		
		while(i < 6) {
			
			if (this.getGDPList().get(i) == null || b.getGDPList().get(i) == null)
				break;

			Double x = (this.getGDPList().get(i) - this.average)/this.stdev;
			Double y = (b.getGDPList().get(i) - b.average)/b.stdev;

			Double z = x*y;
			
			sum += z;
			
			++i;
		}
		
		sum = sum/i;
		
		return sum;
	}
	
	public void calcSigma() {
	
		Double summation = new Double(0);
		
		//Take the sum of gdplist and divide by n
		
		for (Double x1 : getGDPList()) 
			summation += x1;
		
		average = summation/getGDPList().size();
		
		//Now we have the mean
		
		//Take the difference of the GDP by year and the mean
		//Sum all of the squares of the differences
		//Take the square root of the sum == std deviation
		
		summation = new Double(0);
	
		for (Double x1 : getGDPList()) {  // look at each year GDP
			Double x2 = x1 - average; // subtract the average from that year GDP
			
			summation += (x2*x2); // square the result and add it to a total sum	
		}
		
		summation = summation/getGDPList().size();
	
		stdev = Math.sqrt(summation);
	}
	
	public int getWEOcode() {
		return this.weocode;
	}

	public void setWEOcode(int weocode) {
		this.weocode = weocode;
	}

	public ArrayList<Double> getGDPList() {
		return gdplist;
	}

	public void setGDPList(ArrayList<Double> gdplist) {
		this.gdplist = gdplist;
	}

	public void setCode(int weo) {
		this.setWEOcode(weo);
	}
	
	public void setName(String name) {
		this.countryname = name;
	}
	
	public void setCurrency(String cur) {
		this.currency = cur;
	}
	
	public void setScale(String sc) {
		this.scale = sc;
	}
	
	public int getID()
	{
		return this.ID;
	}
	
	public void add(Double num) {
		this.getGDPList().add(num);
	}
	
	public void Print(PrintStream outfile) {
		
		outfile.printf(getWEOcode() + " " + countryname + " " + currency + " " + scale + " ");
		
		for (Double b : getGDPList())
			outfile.printf(b + " ");
		
		outfile.println("Average GDP = " + average + " Standard Deviation = " + stdev);
	}
}
