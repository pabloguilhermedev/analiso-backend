package com.analiso.user.repository;

import com.analiso.user.model.UserDashboardPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDashboardPreferencesRepository extends JpaRepository<UserDashboardPreferencesEntity, Long> {
}
