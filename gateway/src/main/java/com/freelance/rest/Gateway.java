package com.freelance.rest;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
//import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import io.openshift.booster.service.Freelancer;


@Path("/gateway")
@RequestScoped
public class Gateway {

    private WebTarget freelanceService;
    private WebTarget freelancersService;

    @Inject
    @ConfigurationValue("freelancer.service.url")
    private String freelanceUrl;


    @GET
    @Path("/freelancers/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Freelancer getFreelancer(@PathParam("itemId") String itemId) {
        Response response = freelanceService.resolveTemplate("itemId", itemId).request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == 200) {
            return response.readEntity(Freelancer.class);
        } else if (response.getStatus() == 404) {
            return null;
        } else {
            throw new ServiceUnavailableException();
        }
    }

    @GET
    @Path("/freelancers")
    @Produces(MediaType.APPLICATION_JSON)
    public List getFreelancers() {
        Response response = freelancersService.request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == 200) {
            return response.readEntity(List.class);
        } else if (response.getStatus() == 404) {
            return null;
        } else {
            throw new ServiceUnavailableException();
        }
    }

    @PostConstruct
    public void init() {
        freelanceService = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
                .connectionPoolSize(3).build().target(freelanceUrl).path("freelancers").path("{itemId}");
        freelancersService = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
                .connectionPoolSize(3).build().target(freelanceUrl).path("freelancers");
    }
}

