package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;

public class FolderHelper {
    public static void createFolder(RequestSpecification spec, String token, String folderName) {
        given(spec)
                .header("Authorization", "OAuth " + token)
                .queryParam("path", folderName)
                .put("/v1/disk/resources")
                .then()
                .statusCode(201);
    }
    public static void deleteFolder(RequestSpecification spec, String token, String folderName, boolean permanently) {
        given(spec)
                .header("Authorization", "OAuth " + token)
                .queryParam("path", folderName)
                .queryParam("permanently", permanently)
                .delete("/v1/disk/resources");
    }
    public static boolean folderExists(RequestSpecification spec, String token, String folderName) {
        return given(spec)
                .header("Authorization", "OAuth " + token)
                .queryParam("path", folderName)
                .get("/v1/disk/resources")
                .getStatusCode() == 200;
    }
    public static String getTrashPath(RequestSpecification spec, String token, String folderName) {
        Response response = given(spec)
                .header("Authorization", "OAuth " + token)
                .get("/v1/disk/trash/resources")
                .then()
                .statusCode(200)
                .extract()
                .response();
        List<Map<String, Object>> items = response.jsonPath().getList("_embedded.items");
        return items.stream()
                .map(item -> (String) item.get("path"))
                .filter(path -> path.contains(folderName))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Папка не найдена в корзине"));
    }

    public static void deleteTrashFolder(RequestSpecification spec, String token, String trashPath) {
        given(spec)
                .header("Authorization", "OAuth " + token)
                .queryParam("path", trashPath)
                .queryParam("permanently", true)
                .delete("/v1/disk/resources");
    }

    public static void waitForOperationCompletion(RequestSpecification spec, String token, String href) {
        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .until(() -> {
                    Response response = given(spec)
                            .header("Authorization", "OAuth " + token)
                            .get(href);
                    return "success".equals(response.jsonPath().getString("status"));
                });
    }

    public static String deleteFolderAsync(RequestSpecification spec, String token, String folderName, boolean permanently) {
        Response response = given(spec)
                .header("Authorization", "OAuth " + token)
                .queryParam("path", folderName)
                .queryParam("force_async", true)
                .queryParam("permanently", permanently)
                .delete("/v1/disk/resources")
                .then()
                .statusCode(202)
                .extract()
                .response();
        return response.jsonPath().getString("href");
    }

    public static String restoreFolderAsync(RequestSpecification spec, String token, String trashPath) {
        Response response = given(spec)
                .header("Authorization", "OAuth " + token)
                .queryParam("path", trashPath)
                .queryParam("force_async", true)
                .put("/v1/disk/trash/resources/restore")
                .then()
                .statusCode(202)
                .extract()
                .response();
        return response.jsonPath().getString("href");
    }
}
