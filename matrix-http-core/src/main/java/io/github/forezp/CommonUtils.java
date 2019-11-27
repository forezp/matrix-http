package io.github.forezp;

public class CommonUtils {

    public static String decorateUrl(String url) {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        return url;
    }

}
