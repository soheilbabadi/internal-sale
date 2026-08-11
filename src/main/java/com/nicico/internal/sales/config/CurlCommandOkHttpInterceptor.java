package com.nicico.internal.sales.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CurlCommandOkHttpInterceptor implements Interceptor {
	@NotNull
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		try {
			logCurlCommand(request);
		} catch (Exception e) {
			log.error("Error generating curl command", e);
		}
		return chain.proceed(request);
	}

	private void logCurlCommand(Request request) throws IOException {
		StringBuilder curlCommand = new StringBuilder("curl -Lv -X ").append(request.method()).append(" ");
		// Add headers
		request.headers().names().forEach(headerName -> {
			String headerValue = request.header(headerName);
			curlCommand.append("-H \"").append(headerName).append(": ").append(headerValue).append("\" ");
		});
		// Add body if exists
		RequestBody body = request.body();
		if (body != null && body.contentLength() > 0) {
			try {
				Buffer buffer = new Buffer();
				body.writeTo(buffer);
				String bodyString = buffer.readString(StandardCharsets.UTF_8);
				curlCommand.append("--data '").append(bodyString).append("' ");
			} catch (IOException e) {
				log.error("Error reading request body", e);
			}
		}
		// Add URL
		curlCommand.append(request.url());
		log.info(curlCommand.toString());
	}
}