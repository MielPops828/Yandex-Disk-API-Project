package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.FolderHelper;

import static io.restassured.RestAssured.given;

@Epic("Yandex-Disk-API-Test")
@Feature("Асинхронное восстановление папки")
public class AsyncRestoreFolderTest extends BaseTest{
    @Test
    @Description("Асинхронное восстановление папки из корзины")
    @Severity(SeverityLevel.CRITICAL)
    public void testRestoreFolderAsync() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, false);
        String trashPath = FolderHelper.getTrashPath(spec, config.getToken(), folderName);
        String href = FolderHelper.restoreFolderAsync(spec, config.getToken(), trashPath);
        FolderHelper.waitForOperationCompletion(spec, config.getToken(), href);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка не была восстановлена");
        softAssert.assertAll();
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, true);
    }
    @Test
    @Description("Асинхронное восстановление папки без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreFolderAsyncWithoutAuth() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, false);
        String trashPath = FolderHelper.getTrashPath(spec, config.getToken(), folderName);
        Response response = given(spec)
                .queryParam("path", trashPath)
                .queryParam("force_async", true)
                .put("/v1/disk/trash/resources/restore")
                .then()
                .statusCode(401)
                .extract()
                .response();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(response.jsonPath().getString("error"));
        softAssert.assertNotNull(response.jsonPath().getString("description"));
        softAssert.assertNotNull(response.jsonPath().getString("message"));
        softAssert.assertFalse(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка восстановилась без авторизации");
        softAssert.assertAll();
    }
    @Test
    @Description("Асинхронное восстановление несуществующей папки")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreNonExistingFolderAsync() {
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "test1000")
                .queryParam("force_async", true)
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
    @Description("Асинхронное восстановление без указания имени папки")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreFolderAsyncWithoutPath() {
        Response response = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("force_async", true)
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
}
