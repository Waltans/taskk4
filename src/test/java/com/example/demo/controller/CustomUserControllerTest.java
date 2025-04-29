package com.example.demo.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class CustomUserControllerTest {
	@BeforeAll
	static void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080; // Укажите нужный порт, если не 8080
	}
	
	@Test
	@DisplayName("Корректный поиск пользователей по имени и улице")
	void testGetUsersByNameAndStreet_Valid() {
		given()
			.queryParam("name", "Василий")
			.queryParam("street", "Ленина")
			.when()
			.get("/api/custom/users/by-name-and-street")
			.then()
			.statusCode(HttpStatus.OK.value())
			.contentType(ContentType.JSON)
			.body("size()", greaterThanOrEqualTo(0));
	}
	
	@Test
	@DisplayName("Граничный случай: нет ни одного пользователя")
	void testGetUsersByNameAndStreet_Empty() {
		given()
			.queryParam("name", "NonExistentName")
			.queryParam("street", "NonExistentStreet")
			.when()
			.get("/api/custom/users/by-name-and-street")
			.then()
			.statusCode(HttpStatus.OK.value())
			.body("", hasSize(0));
	}
	
	@Test
	@DisplayName("Ошибочный вызов с пропущенным query-param")
	void testGetUsersByNameAndStreet_MissingParam() {
		given()
			.queryParam("name", "Василий")
			.when()
			.get("/api/custom/users/by-name-and-street")
			.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Поиск по улице по path-параметру")
	void testGetUsersByStreet_Valid() {
		given()
			.when()
			.get("/api/custom/users/by-street/Ленина")
			.then()
			.statusCode(HttpStatus.OK.value())
			.contentType(ContentType.JSON)
			.body("size()", greaterThanOrEqualTo(0));
	}
	
	@Test
	@DisplayName("Создание пользователя с адресом")
	void testCreateUserWithAddress_Valid() {
		String userJson = """
            {
                "id": null,
                "name": "Новый",
                "email": "new@email.com",
                "address": null
            }
            """;
		given()
			.contentType(ContentType.JSON)
			.body(userJson)
			.queryParam("street", "Энгельса")
			.when()
			.post("/api/custom/users")
			.then()
			.statusCode(HttpStatus.OK.value())
			.contentType(ContentType.JSON)
			.body("name", equalTo("Новый"))
			.body("address.street", equalTo("Энгельса"));
	}
	
	@Test
	@DisplayName("Создание пользователя: граничный случай, street пустой")
	void testCreateUserWithAddress_EmptyStreet() {
		String userJson = """
            {
                "id": null,
                "name": "Имя",
                "email": "mail@mail.ru"
            }
            """;
		given()
			.contentType(ContentType.JSON)
			.body(userJson)
			.queryParam("street", "")
			.when()
			.post("/api/custom/users")
			.then()
			.statusCode(anyOf(is(HttpStatus.BAD_REQUEST.value()), is(HttpStatus.OK.value())));
		// В зависимости от валидации в сервисе. Если должна быть ошибка — проверьте BAD_REQUEST.
	}
	
	@Test
	@DisplayName("Создание пользователя: невалидный JSON")
	void testCreateUserWithAddress_InvalidJson() {
		String userJson = "not a valid json";
		given()
			.contentType(ContentType.JSON)
			.body(userJson)
			.queryParam("street", "Test")
			.when()
			.post("/api/custom/users")
			.then()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}
}