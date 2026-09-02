package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.bank.model.IssuingBankWithPmsIdView;
import com.nicico.internal.sales.bank.repository.IssuingBankWithPmsIdRepository;
import com.nicico.internal.sales.common.properties.PMSProperties;
import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.pms.dto.PMSCreateBankDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class PmsBankCreateRabbitServiceImpl implements PmsBankCreateRabbitService {
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
	private final RabbitTemplate rabbitTemplate;
	private final RabbitConfigPMSProperties rabbitConfigPMSProperties;
	private final PMSProperties pmsProperties;
	private final IssuingBankWithPmsIdRepository issuingBankWithPmsIdRepository;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;

	@Override
	public void createBank(PMSCreateBankDto.Create bank) {
		log.info("send to rabbit for Creating bank in pms {}", bank);
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.BANK)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;


		if (bank.getBaseBankId() != null && issuingBankWithPmsIdRepository
				.findFirstByPmsBaseBankIdAndBranchCodeAndPmsLcBankIdNotNull(bank.getBaseBankId(), bank.getBranchCode())
				.isPresent())
			throw new InternalSaleCustomException.ValidationException(String.format("bank with pmsBaseId %s and branchCode %s already exists",
					bank.getBaseBankId(), bank.getBranchCode()));
		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getBank().getRoutingKey(),
				new PMSCreateBankDto.RabbitListenerRequestDTO(
						pmsProperties.getBank().getUrl(),
						bank.getAccountingId(),
						bank,
						rabbitConfigPMSProperties.getQueues().getBank().getResponseRoutingKey()
				));
	}

	@Override
	public void createBank(IssuingBankWithPmsIdView bank) {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.BANK)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;

		if (bank.getPmsLcBankId() != null) {
			log.warn("issuing bank {} ({} {}) already exists in PMS: pmsBaseBankId={}, pmsLcBankId={}",
					bank.getId(), bank.getBankName(), bank.getBranchName(), bank.getPmsBaseBankId(), bank.getPmsLcBankId());
			throw new InternalSaleCustomException.ValidationException(String.format(
					"issuing bank %s (%s %s) already exists in PMS (pmsLcBankId=%s)",
					bank.getId(), bank.getBankName(), bank.getBranchName(), bank.getPmsLcBankId()));
		}

		PMSCreateBankDto.Create bankDto = PMSCreateBankDto.Create.builderr().baseBankId(bank.getPmsBaseBankId())
				.user(pmsProperties.getPreFactor().getUser())
				.pass(pmsProperties.getPreFactor().getPass())
				.accountingId(-bank.getId())
				.branchCode(bank.getBranchCode())
				.branchDescription(String.format("%s %s %s", bank.getBankName(), bank.getBranchName(), bank.getCity()))
				.buildd();
		createBank(bankDto);
	}

	@Override
	public void createBank(Long issueBankId) {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.BANK)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;

		createBank(issuingBankWithPmsIdRepository
				.findById(issueBankId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("no bank with id " + issueBankId)));
	}
}
