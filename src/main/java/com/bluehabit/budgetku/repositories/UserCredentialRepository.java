package com.bluehabit.budgetku.repositories;

import com.bluehabit.budgetku.entity.UserCredential;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserCredentialRepository extends PagingAndSortingRepository<UserCredential,String>, CrudRepository<UserCredential,String> {
    @Query("SELECT case when count(m) > 0 then true else false end from UserCredential as m where m.userEmail =:userEmail")
    public Boolean exist(String userEmail);

    Optional<UserCredential> findByUserEmail(String userEmail);
}
