package com.ecommerce.microservices.inventory_service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer();

	@LocalServerPort
	private Integer port;

	static{
		mySQLContainer.start();
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI  = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void testIsInStockPositive() {
		boolean positiveResponse = RestAssured.given()
				.when()
				.get("api/inventory?skuCode=IPHONE 15&quantity=50")
				.then()
				.log().all()
				.statusCode(200)
				.extract().response().as(Boolean.class);
		assertTrue(positiveResponse);
	}

	@Test
	void testIsInStockNegative() {
		boolean negativeResponse = RestAssured.given()
				.when()
				.get("api/inventory?skuCode=IPHONE%2015&quantity=500")
				.then()
				.log().all()
				.statusCode(200)
				.extract().response().as(Boolean.class);
		assertFalse(negativeResponse);
	}
}
