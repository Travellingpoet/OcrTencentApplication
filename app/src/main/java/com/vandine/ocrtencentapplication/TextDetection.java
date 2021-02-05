package com.vandine.ocrtencentapplication;

import java.util.ArrayList;

public class TextDetection {
    private String DetectedText;
    private int Confidence;
    private ArrayList<Coord> Polygon;
    private String AdvancedInfo;
    private ItemCoord ItemPolygon;

    public String getDetectedText() {
        return DetectedText;
    }

    public void setDetectedText(String detectedText) {
        DetectedText = detectedText;
    }

    public int getConfidence() {
        return Confidence;
    }

    public void setConfidence(int confidence) {
        Confidence = confidence;
    }

    public ArrayList<Coord> getPolygon() {
        return Polygon;
    }

    public void setPolygon(ArrayList<Coord> polygon) {
        Polygon = polygon;
    }

    public String getAdvancedInfo() {
        return AdvancedInfo;
    }

    public void setAdvancedInfo(String advancedInfo) {
        AdvancedInfo = advancedInfo;
    }

    public ItemCoord getItemPolygon() {
        return ItemPolygon;
    }

    public void setItemPolygon(ItemCoord itemPolygon) {
        ItemPolygon = itemPolygon;
    }

    static class Coord{
        private int X;
        private int Y;
    }
}
