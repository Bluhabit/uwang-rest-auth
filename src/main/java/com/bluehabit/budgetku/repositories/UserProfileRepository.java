package com.bluehabit.budgetku.repositories;

import com.bluehabit.budgetku.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserProfileRepository extends PagingAndSortingRepository<UserProfile, String>, CrudRepository<UserProfile, String> {
//    @Query("SELECT U FROM UserProfile  U WHERE U.deleted=false")
//    Page<UserProfile> getAllUsers(Pageable pageable);
}
