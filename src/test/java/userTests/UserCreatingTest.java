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


public class UserCreatingTest {

    private String name;
    private String email;
    private String password;
    private User user;
    private UserClient userClient;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        name = "Kraken";
        email = "Kolyan@yandex.ru";
        password = "12345qwerty";
        userClient = new UserClient();
        user = new User();
    }

    @Test
    @DisplayName("Проверка создания уникального пользователя.")
    @Description("Регистрация уникального пользователя c корректными данными.")
    public void checkCreateUserTest() {
        user = new User(name, email, password);
        Response response = UserClient.postCreateNewUser(user);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.notNullValue())
                .and().body("user.name", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Регистрация уже зарегестрированнного пользователя.")
    @Description("Проверка создания пользователя, который уже зарегистрирован.")
    public void checkRegisteredUserTest() {
        user = new User(name, email, password);
        UserClient.postCreateNewUser(user);
        Response response = UserClient.postCreateNewUser(user);
        response.then().log().all()
                .assertThat().statusCode(403).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("User already exists"));
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени.")
    @Description("Неудачная регистрация пользователя без имени")
    public void createUserWithoutNameTest() {
        user.setEmail(email);
        user.setPassword(password);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без email.")
    @Description("Неудачная регистрация пользователя без без email")
    public void createUserWithoutEmailTest() {
        user.setName(name);
        user.setPassword(password);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без пароля.")
    @Description("Неудачная регистрация пользователя без пароля")
    public void createUserWithoutPasswordTest() {
        user.setEmail(email);
        user.setName(name);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени и email.")
    @Description("Неудачная регистрация пользователя без имени и email")
    public void createUserWithoutNameAndEmailTest() {
        user.setPassword(password);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без имени и пароля.")
    @Description("Неудачная регистрация пользователя без имени и пароля")
    public void createUserWithoutNameAndPasswordTest() {
        user.setEmail(email);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без email и пароля.")
    @Description("Неудачная регистрация пользователя без email и пароля")
    public void createUserWithoutEmailAndPasswordTest() {
        user.setName(name);
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }

    @Test
    @DisplayName("Проверка создания пользователя без всех полей.")
    @Description("Неудачная регистрация поверка пользователя без имени, email, пароля.")
    public void createUserWithoutNameAndEmailAndPasswordTest() {
        Response response = UserClient.postCreateNewUser(user);
        userClient.checkFailedResponseAuthRegister(response);
    }
    @After
    public void tearDown(){
        String accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        if (accessToken !=null) {
            userClient.deleteUser(accessToken);
        }
    }
}
