package com.hasalp.ctoulel_user_service.dao;

import com.hasalp.ctoulel_user_service.model.User;
import com.hasalp.ctoulel_user_service.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    private final UserRepository repo;

    public UserDao(UserRepository repo) {
        this.repo = repo;
    }

    public User save(User user) {
        return repo.save(user);
    }
    public List<User> getUsers() {
        return repo.findAll();
    }
    public List<User> findAllById(List<Long> id) {
        return repo.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }
    public Boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return repo.findById(id);
    }

    public void delete(Long id) {
         repo.deleteById(id);
    }
}
