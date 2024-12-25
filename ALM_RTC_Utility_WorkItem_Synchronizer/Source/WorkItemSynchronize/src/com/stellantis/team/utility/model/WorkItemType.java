package com.stellantis.team.utility.model;

import com.ibm.team.workitem.common.model.IWorkItemType;

public class WorkItemType {
	private String workItemTypeName;
	private IWorkItemType workItemType;
	
	public WorkItemType(String workItemTypeName, IWorkItemType workItemType) {
		super();
		this.workItemTypeName = workItemTypeName;
		this.workItemType = workItemType;
	}

	public String getWorkItemTypeName() {
		return workItemTypeName;
	}

	public void setWorkItemTypeName(String workItemTypeName) {
		this.workItemTypeName = workItemTypeName;
	}

	public IWorkItemType getWorkItemType() {
		return workItemType;
	}

	public void setWorkItemType(IWorkItemType workItemType) {
		this.workItemType = workItemType;
	}

	@Override
	public String toString() {
		return workItemTypeName;
	}
	
}
