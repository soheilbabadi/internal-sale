package com.nicico.internal.sales.pms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Subselect("select * from  PMS.HAVALEH_FG@DBL_DS2_COPLINK") // نام جدول رو مطابق دیتابیست بذار
@Data
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PmsRemmitanceModel {
	@Id
	@Column(name = "HAV_CODE")
	private String havCode;

	@Column(name = "HAV_NAME")
	private String havName;

	@Column(name = "HAV_FROM")
	private String havFrom;

	@Column(name = "HAV_TO")
	private String havTo;

	@Column(name = "HAV_DATE")
	private String havDate;

	@Column(name = "HAV_STCPROCCESS")
	private String havStcproccess;

	@Column(name = "HAV_DATE_ETEBAR")
	private String havDateEtebar;

	@Column(name = "HAV_CONTRACTORID")
	private String havContractorid;

	@Column(name = "HAV_FOROSHANDEHID")
	private String havForoshandehid;

	@Column(name = "HAV_KALAID")
	private String havKalaid;

	@Column(name = "HAV_SHARH")
	private String havSharh;

	@Column(name = "CREATE_USER")
	private String createUser;

	@Column(name = "LAST_UPDATE_USER")
	private String lastUpdateUser;

	@Column(name = "CREATE_DATE")
	private String createDate;

	@Column(name = "LAST_UPDATE_DATE")
	private String lastUpdateDate;

	@Column(name = "UNIT_KALA")
	private String unitKala;

	@Column(name = "HAV_STATUS")
	private String havStatus;

	@Column(name = "HAV_PRICE")
	private BigDecimal havPrice;

	@Column(name = "LOADID")
	private String loadid;

	@Column(name = "UNLOADID")
	private String unloadid;

	@Column(name = "HAV_GHARADADNO")
	private String havGharadadno;

	@Column(name = "HAV_GHARADADDESC")
	private String havGharadaddesc;

	@Column(name = "HAV_KHARIDARID")
	private Long havKharidarid;

	@Column(name = "HAV_SANADCODE")
	private String havSanadcode;

	@Column(name = "HAV_DATEGHARARDAD")
	private String havDategharardad;

	@Column(name = "HAV_ISFINAL")
	private String havIsfinal;

	@Column(name = "HAV_STCHAVGROUP")
	private String havStchavgroup;

	@Column(name = "HAV_LASTPAYDATE")
	private String havLastpaydate;

	@Column(name = "HAV_ETEBARGHARARDADDATE")
	private String havEtebargharardaddate;

	@Column(name = "LAST_UPDATE_TIME")
	private String lastUpdateTime;

	@Column(name = "CREATE_TIME")
	private String createTime;

	@Column(name = "HAV_MEGHDAR")
	private BigDecimal havMeghdar;

	@Column(name = "HAV_PAY_FESH_NO")
	private String havPayFeshNo;

	@Column(name = "HAV_PAY_FESH_DATE")
	private String havPayFeshDate;

	@Column(name = "HAV_PAY_BANKCODE")
	private String havPayBankcode;

	@Column(name = "HAV_LD_FESH")
	private String havLdFesh;

	@Column(name = "HAV_LD_FESH_DATE")
	private String havLdFeshDate;

	@Column(name = "HAV_LD_BANKCODE")
	private String havLdBankcode;

	@Column(name = "HAV_PD_BANKCODE")
	private String havPdBankcode;

	@Column(name = "HAV_PD_FESH_DATE")
	private String havPdFeshDate;

	@Column(name = "HAV_PD_FESH")
	private String havPdFesh;

	@Column(name = "HAV_PAY_TYPE")
	private String havPayType;

	@Column(name = "HAV_PAMANKAR_KH")
	private String havPamankarKh;

	@Column(name = "HAV_FINALDATE")
	private String havFinaldate;

	@Column(name = "HAV_FINAL_USER")
	private String havFinalUser;

	@Column(name = "HAV_INVOICESTATE")
	private String havInvoicestate;

	@Column(name = "CUSTOMERIDMALI")
	private String customeridmali;

	@Column(name = "HAV_PERCENT_NAGHDI")
	private BigDecimal havPercentNaghdi;

	@Column(name = "HAV_PERCENT_ETEBARI")
	private BigDecimal havPercentEtebari;

	@Column(name = "HAV_GHEMAT_UNIT_NAGHDI")
	private BigDecimal havGhematUnitNaghdi;

	@Column(name = "HAV_GHEMAT_UNIT_ETEBARI")
	private BigDecimal havGhematUnitEtebari;

	@Column(name = "LCID")
	private String lcid;

	@Column(name = "PREFACTORID")
	private String prefactorid;

	@Column(name = "HAV_PERCENT_ALAYANDE_NAGHDI")
	private BigDecimal havPercentAlayandeNaghdi;

	@Column(name = "HAV_PERCENT_ALAYANDE_ETEBARI")
	private BigDecimal havPercentAlayandeEtebari;

	@Column(name = "HAV_PERCENT_MALYAT_NAGHDI")
	private BigDecimal havPercentMalyatNaghdi;

	@Column(name = "HAV_PERCENT_MALYAT_ETEBARI")
	private BigDecimal havPercentMalyatEtebari;

	@Column(name = "KARGOZARID")
	private String kargozarid;

	@Column(name = "HAV_LOTNUMBER")
	private String havLotnumber;

	@Column(name = "HAV_KHARID_DATE")
	private String havKharidDate;

	@Column(name = "HAV_RESERVE")
	private String havReserve;


}
