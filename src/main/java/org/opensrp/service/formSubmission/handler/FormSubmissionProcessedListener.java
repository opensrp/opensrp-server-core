package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;

import java.util.List;

public interface FormSubmissionProcessedListener {
	
	public void onFormSubmissionProcessed(String client, List<String> dependents, FormSubmission submission);
}
