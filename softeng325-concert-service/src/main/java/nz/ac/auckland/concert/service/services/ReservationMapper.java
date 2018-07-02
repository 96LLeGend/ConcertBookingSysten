package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.service.domain.jpa.Reservation;

public class ReservationMapper {
	
	static ReservationDTO toDto(Reservation reservation) {
		ReservationDTO reservationDTO = new ReservationDTO(
				reservation.getId(),
				ReservationRequestMapper.toDto(reservation.getReservationRequest()),
				SeatMapper.domainSetToDTOSet(reservation.getSeats())
				);
							
		return reservationDTO;
			
	}
	
	static Reservation toDomain(ReservationDTO dtoReservation) {
		Reservation domainReservation = new Reservation(
				dtoReservation.getId(),
				ReservationRequestMapper.toDomain(dtoReservation.getReservationRequest()),
				SeatMapper.dtoSetToDomainSet(dtoReservation.getSeats())
				);
							
		return domainReservation;
			
	}
}
