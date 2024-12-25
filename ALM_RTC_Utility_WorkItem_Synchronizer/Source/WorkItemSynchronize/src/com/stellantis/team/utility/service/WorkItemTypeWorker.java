package com.stellantis.team.utility.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stellantis.team.utility.controller.WorkItemController;
import com.stellantis.team.utility.model.ProjectArea;
import com.stellantis.team.utility.model.Status;
import com.stellantis.team.utility.model.WorkItemType;
import com.stellantis.team.utility.utils.CustomLogger;

public class WorkItemTypeWorker extends UtilitySwingWorker {

	private List<ProjectArea> lstSelectedProjectArea;
	private List<WorkItemType> workItemTypeList;
	
	public WorkItemTypeWorker(List<ProjectArea> lstSelectedProjectArea) {
		this.lstSelectedProjectArea = lstSelectedProjectArea;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		try {
			List<WorkItemType> lstAllWorkItemTypes = new ArrayList<>();
			WorkItemController controller = new WorkItemController();
			if (lstSelectedProjectArea != null && !lstSelectedProjectArea.isEmpty()) {
				publish(Status.INFO.toString() + "@" + "Please wait as we fetch all the work item types for selected Project Areas.");
				for (ProjectArea projectArea : lstSelectedProjectArea) {
					List<WorkItemType> workItemTypes = controller.getWorkItemType(projectArea.getProjectAreaObj());
					for (WorkItemType workItemType : workItemTypes) {
						if (!isWorkItemTypePresent(lstAllWorkItemTypes, workItemType)) {
	                        lstAllWorkItemTypes.add(workItemType);
	                    }
					}
				} 
				
				if(lstAllWorkItemTypes.isEmpty()){
					publish(Status.INFO.toString() + "@" + "No WorkItem Type found for selected Project Area.");
					return false;
				}
				
				Collections.sort(lstAllWorkItemTypes, new Comparator<WorkItemType>() {
		            @Override
		            public int compare(WorkItemType o1, WorkItemType o2) {
		                return o1.getWorkItemTypeName().compareTo(o2.getWorkItemTypeName());
		            }
		        });
				
				getWorkItemTypeList().addAll(lstAllWorkItemTypes);
				if(!getWorkItemTypeList().isEmpty()){
					publish(Status.SUCCESSFUL.toString() + "@" + "WorkItem Type fetched for selected Project Areas.");
					return true;
				}
			}
		} catch (Exception e) {
			CustomLogger.logException(e);
		}
		return false;
	}
	
	private boolean isWorkItemTypePresent(List<WorkItemType> lstAllWorkItemTypes, WorkItemType workItemType) {
        for (WorkItemType item : lstAllWorkItemTypes) {
            if (item.getWorkItemType().getIdentifier().equals(workItemType.getWorkItemType().getIdentifier())) {
                return true;
            }
        }
        return false;
    }


	public List<WorkItemType> getWorkItemTypeList() {
		if(workItemTypeList == null)
			workItemTypeList = new ArrayList<>();
		return workItemTypeList;
	}


	public void setWorkItemTypeList(List<WorkItemType> workItemTypeList) {
		this.workItemTypeList = workItemTypeList;
	}
}
