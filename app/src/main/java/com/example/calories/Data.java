package com.example.calories;

public class Data {

    private String receivedData;

    public Data(String receivedData) {
        this.receivedData = receivedData;
    }

    @Override
    public String toString() {
        return "Data{" +
                "receivedData='" + receivedData + '\'' +
                '}';
    }
}
