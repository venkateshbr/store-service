package com.redhat.coolstore.store;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Properties;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@DefaultDeployment
@RunWith(Arquillian.class)
public class RestApiTest {

    private static String port = System.getProperty("arquillian.swarm.http.port", "18080");

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        Properties properties = new Properties();
        properties.put("swarm.http.port", port);
        return new Swarm(properties).withProfile("local");
    }

    @Before
    public void beforeTest() throws Exception {
        RestAssured.baseURI = String.format("http://localhost:%s", port);
    }

    @Test
    @RunAsClient
    public void testGetStoreStatus() throws Exception {
        given()
            .get("/store/status/{location}", "Raleigh")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("location", equalTo("Raleigh"))
            .body("status", equalTo("CLOSED"));
    }

    @Test
    @RunAsClient
    public void testHealthCheck() throws Exception {
        given()
            .get("/health")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("outcome", equalTo("UP"));
    }
}
