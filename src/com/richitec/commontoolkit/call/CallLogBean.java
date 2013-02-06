package com.richitec.commontoolkit.call;

import java.io.Serializable;

public class CallLogBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2367098775947400160L;

	// call log id
	private Long callLogId;
	// callee name
	private String calleeName;
	// callee phone number
	private String calleePhone;
	// call date
	private Long callDate;
	// call duration
	private Long callDuration;
	// call type
	private CallType callType;

	public Long getCallLogId() {
		return callLogId;
	}

	public void setCallLogId(Long callLogId) {
		this.callLogId = callLogId;
	}

	public String getCalleeName() {
		return calleeName;
	}

	public void setCalleeName(String calleeName) {
		this.calleeName = calleeName;
	}

	public String getCalleePhone() {
		return calleePhone;
	}

	public void setCalleePhone(String calleePhone) {
		this.calleePhone = calleePhone;
	}

	public Long getCallDate() {
		return callDate;
	}

	public void setCallDate(Long callDate) {
		this.callDate = callDate;
	}

	public Long getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(Long callDuration) {
		this.callDuration = callDuration;
	}

	public CallType getCallType() {
		return callType;
	}

	public void setCallType(CallType callType) {
		this.callType = callType;
	}

	@Override
	public String toString() {
		// init call log description
		StringBuilder _callLogDescription = new StringBuilder();

		// append callee name, phone number, date, duration and call type
		_callLogDescription.append("Call log id: ").append(callLogId)
				.append(", ");
		_callLogDescription.append("callee name: ").append(calleeName)
				.append(", ");
		_callLogDescription.append("callee phone number: ").append(calleePhone)
				.append(", ");
		_callLogDescription.append("call date: ").append(callDate).append(", ");
		_callLogDescription.append("call duration: ").append(callDuration)
				.append(" seconds").append(", ");
		_callLogDescription.append("call type: ").append(callType).append("\n");

		return _callLogDescription.toString();
	}

	// inner class
	public enum CallType {
		INCOMING, OUTGOING, MISSED
	}

}
