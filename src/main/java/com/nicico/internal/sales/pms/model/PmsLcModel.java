package com.nicico.internal.sales.pms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Subselect("select * from  PMS.LCTBL@DBL_DS2_COPLINK") // نام جدول رو مطابق دیتابیست بذار
@Data
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PmsLcModel {
	@Id
	@Column(name = "LCID", length = 50)
	private String lcId;
	@Column(name = "LC_PREFACTOR", length = 50)
	private String lcPrefactor;
	@Column(name = "LC_CUSTOMERID", length = 50)
	private String lcCustomerId;
	@Column(name = "LC_BANKLC", length = 50)
	private String lcBankLc;
	@Column(name = "LC_GOODSID")
	private Integer lcGoodsId;
	@Column(name = "LC_DATESEDOR", length = 10)
	private String lcDateSedor;
	@Column(name = "LC_DATESARRESED", length = 10)
	private String lcDateSarreSed;
	@Column(name = "LC_MEGHDAR", precision = 19, scale = 2)
	private BigDecimal lcMeghdar;
	@Column(name = "LC_PRICE", precision = 19, scale = 2)
	private BigDecimal lcPrice;
	@Column(name = "LC_NUMBER", length = 50)
	private String lcNumber;
	@Column(name = "LC_TYPE")
	private Integer lcType;
	@Column(name = "LC_STATE")
	private Integer lcState;
	@Column(name = "LC_USER", length = 100)
	private String lcUser;
	@Column(name = "LC_MARK", length = 4000)
	private String lcMark;
	@Lob
	@Column(name = "LC_PICTURE")
	private byte[] lcPicture;
	@Column(name = "LC_CODEMARKAZHAZINEHLC", length = 50)
	private String lcCodeMarkazHazinehLc;
	@Column(name = "LC_BANKLCMOAMELEH", length = 50)
	private String lcBankLcMoameleh;

}
