import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.hamcrest.core.Every;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestIncome {

    String URL_save = "http://localhost:8081/incomes/save";
    String URL_update = "http://localhost:8081/incomes/update/";
    String URL_findAllByUser = "http://localhost:8081/incomes/user/findAll";
    //String URL_findByIdByUser = "http://localhost:8081/expenses/user/findById/";
    //String URL_findForHomeByUser = "http://localhost:8081/expenses/user/home";
    //String URL_groupByCategoryByUser = "http://localhost:8081/expenses/user/categoryGroup";
    //String URL_statistics = "http://localhost:8081/expenses/user/statistics";
    String URL_delete = "http://localhost:8081/incomes/delete/";

    String amount = "100.0";
    String description = "basket";
    String type = "Mensual";
    Integer currencyId = 1;
    String date = "2022-12-06";
    Boolean isIncluded = true;

    String amount_update = "102.0";
    String description_update = "baskettt";
    String type_update = "Anual";
    String date_update = "2022-12-15";
    Boolean isIncluded_update = false;

    String jwt = "";
    String name = "";
    static Integer incomeId;

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

    @Test
    @Order(1)
    void test_save() {

        JSONObject requestPOST = new JSONObject();
        requestPOST.put("amount", amount);
        requestPOST.put("description", description);
        requestPOST.put("type", type);
        requestPOST.put("currencyId", currencyId);
        requestPOST.put("date", date);
        requestPOST.put("isIncluded", isIncluded);

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
                .and().body("response.date", equalTo(date))
                .and().body("response.type", equalTo(type))
                .extract().response();

        System.out.println("Código del Resultado test_save: " + response.getStatusCode());

        incomeId = response.getBody().jsonPath().getInt("response.id");
    }

    @Test @Order(2)
    void test_update() {

        JSONObject requestPOST = new JSONObject();
        requestPOST.put("amount", amount_update);
        requestPOST.put("description", description_update);
        requestPOST.put("type", type_update);
        requestPOST.put("date", date_update);
        requestPOST.put("isIncluded", isIncluded_update);

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .body(requestPOST)
                .when()
                .put(URL_update + incomeId)
                .then()
                .body("status", equalTo("OK"))
                .and().body("response.description", equalTo(description_update))
                .and().body("response.type", equalTo(type_update))
                .and().body("response.date", equalTo(date_update))
                .extract().response();

        System.out.println("Código del Resultado test_update: " + response.getStatusCode());
    }

    @Test @Order(3)
    void test_getAll() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(URL_findAllByUser)
                .then()
                .statusCode(200)
                .body("status", equalTo("OK"))
                .and().body("response.amount", Every.everyItem(notNullValue()))
                .and().body("response.description", Every.everyItem(notNullValue()))
                .and().body("response.type", Every.everyItem(anyOf(
                        equalTo("Anual"),
                        equalTo("Mensual"),
                        equalTo("Semanal"),
                        equalTo("Diario"))))
                .and().body("response.currencyCode", Every.everyItem(notNullValue()))
                .and().body("response.date", Every.everyItem(notNullValue()))
                .and().body("response.isIncluded", Every.everyItem(anyOf(
                        equalTo(true),
                        equalTo(false))))
                .extract().response();

        System.out.println("Código del Resultado test_getAll: " + response.getStatusCode());
    }

    @Test @Order(8)
    void test_delete() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .delete(URL_delete + incomeId)
                .then()
                .statusCode(204)
                .extract().response();

        System.out.println("Código del Resultado test_delete: " + response.getStatusCode());
    }
}
