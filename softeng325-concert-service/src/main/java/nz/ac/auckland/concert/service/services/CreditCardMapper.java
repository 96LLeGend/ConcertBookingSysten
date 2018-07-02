package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.service.domain.jpa.CreditCard;

public class CreditCardMapper {

	static CreditCardDTO toDto(CreditCard carditCard) {
		CreditCardDTO dtoCreditCard = new CreditCardDTO(
				domainToDTOTypeConverter(carditCard.getType()),
				carditCard.getName(),
				carditCard.getNumber(),
				carditCard.getExpiryDate()
				);
							
		return dtoCreditCard;		
	}
	
	static CreditCard toDomain(CreditCardDTO dtoCarditCard) {
		CreditCard domainCreditCard = new CreditCard(
				dtoToDomainTypeConverter(dtoCarditCard.getType()),
				dtoCarditCard.getName(),
				dtoCarditCard.getNumber(),
				dtoCarditCard.getExpiryDate()
				);
							
		return domainCreditCard;		
	}
	
	private static nz.ac.auckland.concert.common.dto.CreditCardDTO.Type domainToDTOTypeConverter(
			nz.ac.auckland.concert.service.domain.jpa.CreditCard.Type domainType){
		if (domainType == nz.ac.auckland.concert.service.domain.jpa.CreditCard.Type.Master){
			return nz.ac.auckland.concert.common.dto.CreditCardDTO.Type.Master;
		} else {
			return nz.ac.auckland.concert.common.dto.CreditCardDTO.Type.Visa;
		}
	}
	
	private static nz.ac.auckland.concert.service.domain.jpa.CreditCard.Type dtoToDomainTypeConverter(
			nz.ac.auckland.concert.common.dto.CreditCardDTO.Type dtoType){
		if (dtoType == nz.ac.auckland.concert.common.dto.CreditCardDTO.Type.Master){
			return nz.ac.auckland.concert.service.domain.jpa.CreditCard.Type.Master;
		} else {
			return nz.ac.auckland.concert.service.domain.jpa.CreditCard.Type.Visa;
		}
	}
}
