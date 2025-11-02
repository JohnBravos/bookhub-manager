package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateUsernameException extends BaseException {

    public DuplicateUsernameException(String username)
    {
        super(HttpStatus.CONFLICT, "DUPLICATE_USERNAME",
                "Username '" + username + "' is already taken");
    }

    public String getSuggestedSolution() {
        return "Please choose another username or try adding numbers/" +
                "special characters";
    }
}
