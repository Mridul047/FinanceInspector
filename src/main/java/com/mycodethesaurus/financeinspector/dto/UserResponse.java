package com.mycodethesaurus.financeinspector.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private Long id;
  private String userName;
  private String firstName;
  private String lastName;
  private String email;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
}
