package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.service.domain.jpa.ReservationRequest;

public class ReservationRequestMapper {

	static ReservationRequestDTO toDto(ReservationRequest reservationRequest) {
		ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
				reservationRequest.getNumberOfSeats(),
				reservationRequest.getSeatType(),
				reservationRequest.getConcertId(),
				reservationRequest.getDate()
				);
							
		return reservationRequestDTO;
			
	}
	
	static ReservationRequest toDomain(ReservationRequestDTO reservationRequestDTO) {
		ReservationRequest reservationRequestDomain = new ReservationRequest(
				reservationRequestDTO.getNumberOfSeats(),
				reservationRequestDTO.getSeatType(),
				reservationRequestDTO.getConcertId(),
				reservationRequestDTO.getDate()
				);
							
		return reservationRequestDomain;
			
	}
}
