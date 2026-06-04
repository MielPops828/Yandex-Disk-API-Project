package utils;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config.properties"})
public interface Configuration extends Config {
    @Key("base.url")
    String getUrl();

    @Key("access.token")
    String getToken();

    @Key("user.login")
    String getUserLogin();

    @Key("user.display.name")
    String getUserDisplayName();

    @Key("create.folder.name")
    String getFolderName();

    @Key("upload.folder.input.name")
    String getUploadFolderInputName();

    @Key("upload.folder.output.name")
    String getUploadFolderOutputName();

    @Key("download.folder.name")
    String getDownloadFolderName();
}
