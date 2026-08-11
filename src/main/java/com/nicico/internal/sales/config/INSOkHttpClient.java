package com.nicico.internal.sales.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class INSOkHttpClient {
	private final OkHttpClient client;
	private final ObjectMapper objectMapper;

	public INSOkHttpClient(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.client = new OkHttpClient.Builder()
				.addInterceptor(new CurlCommandOkHttpInterceptor())
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.writeTimeout(30, TimeUnit.SECONDS)
				.connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
				.build();
	}

	/**
	 * ارسال درخواست HTTP
	 *
	 * @param url          آدرس endpoint
	 * @param method       متد HTTP (GET, POST, PUT, DELETE)
	 * @param bodyObj      محتوای body (برای GET می تواند null باشد)
	 * @param responseType کلاس پاسخ مورد انتظار
	 * @return شیء deserialize شده از response
	 * @throws IOException در صورت خطا در ارتباط یا پردازش JSON
	 */
	public <T> T sendHttpRequest(String url, String method, Object bodyObj, Class<T> responseType) throws IOException {
		Request.Builder requestBuilder = new Request.Builder()
				.url(url)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json");
		// مدیریت درست body برای متدهای مختلف
		if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)) {
			requestBuilder.method(method, null);
		} else {
			String jsonBody = bodyObj != null
					? objectMapper.writeValueAsString(bodyObj)
					: "{}";
			RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json"));
			requestBuilder.method(method, requestBody);
		}
		Request httpRequest = requestBuilder.build();
		try (Response response = client.newCall(httpRequest).execute()) {
			ResponseBody responseBody = response.body();
			if (!response.isSuccessful()) {
				String errorBody = responseBody != null ? responseBody.string() : "No response body";
				log.error("HTTP request failed. URL: {}, Method: {}, Status: {}, Body: {}",
						url, method, response.code(), errorBody);
				throw new IOException("Unexpected code " + response.code() + ": " + errorBody);
			}
			if (responseBody == null) {
				throw new IOException("Response body is null");
			}
			String responseString = responseBody.string();
			log.debug("Response received. URL: {}, Body length: {}", url, responseString.length());
			try {
				log.info("OKHTTPCLIENT url:{} , request: {} , method: {} , response{}", url, bodyObj, method, responseString);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			// استفاده صحیح از readValue برای deserialize کردن JSON
			return objectMapper.readValue(responseString, responseType);
		}
	}

	/**
	 * متد راحت تر برای POST
	 */
	public <T> T post(String url, Object bodyObj, Class<T> responseType) throws IOException {
		return sendHttpRequest(url, "POST", bodyObj, responseType);
	}

	/**
	 * متد راحت تر برای GET
	 */
	public <T> T get(String url, Class<T> responseType) throws IOException {
		return sendHttpRequest(url, "GET", null, responseType);
	}

	/**
	 * متد راحت تر برای PUT
	 */
	public <T> T put(String url, Object bodyObj, Class<T> responseType) throws IOException {
		return sendHttpRequest(url, "PUT", bodyObj, responseType);
	}

	/**
	 * متد راحت تر برای DELETE
	 */
	public <T> T delete(String url, Class<T> responseType) throws IOException {
		return sendHttpRequest(url, "DELETE", null, responseType);
	}

	@PreDestroy
	public void cleanup() {
		if (client != null) {
			log.info("Shutting down OkHttpClient...");
			client.dispatcher().executorService().shutdown();
			client.connectionPool().evictAll();
		}
	}
}