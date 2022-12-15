import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCategory {

    String URL_save = "http://localhost:8081/categories/save";
    String URL_getAllByUserId = "http://localhost:8081/categories/findAll";
    String URL_getById = "http://localhost:8081/categories/user/findById/";
    String URL_update = "http://localhost:8081/categories/update/";
    String URL_delete = "http://localhost:8081/categories/delete/";

    String name = "test-alimentos";
    String icon = "basket";
    String colorCode = "#B5EC8A";

    String name_update = "test-supermercado";
    String icon_update = "baskets";
    String colorCode_update = "#R4J3K2";

    String jwt = "";
    static Integer categoryId = 0;

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
    }

    @Test @Order(1)
    void test_save() {

        JSONObject requestPOST = new JSONObject();
        requestPOST.put("name", name);
        requestPOST.put("icon", icon);
        requestPOST.put("colorCode", colorCode);

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .body(requestPOST)
                .when()
                .post(URL_save)
                .then()
                .statusCode(201)
                .body("status", equalTo("CREATED"))
                .and().body("response.name", equalTo(name))
                .and().body("response.icon", equalTo(icon))
                .and().body("response.colorCode", equalTo(colorCode))
                .extract().response();

        System.out.println("Código del Resultado test_save: " + response.getStatusCode());

        categoryId = response.getBody().jsonPath().getInt("response.id");
    }

    @Test @Order(2)
    void test_update() {

        JSONObject requestPOST = new JSONObject();
        requestPOST.put("name", name_update);
        requestPOST.put("icon", icon_update);
        requestPOST.put("colorCode", colorCode_update);

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .body(requestPOST)
                .when()
                .put(URL_update + categoryId)
                .then()
                .log().all()
                .body("status", equalTo("OK"))
                .and().body("response.name", equalTo(name_update))
                .and().body("response.icon", equalTo(icon_update))
                .and().body("response.colorCode", equalTo(colorCode_update))
                .extract().response();

        System.out.println("Código del Resultado test_save: " + response.getStatusCode());

        categoryId = response.getBody().jsonPath().getInt("response.id");
    }

    @Test @Order(3)
    void test_getById() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(URL_getById + categoryId)
                .then()
                .statusCode(200)
                .body("status", equalTo("OK"))
                .and().body("response.name", equalTo(name_update))
                .and().body("response.icon", equalTo(icon_update))
                .and().body("response.colorCode", equalTo(colorCode_update))
                .extract().response();

        System.out.println("Código del Resultado test_getById: " + response.getStatusCode());
    }

    @Test @Order(4)
    void test_getAll() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(URL_getAllByUserId)
                .then()
                .statusCode(200)
                .and().body("status", equalTo("OK"))
                .and().body("response[0].isDefault", equalTo(false))
                .and().body("response[0].name", notNullValue())
                .and().body("response[0].icon", notNullValue())
                .and().body("response[0].colorCode", notNullValue())
                .extract().response();

        System.out.println("Código del Resultado: " + response.getStatusCode());
    }

    @Test @Order(5)
    void test_delete() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .delete(URL_delete + categoryId)
                .then()
                .statusCode(204)
                .extract().response();

        System.out.println("Código del Resultado test_delete: " + response.getStatusCode());
    }
}