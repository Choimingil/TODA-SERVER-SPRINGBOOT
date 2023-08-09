package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserInfoDetail;
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

    @Query("select u.userID as userID, u.userCode as userCode, u.email as email, u.password as password, u.createAt as createAt, u.userName as userName, u.appPassword as appPassword, ui.url as profile from User u " +
            "inner join UserImage ui on ui.userID = u.userID where u.userCode like :userCode and ui.status not like 0")
    UserInfoDetail getUserDataByUserCode(String userCode);

    @Query("select u.userID as userID, u.userCode as userCode, u.email as email, u.password as password, u.createAt as createAt, u.userName as userName, u.appPassword as appPassword, ui.url as profile from User u " +
            "inner join UserImage ui on ui.userID = u.userID where u.email like :email and ui.status not like 0")
    UserInfoDetail getUserDataByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User SET appPassword = 99999 WHERE userID = :userID")
    void deleteUser(long userID);
}
