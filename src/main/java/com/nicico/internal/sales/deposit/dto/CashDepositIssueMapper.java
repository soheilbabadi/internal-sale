package com.nicico.internal.sales.deposit.dto;
import com.nicico.internal.sales.deposit.model.CashDepositIssueHistoryModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CashDepositIssueMapper {
	CashDepositIssueHistoryDto.Info toDTO(CashDepositIssueHistoryModel model);

	CashDepositIssueHistoryModel fromDTO(CashDepositIssueHistoryDto.Create request);

}
