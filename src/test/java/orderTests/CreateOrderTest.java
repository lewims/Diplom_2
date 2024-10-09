package orderTests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.api.Ingredients;
import org.api.Order;
import org.api.User;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateOrderTest {

    private String name;
    private String email;
    private String password;
    private UserClient userClient;
    private User user;
    private String accessToken;
    private OrderClient orderClient;
    private List<String> ingredient;
    private Order order;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        name = "Kraken";
        email = "Kolyan@yandex.ru";
        password = "12345qwerty";
        userClient = new UserClient();
        orderClient = new OrderClient();
        user = new User(name, email, password);
        ingredient = new ArrayList<>();
        order = new Order(ingredient);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией.")
    @Description("Успешное создание заказа с авторизацией, с ингредиентами.")
    public void createOrderWithAuthorizationTest() {
        UserClient.postCreateNewUser(user);
        accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        Ingredients ingredients = orderClient.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id());
        ingredient.add(ingredients.getData().get(2).get_id());
        ingredient.add(ingredients.getData().get(3).get_id());
        ingredient.add(ingredients.getData().get(4).get_id());
        ingredient.add(ingredients.getData().get(5).get_id());
        ingredient.add(ingredients.getData().get(7).get_id());
        ingredient.add(ingredients.getData().get(8).get_id());
        Response response = OrderClient.createOrderWithAuthorization(order, accessToken);
        response.then().log().all()
                .assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class))
                .and().body("order.ingredients", Matchers.notNullValue())
                .and().body("order._id", Matchers.notNullValue())
                .and().body("order.owner.name", Matchers.is(name))
                .and().body("order.owner.email", Matchers.is(email.toLowerCase(Locale.ROOT)))
                .and().body("order.status", Matchers.is("done"))
                .and().body("order.name", Matchers.notNullValue())
                .and().body("order.price", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации.")
    @Description("Успешное создание заказа без авторизации.")
    public void createOrderWithoutAuthorizationTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id());
        ingredient.add(ingredients.getData().get(2).get_id());
        ingredient.add(ingredients.getData().get(3).get_id());
        Response response = OrderClient.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и без авторизации.")
    @Description("Проверка создания заказа без ингредиентов и без авторизации.")
    public void createEmptyOrderWithoutAuthorization() {
        Response response = OrderClient.createOrderWithoutAuthorization(order);
        orderClient.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов с авторизацией.")
    @Description("Проверка создания заказа без ингредиентов с авторизацией.")
    public void createEmptyOrderWithAuthorization() {
        UserClient.postCreateNewUser(user);
        accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        Response response = OrderClient.createOrderWithAuthorization(order, accessToken);
        orderClient.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хэшем ингредиентов.")
    @Description("Проверка создания заказа без авторизации с неверным хэшем ингредиентов.")
    public void createOrderWithoutAuthorizationWithWrongHashTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredient.add(ingredients.getData().get(0).get_id() + "mkncvjkcxnv");
        ingredient.add(ingredients.getData().get(1).get_id() + "aopsplamzxxvbt");
        Response response = OrderClient.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем ингредиентов.")
    @Description("Проверка создания заказа с авторизацией с неверным хешем ингредиентов.")
    public void createOrderWithAuthorizationWithWrongHashTest() {
        UserClient.postCreateNewUser(user);
        accessToken = UserClient.checkRequestAuthLogin(user).then().extract().path("accessToken");
        Ingredients ingredients = orderClient.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id() + "dsnkvl;xz;vcb");
        ingredient.add(ingredients.getData().get(2).get_id() + "zlkvxmozifnobr");
        Response response = OrderClient.createOrderWithAuthorization(order, accessToken);
        response.then().log().all()
                .statusCode(500);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
