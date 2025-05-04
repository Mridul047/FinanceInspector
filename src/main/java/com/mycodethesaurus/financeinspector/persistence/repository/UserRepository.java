package com.mycodethesaurus.financeinspector.persistence.repository;

import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {}
