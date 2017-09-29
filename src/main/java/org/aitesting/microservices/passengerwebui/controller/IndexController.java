package org.aitesting.microservices.passengerwebui.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aitesting.microservices.passengermanagement.models.Passenger;
import org.aitesting.microservices.tripmanagement.models.Driver;
import org.aitesting.microservices.tripmanagement.models.Trip;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class IndexController {

	public static Passenger passenger = new Passenger();

	@RequestMapping("/")
	public ModelAndView home(Map<String, Object> model) {
		return new ModelAndView("index");
	}

	@RequestMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response, @RequestParam String username,
			@RequestParam String password, Map<String, Object> model) throws IOException, ServletException {

		// TODO: Request Matching username user to PassengerManagement API
		RestTemplate restTemplate = new RestTemplate();
		Passenger obj = restTemplate.getForObject(
				"http://localhost:8083/api/passengers/login?username=" + username + "&password=" + password,
				Passenger.class);

		if (obj != null && obj.isValid(username, password)) {
			request.getSession().setAttribute("validUser", true);
			request.getSession().setAttribute("username", username);
			request.getSession().setAttribute("idpassenger", obj.getIdpassenger());
			request.getSession().setAttribute("name", obj.getFname() + " " + obj.getLname());
			response.sendRedirect("/services");
		} else {
			request.getSession().setAttribute("validUser", false);
			request.getSession().setAttribute("message", "Passenger " + username + " not found.");
			response.sendRedirect("/message");
		}

	}

	/**
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return ModelAndView with services
	 * @throws IOException
	 */
	@RequestMapping("/services")
	public ModelAndView services(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws IOException {
		boolean validUser;
		if (request.getSession().getAttribute("validUser") != null) {
			validUser = (boolean) request.getSession().getAttribute("validUser");
		} else {
			validUser = false;
		}

		if (validUser) {
			return new ModelAndView("services");
		} else {
			model.put("message", "Invalid username or password.");
			return new ModelAndView("index");
		}
	}

	@RequestMapping("/requestTrip")
	public ModelAndView requestTrip(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer idpassenger, @RequestParam Double latitude, @RequestParam Double longitude, @RequestParam String destinationAddress,
			Map<String, Object> model) {
		Trip requestedTrip = new Trip();
		Passenger passenger = new Passenger();
		passenger.setIdpassenger(idpassenger);
		requestedTrip.setPassenger(passenger);
		requestedTrip.setOriginLocationLat(latitude);
		requestedTrip.setOriginLocationLon(longitude);
		requestedTrip.setDestinationAddress(destinationAddress);
		
		RestTemplate restTemplate = new RestTemplate();
		
		System.out.println("**************************** Create Trip from Trip API *****************************");
		Trip createdTrip = restTemplate.postForObject("http://localhost:8081/api/trips/add", requestedTrip, Trip.class);
		
		System.out.println("**************************** Retrieve List of Drivers from Driver API *****************************");
		List<Driver> drivers = (List<Driver>) restTemplate.getForObject("http://localhost:8082/api/drivers/nearlocation?latitude="+createdTrip.getOriginLocationLat()+"&longitude="+createdTrip.getOriginLocationLon(), List.class);

		Date date = new Date();
		model.put("name", request.getSession().getAttribute("name"));
		model.put("message", "Trip to " + destinationAddress + " was successfully created with date: " + date.getTime());
		model.put("drivers", drivers);
		model.put("trip", createdTrip);
		return new ModelAndView("message");
	}

	/**
	 * Display error message
	 * 
	 * @param request
	 * @param response
	 * @param message
	 * @param model
	 * @return
	 */
	@RequestMapping("/message")
	public ModelAndView message(HttpServletRequest request, HttpServletResponse response, @RequestParam String message,
			Map<String, Object> model) {
		model.put("message", message);
		return new ModelAndView("message");
	}

}
