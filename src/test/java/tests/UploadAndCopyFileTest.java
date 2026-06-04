package tests;

import dto.ErrorResponse;
import dto.OperationResponse;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.FileHelper;
import utils.FolderHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Epic("Yandex-Disk-API-Test")
@Feature("Загрузка и копирование файла")
public class UploadAndCopyFileTest extends BaseTest{
    @Test
    @Description("Проверка загрузки и копирования файла")
    @Severity(SeverityLevel.NORMAL)
    public void testUploadAndCopyFile() throws IOException {
        FolderHelper.createFolder(spec, config.getToken(), config.getUploadFolderInputName());
        FolderHelper.createFolder(spec, config.getToken(), config.getUploadFolderOutputName());

        Path file = Files.createTempFile("data", ".txt");
        Files.writeString(file, "username=SDET\npassword=secret_key");

        FolderHelper.uploadFileOnFolder(spec, config.getToken(), config.getUploadFolderInputName() + "/" + "data.txt", file.toFile());

        Response responseFirst = FileHelper.copyFileFromFolder(spec, config.getToken(), config.getUploadFolderInputName(), config.getUploadFolderOutputName(), "data.txt", 201);
        OperationResponse operationResponse = responseFirst.as(OperationResponse.class);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(operationResponse.getMethod());
        softAssert.assertNotNull(operationResponse.getHref());
        softAssert.assertNotNull(operationResponse.getTemplated());

        Response responseSecond = FileHelper.copyFileFromFolder(spec, config.getToken(), config.getUploadFolderInputName(), config.getUploadFolderOutputName(), "data.txt", 409);
        ErrorResponse errorResponse = responseSecond.as(ErrorResponse.class);
        softAssert.assertNotNull(errorResponse.getError());
        softAssert.assertNotNull(errorResponse.getDescription());
        softAssert.assertNotNull(errorResponse.getMessage());
        softAssert.assertAll();

        Files.deleteIfExists(file);
    }
}
