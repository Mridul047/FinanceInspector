package com.mycodethesaurus.financeinspector.component;

import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.persistence.entity.SalaryIncomeEntity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IncomeMapper {

  public List<SalaryIncomeDto> convertEntityListToDtoList(
      List<SalaryIncomeEntity> salaryIncomeEntities) {

    return salaryIncomeEntities.stream()
        .map(
            entity ->
                new SalaryIncomeDto(
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
                    entity.getUpdatedOn()))
        .toList();
  }
}
