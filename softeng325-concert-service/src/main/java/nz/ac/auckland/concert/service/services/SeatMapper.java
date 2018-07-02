package nz.ac.auckland.concert.service.services;

import java.util.HashSet;
import java.util.Set;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.jpa.Seat;

public class SeatMapper {

	static SeatDTO toDto(Seat seat) {
		SeatDTO dtoSeat = new SeatDTO(
				seat.getRow(),
				seat.getNumber()
				);
							
		return dtoSeat;		
	}
	
	static Seat toDomain(SeatDTO dtoSeat) {
		Seat seat = new Seat(
				dtoSeat.getRow(),
				dtoSeat.getNumber()
				);
							
		return seat;		
	}
	
	static Set<SeatDTO> domainSetToDTOSet(Set<Seat> domainSet){
		Set<SeatDTO> dtoSet = new HashSet<SeatDTO>();
		for (Seat domain : domainSet){
			dtoSet.add(toDto(domain));
		}
		return dtoSet;
	}
	
	static Set<Seat> dtoSetToDomainSet(Set<SeatDTO> dtoSet){
		Set<Seat> domainSet = new HashSet<Seat>();
		for (SeatDTO dto : dtoSet){
			domainSet.add(toDomain(dto));
		}
		return domainSet;
	}
}
