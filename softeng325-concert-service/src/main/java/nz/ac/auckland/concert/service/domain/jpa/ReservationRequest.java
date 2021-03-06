package nz.ac.auckland.concert.service.domain.jpa;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import nz.ac.auckland.concert.common.types.PriceBand;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Domain model to represent reservation requests. 
 * 
 * A ReservationRequest describes a request to reserve seats in terms of:
 * _numberOfSeats the number of seats to try and reserve.
 * _seatType      the priceband (A, B or C) in which to reserve the seats.
 * _concertId     the identity of the concert for which to reserve seats.
 * _date          the date/time of the concert for which seats are to be 
 *                reserved.
 *
 */

@Embeddable
public class ReservationRequest{

	@Column(name="NumberOfSeats")
	private int _numberOfSeats;
	
	@Column(name="SeatType")
	@Enumerated(EnumType.STRING)
	private PriceBand _seatType;
		
	@Column(name="ConcertId")
	private Long _concertId;
		
	@Column(name="Date")
	private LocalDateTime _date;
	
	public ReservationRequest() {}
	
	public ReservationRequest(int numberOfSeats, PriceBand seatType, Long concertId, LocalDateTime date) {
		_numberOfSeats = numberOfSeats;
		_seatType = seatType;
		_concertId = concertId;
		_date = date;
	}
	
	public int getNumberOfSeats() {
		return _numberOfSeats;
	}
	
	public PriceBand getSeatType() {
		return _seatType;
	}
	
	public Long getConcertId() {
		return _concertId;
	}
	
	public LocalDateTime getDate() {
		return _date;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ReservationRequest))
            return false;
        if (obj == this)
            return true;

        ReservationRequest rhs = (ReservationRequest) obj;
        return new EqualsBuilder().
            append(_numberOfSeats, rhs._numberOfSeats).
            append(_seatType, rhs._seatType).
            append(_concertId, rhs._concertId).
            append(_date, rhs._date).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_numberOfSeats).
	            append(_seatType).
	            append(_concertId).
	            append(_date).
	            toHashCode();
	}
}


