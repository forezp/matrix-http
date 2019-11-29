package io.github.forezp;

import java.util.List;

public class Keyswords {

    private int code;
    private List<String> contentKeywords;
    private List<String> titleKeywords;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<String> getContentKeywords() {
        return contentKeywords;
    }

    public void setContentKeywords(List<String> contentKeywords) {
        this.contentKeywords = contentKeywords;
    }

    public List<String> getTitleKeywords() {
        return titleKeywords;
    }

    public void setTitleKeywords(List<String> titleKeywords) {
        this.titleKeywords = titleKeywords;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
