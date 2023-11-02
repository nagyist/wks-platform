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
package com.wks.bpm.engine.exception;

public class ProcessDefinitionNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MESSAGE = "Process Definition Not Found Exception";

	public ProcessDefinitionNotFoundException() {
		super(DEFAULT_MESSAGE);
	}

}
