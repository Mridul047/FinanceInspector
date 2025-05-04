package com.mycodethesaurus.financeinspector.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sys_user", schema = "fip")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", unique = true, nullable = false)
  private String userName;

  @Column(name = "password", unique = true, nullable = false)
  private String password;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
  private List<SalaryIncomeEntity> salaryIncomeEntityList;

  @Column(name = "created_on", nullable = false)
  LocalDateTime createdOn;

  @Column(name = "updated_on", nullable = false)
  LocalDateTime updatedOn;
}
