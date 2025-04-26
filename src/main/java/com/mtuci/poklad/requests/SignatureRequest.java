package com.mtuci.poklad.requests;

public class SignatureRequest {

    private String threatName;       // Название угрозы
    private String signatureData;    // Данные сигнатуры
    private String fileType;         // Тип файла
    private Integer offsetStart;     // Начальный смещение
    private Integer offsetEnd;       // Конечный смещение

    // Конструктор
    public SignatureRequest(String threatName, String signatureData, String fileType,
                            Integer offsetStart, Integer offsetEnd) {
        this.threatName = threatName;
        this.signatureData = signatureData;
        this.fileType = fileType;
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
    }

    // Геттеры и сеттеры
    public String getThreatName() {
        return threatName;
    }

    public void setThreatName(String threatName) {
        this.threatName = threatName;
    }

    public String getSignatureData() {
        return signatureData;
    }

    public void setSignatureData(String signatureData) {
        this.signatureData = signatureData;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getOffsetStart() {
        return offsetStart;
    }

    public void setOffsetStart(Integer offsetStart) {
        this.offsetStart = offsetStart;
    }

    public Integer getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetEnd(Integer offsetEnd) {
        this.offsetEnd = offsetEnd;
    }
}
