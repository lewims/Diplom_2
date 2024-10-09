package userTests;

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

public class LoginUserTest {
    private String email;
    private String password;
    private String name;
    private UserClient userClient;
    private User user;

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
    @DisplayName("Логин пользователя.")
    @Description("Логин пользователя под существующем логином")
    public void authorizationTest() {
        user = new User(name, email, password);
        UserClient.postCreateNewUser(user);
        Response response = UserClient.checkRequestAuthLogin(user);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.notNullValue())
                .and().body("user.name", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным логином.")
    @Description("Неудачная авторизация пользователя c некорректным логином.")
    public void authorizationIncorrectLoginTest() {
        user = new User(email, password);
        user.setEmail("Hdnasjdhbajhnwjdnakljndj2783o127");
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Логин с неверным паролем.")
    @Description("Неудачная авторизация пользователя c некорректным паролем.")
    public void authorizationIncorrectPasswordTest() {
        user = new User(email, password);
        user.setPassword("onihybnklmk");
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация без логина.")
    @Description("Неудачная авторизация пользователя без логина.")
    public void authorizationWithoutLoginTest() {
        user = new User(email, password);
        user.setPassword(password);
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация без пароля.")
    @Description("Неудачная авторизация пользователя без пароля")
    public void authorizationWithoutPasswordTest() {
        user = new User(email, password);
        user.setEmail(email);
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация без логина и пароля.")
    @Description("Неудачная авторизация пользователя без логина и пароля")
    public void authorizationWithoutLoginAndPasswordTest() {
        Response response = UserClient.checkRequestAuthLogin(user);
        userClient.checkFailedResponseAuthLogin(response);
    }

    @After
    public void tearDown(){
        String accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        if (accessToken !=null) {
            userClient.deleteUser(accessToken);
        }
    }
}