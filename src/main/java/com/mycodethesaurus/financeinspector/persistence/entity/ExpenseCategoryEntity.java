package com.mycodethesaurus.financeinspector.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "expense_categories", schema = "fip")
public class ExpenseCategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 100, unique = true)
  private String name;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "color_code", length = 7) // #FFFFFF format
  private String colorCode;

  @Column(name = "icon_name", length = 50)
  private String iconName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private ExpenseCategoryEntity parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ExpenseCategoryEntity> subcategories;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @CreationTimestamp
  @Column(name = "created_on", nullable = false)
  private LocalDateTime createdOn;

  @UpdateTimestamp
  @Column(name = "updated_on", nullable = false)
  private LocalDateTime updatedOn;

  @Column(name = "created_by", length = 100)
  private String createdBy;

  @Column(name = "updated_by", length = 100)
  private String updatedBy;
}
