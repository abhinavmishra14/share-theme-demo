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
package com.github.abhinavmishra14.action.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.User;

/**
 * The Class IsAdmin.
 *
 */
public class IsAdmin extends BaseEvaluator {

	/* (non-Javadoc)
	 * @see org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject)
	 */
	@Override
	public boolean evaluate(final JSONObject jsonObject) {
		try {
			final RequestContext requestCtx = ThreadLocalRequestContext.getRequestContext();
			final User user = requestCtx.getUser();
			return user != null && user.isAdmin();
		} catch (RuntimeException excp) {
			throw new AlfrescoRuntimeException("Exception while running action evaluator: "
							+ excp.getMessage(), excp);
		}
	}
}
