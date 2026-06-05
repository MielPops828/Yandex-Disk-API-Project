package tests;

import dto.OperationResponse;
import dto.User;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.FileHelper;
import utils.FolderHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;

@Epic("Yandex-Disk-API-Test")
@Feature("Скачивание тестового файла")
public class DownloadFileTest extends BaseTest{
    @Test
    @Description("Проверка скачивания тестового файла")
    @Severity(SeverityLevel.NORMAL)
    public void testDownloadFile() throws IOException {
        String content = "username=SDET\npassword=secret_key";
        User expectedUser = new User("SDET", "secret_key");
        FolderHelper.createFolder(spec, config.getToken(), config.getDownloadFolderName());
        Path file = Files.createTempFile("data", ".txt");
        try {
            Files.writeString(file, content);
            FolderHelper.uploadFileOnFolder(spec, config.getToken(), config.getDownloadFolderName() + "/" + "data.txt", file.toFile());

            OperationResponse downloadResponse = given(spec)
                    .header("Authorization", "OAuth " + config.getToken())
                    .queryParam("path", config.getDownloadFolderName() + "/" + "data.txt")
                    .get("/v1/disk/resources/download")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(OperationResponse.class);
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertNotNull(downloadResponse.getHref(), "Ссылка на скачивание отсутствует");
            Response downloadedContent =
                    given().urlEncodingEnabled(false)
                            .get(downloadResponse.getHref())
                            .then()
                            .statusCode(200)
                            .extract()
                            .response();
            String downloadedFile = downloadedContent.getBody().asString();
            User actualUser = FileHelper.parseUser(downloadedFile);
            softAssert.assertEquals(expectedUser, actualUser, "Содержимое файла не совпадает");
            softAssert.assertAll();
        }
        finally {
            Files.deleteIfExists(file);
            FolderHelper.deleteFolder(spec, config.getToken(), config.getDownloadFolderName(), true);
        }
    }
}
