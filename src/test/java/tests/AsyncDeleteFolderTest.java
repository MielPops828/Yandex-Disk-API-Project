package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.FolderHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Yandex-Disk-API-Test")
@Feature("Асинхронное удаление папки")
public class AsyncDeleteFolderTest extends BaseTest{
    @Test
    @Description("Асинхронное удаление существующей папки")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteFolderAsync() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        String href = FolderHelper.deleteFolderAsync(spec, config.getToken(), folderName, false);
        FolderHelper.waitForOperationCompletion(spec, config.getToken(), href);
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", folderName)
                .get("/v1/disk/resources")
                .then()
                .statusCode(404);
    }

    @Test
    @Description("Асинхронное удаление существующей папки без помещения в корзину")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteFolderAsyncNonTrash() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        String href = FolderHelper.deleteFolderAsync(spec, config.getToken(), folderName, true);
        FolderHelper.waitForOperationCompletion(spec, config.getToken(), href);
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", folderName)
                .get("/v1/disk/resources")
                .then()
                .statusCode(404);
    }

    @Test
    @Description("Асинхронное удаление существующей папки без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteFolderAsyncNoAuth() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        given(spec)
                .queryParam("path", folderName)
                .queryParam("force_async", true)
                .delete("/v1/disk/resources")
                .then()
                .statusCode(401)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
        Assert.assertTrue(FolderHelper.folderExists(spec, config.getToken(), folderName));
    }

    @Test
    @Description("Асинхронное удаление папки с указанием несуществующего названия папки")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteFolderAsyncNonExists() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", "testtest")
                .queryParam("force_async", true)
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
    @Description("Асинхронное удаление папки без указания названия папки")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteFolderAsyncNotFolderName() {
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("force_async", true)
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
    @Description("Асинхронное удаление папки - повторный DELETE на ту же папку во время выполнения операции")
    @Severity(SeverityLevel.NORMAL)
    public void testDoubleDeleteAsync() {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", folderName)
                .queryParam("force_async", true)
                .delete("/v1/disk/resources")
                .then()
                .statusCode(202);
        given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", folderName)
                .queryParam("force_async", true)
                .delete("/v1/disk/resources")
                .then()
                .statusCode(423)
                .body(
                        "error", notNullValue(),
                        "description", notNullValue(),
                        "message", notNullValue()
                );
    }
}
