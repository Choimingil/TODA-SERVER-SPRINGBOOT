package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemRepository extends JpaRepository<User, Long> {
    boolean existsByEmailAndAppPasswordNot(String email, int status);
    boolean existsByUserIDAndEmail(long userID, String email);
}