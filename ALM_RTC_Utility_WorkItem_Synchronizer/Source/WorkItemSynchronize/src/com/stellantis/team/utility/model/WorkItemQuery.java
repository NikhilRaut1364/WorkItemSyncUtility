package com.stellantis.team.utility.model;

import com.ibm.team.workitem.common.query.IQueryDescriptor;

public class WorkItemQuery {
	private String queryName;
	private IQueryDescriptor queryDescriptor;
	
	public WorkItemQuery(String queryName, IQueryDescriptor queryDescriptor) {
		super();
		this.queryName = queryName;
		this.queryDescriptor = queryDescriptor;
	}
	
	public String getQueryName() {
		return queryName;
	}
	
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	
	public IQueryDescriptor getQueryDescriptor() {
		return queryDescriptor;
	}
	
	public void setQueryDescriptor(IQueryDescriptor queryDescriptor) {
		this.queryDescriptor = queryDescriptor;
	}
	
	@Override
	public String toString() {
		return queryName;
	}
}
