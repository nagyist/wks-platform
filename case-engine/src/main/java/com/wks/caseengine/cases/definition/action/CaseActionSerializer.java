/*
 * WKS Platform - Open-Source Project
 * 
 * This file is part of the WKS Platform, an open-source project developed by WKS Power.
 * 
 * WKS Platform is licensed under the MIT License.
 * 
 * © 2021 WKS Power. All rights reserved.
 * 
 * For licensing information, see the LICENSE file in the root directory of the project.
 */
package com.wks.caseengine.cases.definition.action;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CaseActionSerializer<T extends CaseAction> implements JsonSerializer<T> {

	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
		
		JsonElement jsonElement = new Gson().toJsonTree(src);
		
		
		if(src instanceof CaseStageUpdateAction) {
			jsonElement.getAsJsonObject().addProperty("actionType", CaseActionType.CASE_STAGE_UPDATE_ACTION.getCode());
			
		}else if(src instanceof CaseQueueAssignAction) {
			jsonElement.getAsJsonObject().addProperty("actionType", CaseActionType.CASE_QUEUE_UPDATE_ACTION.getCode());
		}
		
		
		return jsonElement;
	}

}
