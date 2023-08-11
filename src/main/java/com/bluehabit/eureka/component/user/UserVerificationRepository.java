/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserVerificationRepository extends PagingAndSortingRepository<UserVerification, String>, CrudRepository<UserVerification, String> {
    Optional<UserVerification> findByToken(String token);
}
