package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.service.domain.jpa.Performer;

public class PerformerMapper {

	static PerformerDTO toDto(Performer performer) {
		PerformerDTO dtoPerformer = new PerformerDTO(
				performer.getId(),
				performer.getName(),
				performer.getImageName(),
				performer.getGenre(),
				performer.getConcertIds()
				);
							
		return dtoPerformer;
			
	}
}
