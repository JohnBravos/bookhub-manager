package com.JohnBravos.bookhub_manager.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateAuthorRequest(

        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,


        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        String nationality,

        LocalDate birthDate,

        @Size(max = 2000, message = "Biography must not exceed 2000 characters")
        String biography
) {}
