/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.component.data.useCredential;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserCredentialRepository extends PagingAndSortingRepository<UserCredential, String>, CrudRepository<UserCredential, String> {
    Optional<UserCredential> findByEmail(String email);

    boolean existsByEmail(String email);
}
