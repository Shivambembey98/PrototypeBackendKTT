package com.ktt.auth.repositories;

import com.ktt.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepo extends JpaRepository<User, Long> {
    User findByLogin(String login);
    User findByEmailIdAndCompanyCode(String emailId, String companyCode);
    User findByToken(String token);

}