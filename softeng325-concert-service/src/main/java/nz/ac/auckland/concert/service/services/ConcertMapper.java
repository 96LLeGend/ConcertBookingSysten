package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.service.domain.jpa.Concert;

public class ConcertMapper {
	
	static ConcertDTO toDto(Concert concert) {
		ConcertDTO dtoConcert = new ConcertDTO(
						concert.getId(),
						concert.getTitle(),
						concert.getDates(),
						concert.getTariff(),
						concert.getPerformerIds()
						);
						
		return dtoConcert;
		
	}
}
