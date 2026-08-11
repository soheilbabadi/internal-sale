//package com.nicico.internal.sales.performa.penalty.controller;
//
//import com.nicico.internal.sales.performa.penalty.dto.PenaltyDto;
//import com.nicico.internal.sales.performa.penalty.service.PenaltyService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/ins/performa/penalty")
//@RequiredArgsConstructor
//public class PenaltyController {
//	private final PenaltyService penaltyService;
//
//	@GetMapping("/{performaId}")
//	public ResponseEntity<PenaltyDto> calculatePenalty(@PathVariable("performaId") Long performaId) {
//		return ResponseEntity.ok(penaltyService.calculatePenalty(performaId));
//	}
//
//}
