package ca.uhn.fhir.rest.method;

/*
 * #%L
 * HAPI FHIR Library
 * %%
 * Copyright (C) 2014 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.valueset.RestfulOperationSystemEnum;
import ca.uhn.fhir.model.dstu.valueset.RestfulOperationTypeEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Validate;
import ca.uhn.fhir.rest.client.BaseClientInvocation;
import ca.uhn.fhir.rest.client.PostClientInvocation;
import ca.uhn.fhir.rest.method.SearchMethodBinding.RequestType;
import ca.uhn.fhir.rest.server.Constants;

public class ValidateMethodBinding extends BaseOutcomeReturningMethodBindingWithResourceParam {

	private Integer myIdParameterIndex;

	public ValidateMethodBinding(Method theMethod, FhirContext theContext, Object theProvider) {
		super(theMethod, theContext, Validate.class, theProvider);

		myIdParameterIndex = Util.findIdParameterIndex(theMethod);
	}

	@Override
	public RestfulOperationTypeEnum getResourceOperationType() {
		return RestfulOperationTypeEnum.VALIDATE;
	}

	@Override
	public RestfulOperationSystemEnum getSystemOperationType() {
		return null;
	}

	@Override
	protected void addParametersForServerRequest(Request theRequest, Object[] theParams) {
		if (myIdParameterIndex != null) {
			theParams[myIdParameterIndex] = theRequest.getId();
		}
	}

	@Override
	protected BaseClientInvocation createClientInvocation(Object[] theArgs, IResource theResource) {
		FhirContext context = getContext();
		
		IdDt idDt=null;
		if (myIdParameterIndex != null) {
			idDt = (IdDt) theArgs[myIdParameterIndex];
		}

		PostClientInvocation retVal = createValidateInvocation(theResource, idDt, context);
		return retVal;
	}

	public static PostClientInvocation createValidateInvocation(IResource theResource, IdDt theId, FhirContext theContext) {
		StringBuilder urlExtension = new StringBuilder();
		urlExtension.append(theContext.getResourceDefinition(theResource).getName());
		urlExtension.append('/');
		urlExtension.append(Constants.PARAM_VALIDATE);

		if (theId != null && theId.isEmpty() == false) {
			String id = theId.getValue();
			urlExtension.append('/');
			urlExtension.append(id);
		}
		// TODO: is post correct here?
		PostClientInvocation retVal = new PostClientInvocation(theContext, theResource, urlExtension.toString());
		return retVal;
	}


	@Override
	protected boolean allowVoidReturnType() {
		return true;
	}

	@Override
	protected Set<RequestType> provideAllowableRequestTypes() {
		// TODO: is post correct here?
		return Collections.singleton(RequestType.POST);
	}


	@Override
	protected String getMatchingOperation() {
		return Constants.PARAM_VALIDATE;
	}

}
