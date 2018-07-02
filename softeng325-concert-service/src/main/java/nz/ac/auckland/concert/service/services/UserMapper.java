package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.service.domain.jpa.User;

public class UserMapper {

	static UserDTO toDto(User user) {
		UserDTO dtoUser = new UserDTO(
						user.getUsername(),
						user.getPassword(),
						user.getLastname(),
						user.getFirstname()
						);
						
		return dtoUser;
		
	}
	
	static User toDomain(UserDTO user) {
		User domainUser = new User(
						user.getUsername(),
						user.getPassword(),
						user.getLastname(),
						user.getFirstname()
						);
						
		return domainUser;
		
	}
}
