/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.user;

import com.bluehabit.eureka.component.user.UserCredential;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserCredentialRepository extends PagingAndSortingRepository<UserCredential,String>, CrudRepository<UserCredential,String> {
    boolean existsByUserEmail(String userEmail);
    @Query("SELECT case when count(m) > 0 then true else false end from UserCredential as m where m.userEmail =:userEmail")
    Boolean exist(String userEmail);

    Optional<UserCredential> findByUserEmail(String userEmail);
}
