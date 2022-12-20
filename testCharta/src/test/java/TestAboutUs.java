import io.restassured.response.Response;
import org.hamcrest.core.Every;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAboutUs {

    String URL_save = "http://localhost:8081/aboutUs/save";
    String URL_findAll = "http://localhost:8081/aboutUs/findAll";
    String URL_delete = "http://localhost:8081/aboutUs/delete/";

    String nameComplete = "User Test";
    String role = "BackEnd";
    String description = "Parte del equipo de desarrollo";
    String image = "img.jpg";
    String email = "user-test@email.com";
    String linkedIn = "user-test.li";

    String jwt = "";
    String name = "";
    static Integer aboutUsId;

    @BeforeEach
    void getToken(){

        String URL_login = "http://localhost:8081/users/login";

        JSONObject requestPOST = new JSONObject();
        requestPOST.put("password", "12345678");
        requestPOST.put("email", "usuario@email.com");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestPOST)
                .when()
                .post(URL_login)
                .then()
                .extract().response();

        jwt = response.getBody().jsonPath().getString("response.jwt");
        name = response.getBody().jsonPath().getString("response.firstName");
    }

    @Test @Order(1)
    void test_save() {

        JSONObject requestPOST = new JSONObject();
        requestPOST.put("nameComplete", nameComplete);
        requestPOST.put("role", role);
        requestPOST.put("description", description);
        requestPOST.put("image", image);
        requestPOST.put("email", email);
        requestPOST.put("linkedIn", linkedIn);

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .body(requestPOST)
                .when()
                .post(URL_save)
                .then()
                .statusCode(201)
                .body("status", equalTo("CREATED"))
                .and().body("response.description", equalTo(description))
                .and().body("response.nameComplete", equalTo(nameComplete))
                .and().body("response.role", equalTo(role))
                .and().body("response.description", equalTo(description))
                .and().body("response.image", equalTo(image))
                .and().body("response.email", equalTo(email))
                .extract().response();

        System.out.println("Código del Resultado test_save: " + response.getStatusCode());

        aboutUsId = response.getBody().jsonPath().getInt("response.id");
    }

    @Test @Order(2)
    void test_getAll() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(URL_findAll)
                .then()
                .statusCode(200)
                .body("status", equalTo("OK"))
                .and().body("response.nameComplete", Every.everyItem(notNullValue()))
                .and().body("response.role", Every.everyItem(notNullValue()))
                .and().body("response.description", Every.everyItem(notNullValue()))
                .and().body("response.image", Every.everyItem(notNullValue()))
                .extract().response();

        System.out.println("Código del Resultado test_getAll: " + response.getStatusCode());
    }

    @Test @Order(3)
    void test_delete() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .delete(URL_delete + aboutUsId)
                .then()
                .statusCode(204)
                .extract().response();

        System.out.println("Código del Resultado test_delete: " + response.getStatusCode());
    }
}