package com.stellantis.team.utility.service;

import java.util.List;
import java.util.Map;

import com.ibm.team.workitem.common.model.IWorkItemType;
import com.stellantis.team.utility.controller.QueryController;
import com.stellantis.team.utility.model.ProjectArea;
import com.stellantis.team.utility.model.Status;
import com.stellantis.team.utility.model.WorkItemQuery;
import com.stellantis.team.utility.utils.CustomLogger;
import com.stellantis.team.utility.view.WorkItemSyncMappingTable;

public class ValidateWorkItemQueryWorker extends UtilitySwingWorker {

	private ProjectArea projectArea;
	private List<WorkItemQuery> queryDescriptor;
	
	public ValidateWorkItemQueryWorker(ProjectArea projectArea, List<WorkItemQuery> queryDescriptor) {
		super();
		this.projectArea = projectArea;
		this.queryDescriptor = queryDescriptor;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		try {
			QueryController controller = new QueryController();
			for (WorkItemQuery workItemQuery : queryDescriptor) {
				Map<String, IWorkItemType> workItemTypeFromQuery = controller.getWorkItemTypeFromQuery(projectArea.getProjectAreaObj(), workItemQuery.getQueryDescriptor());
				if(workItemTypeFromQuery.size() == 1)
					WorkItemSyncMappingTable.addRow(projectArea, workItemQuery);
				else if(workItemTypeFromQuery.size() == 0)
					publish(Status.ERROR.toString() + "@" + "No work items found in selected query [" + workItemQuery.getQueryDescriptor().getName() + "]");
				else
					publish(Status.ERROR.toString() + "@" + "Selected query [" + workItemQuery.getQueryDescriptor().getName() + "] have more than one Work Item Type.");
			}
		} catch (Exception e) {
			CustomLogger.logException(e);
		}
		return true;
	}

}
