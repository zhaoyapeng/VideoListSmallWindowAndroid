package com.zhaoyp.video.media;

/**
 * @author zhaoyapeng
 * @version create time:16/5/15上午12:47
 * @Email zyp@jusfoun.com
 * @Description ${TODO}
 */
public class VideoEvent implements IEvent {
    public VideoEvent() {
    }

    private  int  position;
    private String videoUrl;
    private String articleId;//视频的文章id

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getPostion() {
        return position;
    }

    public void setPostion(int postion) {
        this.position = postion;
    }
}
