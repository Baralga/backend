package com.baralga.account;

import java.util.Optional;

public interface AuthenticationManager {

	Optional<User> getCurrentUser();

}
