package com.nicico.internal.sales.accounting.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class PasswordGrantInterceptor implements Interceptor {

    private final PasswordGrantAuthenticator authManager;

    public PasswordGrantInterceptor(PasswordGrantAuthenticator authManager) {
        this.authManager = authManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        String token = authManager.getValidToken();

        requestBuilder.header("Authorization", "Bearer " + token);
        return chain.proceed(requestBuilder.build());
    }
}
