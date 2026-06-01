package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.FolderHelper;

import static io.restassured.RestAssured.given;

@Epic("Yandex-Disk-API-Test")
@Feature("Восстановление папки из корзины")
public class RestoreFolderTest extends BaseTest{
    @Test
    @Description("Успешное восстановление папки из корзины")
    @Severity(SeverityLevel.CRITICAL)
    public void testRestoreFolder() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, false);
        String trashPath = FolderHelper.getTrashPath(spec, config.getToken(), folderName);
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", trashPath)
                .put("/v1/disk/trash/resources/restore")
                .then()
                .statusCode(201)
                .extract()
                .response();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(response.jsonPath().getString("href"));
        softAssert.assertNotNull(response.jsonPath().getString("method"));
        softAssert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка не восстановилась");
        softAssert.assertAll();
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, true);
    }

    @Test
    @Description("Восстановление несуществующей папки")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreNonExistingFolder() {
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "test11111")
                .put("/v1/disk/trash/resources/restore")
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
    @Description("Восстановление папки без указания path")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreFolderWithoutPath() {
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .put("/v1/disk/trash/resources/restore")
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
    @Description("Восстановление папки без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreFolderWithoutAuth() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, false);
        String trashPath = FolderHelper.getTrashPath(spec, config.getToken(), folderName);
        Response response = given(spec)
                .queryParam("path", trashPath)
                .put("/v1/disk/trash/resources/restore")
                .then()
                .statusCode(401)
                .extract()
                .response();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertFalse(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка была восстановлена без авторизации");
        softAssert.assertAll();
        FolderHelper.deleteTrashFolder(spec, config.getToken(), trashPath);
    }
}
