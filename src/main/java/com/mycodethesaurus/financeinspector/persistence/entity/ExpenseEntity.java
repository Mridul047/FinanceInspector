package com.mycodethesaurus.financeinspector.persistence.entity;

import com.mycodethesaurus.financeinspector.enums.PaymentMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "expenses", schema = "fip")
public class ExpenseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private ExpenseCategoryEntity category;

  @Column(name = "amount", nullable = false, precision = 15, scale = 4)
  private BigDecimal amount;

  @Column(name = "currency_code", nullable = false, length = 3)
  private String currencyCode;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "expense_date", nullable = false)
  private LocalDate expenseDate;

  @Column(name = "location")
  private String location;

  @Column(name = "merchant")
  private String merchant;

  @Column(name = "payment_method")
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Column(name = "receipt_url")
  private String receiptUrl;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @ElementCollection
  @CollectionTable(
      name = "expense_tags",
      schema = "fip",
      joinColumns = @JoinColumn(name = "expense_id"))
  @Column(name = "tag")
  private Set<String> tags = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_on", nullable = false)
  private LocalDateTime createdOn;

  @UpdateTimestamp
  @Column(name = "updated_on", nullable = false)
  private LocalDateTime updatedOn;
}
