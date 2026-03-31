package com.healthapp.Nirvana.Wellness.Dto;

import lombok.Data;

import java.util.List;

@Data
public class WellnessResponse {
    private Double avgMoodScore;
    private String avgFrequentMood;
    private Integer totalMoodLogs;
    private String bestday;     //best moodscore
    private String worstday;    //worst moodscore

    //journal stats
    private Integer totalJournalEntries;

    //ai info
    private String aiInsight;
    private List <EmotionDto> emotions;
    private List <String> recommendations;

}
