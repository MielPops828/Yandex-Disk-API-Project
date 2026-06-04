package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.FolderHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

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
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", trashPath)
                .put("/v1/disk/trash/resources/restore")
                .then()
                .statusCode(201)
                .body(
                        "method", notNullValue(),
                        "href", notNullValue(),
                        "templated", notNullValue()
                );
        Assert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка не восстановилась");
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, true);
    }

    @Test
    @Description("Восстановление несуществующей папки")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreNonExistingFolder() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "test11111")
                .put("/v1/disk/trash/resources/restore")
                .then()
                .statusCode(404)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
    }

    @Test
    @Description("Восстановление папки без указания path")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreFolderWithoutPath() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .put("/v1/disk/trash/resources/restore")
                .then()
                .statusCode(400)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
    }

    @Test
    @Description("Восстановление папки без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testRestoreFolderWithoutAuth() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, false);
        String trashPath = FolderHelper.getTrashPath(spec, config.getToken(), folderName);
        given(spec)
                .queryParam("path", trashPath)
                .put("/v1/disk/trash/resources/restore")
                .then()
                .statusCode(401)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
        Assert.assertFalse(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка была восстановлена без авторизации");
        FolderHelper.deleteTrashFolder(spec, config.getToken(), trashPath);
    }
}
