package com.JohnBravos.bookhub_manager.core.exceptions.custom;

import com.JohnBravos.bookhub_manager.core.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class CannotDeleteException extends BaseException {

        public CannotDeleteException(String resourceType, String reason) {
            super(HttpStatus.CONFLICT, "CANNOT_DELETE",
                    String.format("Cannot delete %s: %s", resourceType, reason));
        }

        public String getResourceType () {
            return getMessage().contains("book") ? "book" :
                    getMessage().contains("user") ? "user" :
                            getMessage().contains("author") ? "author" : "resource";
        }

        public String getSuggestedAction () {
            return "Please resolve the dependencies before attempting to delete again";
        }
    }
