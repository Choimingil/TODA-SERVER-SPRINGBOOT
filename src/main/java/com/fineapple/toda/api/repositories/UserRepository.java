package com.fineapple.toda.api.repositories;

import com.fineapple.toda.api.entities.User;
import com.fineapple.toda.api.entities.mappings.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmailAndAppPasswordNot(String email, int status);
    boolean existsByUserIDAndEmail(long userID, String email);
    boolean existsByUserCodeAndAppPasswordNot(String userCode, int appPassword);
    boolean existsByUserIDAndPasswordAndAppPasswordNot(long userID, String password, int appPassword);
    User findByUserID(long userID);

    @Query("select u as user, ui.url as profile from User u inner join UserImage ui on ui.userID = u.userID where u.userCode like :userCode and ui.status not like 0")
    UserDetail getUserDetailByUserCode(String userCode);

    @Query("select u as user, ui.url as profile from User u inner join UserImage ui on ui.userID = u.userID where u.email like :email and ui.status not like 0")
    UserDetail getUserDetailByEmail(String email);

    @Modifying
    @Query("UPDATE User SET appPassword = 99999 WHERE userID = :userID")
    void deleteUser(long userID);
}
