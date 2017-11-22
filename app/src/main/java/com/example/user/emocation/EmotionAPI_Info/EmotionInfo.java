package com.example.user.emocation.EmotionAPI_Info;

/**
 * Created by user on 2017-11-22.
 */

public class EmotionInfo {

    private FaceRectangle faceRectangle;
    private Scores scores;

        /**
         * No args constructor for use in serialization
         *
         */
        public EmotionInfo() {
        }

        /**
         *
         * @param scores
         * @param faceRectangle
         */
        public EmotionInfo(FaceRectangle faceRectangle, Scores scores) {
            super();
            this.faceRectangle = faceRectangle;
            this.scores = scores;
        }

        public FaceRectangle getFaceRectangle() {
            return faceRectangle;
        }

        public void setFaceRectangle(FaceRectangle faceRectangle) {
            this.faceRectangle = faceRectangle;
        }

        public Scores getScores() {
            return scores;
        }

        public void setScores(Scores scores) {
            this.scores = scores;
        }

}