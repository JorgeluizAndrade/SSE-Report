package com.br.report.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SsseReportController {

	public List<SseEmitter> emmiters = new CopyOnWriteArrayList<>();

	@CrossOrigin()
	@RequestMapping(value = "/subs", consumes = MediaType.ALL_VALUE)
	public SseEmitter subscribe() {
		SseEmitter sseEmiter = new SseEmitter(Long.MAX_VALUE);

		try {
			sseEmiter.send(SseEmitter.event().name("INIT"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		sseEmiter.onCompletion(() -> emmiters.remove(sseEmiter));
		emmiters.add(sseEmiter);

		return sseEmiter;
	}

	@PostMapping(value =  "/dispatchEvent")
	public void dispatchEvent(@RequestParam String notification) {
		for(SseEmitter sseE: emmiters) {
				try {
					sseE.send(SseEmitter.event().name("notifications").data(notification));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
