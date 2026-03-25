package org.omkar.loanbackend.repo;

import org.omkar.loanbackend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users,Integer> {
    Users findByUsername(String username);

    boolean existsByUsername(String username);
}
