//package com.nicico.internal.sales.gaam.service;
//
//import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
//import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
//import com.nicico.copper.common.dto.search.SearchDTO;
//import com.nicico.internal.sales.extrabill.dto.*;
//import com.nicico.internal.sales.extrabill.model.GaamModel;
//import com.nicico.internal.sales.gaam.dto.GaamAuditDto;
//import com.nicico.internal.sales.gaam.dto.GaamDto;
//import com.nicico.internal.sales.gaam.dto.GaamReportDto;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Map;
//
//public interface GaamService {
//	SearchDTO.SearchRs<GaamDto.Info> search(SearchDTO.SearchRq request);
//
//	SearchDTO.SearchRs<GaamReportDto.Info> searchReport(SearchDTO.SearchRq request);
//
//	@Transactional
//	List<GaamDto.Info> saveAll(
//			List<ProformaBankBillRequest> requests);
//
//	@Transactional
//	GaamDto.Info save(ProformaBankBillRequest request);
//
//	List<GaamDto.Info> getByMasterId(Long proformaMasterId);
//
//	@Transactional
//	GaamDto.Info updateBillFiles(ProformaBankBillFileUpdateDto updateDto);
//
//	@Transactional
//	void sendReckoningEmail(Long extraBillId);
//
//	String generateExtraBillBrokerEmailContent(long extraBillId);
//
//	Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long extraBillId);
//
//	ProcessInstanceHistory getHistoryDetail(Long extraBillId);
//
//	@Transactional
//	GaamDto.Info updateExtraBill(UpdateExtraBillRequest updateExtraBillRequest);
//
//	@Transactional(readOnly = true)
//	List<GaamAuditDto> getAuditHistory(Long extraBillId);
//
//	SearchDTO.SearchRs<GaamReportDto.Info> findReadyReckoning(SearchDTO.SearchRq request);
//
//	void markAllAsReckoning(Long proformaMasterId);
//
//	void cancel(ExtraBillCancelRequest request);
//
//	void cancelExtraBillModel(GaamModel model, ExtraBillCancelRequest request);
//}
