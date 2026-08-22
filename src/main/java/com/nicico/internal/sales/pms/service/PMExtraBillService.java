package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.extrabill.dto.ProformaBankBillDto;

import java.io.IOException;
import java.util.List;

public interface PMExtraBillService {
    void createPMSExtraBill(Long proformaMasterId, String username, boolean resend);

    void updatePmsExtraBill(String pmsId, String username);


    void createPMSExtraBill(Long extraBillID, boolean resend) throws IOException;

    void sendExtraBillModelToPMS(com.nicico.internal.sales.extraBill.model.ExtraBillModel model, String username);

    List<ProformaBankBillDto.Info> findRemittanceExtraBillWithoutPmsId();
}
