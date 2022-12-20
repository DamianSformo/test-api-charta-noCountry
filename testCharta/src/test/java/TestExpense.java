import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestExpense {

    String URL_save = "http://localhost:8081/expenses/save";
    String URL_update = "http://localhost:8081/expenses/update/";
    String URL_findAllByUser = "http://localhost:8081/expenses/user/findAll";
    String URL_findByIdByUser = "http://localhost:8081/expenses/user/findById/";
    String URL_findForHomeByUser = "http://localhost:8081/expenses/user/home";
    String URL_groupByCategoryByUser = "http://localhost:8081/expenses/user/categoryGroup";
    String URL_statistics = "http://localhost:8081/expenses/user/statistics";
    String URL_delete = "http://localhost:8081/expenses/delete/";

    String amount = "100.0";
    String description = "basket";
    Integer categoryId = 1;
    Integer currencyId = 1;
    String date = "2022-12-06";
    Boolean isIncluded = true;

    String amount_update = "102.0";
    String description_update = "baskettt";
    Integer categoryId_update = 3;
    String date_update = "2022-12-15";
    Boolean isIncluded_update = false;

    String jwt = "";
    String name = "";
    static Integer expenseId;

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
        requestPOST.put("amount", amount);
        requestPOST.put("description", description);
        requestPOST.put("categoryId", categoryId);
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
                .extract().response();

        System.out.println("Código del Resultado test_save: " + response.getStatusCode());

        expenseId = response.getBody().jsonPath().getInt("response.id");
    }

    @Test @Order(2)
    void test_update() {

        JSONObject requestPOST = new JSONObject();
        requestPOST.put("amount", amount_update);
        requestPOST.put("description", description_update);
        requestPOST.put("categoryId", categoryId_update);
        requestPOST.put("date", date_update);
        requestPOST.put("isIncluded", isIncluded_update);

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .body(requestPOST)
                .when()
                .put(URL_update + expenseId)
                .then()
                .body("status", equalTo("OK"))
                .and().body("response.description", equalTo(description_update))
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
                .and().body("status", equalTo("OK"))
                .and().body("response[0].description", notNullValue())
                .and().body("response[0].amount", notNullValue())
                .extract().response();

        System.out.println("Código del Resultado test_getAll: " + response.getStatusCode());
    }

    @Test @Order(4)
    void test_getById() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(URL_findByIdByUser + expenseId)
                .then()
                .statusCode(200)
                .body("status", equalTo("OK"))
                .and().body("response.description", equalTo(description_update))
                .and().body("response.date", equalTo(date_update))
                .and().body("response.isIncluded", equalTo(isIncluded_update))
                .extract().response();

        System.out.println("Código del Resultado test_getById: " + response.getStatusCode());
    }

    @Test @Order(5)
    void test_findForHomeByUser() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(URL_findForHomeByUser)
                .then()
                .log().all()
                .statusCode(200)
                .body("status", equalTo("OK"))
                .and().body("response.firstName", equalTo(name))
                .and().body("response.moves.type", Matchers.hasItem(notNullValue()))
                .and().body("response.moves.type", Matchers.hasItems(anyOf(
                        containsString("ingreso"),
                        containsString("gasto"))))
                .extract().response();

        System.out.println("Código del Resultado test_findForHomeByUser: " + response.getStatusCode());
    }

    @Test @Order(6)
    void test_groupByCategoryByUserId() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(URL_groupByCategoryByUser)
                .then()
                .statusCode(200)
                .body("status", equalTo("OK"))
                .extract().response();

        System.out.println("Código del Resultado test_groupByCategoryByUserId: " + response.getStatusCode());
    }

    @Test @Order(7)
    void test_statistics() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .get(URL_statistics)
                .then()
                .statusCode(200)
                .body("status", equalTo("OK"))
                .log().all()
                .body("response.incomes", iterableWithSize(12))
                .body("response.incomes", Matchers.allOf(notNullValue()))
                .body("response.expenses", iterableWithSize(12))
                .body("response.expenses", Matchers.allOf(notNullValue()))
                .body("response.months", iterableWithSize(12))
                .body("response.months", Matchers.hasItems("DECEMBER", "JANUARY"))
                .extract().response();

        System.out.println("Código del Resultado test_statistics: " + response.getStatusCode());
    }

    @Test @Order(8)
    void test_delete() {

        Response response = given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .delete(URL_delete + expenseId)
                .then()
                .statusCode(204)
                .extract().response();

        System.out.println("Código del Resultado test_delete: " + response.getStatusCode());
    }
}
