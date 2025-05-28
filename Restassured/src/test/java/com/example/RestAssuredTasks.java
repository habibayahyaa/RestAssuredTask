package com.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RestAssuredTasks {

    private static final String REQRES_API_KEY = "reqres-free-v1"; // Replace with your actual API key if needed

    @BeforeAll
    public static void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // ✅ Task 1: GET pets by status
    @Test
    public void getAvailablePets() {
        RestAssured.baseURI = "https://petstore.swagger.io";

        Response response = given()
                .queryParam("status", "available")
                .when()
                .get("/v2/pet/findByStatus")
                .then()
                .statusCode(200)
                .extract().response();

        // Extract and print first pet name
        String petName = response.jsonPath().getString("[0].name");
        System.out.println("Pet name: " + petName);
    }

    // ✅ Task 2: Create a new pet
    @Test
    public void createNewPet() {
        RestAssured.baseURI = "https://petstore.swagger.io";

        String newPetJson = """
            {
              "id": 987654321,
              "name": "TestPet",
              "photoUrls": ["http://example.com/photo.jpg"],
              "status": "available"
            }
            """;

        given()
                .header("Content-Type", "application/json")
                .body(newPetJson)
                .when()
                .post("/v2/pet")
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(201)))
                .body("name", equalTo("TestPet"))
                .body("status", equalTo("available"));
    }

    // ✅ Task 3: Login and fully validate GET response structure
    @Test
    public void loginAndGetUser() {
        RestAssured.baseURI = "https://reqres.in";

        // Step 1: Login and extract token
        String loginPayload = """
            {
              "email": "eve.holt@reqres.in",
              "password": "cityslicka"
            }
            """;

        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .header("x-api-key", REQRES_API_KEY)
                .body(loginPayload)
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().response();

        String token = loginResponse.jsonPath().getString("token");
        System.out.println("Login token: " + token);

        // Step 2: GET user with Authorization and assert full structure
        given()
                .header("Authorization", "Bearer " + token)
                .header("x-api-key", REQRES_API_KEY)
                .when()
                .get("/api/users/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.email", equalTo("janet.weaver@reqres.in"))
                .body("data.first_name", equalTo("Janet"))
                .body("data.last_name", equalTo("Weaver"))
                .body("data.avatar", startsWith("https://reqres.in/img/faces/"))
                .body("data.avatar", endsWith(".jpg"));
    }
}
