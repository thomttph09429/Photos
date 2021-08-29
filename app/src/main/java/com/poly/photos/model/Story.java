package com.poly.photos.model;

public class Story {
    private String image;
    private long timeStart;
    private long timeEnd;

    public Story() {
    }

    private String storyId;
    private String userId;

    public Story(String image, long timeStart, long timeEnd, String storyId, String userId) {
        this.image = image;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.storyId = storyId;
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
