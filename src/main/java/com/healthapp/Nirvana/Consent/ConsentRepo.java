package com.healthapp.Nirvana.Consent;

import com.healthapp.Nirvana.Consent.Dto.Consent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsentRepo extends JpaRepository<Consent, Long> {
        Optional <Consent> findBydoctorIdAndPatientId(Long doctorid, Long patientid);       //default present
}
