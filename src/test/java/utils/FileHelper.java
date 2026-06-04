package utils;

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
}
