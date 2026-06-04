package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.FolderHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Yandex-Disk-API-Test")
@Feature("Создание папки")
public class CreateFolderTest extends BaseTest{
    @Test
    @Description("Успешное создание новой папки")
    @Severity(SeverityLevel.CRITICAL)
    public void testSuccessCreateFolderTest(){
        String folderName = "test_" + System.currentTimeMillis();
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", folderName)
                .put("/v1/disk/resources")
                .then()
                .statusCode(201)
                .body(
                        "method", notNullValue(),
                        "href", notNullValue(),
                        "templated", notNullValue()
                );
        Assert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка не была создана");
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, true);
    }

    @Test
    @Description("Создание папки с существующим названием")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateExistingFolder() {
        FolderHelper.createFolder(spec, config.getToken(), config.getFolderName());
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", config.getFolderName())
                .put("/v1/disk/resources")
                .then()
                .statusCode(409)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
        FolderHelper.deleteFolder(spec, config.getToken(), config.getFolderName(), true);
    }

    @Test
    @Description("Создание папки без указания названия")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateFolderWithoutPath() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .put("/v1/disk/resources")
                .then()
                .statusCode(400)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
    }

    @Test
    @Description("Создание папки с названием из пробелов")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateFolderWithSpaces() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "   ")
                .put("/v1/disk/resources")
                .then()
                .statusCode(400)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
    }

    @Test
    @Description("Создание папки со спецсимволами")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateFolderWithSpecialSymbols() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "@#$%^")
                .put("/v1/disk/resources")
                .then()
                .statusCode(201)
                .body(
                        "method", notNullValue(),
                        "href", notNullValue(),
                        "templated", notNullValue()
                );
        Assert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), "@#$%^"));
        FolderHelper.deleteFolder(spec, config.getToken(), "@#$%^", true);
    }

    @Test
    @Description("Создание папки без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateFolderWithoutAuth() {
        given(spec)
                .queryParam("path", config.getFolderName())
                .put("/v1/disk/resources")
                .then()
                .statusCode(401)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
    }
}
