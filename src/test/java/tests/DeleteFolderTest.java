package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.FolderHelper;

import static io.restassured.RestAssured.given;

@Epic("Yandex-Disk-API-Test")
@Feature("Удаление папки")
public class DeleteFolderTest extends BaseTest{
    @Test
    @Description("Успешное удаление существующей папки")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteFolder() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", folderName)
                .delete("/v1/disk/resources")
                .then()
                .statusCode(204);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка не была удалена");
        softAssert.assertAll();
    }

    @Test
    @Description("Успешное удаление папки без помещения в корзину")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteFolderPermanently() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", folderName)
                .queryParam("permanently", true)
                .delete("/v1/disk/resources")
                .then()
                .statusCode(204);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка не была удалена");
        softAssert.assertAll();
    }

    @Test
    @Description("Удаление несуществующей папки")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistingFolder() {
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "test1000")
                .delete("/v1/disk/resources")
                .then()
                .statusCode(404)
                .extract()
                .response();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertAll();
    }

    @Test
    @Description("Удаление папки без указания названия")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteFolderWithoutPath() {
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .delete("/v1/disk/resources")
                .then()
                .statusCode(400)
                .extract()
                .response();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertAll();
    }

    @Test
    @Description("Удаление папки без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteFolderWithoutAuth() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        Response response = given(spec)
                .queryParam("path", folderName)
                .delete("/v1/disk/resources")
                .then()
                .statusCode(401)
                .extract()
                .response();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка была удалена без авторизации");
        softAssert.assertAll();
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, true);
    }
}
