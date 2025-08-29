package com.mycodethesaurus.financeinspector.component;

import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
import com.mycodethesaurus.financeinspector.persistence.entity.SalaryIncomeEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IncomeMapper {

  public List<SalaryIncomeDto> convertEntityListToDtoList(
      List<SalaryIncomeEntity> salaryIncomeEntities) {

    return salaryIncomeEntities.stream().map(this::convertEntityToDto).toList();
  }

  public SalaryIncomeDto convertEntityToDto(SalaryIncomeEntity entity) {
    return new SalaryIncomeDto(
        entity.getId(),
        entity.getUserEntity().getId(),
        entity.getCurrencyCode(),
        entity.getBasicAmount(),
        entity.getHraAmount(),
        entity.getOtherAllowanceAmount(),
        entity.getBonusAmount(),
        entity.getEmpPfAmount(),
        entity.getProfessionTaxAmount(),
        entity.getIncomeTaxAmount(),
        entity.getCreatedOn(),
        entity.getUpdatedOn());
  }

  public SalaryIncomeEntity createRequestToEntity(
      SalaryIncomeCreateRequest request, UserEntity userEntity) {
    SalaryIncomeEntity entity = new SalaryIncomeEntity();
    entity.setUserEntity(userEntity);
    entity.setCurrencyCode(request.getCurrencyCode());
    entity.setBasicAmount(request.getBasicAmount());
    entity.setHraAmount(request.getHraAmount());
    entity.setOtherAllowanceAmount(request.getOtherAllowanceAmount());
    entity.setBonusAmount(request.getBonusAmount());
    entity.setEmpPfAmount(request.getEmpPfAmount());
    entity.setProfessionTaxAmount(request.getProfessionTaxAmount());
    entity.setIncomeTaxAmount(request.getIncomeTaxAmount());
    entity.setCreatedOn(LocalDateTime.now());
    entity.setUpdatedOn(LocalDateTime.now());
    return entity;
  }

  public void updateEntityFromRequest(
      SalaryIncomeEntity entity, SalaryIncomeUpdateRequest request) {
    entity.setCurrencyCode(request.getCurrencyCode());
    entity.setBasicAmount(request.getBasicAmount());
    entity.setHraAmount(request.getHraAmount());
    entity.setOtherAllowanceAmount(request.getOtherAllowanceAmount());
    entity.setBonusAmount(request.getBonusAmount());
    entity.setEmpPfAmount(request.getEmpPfAmount());
    entity.setProfessionTaxAmount(request.getProfessionTaxAmount());
    entity.setIncomeTaxAmount(request.getIncomeTaxAmount());
    entity.setUpdatedOn(LocalDateTime.now());
  }
}
