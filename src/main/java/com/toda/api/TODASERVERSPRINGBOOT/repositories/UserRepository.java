package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmailAndAppPasswordNot(String email, int status);
    boolean existsByUserIDAndEmail(long userID, String email);
    boolean existsByUserCodeAndAppPasswordNot(String userCode, int appPassword);
    boolean existsByUserIDAndPasswordAndAppPasswordNot(long userID, String password, int appPassword);
    User findByUserID(long userID);

    @Modifying
    @Transactional
    @Query("UPDATE User SET appPassword = 99999 WHERE userID = :userID")
    void deleteUser(long userID);
}
