package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProformaModelResponse implements Serializable {
	@Serial
	private static final long serialVersionUID = -7128471758955760886L;
	private ProformaMasterModel masterModel;
	private List<ProformaDetailModel> detailModels;
}
