package com.baralga.account.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpModel {

    @NotNull
    @Size(min = 3, max = 30)
    private String username;

    @Email
    private String email;

    @NotNull
    @Size(min = 3, max = 30)
    private String password;

    public Optional<ObjectError> validatePassword() {
        return Optional.empty();
    }
}
