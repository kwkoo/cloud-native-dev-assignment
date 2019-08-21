package com.freelance.rest;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.script.Invocable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import io.openshift.booster.service.Freelancer;


@Path("/gateway")
@RequestScoped
public class Gateway {

    private WebTarget freelanceService;
    private WebTarget freelancersService;

    private WebTarget allProjectsService;
    private WebTarget singleProjectService;
    private WebTarget projectStatusService;

    @Inject
    @ConfigurationValue("freelancer.service.url")
    private String freelanceUrl;

    @Inject
    @ConfigurationValue("projects.service.url")
    private String projectsUrl;


    @GET
    @Path("/freelancers/{freelancerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Freelancer getFreelancer(@PathParam("freelancerId") String freelancerId) {
        Response response = freelanceService.resolveTemplate("freelancerId", freelancerId).request(MediaType.APPLICATION_JSON).get();
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

    @GET
    @Path("/projects/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getSingleProject(@PathParam("projectId") String projectId) {
        Response response = singleProjectService.resolveTemplate("projectId", projectId).request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == 200) {
            return response.readEntity(Object.class);
        } else if (response.getStatus() == 404) {
            return null;
        } else {
            throw new ServiceUnavailableException();
        }
    }

    @GET
    @Path("/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public List getAllProjects() {
        Response response = allProjectsService.request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == 200) {
            return response.readEntity(List.class);
        } else if (response.getStatus() == 404) {
            return null;
        } else {
            throw new ServiceUnavailableException();
        }
    }

    @GET
    @Path("/projects/status/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public List getProjectsStatus(@PathParam("status") String status) {
        Response response = projectStatusService.resolveTemplate("status", status).request(MediaType.APPLICATION_JSON).get();
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
                .connectionPoolSize(3).build().target(freelanceUrl).path("freelancers").path("{freelancerId}");
        freelancersService = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
                .connectionPoolSize(3).build().target(freelanceUrl).path("freelancers");

        allProjectsService = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
        .connectionPoolSize(3).build().target(projectsUrl).path("projects");
        singleProjectService = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
                .connectionPoolSize(3).build().target(projectsUrl).path("project").path("{projectId}");
        projectStatusService = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
                .connectionPoolSize(3).build().target(projectsUrl).path("projects/status").path("{status}");
    }
}

