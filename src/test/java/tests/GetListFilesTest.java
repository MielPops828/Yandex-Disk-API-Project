package tests;

import io.qameta.allure.*;
import org.testng.annotations.Test;
import utils.FolderHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Epic("Yandex-Disk-API-Test")
@Feature("Получение списка файлов")
public class GetListFilesTest extends BaseTest{
    @Test
    @Description("Проверка получения списка файлов")
    @Severity(SeverityLevel.NORMAL)
    public void testGetListFiles() throws IOException {
        String folderName = "test_" + System.currentTimeMillis();
        FolderHelper.createFolder(spec, config.getToken(), folderName);
        List<Path> files = new ArrayList<>();
        for (int i = 1; i < 3; i++){
            Path file = Files.createTempFile("data_" + i, ".txt");
            Files.writeString(file, "test content " + i);
            FolderHelper.uploadFileOnFolder(spec, config.getToken(), folderName + "/" + file.getFileName().toString(), file.toFile());
            files.add(file);
        }
        try {
            given(spec)
                    .header("Authorization", "OAuth " + config.getToken())
                    .get("/v1/disk/resources/files")
                    .then()
                    .statusCode(200)
                    .body(matchesJsonSchemaInClasspath("schemas/files-schema.json"));
        }
        finally {
            for (Path file : files) {
                Files.deleteIfExists(file);
            }
            FolderHelper.deleteFolder(spec, config.getToken(), folderName, true);
        }
    }
}
