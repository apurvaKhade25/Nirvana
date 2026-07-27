package com.healthapp.Nirvana.Consent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsentRepo extends JpaRepository<Consent, Long> {
        Optional <Consent> findBydoctorIdAndPatientId(Long doctorid, Long patientid);
        //default present

        List <Consent> findByPatientIdAndStatus(Long patientid, ConsentStatus status);

        List <Consent> findByDoctorIdAndStatus(Long doctorid, ConsentStatus status);
}
