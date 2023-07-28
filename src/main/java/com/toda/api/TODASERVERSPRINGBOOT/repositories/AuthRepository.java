package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    UserInfo findByEmail(String email);

    // List로 리턴하는 예시
//    List<UserInfoMappings> findByUserName(String userName);

    @Transactional
    @Modifying
    @Query("UPDATE User SET password = :password WHERE email = :email")
    void setUserPasswordEncoded(@Param("email") String email, @Param("password") String password);
}