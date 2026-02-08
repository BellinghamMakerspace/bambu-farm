package com.tfyre.bambu.user;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserEntity CRUD operations.
 * Uses Panache for simplified data access.
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {

    public Optional<UserEntity> findByUsername(String username) {
        return find("username", username.toLowerCase()).firstResultOptional();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public List<UserEntity> listAllOrdered() {
        return list("ORDER BY username");
    }

    @Transactional
    public UserEntity createUser(String username, String passwordHash, String role) {
        UserEntity user = new UserEntity(username, passwordHash, role);
        persist(user);
        return user;
    }

    @Transactional
    public UserEntity createUser(String username, String passwordHash, String role, String source) {
        UserEntity user = new UserEntity(username, passwordHash, role);
        user.setSource(source);
        persist(user);
        return user;
    }

    @Transactional
    public boolean updatePassword(String username, String newPasswordHash) {
        return findByUsername(username).map(user -> {
            user.setPasswordHash(newPasswordHash);
            persist(user);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean updateRole(String username, String newRole) {
        return findByUsername(username).map(user -> {
            user.setRole(newRole);
            persist(user);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean deleteByUsername(String username) {
        return findByUsername(username).map(user -> {
            delete(user);
            return true;
        }).orElse(false);
    }
}
