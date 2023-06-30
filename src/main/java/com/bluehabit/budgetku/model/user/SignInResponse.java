package com.bluehabit.budgetku.model.user;

import com.bluehabit.budgetku.entity.UserCredential;

public record SignInResponse(
       String token,
       UserCredential credential
) {
}
