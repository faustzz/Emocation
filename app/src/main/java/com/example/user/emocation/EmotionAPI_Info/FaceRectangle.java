package com.example.user.emocation.EmotionAPI_Info;

/**
 * Created by user on 2017-11-22.
 */

public class FaceRectangle {

    private Integer top;
    private Integer left;
    private Integer width;
    private Integer height;

    /**
     * No args constructor for use in serialization
     *
     */
    public FaceRectangle() {
    }

    /**
     *
     * @param height
     * @param width
     * @param left
     * @param top
     */
    public FaceRectangle(Integer top, Integer left, Integer width, Integer height) {
        super();
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

}