package com.nicico.internal.sales.proforma.records;

public record PricingFormulaInput(double unitPrice,
                                  double vatRate,
                                  double quantity,
                                  double netWeight,
                                  double cashPercentage,
                                  double commissionPercentage,
                                  boolean cashPercentTotal) {

	public static PricingFormulaInput of(double unitPrice,
	                                     double vatRate,
	                                     double quantity,
	                                     double netWeight,
	                                     double cashPercentage,
	                                     double commissionPercentage,
	                                     boolean cashPercentTotal) {
		return new PricingFormulaInput(unitPrice, vatRate, quantity, netWeight, cashPercentage,
				commissionPercentage, cashPercentTotal);
	}
}