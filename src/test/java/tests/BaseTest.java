package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.BeforeClass;
import utils.Configuration;

public abstract class BaseTest {
    protected RequestSpecification spec;
    protected Configuration config;

    @BeforeClass
    public void setup(){
        config = ConfigFactory.create(Configuration.class);
        spec = new RequestSpecBuilder()
                .setBaseUri(config.getUrl())
                .setContentType(ContentType.JSON)
                .build();
    }
}
