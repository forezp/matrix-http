package io.github.forezp;

public class CommonUtils {

    public static String decorateUrl(String url) {
        url = url.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

}
