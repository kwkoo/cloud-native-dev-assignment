package com.freelance.rest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.openshift.booster.service.Freelancer;

@RunWith(Arquillian.class)
public class GatewayTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, Gateway.class.getPackage())
                .addPackages(true, Freelancer.class.getPackage())
                .addAsResource("project-local.yml", "project-defaults.yml");
    }

    @Inject
    private Gateway gatewayService;

    @Test
    public void getFreelancer() throws Exception {
        assertThat(gatewayService, notNullValue());
        Freelancer freelancer = gatewayService.getFreelancer("1");
        assertThat(freelancer, notNullValue());
        assertThat(freelancer.getEmail(), notNullValue());
    }

    @Test
    public void getFreelancers() throws Exception {
        assertThat(gatewayService, notNullValue());
        List freelancers = gatewayService.getFreelancers();
        assertThat(freelancers, notNullValue());
        for (Object o : freelancers) {
            assertThat(o.toString(), containsString("firstName"));
            assertThat(o.toString(), containsString("lastName"));
            assertThat(o.toString(), containsString("skills"));
        }
    }
    
    @Test
    public void getSingleProject() throws Exception {
        assertThat(gatewayService, notNullValue());
        Object project = gatewayService.getSingleProject("1");
        assertThat(project, notNullValue());
        assertThat(project.toString(), containsString("projectId"));
        assertThat(project.toString(), containsString("ownerFirstName"));
        assertThat(project.toString(), containsString("ownerLastName"));
    }

    @Test
    public void getAllProjects() throws Exception {
        assertThat(gatewayService, notNullValue());
        List projects = gatewayService.getAllProjects();
        assertThat(projects, notNullValue());
        for (Object o : projects) {
            assertThat(o.toString(), containsString("projectId"));
            assertThat(o.toString(), containsString("ownerFirstName"));
            assertThat(o.toString(), containsString("ownerLastName"));
        }
    }

    @Test
    public void getProjectsStatus() throws Exception {
        assertThat(gatewayService, notNullValue());
        List projects = gatewayService.getProjectsStatus("open");
        assertThat(projects, notNullValue());
        for (Object o : projects) {
            assertThat(o.toString(), containsString("projectId"));
            assertThat(o.toString(), containsString("ownerFirstName"));
            assertThat(o.toString(), containsString("ownerLastName"));
        }
    }
}

