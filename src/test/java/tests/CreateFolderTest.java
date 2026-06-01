package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.FolderHelper;

import static io.restassured.RestAssured.given;

@Epic("Yandex-Disk-API-Test")
@Feature("Создание папки")
public class CreateFolderTest extends BaseTest{
    @Test
    @Description("Успешное создание новой папки")
    @Severity(SeverityLevel.CRITICAL)
    public void testSuccessCreateFolderTest(){
        SoftAssert softAssert = new SoftAssert();
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", config.getFolderName())
                .put("/v1/disk/resources")
                .then()
                .statusCode(201)
                .extract()
                .response();
        softAssert.assertNotNull(response.jsonPath().getString("href"));
        softAssert.assertNotNull(response.jsonPath().getString("method"));
        softAssert.assertFalse(response.jsonPath().getBoolean("templated"));
        softAssert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), config.getFolderName()), "Папка не была создана");
        softAssert.assertAll();
        FolderHelper.deleteFolder(spec, config.getToken(), config.getFolderName(), false);
    }

    @Test
    @Description("Создание папки с существующим названием")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateExistingFolder() {
        SoftAssert softAssert = new SoftAssert();
        FolderHelper.createFolder(spec, config.getToken(), config.getFolderName());
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", config.getFolderName())
                .put("/v1/disk/resources")
                .then()
                .statusCode(409)
                .extract()
                .response();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertAll();
        FolderHelper.deleteFolder(spec, config.getToken(), config.getFolderName(), false);
    }

    @Test
    @Description("Создание папки без указания названия")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateFolderWithoutPath() {
        SoftAssert softAssert = new SoftAssert();
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .put("/v1/disk/resources")
                .then()
                .statusCode(400)
                .extract()
                .response();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertAll();
    }

    @Test
    @Description("Создание папки с названием из пробелов")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateFolderWithSpaces() {
        SoftAssert softAssert = new SoftAssert();
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "   ")
                .put("/v1/disk/resources")
                .then()
                .statusCode(400)
                .extract()
                .response();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertAll();
    }

    @Test
    @Description("Создание папки со спецсимволами")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateFolderWithSpecialSymbols() {
        SoftAssert softAssert = new SoftAssert();
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "@#$%^")
                .put("/v1/disk/resources")
                .then()
                .statusCode(201)
                .extract()
                .response();
        softAssert.assertNotNull(response.jsonPath().getString("href"));
        softAssert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), config.getToken()));
        softAssert.assertAll();
        FolderHelper.deleteFolder(spec, config.getToken(), config.getToken(), false);
    }

    @Test
    @Description("Создание папки без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateFolderWithoutAuth() {
        SoftAssert softAssert = new SoftAssert();
        Response response = given(spec)
                .queryParam("path", config.getFolderName())
                .put("/v1/disk/resources")
                .then()
                .statusCode(401)
                .extract()
                .response();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertAll();
    }
}
