package com.mycodethesaurus.financeinspector.persistence.repository;

import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByUserName(String userName);

  Optional<UserEntity> findByEmail(String email);

  boolean existsByUserName(String userName);

  boolean existsByEmail(String email);

  @Query("SELECT u FROM UserEntity u WHERE u.userName = :userName AND u.id != :excludeId")
  Optional<UserEntity> findByUserNameExcludingId(
      @Param("userName") String userName, @Param("excludeId") Long excludeId);

  @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.id != :excludeId")
  Optional<UserEntity> findByEmailExcludingId(
      @Param("email") String email, @Param("excludeId") Long excludeId);
}
