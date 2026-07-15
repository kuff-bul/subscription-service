package ru.adnr.subscriptionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.adnr.subscriptionservice.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
}
