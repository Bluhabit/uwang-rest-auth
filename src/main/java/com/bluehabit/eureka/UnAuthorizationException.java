/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka;

public class UnAuthorizationException extends Exception{
    public UnAuthorizationException(String message){
        super(message);
    }

}
