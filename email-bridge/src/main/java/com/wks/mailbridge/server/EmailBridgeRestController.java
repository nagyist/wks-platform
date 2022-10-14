package com.wks.mailbridge.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wks.mailbridge.client.ReceiveMailService;
import com.wks.mailbridge.client.SendEmailService;

@RestController
public class EmailBridgeRestController {

	@Autowired
	private SendEmailService sendEmailService;
	
	@Autowired
	private ReceiveMailService receiveEmailService;

	@GetMapping(value = "/email-send")
	public void send() {
		sendEmailService.send("user@fakemail.com", "subject", "test");
	}

	@GetMapping(value = "/email-receive")
	public void receive() throws Exception {
		receiveEmailService.receive();
	}

}