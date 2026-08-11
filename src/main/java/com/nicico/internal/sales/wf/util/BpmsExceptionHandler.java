package com.nicico.internal.sales.wf.util;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;

@Slf4j
public final class BpmsExceptionHandler {

	private static final String DEFAULT_BPMS_CONNECTION_ERROR = "خطا در اتصال به کارتابل";
	private static final String DEFAULT_TASK_EXECUTION_ERROR = "خطا در انجام تسک";
	private static final String DEFAULT_VALIDATION_ERROR = "خطا در اعتبارسنجی داده ها";

	private BpmsExceptionHandler() {

	}


	public static <T> T executeWithCustomHandling(ThrowingSupplier<T> supplier, String errorMessage) {
		try {
			return supplier.get();
		} catch (InternalSaleCustomException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Operation failed: {}", ex.getMessage(), ex);
			throw new InternalSaleCustomException.BpmsClientException(
					errorMessage,
					new ArrayList<>(Collections.singletonList(ex.getMessage()))
			);
		}
	}

	public static void executeWithCustomHandling(ThrowingRunnable runnable, String errorMessage) {
		try {
			runnable.run();
		} catch (InternalSaleCustomException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Operation failed: {}", ex.getMessage(), ex);
			throw new InternalSaleCustomException.BpmsClientException(
					errorMessage,
					new ArrayList<>(Collections.singletonList(ex.getMessage()))
			);
		}
	}


	@FunctionalInterface
	public interface ThrowingSupplier<T> {
		T get() throws Exception;
	}

	@FunctionalInterface
	public interface ThrowingRunnable {
		void run() throws Exception;
	}


}