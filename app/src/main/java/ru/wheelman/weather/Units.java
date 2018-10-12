package ru.wheelman.weather;

enum Units {
    CELSIUS(0), FAHRENHEIT(1);

    private int unitIndex;

    Units(int unitIndex) {
        this.unitIndex = unitIndex;
    }

    public static Units getUnitByIndex(int unitIndex) {
        for (int i = 0; i < Units.values().length; i++) {
            if (Units.values()[i].unitIndex == unitIndex)
                return Units.values()[i];
        }
        return null;
    }

    public int getUnitIndex() {
        return unitIndex;
    }
}
