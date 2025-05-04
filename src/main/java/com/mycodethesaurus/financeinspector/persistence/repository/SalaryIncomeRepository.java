package com.mycodethesaurus.financeinspector.persistence.repository;

import com.mycodethesaurus.financeinspector.persistence.entity.SalaryIncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryIncomeRepository extends JpaRepository<SalaryIncomeEntity, Long> {}
