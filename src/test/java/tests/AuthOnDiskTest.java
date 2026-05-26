package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static io.restassured.RestAssured.given;

@Epic("Yandex-Disk-API-Test")
@Feature("Проверка авторизации")
public class AuthOnDiskTest extends BaseTest{
    @Test
    @Description("Авторизация с валидным токеном")
    @Severity(SeverityLevel.BLOCKER)
    public void testAuthWithValidToken(){
        SoftAssert softAssert = new SoftAssert();
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .get("/v1/disk")
                .then()
                .statusCode(200)
                .extract()
                .response();
        String userLogin = response.jsonPath().getString("user.login");
        String userDisplayName = response.jsonPath().getString("user.display_name");
        softAssert.assertEquals(userLogin, config.getUserLogin(), "Полученное значение login пользователя не соответствует ожидаемому");
        softAssert.assertEquals(userDisplayName, config.getUserDisplayName(), "Полученное значение display_name пользователя не соответствует ожидаемому");
        softAssert.assertAll();
    }

    @Test
    @Description("Авторизация без токена")
    @Severity(SeverityLevel.BLOCKER)
    public void AuthWithoutToken(){
        SoftAssert softAssert = new SoftAssert();
        Response response = given(spec)
                .get("/v1/disk")
                .then()
                .statusCode(401)
                .extract()
                .response();
        String resultError = response.jsonPath().getString("error");
        String resultDescription = response.jsonPath().getString("description");
        String resultMessage = response.jsonPath().getString("message");
        softAssert.assertEquals(resultError, "UnauthorizedError", "Полученное значение error не соответствует ожидаемому");
        softAssert.assertEquals(resultDescription, "Unauthorized", "Полученное значение description не соответствует ожидаемому");
        softAssert.assertEquals(resultMessage, "Не авторизован.", "Полученное значение message не соответствует ожидаемому");
        softAssert.assertAll();
    }
}
