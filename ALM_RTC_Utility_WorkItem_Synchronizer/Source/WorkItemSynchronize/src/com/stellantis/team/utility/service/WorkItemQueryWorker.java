package com.stellantis.team.utility.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.workitem.common.query.IQueryDescriptor;
import com.stellantis.team.utility.controller.QueryController;
import com.stellantis.team.utility.model.Status;
import com.stellantis.team.utility.model.WorkItemQuery;
import com.stellantis.team.utility.utils.CustomLogger;

public class WorkItemQueryWorker extends UtilitySwingWorker{

	private IProjectArea projectArea;
	private List<WorkItemQuery> lstWorkItemQuery;

	public List<WorkItemQuery> getLstWorkItemQuery() {
		if(lstWorkItemQuery == null)
			lstWorkItemQuery = new ArrayList<>();
		return lstWorkItemQuery;
	}

	public void setLstWorkItemQuery(List<WorkItemQuery> lstWorkItemQuery) {
		this.lstWorkItemQuery = lstWorkItemQuery;
	}

	public WorkItemQueryWorker(IProjectArea projectArea) {
		this.projectArea = projectArea;
	}
	
	@Override
	protected Boolean doInBackground() throws Exception {
		try {
			publish(Status.INFO.toString() + "@" + "Please wait as we fetch all the work item queries for selected Project Areas [" + projectArea.getName() + "]");
			QueryController queryController = new QueryController();
			Map<String, IQueryDescriptor> workItemQueries = queryController.fetchQueryAssignedToSelectedProjectArea(projectArea);
			for (Map.Entry<String, IQueryDescriptor> entry : workItemQueries.entrySet()){
				IQueryDescriptor queryDescriptor = entry.getValue();
				getLstWorkItemQuery().add(new WorkItemQuery(queryDescriptor.getName(), queryDescriptor));
			}
			if(getLstWorkItemQuery().size() > 0)
				return true;
		} catch (Exception e) {
			CustomLogger.logException(e);
		}
		return false;
	}
}
