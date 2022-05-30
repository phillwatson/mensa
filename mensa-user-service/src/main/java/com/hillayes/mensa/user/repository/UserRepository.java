package com.hillayes.mensa.user.repository;

import com.hillayes.mensa.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
