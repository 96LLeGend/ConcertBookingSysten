package nz.ac.auckland.concert.service.services;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.common.util.CookieName;
import nz.ac.auckland.concert.service.domain.jpa.Booking;
import nz.ac.auckland.concert.service.domain.jpa.Concert;
import nz.ac.auckland.concert.service.domain.jpa.ConcertBookingStatus;
import nz.ac.auckland.concert.service.domain.jpa.CreditCard;
import nz.ac.auckland.concert.service.domain.jpa.NewsItem;
import nz.ac.auckland.concert.service.domain.jpa.Performer;
import nz.ac.auckland.concert.service.domain.jpa.Reservation;
import nz.ac.auckland.concert.service.domain.jpa.ReservationRequest;
import nz.ac.auckland.concert.service.domain.jpa.Seat;
import nz.ac.auckland.concert.service.domain.jpa.User;
import nz.ac.auckland.concert.service.util.TheatreUtility;

@Path("/concerts")
public class ConcertResource {

	//Logger 
	private static Logger _logger = LoggerFactory.getLogger(ConcertResource.class);
	
	//Each reservation has its own session, which contain a timer and the reservation's ID
	private static Map<Long, Timer> _sessions = new HashMap<Long, Timer>();
	
	//All the reservation that pending to be confirmed
	private static List<Long> _pendingReservation = new ArrayList<Long>();
	
	//For sending news letter
	private static  List<AsyncResponse> _subscribers = new ArrayList<AsyncResponse>();
	
	//For client that who don't want to subscribe any more
	private static  List<String> termainateSubscription = new ArrayList<String>();
	
	/**
	 *Retuen all concerts 
	 *
	 * @param Null
	 * 
	 * @return a list of ConcertDTO objects. 
	 */
	@GET
	@Path("getConcerts")
	@Produces({"application/xml"})
	public Response getConcerts() {
		
		//Entity manger for processing relational database
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
				
		//Start a new transaction.
		entityManager.getTransaction().begin();
		
		//Initialized status code as "204" (No content)first
		ResponseBuilder builder = Response.status(Status.NO_CONTENT);		
		
		//Get list of concerts
		TypedQuery<Concert> concertQuery =
				entityManager.createQuery("select c from Concerts c", Concert.class);
		List<Concert> concertList = concertQuery.getResultList();
				
		//Map the list of concert object into concertDTO
		List<ConcertDTO> concertDTOList = new ArrayList<ConcertDTO>();
		for (Concert currentConcert : concertList){
			ConcertDTO newConcertDTO = ConcertMapper.toDto(currentConcert);
			concertDTOList.add(newConcertDTO);
		}
		
		//Concert the list to entity
		GenericEntity<List<nz.ac.auckland.concert.common.dto.ConcertDTO>> entity = 
				new GenericEntity<List<nz.ac.auckland.concert.common.dto.ConcertDTO>>(concertDTOList) {};
		
		//If the list is not empty return the list, otherwise remain "No Content"
		if (concertList != null){
			builder = Response.ok(entity);	
		}
		
		//Commit the transaction.
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return builder.build();
	}
	
	
	/**
	 *Retuen all Performers
	 *
	 * @param Null
	 * 
	 * @return a list of PerformerDTO objects. 
	 */
	@GET
	@Path("getPerformers")
	@Produces({"application/xml"})
	public Response getPerformers() {
		
		//Entity manger for processing relational database
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
		
		//Start a new transaction.
		entityManager.getTransaction().begin();
		
		//Initialized status code as "204" (No content)first
		ResponseBuilder builder = Response.status(Status.NO_CONTENT);		
		
		//Get list of performers
		TypedQuery<Performer> performerQuery =
				entityManager.createQuery("select c from Performers c", Performer.class);
		List<Performer> performerList = performerQuery.getResultList();
		
		//Map the list of concert object into concertDTO
		List<PerformerDTO> performerDTOList = new ArrayList<PerformerDTO>();
		for (Performer currentPerformer : performerList){
			PerformerDTO newPerformerDTO = PerformerMapper.toDto(currentPerformer);
			performerDTOList.add(newPerformerDTO);
		}
				
		//Concert the list to entity
		GenericEntity<List<nz.ac.auckland.concert.common.dto.PerformerDTO>> entity = 
				new GenericEntity<List<nz.ac.auckland.concert.common.dto.PerformerDTO>>(performerDTOList) {};
		
		//If the list is not empty return the list, otherwise remain "No Content"
		if (performerList != null){
			builder = Response.ok(entity);	
		}
		
		//Commit the transaction.
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return builder.build();
	}
	
	
	/**
	 *Retuen all bookings 
	 *
	 * @param Null
	 * 
	 * @return a list of BookingDTO objects. 
	 */
	@GET
	@Path("getBookings")
	@Produces({"application/xml"})
	public Response getBookings() {
		
		//Entity manger for processing relational database
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
				
		//Start a new transaction.
		entityManager.getTransaction().begin();
		
		//Initialized status code as "204" (No content)first
		ResponseBuilder builder = Response.status(Status.NO_CONTENT);		
		
		//Get list of bookings
		TypedQuery<Booking> bookingQuery =
				entityManager.createQuery("select c from Bookings c", Booking.class);
		List<Booking> bookingList = bookingQuery.getResultList();
				
		//Map the list of concert object into concertDTO
		List<BookingDTO> bookingDTOList = new ArrayList<BookingDTO>();
		for (Booking currentBooking : bookingList){
			BookingDTO newBookingDTO = BookingMapper.toDto(currentBooking);
			bookingDTOList.add(newBookingDTO);
		}
		
		//Concert the list to entity
		GenericEntity<List<BookingDTO>> entity = 
				new GenericEntity<List<BookingDTO>>(bookingDTOList) {};
		
		//If the list is not empty return the list, otherwise remain "No Content"
		if (bookingDTOList != null){
			builder = Response.ok(entity);	
		}
		
		//Commit the transaction.
		entityManager.getTransaction().commit();
		entityManager.close();
		
		return builder.build();
	}
	
	
	/**
	 *Create a user
	 *
	 * @param a UserDTO object
	 * 
	 * @return: - A UserDTO onject, if the user's all fields are setted
	 * 			- A error message of CREATE_USER_WITH_MISSING_FIELDS, if there is missing field
	 * 			- A error message of CREATE_USER_WITH_NON_UNIQUE_NAME, if there is duplicate user
	 */
	@POST
	@Path("createUser")
	@Consumes({"application/xml"})
	public Response createUser(UserDTO newUser){

		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
		ResponseBuilder builder = null;
		
		//Check if the newUser has empty field
		if (newUser == null) {
			throw new BadRequestException(Response.status (Status.BAD_REQUEST)
					.entity(Messages.CREATE_USER_WITH_MISSING_FIELDS).build());
		}
		if ((newUser.getUsername() == null) || (newUser.getPassword() == null) 
				|| (newUser.getFirstname() == null) || (newUser.getLastname() == null)) {
			throw new BadRequestException(Response.status (Status.BAD_REQUEST)
					.entity(Messages.CREATE_USER_WITH_MISSING_FIELDS).build());
		}	
		
		//Map the DTO as domain model
		User user = UserMapper.toDomain(newUser);
		
		//Start a new transaction.
		entityManager.getTransaction().begin();
			
		//Check if there is user with same username
		if (entityManager.find(User.class, newUser.getUsername()) != null){
			//Commit close the transaction then throw the exception.
			entityManager.getTransaction().commit();
			entityManager.close();
			throw new BadRequestException(Response.status (Status.BAD_REQUEST)
					.entity(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME).build());	
		}
		
		//Prepare the cookie
		String newCookieBody = UUID.randomUUID().toString();
		NewCookie newCookie = new NewCookie(CookieName.theName, newCookieBody);
				 
		//Add the cookie value to the data base
		user.setCookieValue(newCookieBody);
		
		//Add the concert
		entityManager.persist(user);
						
		//Get the return userDTO object
		User returnUser = entityManager.find(User.class, newUser.getUsername());
		
		//Map the user as a DTO
		UserDTO returnDTO = UserMapper.toDto(returnUser);
		
		//Commit the transaction.
		entityManager.getTransaction().commit();
		entityManager.close();
		
		//Build response
		builder = Response.status(Status.CREATED).entity(returnDTO).cookie(newCookie);
		return builder.build();	
	}
	
	
	/**
	 *Authenticate an user and log them into the remote service, then return a cookie
	 *
	 * @param a UserDTO object
	 * 
	 * @return: - A UserDTO onject, if the user's username and password are found under a same user
	 * 			- A error message of AUTHENTICATE_USER_WITH_MISSING_FIELDS, if there is missing field
	 * 			- A error message of AUTHENTICATE_NON_EXISTENT_USER, if the user is not exis- 
	 * 			- A error message of AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD, if the user's password is not correct
	 */
	@POST
	@Path("authenticateUser")
	@Consumes({"application/xml"})
	public Response authenticateUser(UserDTO user){
		
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
		ResponseBuilder builder = null;
		UserDTO returnDTO = null;
		NewCookie newCookie = null;
		
		//Start a new transaction.
		entityManager.getTransaction().begin();
		
		try{
			
			//Check if the user has empty field
			if (user == null) {
				throw new BadRequestException(Response.status (Status. BAD_REQUEST)
						.entity(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS).build());	
			}
			if ((user.getUsername() == null) || (user.getPassword() == null)) {
				throw new BadRequestException(Response.status (Status. BAD_REQUEST)
						.entity(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS).build());	
			}
		
			//Find the user from the database 
			User domainUser = entityManager.find(User.class, user.getUsername());	
			
			//Check if there the user exist
			if (domainUser == null){
				throw new NotAuthorizedException(Response.status (Status. UNAUTHORIZED)
						.entity(Messages.AUTHENTICATE_NON_EXISTENT_USER).build());
			}	
			
			//Check if the password is correct
			if (!domainUser.getPassword().equals(user.getPassword())){
				throw new NotAuthorizedException(Response.status (Status. UNAUTHORIZED)
						.entity(Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD).build());
			}	
			
			//Prepare new cookie
			String newCookieBody = UUID.randomUUID().toString();
			newCookie = new NewCookie(CookieName.theName, newCookieBody);
			
			//Presist the new cookie
			domainUser.setCookieValue(newCookieBody);
			
			//Map the user as a DTO
			returnDTO = UserMapper.toDto(domainUser);
			
		} finally {
			//Commit the transaction.
			entityManager.getTransaction().commit();
			entityManager.close();
		}
		//Build and returnresponse
		builder = Response.status(Status.OK).entity(returnDTO).cookie(newCookie);
		return builder.build();	
	}
	
	
	/**
	 *Let user to make reservation
	 *
	 * @param 	- a ReservationRequestDTO object as the request reservation
	 * 			- A token that for identify the user
	 * 
	 * @return: - A ReservationDTO onject, if none of the error below occur
	 * 			- A error message of UNAUTHENTICATED_REQUEST, if the request is made by an unauthenticated user.
	 * 			- A error message of BAD_AUTHENTICATON_TOKEN, if there is a authentication token but it's not recognised.
	 * 			- A error message of RESERVATION_REQUEST_WITH_MISSING_FIELDS, if the reservation request is incomplete.
	 * 			- A error message of CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE, if there is not concert the date.
	 * 			- A error message of INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION, if there is not enough seat.
	 */
	@POST
	@Path("reserveSeats")
	@Consumes({"application/xml"})
	public Response reserveSeats(ReservationRequestDTO newRequest, @CookieParam("ThisCookieHasNoName")Cookie token){
		
		//Update _sessions
		updateSessions();
		
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
		ResponseBuilder builder = null;
		ReservationDTO newReservationDTO = null;
		entityManager.getTransaction().begin();
		
		//Map the request as a domain model
		ReservationRequest requestDomain = ReservationRequestMapper.toDomain(newRequest);

		try{
			//Check if a cookie is sent
			if (token == null){
				throw new NotAuthorizedException(Response.status (Status. UNAUTHORIZED)
						.entity(Messages.UNAUTHENTICATED_REQUEST).build());	
			}
			
			//Check for bad token
			if (checkBadToken(token.getValue().toString())){
				throw new NotAuthorizedException(Response.status (Status. UNAUTHORIZED)
						.entity(Messages.BAD_AUTHENTICATON_TOKEN).build());
			}
			
			//Check if the user has empty field
			if (requestDomain == null) {
				throw new BadRequestException(Response.status (Status. BAD_REQUEST)
						.entity(Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS).build());	
			}
			if ((requestDomain.getNumberOfSeats() == 0) || (requestDomain.getSeatType() == null) || 
					(requestDomain.getConcertId() == null) || (requestDomain.getDate() == null)) {
				throw new BadRequestException(Response.status (Status. BAD_REQUEST)
						.entity(Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS).build());	
			}
			
			//Find the concert from the request
			Concert targetConcert = entityManager.find(Concert.class, requestDomain.getConcertId());
			//Check if the concert requested is a vaild concert, if not treat it as a miss field error
			if (targetConcert == null) {
				throw new BadRequestException(Response.status (Status. BAD_REQUEST)
						.entity(Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS).build());	
			}
			
			//Check if the date is valid for that request
			if (!targetConcert.getDates().contains(requestDomain.getDate())) {
				throw new BadRequestException(Response.status (Status. BAD_REQUEST)
					.entity(Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE).build());	
			}
			
			//Find if the concertBookingStatus object for this concert then lock it incase of concurrency transaction;
			ConcertBookingStatus theBookingStatus = entityManager.find(ConcertBookingStatus.class, 
					requestDomain.getDate(),
					LockModeType.OPTIMISTIC_FORCE_INCREMENT);
		
			//If the concertBookingStatus object is not exist, create one
			if (theBookingStatus == null){
				theBookingStatus = new ConcertBookingStatus(newRequest.getDate());
				entityManager.persist(theBookingStatus);
			}
			
			//Check if there is enough seat
			if (requestDomain.getNumberOfSeats() > theBookingStatus.getRemainingSeat(requestDomain.getSeatType())){
				throw new BadRequestException(Response.status (Status. BAD_REQUEST)
						.entity(Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION).build());	
			}
			
			//Get the possible seat
			Set<SeatDTO> DTOProtentialSeats = TheatreUtility.findAvailableSeats(
					requestDomain.getNumberOfSeats(),
					requestDomain.getSeatType(),
					SeatMapper.domainSetToDTOSet(theBookingStatus.getAllReservedSeats()));
			
			//Update and save the list of booked seat and decrease the avaliable seats
			for (SeatDTO currentSeatDTO : DTOProtentialSeats){
				Seat currentSeatDomain = SeatMapper.toDomain(currentSeatDTO);
				theBookingStatus.addReservation(currentSeatDomain);
			}
			theBookingStatus.decreaseRemainingSeat(newRequest.getSeatType(), newRequest.getNumberOfSeats());
			
			//Form and save the new neservation
			Reservation newReservation = new Reservation(requestDomain, SeatMapper.dtoSetToDomainSet(DTOProtentialSeats));
			entityManager.persist(newReservation);
			
			//Map the reservation domain to reservation DTO
			newReservationDTO = ReservationMapper.toDto(newReservation);
			
			/*
			 * Start the timer, so that if the booking is not confirm while the time is up, the current reservation will
			 * be deleted. When confirmBooking() is called, it will stop the timer and eventuality remove this session
			 */
			Timer timer = new Timer();
			timer.schedule (new expiryAction(newReservation.getId()), ConcertApplication.RESERVATION_EXPIRY_TIME_IN_SECONDS * 1000);

			//Wrap the timer with the reservation's ID to the map
			_sessions.put(newReservationDTO.getId(), timer);
			
			//Add this reservation's ID into the list of pending reservation
			_pendingReservation.add(newReservationDTO.getId());
			
		//In case of roll back
		} catch (RollbackException e){
			throw new BadRequestException(Response.status (Status. BAD_REQUEST)
					.entity(Messages.SERVICE_COMMUNICATION_ERROR).build());	
		} finally {
			//Commit and close the entity manager
			entityManager.getTransaction().commit();
			entityManager.close();
		}
		//Build  and return response
		ReservationDTO dto = new ReservationDTO();
		builder = Response.status(Status.OK).entity(newReservationDTO);
		return builder.build();		
	}
	
	
	/**
	 *Register a credit card
	 *
	 * @param   - a CreditCardDTO object
	 * 			- A token that for identify the user
	 * 
	 * @return: - A status of "created"(201), if the registration is successful
	 * 			- A error message of REGISTER_WITH_MISSING_FIELDS, if there is empty in the credit card
	 * 			- A error message of UNAUTHENTICATED_REQUEST, if the request is made by an unauthenticated user
	 * 			- A error message of BAD_AUTHENTICATON_TOKEN, if the token is not recognised.
	 */
	@POST
	@Path("registerCreditCard")
	@Consumes({"application/xml"})
	public Response registerCreditCard(CreditCardDTO newCreditCard, @CookieParam("ThisCookieHasNoName")Cookie token){
		
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
		ResponseBuilder builder = null;
		entityManager.getTransaction().begin();
		
		try{
			//Check if a cookie is sent
			if (token == null){
				throw new NotAuthorizedException(Response.status (Status. UNAUTHORIZED)
						.entity(Messages.UNAUTHENTICATED_REQUEST).build());	
			}
			
			//Check for bad token
			if (checkBadToken(token.getValue().toString())){
				throw new NotAuthorizedException(Response.status (Status. UNAUTHORIZED)
						.entity(Messages.BAD_AUTHENTICATON_TOKEN).build());
			}
			
			//Check if the newCreditCard has empty field
			if (newCreditCard == null) {
				throw new BadRequestException(Response.status (Status.BAD_REQUEST)
						.entity(Messages.REGISTER_WITH_MISSING_FIELDS).build());
			}	
			if ((newCreditCard.getType() == null) || (newCreditCard.getName() == null) 
					|| (newCreditCard.getNumber() == null) || (newCreditCard.getExpiryDate() == null)) {
				throw new BadRequestException(Response.status (Status.BAD_REQUEST)
						.entity(Messages.REGISTER_WITH_MISSING_FIELDS).build());
			}	
			
			//Map the DTO as domain model
			CreditCard domainNewCreditCard = CreditCardMapper.toDomain(newCreditCard);
			
			//Add the credit to the corrisponding user
			User currentUser = entityManager.find(User.class, 
					findUserByCookieValue(token.getValue().toString()).getUsername());
			currentUser.setCreditCard(domainNewCreditCard);
	
		} finally {
			//Commit the transaction.
			entityManager.getTransaction().commit();
			entityManager.close();
		}
		//Build response
		builder = Response.status(Status.CREATED);
		return builder.build();	
	}
	
	
	/**
	 * Confrim a reservation
	 * 
	 * @param 	- ReservationDTO that going to be confrime
	 * 	       	- A token that for identify the user
	 * 
	 * @return 	- A BookingDTO
	 * 			- A error message of CONFIRMATION_WITH_MISSING_FIELDS, if the reservation that going to be confrima has missing field.
	 * 			- A error message of UNAUTHENTICATED_REQUEST, if the request is made by an unauthenticated user.
	 * 			- A error message of BAD_AUTHENTICATON_TOKEN, if the token is not recognised.
	 * 			- A error message of EXPIRED_RESERVATION, if the reservation is expiry already.
	 * 			- A error message of CREDIT_CARD_NOT_REGISTERED, if the user haven't register a credit card.
	 */
	@POST
	@Path("confirmBooking")
	@Consumes({"application/xml"})
	public Response confirmBooking(ReservationDTO reservation, @CookieParam("ThisCookieHasNoName")Cookie token) {
		
		//Update _sessions
		updateSessions();
		
		ResponseBuilder builder = null;
		
		try{	
			//Check if a cookie is sent
			if (token == null){
				throw new NotAuthorizedException(Response.status (Status. UNAUTHORIZED)
						.entity(Messages.UNAUTHENTICATED_REQUEST).build());	
			}
			
			//Check for bad token
			if (checkBadToken(token.getValue().toString())){
				throw new NotAuthorizedException(Response.status (Status. UNAUTHORIZED)
						.entity(Messages.BAD_AUTHENTICATON_TOKEN).build());
			}
			
			//Check if the user has a credit card
			User user = findUserByCookieValue(token.getValue().toString());
			if (user.getCreditCard() == null){
				throw new BadRequestException(Response.status (Status.BAD_REQUEST)
						.entity(Messages.CREDIT_CARD_NOT_REGISTERED).build());
			}
			
			//Check for request with missing field
			if (reservation == null) {
				throw new BadRequestException(Response.status (Status.BAD_REQUEST)
						.entity(Messages.CONFIRMATION_WITH_MISSING_FIELDS).build());	
			}
			if ((reservation.getId() == null) || (reservation.getReservationRequest() == null)
					|| (reservation.getSeats() == null)) {
				throw new BadRequestException(Response.status (Status.BAD_REQUEST)
						.entity(Messages.CONFIRMATION_WITH_MISSING_FIELDS).build());	
			}
			
			//Check if the reservation is pending for confriming, if not, that mean the reservation is delete already, 
			//which mean the reservation is expiry already
			if(!_pendingReservation.contains(reservation.getId())){
				throw new BadRequestException(Response.status (Status.BAD_REQUEST)
						.entity(Messages.EXPIRED_RESERVATION).build());
			}
			
			//Form a booking and this will add the booking to data base as well
			BookingDTO newBookingDTO = makeBooking(reservation);
			
		} finally {
		}
		
		//Build the response
		builder = Response.status(Status.OK);
		return builder.build();
	}
	
	
	/**
	 * Asynchronous Web Model:
	 * Register the response who expect to be a asynchronous response 
	 * 
	 * @param a LocalDateTime represent the subscription's ID
	 */
	@GET
	@Path("subscribe")
	public synchronized void subscribe(@Suspended AsyncResponse response, 
			@QueryParam("SubscriptionID") String SubscriptionID) {
		
		//If the current subscription's ID is in the list of termainateSubscription,
		//response a "400" to tell the client that he/she already cancel subscription.
		//So the client won't sending request anymore. 
		//Since this subscription is end, remove it's ID from termainateSubscription list.
		if (termainateSubscription.contains(SubscriptionID)) {
			termainateSubscription.remove(SubscriptionID);
			ResponseBuilder newResponse = Response.status(Status.BAD_REQUEST);
			response.resume(newResponse.build());
			
		//Otherwise add the response to the list of listener
		} else {
			_subscribers.add(response);
		}
	}
	
	
	/**
	 * Asynchronous Web Model:
	 * Send a newsItemDTO to all the subscribers, and clear all the saved response.
	 */
	@POST
	@Consumes({"application/xml"})
	public synchronized void send(NewsItemDTO newsLetter) {
		// Notify subscribers.
		for (AsyncResponse response : _subscribers) {
			ResponseBuilder newResponse = Response.status(Status.OK).entity(newsLetter);
			response.resume(newResponse.build());
		}
		_subscribers.clear();
	}
	
	
	/**
	 * Asynchronous Web Model:
	 * Put a certain subscription's ID to termainateSubscription list so that next time the 
	 * same subscription calling for newsItem, the server will reject the request
	 * 
	 * @param a LocalDateTime represent the subscription's ID
	 */
	@POST
	@Path("deSubscribe")
	@Consumes({"application/xml"})
	public Response deSubscribe(String SubscriptionID) {
		termainateSubscription.add(SubscriptionID);
		ResponseBuilder newResponse = Response.status(Status.OK);
		return newResponse.build();
	}
	
	
	/**
	 * Intent to used by the officer that managering the ticket selling. He/She should pass in some
	 * content, then the function will get the release time from the system and form a NewsItem object.
	 * The newsItem will be storaged in the database and sent to all the listener.
	 * 
	 * @param a string of news content
	 * 
	 * @return none
	 */
	@POST
	@Path("SendNews")
	@Consumes({"application/xml"})
	public Response addNewsItem(String newsContent){
		
		//Get the current time
		LocalDateTime releaseTime = LocalDateTime.now();
		
		//Form new newsItem
		NewsItem newNews = new NewsItem(releaseTime, newsContent);
	
		//Add the new NewsItem to database
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
		entityManager.getTransaction().begin();
		entityManager.persist(newNews);
		entityManager.getTransaction().commit();
		entityManager.close();
		
		//Map the NewsItem as its DTO
		NewsItemDTO publishedNews = NewsItemMapper.toDto(newNews);
	
		//Send the news
		send(publishedNews);
		
		//Build response
		ResponseBuilder builder = Response.status(Status.OK);
		return builder.build();	
	}
	
	
	/*=========================================================================================================*/
	/**
	 * Helper function for checking bad token
	 * 
	 * @param a string that will become the cookie's body
	 * 
	 * @return --True if the user can be found via cookie
	 *         --Otherwise false
	 */
	private boolean checkBadToken(String cookieValue){
		
		//Check if the user can be find by the cookie value
		if (findUserByCookieValue(cookieValue) == null){
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Helper function, instead of find the user via its user name, this function find the user via its cookie's value
	 * 
	 * @param a string that will become the cookie's body
	 * 
	 * @retuen the User
	 */
	private User findUserByCookieValue(String cookieValue){
		
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();
		User user = null;
		
		//Find user by cookie value
		TypedQuery<User> cookieQuery =
				entityManager.createQuery("SELECT u FROM Users u WHERE u._cookieValue =:cookieValue", User.class )
				.setParameter("cookieValue", cookieValue);
		
		//If found no user, that mean that is a bad token
		try{
			user = cookieQuery.getSingleResult();
		}catch (NoResultException nre){
			return null;
		}
		return user;
	}
	
	/**
	 * Helper function for make a booking by reservation, and map the booking as a dto object
	 * 
	 * @param a reservation object
	 * 
	 * @return a booking dto
	 */
	private BookingDTO makeBooking(ReservationDTO reservation){
		
		//Start a transaction
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
		entityManager.getTransaction().begin();
		
		//Find the concert
		Concert theConcert = entityManager.find(Concert.class, reservation.getReservationRequest().getConcertId());
		
		//Assemply a booking
		Booking newBooking = new Booking(
				theConcert.getTitle(), 
				ReservationMapper.toDomain(reservation)
				);
		
		//Persist the booking
		entityManager.merge(newBooking);
		
		//Commit the transaction.
		entityManager.getTransaction().commit();
		entityManager.close();
		
		//Map the booking as a dto and return it
		return BookingMapper.toDto(newBooking);
	}
	
	
	/**
	 * Helper class for schedule an action(delete the reservation and free up the associate seats) when 
	 * the reservation is expiry.
	 */
	class expiryAction extends TimerTask  {
		
		private Long _reservationId;
		
		public expiryAction (Long reservationId){
			_reservationId = reservationId;
		}
		
		public void run (){
			//Since the session is expiry, the old reservation should be delete and free up the seats
			this.cancel();
			
			//Free seats
			freeUpSeats(_reservationId);
			
			//Remove the reservation from the list of pending reservation
			_pendingReservation.remove(_reservationId);
	    }
	}
	
	
	/**
	 * Helper function for Update _sessions, when the reservation is not pending to confirm,
	 *  the associated session should be delete as well
	 */
	private void updateSessions(){
		Iterator<Map.Entry<Long, Timer>> theIterator = _sessions.entrySet().iterator();
		while (theIterator.hasNext()) {
		    Map.Entry<Long, Timer> currentSession = theIterator.next();
		    if (!_pendingReservation.contains(currentSession.getKey())){
		    	theIterator.remove();
		    }
		}
	}
	
	
	/**
	 * Helper function for Update seats that have been reservased but the reservasation expiries
	 * 
	 * @param the ID of the reservation
	 * 
	 * @return none
	 */
	private void freeUpSeats(Long reservationId){
		
		EntityManager entityManager = PersistenceManager.instance().createEntityManager();	
		try{
			//Start the transaction
			entityManager.getTransaction().begin();
			
			//Find the associate Reservation and ConcertBookingStatus.
			//Note no lock in here, because the all other request will only decrease the number of available seats,
			//which will not effect freeing up seatd
			Reservation theReservation = entityManager.find(Reservation.class, reservationId);
			ConcertBookingStatus theBookingStatus = entityManager.find(ConcertBookingStatus.class, 
					theReservation.getReservationRequest().getDate());
			
			//Free up seats
			int numberOfSeat = 0;
			for(Seat currentSeat : theReservation.getSeats()){
				theBookingStatus.deleteReservation(currentSeat);
				numberOfSeat++;
			}
			
			//Increase the number of available seat
			theBookingStatus.increaseRemainingSeat(theReservation.getReservationRequest().getSeatType(), numberOfSeat);
			
		//In case of roll back, try again until no roll back
		} catch (RollbackException e){
			freeUpSeats(reservationId);
		} finally {
			//Commit the transaction.
			entityManager.getTransaction().commit();
			entityManager.close();
		}
	}
}
