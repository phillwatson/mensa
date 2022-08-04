package com.hillayes.mensa.user.repository;

import com.hillayes.mensa.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public interface UserRepository extends JpaRepository<User, UUID> {
}
