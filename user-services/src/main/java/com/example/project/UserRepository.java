package com.example.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUserName(String username);
    List<User> findAllByUserNameAndAge(String username, int age);
    boolean existsByUserName(String userName);
}
