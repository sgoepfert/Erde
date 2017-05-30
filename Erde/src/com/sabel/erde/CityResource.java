package com.sabel.erde;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sabel.erde.City;

@Path("/cities")
public class CityResource {
	private EntityManagerFactory emf;
	private EntityManager em;

	public CityResource() {
		emf = Persistence.createEntityManagerFactory("Cities");
		em = emf.createEntityManager();
	}

	private void startConnection() {
		em.getTransaction().begin();
	}

	private void stopConnection() {
		em.getTransaction().commit();
	}

	public void close() {
		if (em != null) {
			em.close();
		}
		em = null;
		if (emf != null) {
			emf.close();
		}
		emf = null;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<City> getAllCities() {
		TypedQuery<City> query = em.createQuery("select c from City c", City.class);
		List<City> result = query.getResultList();
		return result;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("query")
	public City liefereCity(@QueryParam("id") int id) {
		return em.find(City.class, id);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("id/{id}")
	public City getCity(@PathParam("id") int id) {
		City city = em.find(City.class, id);
		return city;
	}

	@DELETE
	@Path("id/{id}")
	public City deleteCity(@PathParam("id") int id) {
		City city = getCity(id);
		System.out.println("vor if");
		if (city != null) {
			startConnection();
			System.out.println("Conection");
			em.remove(city);
			stopConnection();
		}
		return city;
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/new")
	public String addCity(@FormParam("name") String name, @FormParam("countryCode") String countryCode,
			@FormParam("district") String district, @FormParam("population") int population) {
		City newCity = new City();
		newCity.setName(name);
		newCity.setCountryCode(countryCode);
		newCity.setDistrict(district);
		newCity.setPopulation(population);
		startConnection();
		em.merge(newCity);
		stopConnection();
		return "<html><head></head><body><p>Erfolgreich hinzugefügt</p></body></html>";
	}
}
