package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.FolderHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

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
        Assert.assertFalse(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка не была удалена");
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
        Assert.assertFalse(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка не была удалена");
    }

    @Test
    @Description("Удаление несуществующей папки")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistingFolder() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "test1000")
                .delete("/v1/disk/resources")
                .then()
                .statusCode(404)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
    }

    @Test
    @Description("Удаление папки без указания названия")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteFolderWithoutPath() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .delete("/v1/disk/resources")
                .then()
                .statusCode(400)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
    }

    @Test
    @Description("Удаление папки без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteFolderWithoutAuth() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        given(spec)
                .queryParam("path", folderName)
                .delete("/v1/disk/resources")
                .then()
                .statusCode(401)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
        Assert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), folderName), "Папка была удалена без авторизации");
        FolderHelper.deleteFolder(spec, config.getToken(), folderName, true);
    }
}
