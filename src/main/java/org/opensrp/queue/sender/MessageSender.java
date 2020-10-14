/**
 * 
 */
package org.opensrp.queue.sender;

import org.opensrp.queue.PlanEvaluatorMessage;
import org.opensrp.queue.ResourceEvaluatorMessage;

/**
 * @author Samuel Githengi created on 08/28/20
 */
public interface MessageSender {
	
	/**
	 * Sends plan message to queue system for evaluation
	 * 
	 * @param planEvaluatorMessage
	 */
	void send(PlanEvaluatorMessage planEvaluatorMessage);
	
	/**
	 * Sends resource message to queue system for evaluation
	 * 
	 * @param resourceEvaluatorMessage
	 */
	void send(ResourceEvaluatorMessage resourceEvaluatorMessage);
}
