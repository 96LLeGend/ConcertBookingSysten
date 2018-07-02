package nz.ac.auckland.concert.service.domain.jpa;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.jaxb.LocalDateTimeAdapter;
import nz.ac.auckland.concert.common.types.PriceBand;

/**
 * Domain model to represent bookings (confirmed reservations). 
 * 
 * A Booking describes a booking in terms of:
 * _concertId      the unique identifier for a concert.
 * _concertTitle   the concert's title.
 * _dateTime       the concert's scheduled date and time for which the booking 
 *                 applies.
 * _seats          the seats that have been booked (represented as a  Set of 
 *                 SeatDTO objects).
 * _priceBand      the price band of the booked seats (all seats are within the 
 *                 same price band).
 *
 */
@Entity(name = "Bookings")
public class Booking {

	@Id
	@GeneratedValue
	@Column(name="ID")
	private Long _id;
	
	@Column(name="ConcertTitle")
	private String _concertTitle;
	
	@OneToOne(cascade = {CascadeType.MERGE,CascadeType.REMOVE})
	private Reservation _reservation;

	public Booking() {
	}

	public Booking(String concertTitle, Reservation reservation) {
		_concertTitle = concertTitle;
		_reservation = reservation;
	}

	public Long getId(){
		return _id;
	}

	public String getConcertTitle() {
		return _concertTitle;
	}
	
	public Reservation getReservation() {
		return _reservation;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Seat))
			return false;
		if (obj == this)
			return true;

		Booking rhs = (Booking) obj;
		return new EqualsBuilder().append(_reservation, rhs._reservation)
				.append(_id, rhs._id)
				.append(_concertTitle, rhs._concertTitle).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(_id).append(_reservation)
				.append(_concertTitle).toHashCode();
	}
}
