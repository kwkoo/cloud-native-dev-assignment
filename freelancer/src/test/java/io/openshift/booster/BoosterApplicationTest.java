/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openshift.booster;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import io.openshift.booster.service.Freelancer;
import io.openshift.booster.service.FreelancerRepository;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoosterApplicationTest {

    private static final String FRUITS_PATH = "freelancers";

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private FreelancerRepository fruitRepository;

    @Before
    public void beforeTest() {
        fruitRepository.deleteAll();
        RestAssured.baseURI = String.format("http://localhost:%d/" + FRUITS_PATH, port);
    }

    @Test
    public void testGetAll() {
        Freelancer cherry = fruitRepository.save(new Freelancer("Cherry", "Last", "Email", "Skills"));
        Freelancer apple = fruitRepository.save(new Freelancer("Apple", "Last", "Email", "Skills"));
        requestSpecification()
                .get()
                .then()
                .statusCode(200)
                .body("id", hasItems(cherry.getId(), apple.getId()))
                .body("name", hasItems(cherry.getFirstName(), apple.getFirstName()));
    }

    @Test
    public void testGetOne() {
        Freelancer cherry = fruitRepository.save(new Freelancer("Cherry", "Last", "Email", "Skills"));
        requestSpecification()
                .get(String.valueOf(cherry.getId()))
                .then()
                .statusCode(200)
                .body("id", is(cherry.getId()))
                .body("name", is(cherry.getFirstName()));
    }

    @Test
    public void testGetNotExisting() {
        requestSpecification()
                .get("0")
                .then()
                .statusCode(404);
    }

    private RequestSpecification requestSpecification() {
        return given().baseUri(String.format("http://localhost:%d/%s", port, FRUITS_PATH));
    }
}
