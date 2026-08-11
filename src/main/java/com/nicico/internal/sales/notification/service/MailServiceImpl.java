package com.nicico.internal.sales.notification.service;

import com.nicico.internal.sales.notification.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
	@Value("${spring.mail.username}")
	private String mailUsername;
	@Value("${spring.mail.password}")
	private String mailPassword;
	@Value("${nicico.mail-service-url}")
	private String mailServiceUrl;

	@Override
	public HttpResponse<String> sendMail(EmailRequest emailRequest, String pathOfPdf) {
		String boundary = "----Boundary" + UUID.randomUUID();
		Path filePath = Path.of(pathOfPdf);
		String jsonPayload = """
				{
				 "username":"%s",
				 "password":"%s",
				 "toRecipients":"%s","bccRecipients":"%s",
				 "subject":"%s",
				 "content":"%s"
				}
				""".formatted(mailUsername, mailPassword, emailRequest.getToRecipients(), emailRequest.getBccRecipients(), emailRequest.getSubject(), emailRequest.getContent());

		String multipartBody = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"request\"\r\n" + "Content-Type: application/json\r\n\r\n" + jsonPayload + "\r\n" + "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"files\"; filename=\"attachment.pdf\"\r\n" + "Content-Type: application/pdf\r\n\r\n";
		byte[] part1 = multipartBody.getBytes();
		byte[] fileBytes = null;
		try {
			fileBytes = java.nio.file.Files.readAllBytes(filePath);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		byte[] part3 = ("\r\n--" + boundary + "--").getBytes();
		assert fileBytes != null;
		byte[] requestBody = new byte[part1.length + fileBytes.length + part3.length];
		System.arraycopy(part1, 0, requestBody, 0, part1.length);
		System.arraycopy(fileBytes, 0, requestBody, part1.length, fileBytes.length);
		System.arraycopy(part3, 0, requestBody, part1.length + fileBytes.length, part3.length);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(mailServiceUrl)).header("Content-Type", "multipart/form-data; boundary=" + boundary).POST(HttpRequest.BodyPublishers.ofByteArray(requestBody)).build();
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info("{}-----{}", request.toString(), response.toString());
		return response;
	}

	@Override
	public HttpResponse<String> sendMail(EmailRequest emailRequest) {
		String boundary = "----Boundary" + UUID.randomUUID();

		String jsonPayload = """
				{
				 "username":"%s",
				 "password":"%s",
				 "toRecipients":"%s","bccRecipients":"%s",
				 "subject":"%s",
				 "content":"%s"
				}
				""".formatted(mailUsername, mailPassword, emailRequest.getToRecipients(), emailRequest.getBccRecipients(), emailRequest.getSubject(), emailRequest.getContent());

		String multipartBody = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"request\"\r\n" + "Content-Type: application/json\r\n\r\n" + jsonPayload + "\r\n" + "--" + boundary + "--\r\n";
		byte[] part1 = multipartBody.getBytes();
		byte[] part3 = ("\r\n--" + boundary + "--").getBytes();
		byte[] requestBody = new byte[part1.length + part3.length];
		System.arraycopy(part1, 0, requestBody, 0, part1.length);
		System.arraycopy(part3, 0, requestBody, part1.length, part3.length);

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(mailServiceUrl)).header("Content-Type", "multipart/form-data; boundary=" + boundary).POST(HttpRequest.BodyPublishers.ofByteArray(requestBody)).build();
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info("{}-----{}", request.toString(), response.toString());
		return response;

	}


}
