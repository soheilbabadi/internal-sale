package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.dto.QCAnalysisMasterDto.Info;
import com.nicico.internal.sales.pricing.dto.QCAnalysisMasterDto.ParentOption;
import com.nicico.internal.sales.pricing.dto.QCAnalysisMasterDto.SaveRequest;
import com.nicico.internal.sales.pricing.model.QCAnalysisItemModel;
import com.nicico.internal.sales.pricing.model.QCAnalysisMasterModel;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T10:57:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class QCAnalysisMapperImpl implements QCAnalysisMapper {

    @Override
    public Info toDto(QCAnalysisMasterModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setItems( toItemDtoList( model.getItems() ) );
        info.setId( model.getId() );
        info.setAnalysisStage( model.getAnalysisStage() );
        info.setCommodityId( model.getCommodityId() );
        info.setCommodityCode( model.getCommodityCode() );
        info.setCommodityName( model.getCommodityName() );
        info.setQcIndicatorNo( model.getQcIndicatorNo() );
        info.setParentAnalysisId( model.getParentAnalysisId() );
        info.setRefIndicatorNo( model.getRefIndicatorNo() );
        info.setRegisterDate( model.getRegisterDate() );
        info.setPersianRegisterDate( model.getPersianRegisterDate() );
        info.setOfferDate( model.getOfferDate() );
        info.setPersianOfferDate( model.getPersianOfferDate() );
        info.setLoadingPlaceId( model.getLoadingPlaceId() );
        info.setLoadingPlace( model.getLoadingPlace() );
        info.setStatus( model.getStatus() );
        info.setDescription( model.getDescription() );
        info.setCreatedDate( model.getCreatedDate() );
        info.setLastModifiedDate( model.getLastModifiedDate() );
        info.setCreatedBy( model.getCreatedBy() );
        info.setLastModifiedBy( model.getLastModifiedBy() );

        return info;
    }

    @Override
    public QCAnalysisItemDto toItemDto(QCAnalysisItemModel model) {
        if ( model == null ) {
            return null;
        }

        QCAnalysisItemDto qCAnalysisItemDto = new QCAnalysisItemDto();

        qCAnalysisItemDto.setId( model.getId() );
        qCAnalysisItemDto.setAnalysisMasterId( model.getAnalysisMasterId() );
        qCAnalysisItemDto.setRowNo( model.getRowNo() );
        qCAnalysisItemDto.setFromBarrelNo( model.getFromBarrelNo() );
        qCAnalysisItemDto.setToBarrelNo( model.getToBarrelNo() );
        qCAnalysisItemDto.setLotNo( model.getLotNo() );
        qCAnalysisItemDto.setTotalBarrelsCount( model.getTotalBarrelsCount() );
        qCAnalysisItemDto.setLoadingPlaceId( model.getLoadingPlaceId() );
        qCAnalysisItemDto.setLoadingPlace( model.getLoadingPlace() );
        qCAnalysisItemDto.setGrossWetWeight( model.getGrossWetWeight() );
        qCAnalysisItemDto.setNetDryWeight( model.getNetDryWeight() );
        qCAnalysisItemDto.setOfferLotWeightMt( model.getOfferLotWeightMt() );
        qCAnalysisItemDto.setPricePerKgBarrel( model.getPricePerKgBarrel() );
        qCAnalysisItemDto.setMoisturePct( model.getMoisturePct() );
        qCAnalysisItemDto.setAgPct( model.getAgPct() );
        qCAnalysisItemDto.setAgKg( model.getAgKg() );
        qCAnalysisItemDto.setAgPpm( model.getAgPpm() );
        qCAnalysisItemDto.setAuPct( model.getAuPct() );
        qCAnalysisItemDto.setAuKg( model.getAuKg() );
        qCAnalysisItemDto.setAuPpm( model.getAuPpm() );
        qCAnalysisItemDto.setSePct( model.getSePct() );
        qCAnalysisItemDto.setSeKg( model.getSeKg() );
        qCAnalysisItemDto.setCuPct( model.getCuPct() );
        qCAnalysisItemDto.setCuKg( model.getCuKg() );
        qCAnalysisItemDto.setPtPpm( model.getPtPpm() );
        qCAnalysisItemDto.setPdPpm( model.getPdPpm() );
        qCAnalysisItemDto.setMoPct( model.getMoPct() );
        qCAnalysisItemDto.setFePct( model.getFePct() );
        qCAnalysisItemDto.setSPct( model.getSPct() );
        qCAnalysisItemDto.setPbPct( model.getPbPct() );
        qCAnalysisItemDto.setOilPct( model.getOilPct() );
        qCAnalysisItemDto.setCuCalcPct( model.getCuCalcPct() );
        qCAnalysisItemDto.setAuCalcPpm( model.getAuCalcPpm() );
        qCAnalysisItemDto.setAgCalcPpm( model.getAgCalcPpm() );
        qCAnalysisItemDto.setSmeltingCost( model.getSmeltingCost() );
        qCAnalysisItemDto.setRefiningCost( model.getRefiningCost() );
        qCAnalysisItemDto.setCathodePrice( model.getCathodePrice() );
        qCAnalysisItemDto.setOxidePrice( model.getOxidePrice() );
        qCAnalysisItemDto.setFractionDeduction( model.getFractionDeduction() );
        qCAnalysisItemDto.setAvgMoisturePct( model.getAvgMoisturePct() );
        qCAnalysisItemDto.setAvgAgPct( model.getAvgAgPct() );
        qCAnalysisItemDto.setAvgAgKg( model.getAvgAgKg() );
        qCAnalysisItemDto.setAvgAgPpm( model.getAvgAgPpm() );
        qCAnalysisItemDto.setAvgAuPct( model.getAvgAuPct() );
        qCAnalysisItemDto.setAvgAuKg( model.getAvgAuKg() );
        qCAnalysisItemDto.setAvgAuPpm( model.getAvgAuPpm() );
        qCAnalysisItemDto.setAvgSePct( model.getAvgSePct() );
        qCAnalysisItemDto.setAvgSeKg( model.getAvgSeKg() );
        qCAnalysisItemDto.setAvgCuPct( model.getAvgCuPct() );
        qCAnalysisItemDto.setAvgCuKg( model.getAvgCuKg() );
        qCAnalysisItemDto.setAvgPtPpm( model.getAvgPtPpm() );
        qCAnalysisItemDto.setAvgPdPpm( model.getAvgPdPpm() );
        qCAnalysisItemDto.setAvgMoPct( model.getAvgMoPct() );
        qCAnalysisItemDto.setAvgFePct( model.getAvgFePct() );
        qCAnalysisItemDto.setAvgSPct( model.getAvgSPct() );
        qCAnalysisItemDto.setAvgPbPct( model.getAvgPbPct() );
        qCAnalysisItemDto.setAvgOilPct( model.getAvgOilPct() );
        qCAnalysisItemDto.setAvgCuCalcPct( model.getAvgCuCalcPct() );
        qCAnalysisItemDto.setAvgAuCalcPpm( model.getAvgAuCalcPpm() );
        qCAnalysisItemDto.setAvgAgCalcPpm( model.getAvgAgCalcPpm() );
        qCAnalysisItemDto.setRemarks( model.getRemarks() );

        formatBarrelRange( model, qCAnalysisItemDto );

        return qCAnalysisItemDto;
    }

    @Override
    public List<QCAnalysisItemDto> toItemDtoList(List<QCAnalysisItemModel> models) {
        if ( models == null ) {
            return null;
        }

        List<QCAnalysisItemDto> list = new ArrayList<QCAnalysisItemDto>( models.size() );
        for ( QCAnalysisItemModel qCAnalysisItemModel : models ) {
            list.add( toItemDto( qCAnalysisItemModel ) );
        }

        return list;
    }

    @Override
    public QCAnalysisMasterModel toModel(SaveRequest request) {
        if ( request == null ) {
            return null;
        }

        QCAnalysisMasterModel qCAnalysisMasterModel = new QCAnalysisMasterModel();

        qCAnalysisMasterModel.setDescription( request.getDescription() );
        qCAnalysisMasterModel.setId( request.getId() );
        qCAnalysisMasterModel.setAnalysisStage( request.getAnalysisStage() );
        qCAnalysisMasterModel.setCommodityId( request.getCommodityId() );
        qCAnalysisMasterModel.setCommodityCode( request.getCommodityCode() );
        qCAnalysisMasterModel.setCommodityName( request.getCommodityName() );
        qCAnalysisMasterModel.setQcIndicatorNo( request.getQcIndicatorNo() );
        qCAnalysisMasterModel.setParentAnalysisId( request.getParentAnalysisId() );
        qCAnalysisMasterModel.setRefIndicatorNo( request.getRefIndicatorNo() );
        qCAnalysisMasterModel.setRegisterDate( request.getRegisterDate() );
        qCAnalysisMasterModel.setPersianRegisterDate( request.getPersianRegisterDate() );
        qCAnalysisMasterModel.setOfferDate( request.getOfferDate() );
        qCAnalysisMasterModel.setPersianOfferDate( request.getPersianOfferDate() );
        qCAnalysisMasterModel.setLoadingPlaceId( request.getLoadingPlaceId() );
        qCAnalysisMasterModel.setLoadingPlace( request.getLoadingPlace() );
        qCAnalysisMasterModel.setStatus( request.getStatus() );

        return qCAnalysisMasterModel;
    }

    @Override
    public QCAnalysisItemModel toItemModel(QCAnalysisItemDto dto) {
        if ( dto == null ) {
            return null;
        }

        QCAnalysisItemModel qCAnalysisItemModel = new QCAnalysisItemModel();

        qCAnalysisItemModel.setId( dto.getId() );
        qCAnalysisItemModel.setAnalysisMasterId( dto.getAnalysisMasterId() );
        qCAnalysisItemModel.setRowNo( dto.getRowNo() );
        qCAnalysisItemModel.setFromBarrelNo( dto.getFromBarrelNo() );
        qCAnalysisItemModel.setToBarrelNo( dto.getToBarrelNo() );
        qCAnalysisItemModel.setLotNo( dto.getLotNo() );
        qCAnalysisItemModel.setTotalBarrelsCount( dto.getTotalBarrelsCount() );
        qCAnalysisItemModel.setLoadingPlaceId( dto.getLoadingPlaceId() );
        qCAnalysisItemModel.setLoadingPlace( dto.getLoadingPlace() );
        qCAnalysisItemModel.setGrossWetWeight( dto.getGrossWetWeight() );
        qCAnalysisItemModel.setNetDryWeight( dto.getNetDryWeight() );
        qCAnalysisItemModel.setOfferLotWeightMt( dto.getOfferLotWeightMt() );
        qCAnalysisItemModel.setPricePerKgBarrel( dto.getPricePerKgBarrel() );
        qCAnalysisItemModel.setMoisturePct( dto.getMoisturePct() );
        qCAnalysisItemModel.setAgPct( dto.getAgPct() );
        qCAnalysisItemModel.setAgKg( dto.getAgKg() );
        qCAnalysisItemModel.setAgPpm( dto.getAgPpm() );
        qCAnalysisItemModel.setAuPct( dto.getAuPct() );
        qCAnalysisItemModel.setAuKg( dto.getAuKg() );
        qCAnalysisItemModel.setAuPpm( dto.getAuPpm() );
        qCAnalysisItemModel.setSePct( dto.getSePct() );
        qCAnalysisItemModel.setSeKg( dto.getSeKg() );
        qCAnalysisItemModel.setCuPct( dto.getCuPct() );
        qCAnalysisItemModel.setCuKg( dto.getCuKg() );
        qCAnalysisItemModel.setPtPpm( dto.getPtPpm() );
        qCAnalysisItemModel.setPdPpm( dto.getPdPpm() );
        qCAnalysisItemModel.setMoPct( dto.getMoPct() );
        qCAnalysisItemModel.setFePct( dto.getFePct() );
        qCAnalysisItemModel.setSPct( dto.getSPct() );
        qCAnalysisItemModel.setPbPct( dto.getPbPct() );
        qCAnalysisItemModel.setOilPct( dto.getOilPct() );
        qCAnalysisItemModel.setCuCalcPct( dto.getCuCalcPct() );
        qCAnalysisItemModel.setAuCalcPpm( dto.getAuCalcPpm() );
        qCAnalysisItemModel.setAgCalcPpm( dto.getAgCalcPpm() );
        qCAnalysisItemModel.setSmeltingCost( dto.getSmeltingCost() );
        qCAnalysisItemModel.setRefiningCost( dto.getRefiningCost() );
        qCAnalysisItemModel.setCathodePrice( dto.getCathodePrice() );
        qCAnalysisItemModel.setOxidePrice( dto.getOxidePrice() );
        qCAnalysisItemModel.setFractionDeduction( dto.getFractionDeduction() );
        qCAnalysisItemModel.setAvgMoisturePct( dto.getAvgMoisturePct() );
        qCAnalysisItemModel.setAvgAgPct( dto.getAvgAgPct() );
        qCAnalysisItemModel.setAvgAgKg( dto.getAvgAgKg() );
        qCAnalysisItemModel.setAvgAgPpm( dto.getAvgAgPpm() );
        qCAnalysisItemModel.setAvgAuPct( dto.getAvgAuPct() );
        qCAnalysisItemModel.setAvgAuKg( dto.getAvgAuKg() );
        qCAnalysisItemModel.setAvgAuPpm( dto.getAvgAuPpm() );
        qCAnalysisItemModel.setAvgSePct( dto.getAvgSePct() );
        qCAnalysisItemModel.setAvgSeKg( dto.getAvgSeKg() );
        qCAnalysisItemModel.setAvgCuPct( dto.getAvgCuPct() );
        qCAnalysisItemModel.setAvgCuKg( dto.getAvgCuKg() );
        qCAnalysisItemModel.setAvgPtPpm( dto.getAvgPtPpm() );
        qCAnalysisItemModel.setAvgPdPpm( dto.getAvgPdPpm() );
        qCAnalysisItemModel.setAvgMoPct( dto.getAvgMoPct() );
        qCAnalysisItemModel.setAvgFePct( dto.getAvgFePct() );
        qCAnalysisItemModel.setAvgSPct( dto.getAvgSPct() );
        qCAnalysisItemModel.setAvgPbPct( dto.getAvgPbPct() );
        qCAnalysisItemModel.setAvgOilPct( dto.getAvgOilPct() );
        qCAnalysisItemModel.setAvgCuCalcPct( dto.getAvgCuCalcPct() );
        qCAnalysisItemModel.setAvgAuCalcPpm( dto.getAvgAuCalcPpm() );
        qCAnalysisItemModel.setAvgAgCalcPpm( dto.getAvgAgCalcPpm() );
        qCAnalysisItemModel.setRemarks( dto.getRemarks() );

        return qCAnalysisItemModel;
    }

    @Override
    public List<QCAnalysisItemModel> toItemModelList(List<QCAnalysisItemDto> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<QCAnalysisItemModel> list = new ArrayList<QCAnalysisItemModel>( dtos.size() );
        for ( QCAnalysisItemDto qCAnalysisItemDto : dtos ) {
            list.add( toItemModel( qCAnalysisItemDto ) );
        }

        return list;
    }

    @Override
    public void updateItemModelFromDto(QCAnalysisItemDto dto, QCAnalysisItemModel model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getFromBarrelNo() != null ) {
            model.setFromBarrelNo( dto.getFromBarrelNo() );
        }
        if ( dto.getToBarrelNo() != null ) {
            model.setToBarrelNo( dto.getToBarrelNo() );
        }
        if ( dto.getLotNo() != null ) {
            model.setLotNo( dto.getLotNo() );
        }
        if ( dto.getTotalBarrelsCount() != null ) {
            model.setTotalBarrelsCount( dto.getTotalBarrelsCount() );
        }
        if ( dto.getLoadingPlaceId() != null ) {
            model.setLoadingPlaceId( dto.getLoadingPlaceId() );
        }
        if ( dto.getLoadingPlace() != null ) {
            model.setLoadingPlace( dto.getLoadingPlace() );
        }
        if ( dto.getGrossWetWeight() != null ) {
            model.setGrossWetWeight( dto.getGrossWetWeight() );
        }
        if ( dto.getNetDryWeight() != null ) {
            model.setNetDryWeight( dto.getNetDryWeight() );
        }
        if ( dto.getOfferLotWeightMt() != null ) {
            model.setOfferLotWeightMt( dto.getOfferLotWeightMt() );
        }
        if ( dto.getPricePerKgBarrel() != null ) {
            model.setPricePerKgBarrel( dto.getPricePerKgBarrel() );
        }
        if ( dto.getMoisturePct() != null ) {
            model.setMoisturePct( dto.getMoisturePct() );
        }
        if ( dto.getAgPct() != null ) {
            model.setAgPct( dto.getAgPct() );
        }
        if ( dto.getAgKg() != null ) {
            model.setAgKg( dto.getAgKg() );
        }
        if ( dto.getAgPpm() != null ) {
            model.setAgPpm( dto.getAgPpm() );
        }
        if ( dto.getAuPct() != null ) {
            model.setAuPct( dto.getAuPct() );
        }
        if ( dto.getAuKg() != null ) {
            model.setAuKg( dto.getAuKg() );
        }
        if ( dto.getAuPpm() != null ) {
            model.setAuPpm( dto.getAuPpm() );
        }
        if ( dto.getSePct() != null ) {
            model.setSePct( dto.getSePct() );
        }
        if ( dto.getSeKg() != null ) {
            model.setSeKg( dto.getSeKg() );
        }
        if ( dto.getCuPct() != null ) {
            model.setCuPct( dto.getCuPct() );
        }
        if ( dto.getCuKg() != null ) {
            model.setCuKg( dto.getCuKg() );
        }
        if ( dto.getPtPpm() != null ) {
            model.setPtPpm( dto.getPtPpm() );
        }
        if ( dto.getPdPpm() != null ) {
            model.setPdPpm( dto.getPdPpm() );
        }
        if ( dto.getMoPct() != null ) {
            model.setMoPct( dto.getMoPct() );
        }
        if ( dto.getFePct() != null ) {
            model.setFePct( dto.getFePct() );
        }
        if ( dto.getSPct() != null ) {
            model.setSPct( dto.getSPct() );
        }
        if ( dto.getPbPct() != null ) {
            model.setPbPct( dto.getPbPct() );
        }
        if ( dto.getOilPct() != null ) {
            model.setOilPct( dto.getOilPct() );
        }
        if ( dto.getCuCalcPct() != null ) {
            model.setCuCalcPct( dto.getCuCalcPct() );
        }
        if ( dto.getAuCalcPpm() != null ) {
            model.setAuCalcPpm( dto.getAuCalcPpm() );
        }
        if ( dto.getAgCalcPpm() != null ) {
            model.setAgCalcPpm( dto.getAgCalcPpm() );
        }
        if ( dto.getSmeltingCost() != null ) {
            model.setSmeltingCost( dto.getSmeltingCost() );
        }
        if ( dto.getRefiningCost() != null ) {
            model.setRefiningCost( dto.getRefiningCost() );
        }
        if ( dto.getCathodePrice() != null ) {
            model.setCathodePrice( dto.getCathodePrice() );
        }
        if ( dto.getOxidePrice() != null ) {
            model.setOxidePrice( dto.getOxidePrice() );
        }
        if ( dto.getFractionDeduction() != null ) {
            model.setFractionDeduction( dto.getFractionDeduction() );
        }
        if ( dto.getAvgMoisturePct() != null ) {
            model.setAvgMoisturePct( dto.getAvgMoisturePct() );
        }
        if ( dto.getAvgAgPct() != null ) {
            model.setAvgAgPct( dto.getAvgAgPct() );
        }
        if ( dto.getAvgAgKg() != null ) {
            model.setAvgAgKg( dto.getAvgAgKg() );
        }
        if ( dto.getAvgAgPpm() != null ) {
            model.setAvgAgPpm( dto.getAvgAgPpm() );
        }
        if ( dto.getAvgAuPct() != null ) {
            model.setAvgAuPct( dto.getAvgAuPct() );
        }
        if ( dto.getAvgAuKg() != null ) {
            model.setAvgAuKg( dto.getAvgAuKg() );
        }
        if ( dto.getAvgAuPpm() != null ) {
            model.setAvgAuPpm( dto.getAvgAuPpm() );
        }
        if ( dto.getAvgSePct() != null ) {
            model.setAvgSePct( dto.getAvgSePct() );
        }
        if ( dto.getAvgSeKg() != null ) {
            model.setAvgSeKg( dto.getAvgSeKg() );
        }
        if ( dto.getAvgCuPct() != null ) {
            model.setAvgCuPct( dto.getAvgCuPct() );
        }
        if ( dto.getAvgCuKg() != null ) {
            model.setAvgCuKg( dto.getAvgCuKg() );
        }
        if ( dto.getAvgPtPpm() != null ) {
            model.setAvgPtPpm( dto.getAvgPtPpm() );
        }
        if ( dto.getAvgPdPpm() != null ) {
            model.setAvgPdPpm( dto.getAvgPdPpm() );
        }
        if ( dto.getAvgMoPct() != null ) {
            model.setAvgMoPct( dto.getAvgMoPct() );
        }
        if ( dto.getAvgFePct() != null ) {
            model.setAvgFePct( dto.getAvgFePct() );
        }
        if ( dto.getAvgSPct() != null ) {
            model.setAvgSPct( dto.getAvgSPct() );
        }
        if ( dto.getAvgPbPct() != null ) {
            model.setAvgPbPct( dto.getAvgPbPct() );
        }
        if ( dto.getAvgOilPct() != null ) {
            model.setAvgOilPct( dto.getAvgOilPct() );
        }
        if ( dto.getAvgCuCalcPct() != null ) {
            model.setAvgCuCalcPct( dto.getAvgCuCalcPct() );
        }
        if ( dto.getAvgAuCalcPpm() != null ) {
            model.setAvgAuCalcPpm( dto.getAvgAuCalcPpm() );
        }
        if ( dto.getAvgAgCalcPpm() != null ) {
            model.setAvgAgCalcPpm( dto.getAvgAgCalcPpm() );
        }
        if ( dto.getRemarks() != null ) {
            model.setRemarks( dto.getRemarks() );
        }
    }

    @Override
    public void updateModelFromRequest(SaveRequest request, QCAnalysisMasterModel model) {
        if ( request == null ) {
            return;
        }

        model.setDescription( request.getDescription() );
        model.setAnalysisStage( request.getAnalysisStage() );
        model.setCommodityId( request.getCommodityId() );
        model.setCommodityCode( request.getCommodityCode() );
        model.setCommodityName( request.getCommodityName() );
        model.setQcIndicatorNo( request.getQcIndicatorNo() );
        model.setParentAnalysisId( request.getParentAnalysisId() );
        model.setRefIndicatorNo( request.getRefIndicatorNo() );
        model.setRegisterDate( request.getRegisterDate() );
        model.setPersianRegisterDate( request.getPersianRegisterDate() );
        model.setOfferDate( request.getOfferDate() );
        model.setPersianOfferDate( request.getPersianOfferDate() );
        model.setLoadingPlaceId( request.getLoadingPlaceId() );
        model.setLoadingPlace( request.getLoadingPlace() );
        model.setStatus( request.getStatus() );
    }

    @Override
    public ParentOption toParentOption(QCAnalysisMasterModel model) {
        if ( model == null ) {
            return null;
        }

        ParentOption parentOption = new ParentOption();

        parentOption.setId( model.getId() );
        parentOption.setQcIndicatorNo( model.getQcIndicatorNo() );
        parentOption.setPersianRegisterDate( model.getPersianRegisterDate() );
        parentOption.setPersianOfferDate( model.getPersianOfferDate() );
        parentOption.setCommodityId( model.getCommodityId() );
        parentOption.setCommodityName( model.getCommodityName() );
        parentOption.setAnalysisStage( model.getAnalysisStage() );

        return parentOption;
    }

    @Override
    public List<ParentOption> toParentOptionList(List<QCAnalysisMasterModel> models) {
        if ( models == null ) {
            return null;
        }

        List<ParentOption> list = new ArrayList<ParentOption>( models.size() );
        for ( QCAnalysisMasterModel qCAnalysisMasterModel : models ) {
            list.add( toParentOption( qCAnalysisMasterModel ) );
        }

        return list;
    }
}
