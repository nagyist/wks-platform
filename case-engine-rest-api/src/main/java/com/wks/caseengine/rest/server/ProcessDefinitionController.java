package com.wks.caseengine.rest.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wks.bpm.engine.client.BpmEngineClientFacade;
import com.wks.bpm.engine.model.spi.ProcessDefinition;
import com.wks.caseengine.bpm.BpmEngineService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("process-definition")
@Tag(name = "Process Definition", description = "Access information about processes definitions in Camunda")
public class ProcessDefinitionController {

	@Autowired
	private BpmEngineClientFacade processEngineClientFacade;

	@Autowired
	private BpmEngineService bpmEngineService;

	@GetMapping(value = "/{bpmEngineId}/{processDefinitionId}/xml", produces = MediaType.APPLICATION_XML_VALUE)
	public String get(@PathVariable final String bpmEngineId, @PathVariable final String processDefinitionId)
			throws Exception {
		return processEngineClientFacade.getProcessDefinitionXMLById(processDefinitionId, bpmEngineService.get(bpmEngineId));
	}

	@GetMapping(value = "/{bpmEngineId}/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ProcessDefinition[] find(@PathVariable final String bpmEngineId)
			throws Exception {
		return processEngineClientFacade.findProcessDefinitions(bpmEngineService.get(bpmEngineId));
	}

}
