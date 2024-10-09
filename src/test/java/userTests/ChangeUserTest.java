package userTests;

import client.UserClient;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.api.User;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;

public class ChangeUserTest {
    private String name;
    private String email;
    private String password;
    private UserClient userClient;
    private User user;
    private String accessToken;

    private final String modifiedName = "iognsmkls";
    private final String modifiedEmail = "LINGAnon@yandex.ru";
    private final String modifiedPassword = "10000132e3";
    User changeUser = new User();


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
    @DisplayName("Изменение имени пользователя с авторизацией.")
    @Description("Успешное изменение имени пользователя с авторизацией.")
    public void changeUserNameWithAuthorizationTest() {
        UserClient.postCreateNewUser(user);
        accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        changeUser.setName(modifiedName);
        user.setName(modifiedName);
        Response response = userClient.sendPatchRequestWithAuthorizationApiAuthUser(changeUser, accessToken);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией.")
    @Description("Успешное изменение email пользователя с авторизацией.")
    public void changeUserEmailWithAuthorizationTest() {
        UserClient.postCreateNewUser(user);
        accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        changeUser.setName(modifiedEmail);
        user.setEmail(modifiedEmail);
        Response response = userClient.sendPatchRequestWithAuthorizationApiAuthUser(changeUser, accessToken);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией.")
    @Description("Успешное изменение пароля пользователя с авторизацией.")
    public void changeUserPasswordWithAuthorizationTest() {
        UserClient.postCreateNewUser(user);
        accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        changeUser.setPassword(modifiedPassword);
        user.setPassword(modifiedPassword);
        Response response = userClient.sendPatchRequestWithAuthorizationApiAuthUser(changeUser, accessToken);
        userClient.checkSuccessResponseAuthUser(response, email, name);
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    @Description("Неудачное изменение имени пользователя без авторизации.")
public void changeUserNameWithoutAuthorization(){
        changeUser.setName(modifiedName);
        user.setName(modifiedName);
        Response response = userClient.sendPatchRequestWithoutAuthorizationApiAuthUser(changeUser);
        userClient.checkFailedResponseAuthUser(response);
    }

    @Test
    @DisplayName("Изменение пароля пользователя без авторизации")
    @Description("Неудачное изменение пароля пользователя без авторизации.")
    public void changeUserPasswordWithoutAuthorization(){
        changeUser.setPassword(modifiedPassword);
        user.setName(modifiedPassword);
        Response response = userClient.sendPatchRequestWithoutAuthorizationApiAuthUser(changeUser);
        userClient.checkFailedResponseAuthUser(response);
    }

    @Test
    @DisplayName("Изменение e-mail пользователя без авторизации")
    @Description("Неудачное изменение e-mail пользователя без авторизации.")
    public void changeUserEmailWithoutAuthorization(){
        changeUser.setEmail(modifiedEmail);
        user.setEmail(modifiedEmail);
        Response response = userClient.sendPatchRequestWithoutAuthorizationApiAuthUser(changeUser);
        userClient.checkFailedResponseAuthUser(response);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}