package cn.edu.nju.software.tongbaoshipper.model;

public class Banner {

    /**
     * Banner image url
     */
    private String imgUrl;
    /**
     * click response html url
     */
    private String targetUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
