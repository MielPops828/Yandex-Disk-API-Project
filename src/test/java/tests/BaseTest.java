package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import utils.Configuration;
import utils.FolderHelper;

public abstract class BaseTest {
    protected RequestSpecification spec;
    protected Configuration config;
    protected String token;

    @BeforeClass
    public void setup(){
        config = ConfigFactory.create(Configuration.class);
        token = config.getToken();
        spec = new RequestSpecBuilder()
                .setBaseUri(config.getUrl())
                .setContentType(ContentType.JSON)
                .build();
    }
    @AfterMethod
    public void cleanup(){
        FolderHelper.deleteFolder(spec, config.getToken(), config.getUploadFolderInputName(), true);
        FolderHelper.deleteFolder(spec, config.getToken(), config.getUploadFolderOutputName(), true);
        FolderHelper.deleteFolder(spec, config.getToken(), config.getDownloadFolderName(), true);
    }
}
