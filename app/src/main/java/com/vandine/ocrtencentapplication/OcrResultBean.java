package com.vandine.ocrtencentapplication;

import java.util.ArrayList;

public class OcrResultBean {
    private ArrayList<TextDetection> TestDetections;
    private String Language;
    private Float Angel;
    private Integer PdfPageSize;
    private String RequestId;

    public ArrayList<TextDetection> getTestDetections() {
        return TestDetections;
    }

    public void setTestDetections(ArrayList<TextDetection> testDetections) {
        TestDetections = testDetections;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public Float getAngel() {
        return Angel;
    }

    public void setAngel(Float angel) {
        Angel = angel;
    }

    public Integer getPdfPageSize() {
        return PdfPageSize;
    }

    public void setPdfPageSize(Integer pdfPageSize) {
        PdfPageSize = pdfPageSize;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }
}
