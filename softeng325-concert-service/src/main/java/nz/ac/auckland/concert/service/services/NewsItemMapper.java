package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.service.domain.jpa.NewsItem;

public class NewsItemMapper {

	static NewsItemDTO toDto(NewsItem domainNewsItem) {
		NewsItemDTO dtoNewsItem = new NewsItemDTO(
				domainNewsItem.getId(),
				domainNewsItem.getTimetamp(),
				domainNewsItem.getContent()
				);
							
		return dtoNewsItem;
			
	}
}
