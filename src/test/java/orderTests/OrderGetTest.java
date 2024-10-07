package orderTests;

import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.api.User;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;

public class OrderGetTest {
    private String email;
    private String password;
    private String name;
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        name = "Kraken";
        email = "Kolyan@yandex.ru";
        password = "12345qwerty";
        userClient = new UserClient();
        user = new User(name, email, password);
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя.")
    @Description("Успешная проверка получение списка заказов авторизованного пользователя.")
    public void getUserOrderWithAuthorizationTest() {
        UserClient.postCreateNewUser(user);
        accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .header("authorization", accessToken)
                .when()
                .get("/api/orders");
        response.then().log().all()
                .assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("orders", Matchers.notNullValue())
                .and().body("total", Matchers.any(Integer.class))
                .and().body("totalToday", Matchers.any(Integer.class));
    }

    @Test
    @DisplayName("Получение списка заказов без авторизации.")
    @Description("Неудачная проверка получение списка заказов без авторизации.")
    public void getUserOrderWithoutAuthorizationTest() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .when()
                .get("/api/orders");
        response.then().log().all()
                .assertThat().statusCode(401).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("You should be authorised"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
