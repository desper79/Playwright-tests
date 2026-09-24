package com.playwright.tests.api.qasandbox.mythology;

public class MythologyFigure {
    private int id;
    private String name;
    private String category;
    private String desc;
    private String img;
    private String error;

    // Пустой конструктор (необходим для десериализации)
    public MythologyFigure() {}

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getError() { return error; }
}