package com.wks.emailtocase.server;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wks.caseengine.cases.instance.CaseInstance;
import com.wks.caseengine.cases.instance.CaseInstanceService;
import com.wks.emailtocase.caseemail.CaseEmail;
import com.wks.emailtocase.caseemail.CaseEmailService;
import com.wks.emailtocase.caseemail.CaseEmailType;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("email")
@Tag(name = "Email to case", description = "Email to case API")
@Slf4j
public class EmailToCaseRestController {

	@Autowired
	private CaseEmailService caseEmailService;

	@Autowired
	private CaseInstanceService caseInstanceService;

	@Autowired
	private CaseEmailFactory caseEmailFactory;

	@PostMapping(value = "/receive")
	public void receive(@RequestParam(name = "to") String to, @RequestParam(name = "from") String from,
			@RequestParam(name = "subject") String subject, @RequestParam(name = "text") String text,
			@RequestParam(name = "html") String html) throws Exception {

		log.debug("### Email received - Processing started ###");
		log.debug("To: " + to);
		log.debug("From: " + from);
		log.debug("Subject: " + subject);
		log.debug("Text: " + text);
		log.debug("Html: " + html);

		CaseEmail caseEmail = caseEmailFactory.create(to, from, subject, text, html);

		if (caseEmail.getCaseEmailType().equals(CaseEmailType.NEW_CASE_EMAIL)) {
			CaseInstance caseInstance = caseInstanceService.create(caseEmail.getCaseDefinitionId());
			caseEmail.setCaseInstanceBusinessKey(caseInstance.getBusinessKey());

			caseEmailService.save(caseEmail);

		} else if (caseEmail.getCaseEmailType().equals(CaseEmailType.UPDATE_CASE_EMAIL)) {
			caseEmailService.save(caseEmail);
		}

		log.debug("### Email received - Processing finished ###");

	}

	@GetMapping(value = "/")
	public List<CaseEmail> find(@RequestParam(required = false) String caseInstanceBusinessKey,
			@RequestParam(required = false) String caseDefinitionId) throws Exception {

		return caseEmailService.find(Optional.ofNullable(caseInstanceBusinessKey),
				Optional.ofNullable(caseDefinitionId));
	}

}