package com.healthapp.Nirvana.MoodPrediction;

import lombok.Data;

@Data
public class MoodPredictResponse {
    PatientCycleDto data;
    String narrativeInsight;

}
