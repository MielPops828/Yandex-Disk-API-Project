package utils;

import dto.User;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class FileHelper {
    public static Response copyFileFromFolder(RequestSpecification spec, String token, String from, String to, String filepath, int status){
        return given(spec)
                .header("Authorization", "OAuth " + token)
                .queryParam("from", from + "/" + filepath)
                .queryParam("path", to + "/" + filepath)
                .post("/v1/disk/resources/copy")
                .then()
                .statusCode(status)
                .extract()
                .response();
    }
    public static User parseUser(String content) {
        String[] lines = content.split("\\R");
        String username = null;
        String password = null;
        for (String line : lines) {
            String[] parts = line.split("=", 2);
            if ("username".equals(parts[0])) {
                username = parts[1];
            }
            if ("password".equals(parts[0])) {
                password = parts[1];
            }
        }
        return new User(username, password);
    }
}
