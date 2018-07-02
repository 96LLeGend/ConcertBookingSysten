package nz.ac.auckland.concert.service.domain.jpa;

import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import nz.ac.auckland.concert.service.domain.jpa.Seat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.common.util.SeatsManager;


/**
 * Helper class for recording which seats in a particulate concert have been reserved, it containt:
 * _dateTime:  The date and time the concert performing
 * _reservedSeats:  A Set of seats that have been reserved
 * _ProceBandARemain: Number of PriceBandA seats available
 * _ProceBandBRemain: Number of PriceBandB seats available
 * _ProceBandCRemain: Number of PriceBandC seats available
 */

@Entity(name = "concertBookingStatus")
public class ConcertBookingStatus {

	@Id
	@Column(name="ID")
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _dateTime;
	
	@Version
	@Column(name = "Version")
	private int _version;
	
	@Column(name="A_Remain")
	private int _PriceBandARemain;
	
	@Column(name="B_Remain")
	private int _PriceBandBRemain;
	
	@Column(name="C_Remain")
	private int _PriceBandCRemain;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ReservedSeatsPerConcert")
	@Column(name="ReservedSeats")
	private Set<Seat> _reservedSeats;
	
	public ConcertBookingStatus (){}
	
	public ConcertBookingStatus (LocalDateTime dateTime){
		_dateTime = dateTime;
		_version = 0;
		//When the status is created, no one reserved it yet, so always empty 
		//and always remain the maximum number of seat
		_reservedSeats = new HashSet<Seat>();
		_PriceBandARemain = SeatsManager.getNumberOfSeats(PriceBand.PriceBandA);
		_PriceBandBRemain = SeatsManager.getNumberOfSeats(PriceBand.PriceBandB);
		_PriceBandCRemain = SeatsManager.getNumberOfSeats(PriceBand.PriceBandC);
	}
	
	public LocalDateTime getDateTime(){
		return _dateTime;
	}
	
	public Set<Seat> getAllReservedSeats(){
		return _reservedSeats;
	}
	
	//Get remaining seat for a particular priceband
	public int getRemainingSeat(PriceBand priceBand){
		if (priceBand == PriceBand.PriceBandA) {
			return _PriceBandARemain;
		} else if (priceBand == PriceBand.PriceBandB) {
			return _PriceBandBRemain; 
		} else {
			return _PriceBandCRemain; 
		}
	}
	
	public boolean checkAvailablity(Seat targetSeat){
		if (_reservedSeats.contains(targetSeat)){
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * Change the number of avaliable seat
	 */
	public void increaseRemainingSeat(PriceBand priceBand, int numberOfSeats){
		if (priceBand == PriceBand.PriceBandA) {
			 _PriceBandARemain+=numberOfSeats;
		} else if (priceBand == PriceBand.PriceBandB) {
			_PriceBandBRemain+=numberOfSeats; 
		} else {
			_PriceBandCRemain+=numberOfSeats; 
		}
	}
	
	public void decreaseRemainingSeat(PriceBand priceBand, int numberOfSeats){
		if (priceBand == PriceBand.PriceBandA) {
			 _PriceBandARemain-=numberOfSeats;
		} else if (priceBand == PriceBand.PriceBandB) {
			_PriceBandBRemain-=numberOfSeats; 
		} else {
			_PriceBandCRemain-=numberOfSeats; 
		}
	}
	
	/*
	 * Add and delete reservation, return a boolean to indicate if the action is
	 * successfully carried out.
	 */
	public void addReservation(Seat newReserve){
		if (checkAvailablity(newReserve)){
			_reservedSeats.add(newReserve);
		} 
	}
	
	public void deleteReservation(Seat newReserve){
		if (!checkAvailablity(newReserve)){
			_reservedSeats.remove(newReserve);
		}
	}
	
	//Update the number of seat remaining.
	public void decreaseRemaining(int num, PriceBand priceBand){
		if (priceBand == PriceBand.PriceBandA) {
			_PriceBandARemain = _PriceBandARemain - num; 
		} else if (priceBand == PriceBand.PriceBandB) {
			_PriceBandBRemain = _PriceBandBRemain - num; 
		} else {
			_PriceBandCRemain = _PriceBandCRemain - num; 
		}
	}
	public void increaseRemaining(int num, PriceBand priceBand){
		if (priceBand == PriceBand.PriceBandA) {
			_PriceBandARemain = _PriceBandARemain + num; 
		} else if (priceBand == PriceBand.PriceBandB) {
			_PriceBandBRemain = _PriceBandBRemain + num; 
		} else {
			_PriceBandCRemain = _PriceBandCRemain + num; 
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConcertBookingStatus))
            return false;
        if (obj == this)
            return true;

        ConcertBookingStatus rhs = (ConcertBookingStatus) obj;
        return new EqualsBuilder().
            append(_dateTime, rhs._dateTime).
            append(_reservedSeats, rhs._reservedSeats).
            append(_PriceBandARemain, rhs._PriceBandARemain).
            append(_PriceBandBRemain, rhs._PriceBandBRemain).
            append(_PriceBandCRemain, rhs._PriceBandCRemain).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_dateTime).
	            append(_reservedSeats).
	            append(_PriceBandARemain).
	            append(_PriceBandBRemain).
	            append(_PriceBandCRemain).
	            toHashCode();
	}
}
