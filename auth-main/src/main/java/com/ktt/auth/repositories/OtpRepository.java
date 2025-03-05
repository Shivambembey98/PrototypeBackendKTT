package com.ktt.auth.repositories;

import com.ktt.auth.entities.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp,Long> {

    Otp findByEmailIdAndCompanyCode(String emailId,String companyCode);

}
