package com.healthapp.Nirvana.Consent.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DoctorSummary {
    private Long ConsentId;
    private Long doctorId;
    private String doctorName;
    private String doctorEmail;

    public DoctorSummary(Long id, Long doctorId, String username, String email) {
        this.ConsentId = id;
        this.doctorId = doctorId;
        this.doctorName = username;
        this.doctorEmail = email;

    }
}
