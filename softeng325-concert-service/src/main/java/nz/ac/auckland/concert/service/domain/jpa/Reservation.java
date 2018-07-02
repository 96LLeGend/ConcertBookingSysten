package nz.ac.auckland.concert.service.domain.jpa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.jaxb.LocalDateTimeAdapter;

/**
 * Domain model to represent reservations. 
 * 
 * A Reservation describes a reservation in terms of:
 * _id                 the unique identifier for a reservation.
 * _reservationRequest details of the corresponding reservation request, 
 *                     including the number of seats and their type, concert
 *                     identity, and the date/time of the concert for which a 
 *                     reservation was requested.
 * _seats              the seats that have been reserved (represented as a Set
 *                     of SeatDTO objects).
 *
 */
@Entity(name="Reservations")
public class Reservation {
	
	@Id
	@GeneratedValue
	@Column(name="ID")
	private Long _id;
	
	@Embedded
	private ReservationRequest _request;
	
	@ElementCollection
	@CollectionTable(name = "Seats")
	@Column(name="Seats")
	private Set<Seat> _seats;
	
	public Reservation() {}
	
	public Reservation(ReservationRequest request, Set<Seat> seats) {
		_id = null;
		_request = request;
		_seats = new HashSet<Seat>(seats);
	}
	
	public Reservation(Long id, ReservationRequest request, Set<Seat> seats) {
		_id = id;
		_request = request;
		_seats = new HashSet<Seat>(seats);
	}
	
	public Long getId() {
		return _id;
	}
	
	public ReservationRequest getReservationRequest() {
		return _request;
	}
	
	public Set<Seat> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Reservation))
            return false;
        if (obj == this)
            return true;

        Reservation rhs = (Reservation) obj;
        return new EqualsBuilder().
            append(_request, rhs._request).
            append(_seats, rhs._seats).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_request).
	            append(_seats).
	            toHashCode();
	}
}
