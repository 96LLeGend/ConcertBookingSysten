package nz.ac.auckland.concert.service.services;


import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.service.domain.jpa.Booking;

public class BookingMapper {

	static BookingDTO toDto(Booking domainBooking) {
		BookingDTO dtoBooking = new BookingDTO(
				domainBooking.getReservation().getReservationRequest().getConcertId(),
				domainBooking.getConcertTitle(),
				domainBooking.getReservation().getReservationRequest().getDate(),
				SeatMapper.domainSetToDTOSet(domainBooking.getReservation().getSeats()),
				domainBooking.getReservation().getReservationRequest().getSeatType()
						);
						
		return dtoBooking;
		
	}
}
