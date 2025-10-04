package com.eco.EcoChain.enums;

public enum MaterialCategory{
    METAL("Металл"),
    PLASTIC("Пластик"),
    WOOD("Дерево"),
    CONSTRUCTION("Строительные материалы"),
    TEXTILE("Текстиль"),
    ELECTRONICS("Электроника"),
    PAPER("Бумага и картон"),
    ORGANIC("Органические отходы"),
    CHEMICAL("Химические вещества"),
    GLASS("Стекло");

    private final String displayName;

    MaterialCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}