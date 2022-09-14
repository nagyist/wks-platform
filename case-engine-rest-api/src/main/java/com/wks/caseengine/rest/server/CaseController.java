package com.wks.caseengine.rest.server;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.wks.caseengine.cases.definition.CaseStatus;
import com.wks.caseengine.cases.instance.CaseInstance;
import com.wks.caseengine.cases.instance.CaseInstanceNotFoundException;
import com.wks.caseengine.cases.instance.CaseInstanceService;

@RestController
public class CaseController {

	@Autowired
	private CaseInstanceService caseInstanceService;

	@GetMapping(value = "/case")
	public List<CaseInstance> find(@RequestParam(required = false) String status) throws Exception {
		if (status == null) {
			return caseInstanceService.find(Optional.empty());
		} else {
			return caseInstanceService.find(Optional.of(CaseStatus.valueOf(status)));
		}
	}

	@GetMapping(value = "/case/{businessKey}")
	public CaseInstance get(@PathVariable String businessKey) throws Exception {
		return caseInstanceService.get(businessKey);
	}

	@PostMapping(value = "/case")
	public CaseInstance save(@RequestBody CaseInstance caseInstance) throws Exception {
		return caseInstanceService.create(caseInstance);
	}

	@PatchMapping(value = "case/{businessKey}")
	public void update(@PathVariable String businessKey, @RequestBody CaseInstance caseInstance) throws Exception {
		caseInstanceService.updateStatus(businessKey, caseInstance.getStatus());
	}

	@DeleteMapping(value = "/case/{businessKey}")
	public void delete(@PathVariable String businessKey) throws Exception {
		try {
			caseInstanceService.delete(businessKey);
		} catch (CaseInstanceNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Case Instance Not Found - " + businessKey, e);
		}
	}

}