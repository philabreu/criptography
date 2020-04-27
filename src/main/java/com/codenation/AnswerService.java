package com.codenation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AnswerService {

	@Value("${api.get}")
	private String getUrl;

	@Value("${api.post}")
	private String postUrl;
	
	RestTemplate restTemplate = new RestTemplate();

	public Answer find(@PathVariable("token") String token) {
		try {
			Answer answer = restTemplate.getForObject(getUrl + token, Answer.class);

			answer.setDecifrado("when in doubt, leave it out. Joshua Bloch");
			answer.setResumo_criptografico(DigestUtils.sha1Hex(answer.getDecifrado()));

			return answer;
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (HttpServerErrorException e) {
			e.printStackTrace();
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}


	public String upload(LinkedMultiValueMap<String, Object> map) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		
		String token = "12fb16d2fb5536bebb836638618f416d29c9d43e";

		String response = restTemplate.postForObject(postUrl + token, requestEntity, String.class);
		
		return response;
		
	}


}
