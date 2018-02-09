import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class ApplicationTests {

   // @Ignore
    @Test
    public void testHomePage() throws URISyntaxException {
        RestAssured
                .when()
                .get(new URI("http://localhost:8080/"))
                .then()
                .statusCode(200);
    }

   // @Ignore
    @Test
    public void testUserLoggedIn() throws URISyntaxException {
        RestAssured
                .given()
                .param("emailID", "abc@def.com")
                .param("password", "123")
                .when()
                .post(new URI("http://localhost:8080/login"))
                .then()
                .statusCode(200);
    }
 //@Ignore
    @Test
    public void testUserRegistered() throws URISyntaxException {
        RestAssured
                .given()
                .param("emailID", "ankit@csye.com")
                .param("password", "321")
                .when()
                .post(new URI("http://localhost:8080/createAccount"))
                .then()
                .statusCode(200);
    }

}
