package nz.ac.auckland.concert.common.util;

import java.util.Set;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.util.TheatreLayout;

/**
 * Helper class for carry out all the operation that relate to reserve seat
 */
public class SeatsManager {
	
	// This is utility class, so hide the constructor to prevent instantiation.
	private SeatsManager(){};
	
	/**
	 * Get how many number in a particular priceband
	 * 
	 * @param the priceband that going to be checked
	 * 
	 * @return the total number of seat
	 */
	static public int getNumberOfSeats(PriceBand priceBand){
		
		//First get how many row for that priceBand
		Set<SeatRow> rows = TheatreLayout.getRowsForPriceBand(priceBand);
		
		//Sum the seat number on each row
		int numberOfSeats = 0;
		for(SeatRow currentRow : rows) {
			numberOfSeats += TheatreLayout.getNumberOfSeatsForRow(currentRow);
		}
		
		return numberOfSeats;
	}
	

}
