package ru.wheelman.weather.data.data_sources.network;

import java.util.List;

import ru.wheelman.weather.domain.entities.FiveDayForecast;

public class WorstConditionFinder {
    private final List<FiveDayForecast.OneDay.DataPiece> dataPieces;
    private String worstWeatherConditionDescription;
    private String worstWeatherIconURL;

    public WorstConditionFinder(List<FiveDayForecast.OneDay.DataPiece> dataPieces) {
        this.dataPieces = dataPieces;

        findWorst();
    }

    public String getWorstWeatherConditionDescription() {
        return worstWeatherConditionDescription;
    }

    public String getWorstWeatherIconURL() {
        return worstWeatherIconURL;
    }

    private void findWorst() {

        for (int i = 0; i < Conditions.getConditions().size(); i++) {
            for (int j = 0; j < dataPieces.size(); j++) {

                String mDescription = dataPieces.get(j).getWeatherConditionDescription().toLowerCase();
                String targetDescription = Conditions.getConditions().get(i)[0][0].toLowerCase();

                if (mDescription.equals(targetDescription)) {
                    worstWeatherConditionDescription = mDescription;
                    worstWeatherIconURL = dataPieces.get(j).getWeatherIconURL();
                    return;
                }
            }
        }

    }
}
