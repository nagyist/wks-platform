package com.wks.caseengine.tasks.forms;

import com.wks.bpm.engine.model.spi.TaskForm;

public interface TaskFormService {

	TaskForm get(String taskId, final String bpmEngineId) throws Exception;

}
