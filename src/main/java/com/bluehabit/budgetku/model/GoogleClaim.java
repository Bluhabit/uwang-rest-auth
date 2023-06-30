package com.bluehabit.budgetku.model;

public record GoogleClaim(
    String email,
    String picture,
    String fullName,
    String locale,
    String message
){

}