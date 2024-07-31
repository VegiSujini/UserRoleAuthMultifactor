package com.example.userRoleAuth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.userRoleAuth.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
