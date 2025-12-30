package com.vbs.demo.repositories;

import com.vbs.demo.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    User findByUsername(String username);

    User findByEmail(String value);

    List<User> findAllByRole(String customer, Sort sort);

    List<User> findByUsernameContainingIgnoreCaseAndRole(String keyword, String customer);
}
