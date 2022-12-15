import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;

public class TestRest {
    String URL = "http://localhost:8081/expenses/user";
    Response response = get(URL);
    int statusCode = response.getStatusCode();

    @Test
    void getToken(){

        RestAssured.baseURI ="http://localhost:8081";
        RequestSpecification request = RestAssured.given();

        JSONObject requestPOST = new JSONObject();
        requestPOST.put("password", "12345678");
        requestPOST.put("email", "usuario@email.com");

        //request.body(requestPOST.toJSONString());


        Response response = request.post(
                "/users/validate");

        System.out.println(response.getBody().asString());


    }


    @Test
    void test_3() {
        given()
                .header("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvQGVtYWlsLmNvbSIsImlhdCI6MTY2OTg1NjQyNywiZXhwIjoxNjY5ODU3MTI3LCJ1c2VyX2lkIjoxLCJhdXRob3JpdHkiOiJVU0VSIn0.3u1_-JTza3JyNdzd1aHsz1iyhFNmmRTF9Czwws_kYeg")
                .get(URL)
                .then()
                .statusCode(200)
                .body("response", notNullValue())
                .and().body("response[0].codeCurrency", equalTo("REA"))
                .and().body("response[0].isIncluded", equalTo(true))
;
        //System.out.println(response.getBody().asString());
        //System.out.println("Código del Resultado: " + response.getStatusCode());
    }


}