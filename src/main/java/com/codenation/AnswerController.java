package com.codenation;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/codenation")
public class AnswerController {

	@Autowired
	private AnswerService service;

	@GetMapping("/{token}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> find(@PathVariable String token) {
		Answer answer = service.find(token);

		return ResponseEntity.ok(answer);
	}

	@PostMapping("/upload")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> upload(@RequestPart("answer") MultipartFile[] answer) throws IOException {
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		String response;
	    HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			for (MultipartFile file : answer) {
				if (!file.isEmpty()) {
					map.add("answer",
							new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
				}
			}
			
			response = service.upload(map);

		} catch (HttpStatusCodeException e) {
			httpStatus = HttpStatus.valueOf(e.getStatusCode().value());
			response = e.getResponseBodyAsString();
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			response = e.getMessage();
		}

		return new ResponseEntity<>(response, httpStatus);
	}

	class MultipartInputStreamFileResource extends InputStreamResource {

		private final String filename;

		MultipartInputStreamFileResource(InputStream inputStream, String filename) {
			super(inputStream);
			this.filename = filename;
		}

		@Override
		public String getFilename() {
			return this.filename;
		}

		@Override
		public long contentLength() throws IOException {
			return -1;
		}
	}

}
