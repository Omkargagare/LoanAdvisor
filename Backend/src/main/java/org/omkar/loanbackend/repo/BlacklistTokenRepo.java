package org.omkar.loanbackend.repo;

import org.omkar.loanbackend.model.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistTokenRepo extends JpaRepository<BlacklistToken,String> {
}
