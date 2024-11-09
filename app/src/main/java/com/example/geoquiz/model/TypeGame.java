package com.example.geoquiz.model;

public class TypeGame {
    String typeGameName;
    int typeGameImg, typeGameId;

    public TypeGame(int typeGameId, String typeGameName, int typeGameImg) {
        this.typeGameId = typeGameId;
        this.typeGameName = typeGameName;
        this.typeGameImg = typeGameImg;
    }

    public int getTypeGameId() {return typeGameId;}
    public void setTypeGameId(int typeId) {
        this.typeGameId = typeGameId;
    }
    public String getTypeGameName() {
        return typeGameName;
    }
    public void setTypeGameName(String typeGameName) {
        this.typeGameName = typeGameName;
    }
    public int getTypeGameImg() {
        return typeGameImg;
    }
    public void setTypeGameImg(int typeGameImg) {
        this.typeGameImg = typeGameImg;
    }


}
