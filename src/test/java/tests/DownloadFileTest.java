package tests;

import dto.OperationResponse;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.FolderHelper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;

@Epic("Yandex-Disk-API-Test")
@Feature("Скачивание тестового файла")
public class DownloadFileTest extends BaseTest{
    @Test
    @Description("Проверка скачивания тестового файла")
    @Severity(SeverityLevel.NORMAL)
    public void testDownloadFile() throws IOException, InterruptedException {
        String content = "username=SDET\npassword=secret_key";
        FolderHelper.createFolder(spec, config.getToken(), config.getDownloadFolderName());
        Path file = Files.createTempFile("data", ".txt");
        Files.writeString(file, content);
        FolderHelper.uploadFileOnFolder(spec, config.getToken(), config.getDownloadFolderName() + "/" + "data.txt", file.toFile());

        String downloadResponse = given(spec)
                .header("Authorization", "OAuth " + config.getToken())
                .queryParam("path", config.getDownloadFolderName() + "/" + "data.txt")
                .get("/v1/disk/resources/download")
                .then()
                .statusCode(200)
                .extract()
                .as(OperationResponse.class).getHref();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(downloadResponse, "Ссылка на скачивание отсутствует");
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadResponse))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        softAssert.assertEquals(response.statusCode(), 200, "Статус ответа должен быть 200");
        softAssert.assertEquals(response.body().trim(), content.trim(), "Содержимое файла не совпадает");
        softAssert.assertAll();

        Files.deleteIfExists(file);
    }
}
