package com.mycodethesaurus.financeinspector.component;

import com.mycodethesaurus.financeinspector.dto.ExpenseCreateRequest;
import com.mycodethesaurus.financeinspector.dto.ExpenseResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseUpdateRequest;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

  public ExpenseEntity createRequestToEntity(
      ExpenseCreateRequest request, UserEntity user, ExpenseCategoryEntity category) {
    ExpenseEntity expense = new ExpenseEntity();
    expense.setUser(user);
    expense.setCategory(category);
    expense.setAmount(request.getAmount());
    expense.setCurrencyCode(request.getCurrencyCode());
    expense.setDescription(request.getDescription());
    expense.setExpenseDate(request.getExpenseDate());
    expense.setLocation(request.getLocation());
    expense.setMerchant(request.getMerchant());
    expense.setPaymentMethod(request.getPaymentMethod());
    expense.setReceiptUrl(request.getReceiptUrl());
    expense.setNotes(request.getNotes());
    expense.setTags(request.getTags());
    return expense;
  }

  public void updateEntityFromRequest(
      ExpenseEntity expense, ExpenseUpdateRequest request, ExpenseCategoryEntity category) {
    expense.setCategory(category);
    expense.setAmount(request.getAmount());
    expense.setCurrencyCode(request.getCurrencyCode());
    expense.setDescription(request.getDescription());
    expense.setExpenseDate(request.getExpenseDate());
    expense.setLocation(request.getLocation());
    expense.setMerchant(request.getMerchant());
    expense.setPaymentMethod(request.getPaymentMethod());
    expense.setReceiptUrl(request.getReceiptUrl());
    expense.setNotes(request.getNotes());
    expense.setTags(request.getTags());
  }

  public ExpenseResponse entityToResponse(ExpenseEntity expense) {
    ExpenseResponse response = new ExpenseResponse();
    response.setId(expense.getId());
    response.setUserId(expense.getUser().getId());
    response.setCategory(buildCategorySummary(expense.getCategory()));
    response.setAmount(expense.getAmount());
    response.setCurrencyCode(expense.getCurrencyCode());
    response.setDescription(expense.getDescription());
    response.setExpenseDate(expense.getExpenseDate());
    response.setLocation(expense.getLocation());
    response.setMerchant(expense.getMerchant());
    response.setPaymentMethod(expense.getPaymentMethod());
    response.setReceiptUrl(expense.getReceiptUrl());
    response.setNotes(expense.getNotes());
    response.setTags(expense.getTags());
    response.setCreatedOn(expense.getCreatedOn());
    response.setUpdatedOn(expense.getUpdatedOn());
    return response;
  }

  public List<ExpenseResponse> entitiesToResponses(List<ExpenseEntity> expenses) {
    return expenses.stream().map(this::entityToResponse).collect(Collectors.toList());
  }

  private ExpenseResponse.CategorySummary buildCategorySummary(ExpenseCategoryEntity category) {
    if (category == null) {
      return null;
    }

    ExpenseResponse.CategorySummary summary = new ExpenseResponse.CategorySummary();
    summary.setId(category.getId());
    summary.setName(category.getName());
    summary.setColorCode(category.getColorCode());
    summary.setIconName(category.getIconName());

    // Include parent category if exists
    if (category.getParent() != null) {
      summary.setParent(buildCategorySummary(category.getParent()));
    }

    return summary;
  }
}
