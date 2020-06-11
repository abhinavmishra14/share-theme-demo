/*
 * Created By: Abhinav Kumar Mishra
 * Copyright &copy; 2020. Abhinav Kumar Mishra. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.abhinavmishra14.webscript;

import java.io.IOException;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * The Class ChangeTypeWebscript.<br>
 * Extension of OOTB change type WebScript.
 */
public class ChangeTypeWebscript extends AbstractWebScript {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ChangeTypeWebscript.class);

	/** The Constant TYPE_PARAM_KEY. */
	private static final String TYPE_PARAM_KEY = "type";

	/** The Constant STORE_TYPE. */
	private static final String STORE_TYPE = "store_type";

	/** The Constant STORE_ID. */
	private static final String STORE_ID = "store_id";

	/** The Constant PARAM_ID. */
	private static final String PARAM_ID = "id";

	/** The Constant CONTENT_LENGTH. */
	private static final String CONTENT_LENGTH = "Content-Length";

	/** The Constant ENCODING_UTF_8. */
	private static final String ENCODING_UTF_8 = "UTF-8";

	/** The Constant CURRENT_KEY. */
	private static final String CURRENT_KEY = "current";

	/** The Constant CACHE_CONTROL. */
	private static final String CACHE_CONTROL = "cache-Control";
	/** The Constant NO_CACHE. */
	private static final String NO_CACHE = "no-cache";
	/** The Constant EXPIRES. */
	private static final String EXPIRES = "Expires";

	/** The Constant PRAGMA. */
	private static final String PRAGMA = "Pragma";

	/** The namespace service. */
	private final transient NamespaceService namespaceService;

	/** The node service. */
	private final transient NodeService nodeService;

	/**
	 * The Constructor.
	 *
	 * @param namespaceService the namespace service
	 * @param nodeService      the node service
	 */
	public ChangeTypeWebscript(final NamespaceService namespaceService, final NodeService nodeService) {
		super();
		this.namespaceService = namespaceService;
		this.nodeService = nodeService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.extensions.webscripts.WebScript#execute(org.
	 * springframework.extensions.webscripts.WebScriptRequest,
	 * org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		QName targetType = null;
		try {
			final JSONObject requestPayload = new JSONObject(request.getContent().getContent());
			targetType = getTargetType(requestPayload);
			LOGGER.info("Changing type, the targetType is: {}", targetType);
			final NodeRef nodeRef = getNodeRef(request);
			nodeService.setType(nodeRef, targetType);
			final QName changedType = nodeService.getType(nodeRef);
			writeResponse(response, changedType.getPrefixedQName(namespaceService));
		} catch (JSONException excp) {
			LOGGER.error("Error occurred while changing the type {}", targetType, excp);
			throw new WebScriptException("Change type failed!, targetType was: " + targetType, excp);
		}
	}

	/**
	 * Gets the target type.
	 *
	 * @param payload the payload
	 * @return the target type
	 */
	private QName getTargetType(final JSONObject payload) {
		try {
			final String type = payload.getString(TYPE_PARAM_KEY);
			QName qname;
			if (type.indexOf(String.valueOf(QName.NAMESPACE_BEGIN)) != -1) {
				qname = QName.createQName(type);
			} else {
				qname = QName.createQName(type, namespaceService);
			}
			return qname;
		} catch (JSONException jsonex) {
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
					"Error occurred while extracting the target type from input json payload", jsonex);
		}
	}

	/**
	 * Write response.
	 *
	 * @param response    the response
	 * @param changedType the changed type
	 * @throws IOException the IO exception
	 */
	private void writeResponse(final WebScriptResponse response, final QName changedType) throws IOException {
		try {
			final JSONObject responsePayload = new JSONObject();
			responsePayload.put(CURRENT_KEY, changedType.getPrefixString());
			writeResponse(response, responsePayload, false, HttpStatus.SC_OK);
		} catch (JSONException jsonex) {
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
					"Error occurred while writing json response for changedType: " + changedType, jsonex);
		}
	}

	/**
	 * Gets the node ref.
	 *
	 * @param request the request
	 * @return the node ref
	 */
	private NodeRef getNodeRef(final WebScriptRequest request) {
		final String storeType = getParam(request, STORE_TYPE);
		final String storeId = getParam(request, STORE_ID);
		final String identifier = getParam(request, PARAM_ID);
		return new NodeRef(storeType, storeId, identifier);
	}

	/**
	 * Gets the param.
	 *
	 * @param request   the request
	 * @param paramName the param name
	 * @return the param
	 */
	private String getParam(final WebScriptRequest request, final String paramName) {
		final String value = StringUtils.trimToNull(request.getServiceMatch().getTemplateVars().get(paramName));
		if (StringUtils.isBlank(value)) {
			throw new WebScriptException(Status.STATUS_BAD_REQUEST,
					String.format("Value for param '%s' is missing, Please provide a valid input", paramName));
		}
		return value;
	}

	/**
	 * Write response.
	 * 
	 * @param response           the response
	 * @param jsonObject         the json object
	 * @param clearCache         the clear cache
	 * @param responseStatusCode the response status code
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void writeResponse(final WebScriptResponse response, final JSONObject jsonObject, final boolean clearCache,
			final int responseStatusCode) throws IOException {
		final int length = jsonObject.toString().getBytes(ENCODING_UTF_8).length;
		response.setContentType(MimetypeMap.MIMETYPE_JSON);
		response.setContentEncoding(ENCODING_UTF_8);
		response.addHeader(CONTENT_LENGTH, String.valueOf(length));
		if (clearCache) {
			response.addHeader(CACHE_CONTROL, NO_CACHE);
			// Calculate the expires date as per you need, i have kept a regular date for
			// example purpose.
			response.addHeader(EXPIRES, "Thu, 04 Jan 2020 00:00:00 EDT");
			response.addHeader(PRAGMA, NO_CACHE);
		}
		response.setStatus(responseStatusCode);
		response.getWriter().write(jsonObject.toString());
	}
}