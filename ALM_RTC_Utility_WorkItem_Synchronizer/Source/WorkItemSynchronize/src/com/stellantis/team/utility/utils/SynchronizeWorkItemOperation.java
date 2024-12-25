package com.stellantis.team.utility.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.WorkItemOperation;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemType;
import com.ibm.team.workitem.common.workflow.IWorkflowInfo;

public class SynchronizeWorkItemOperation extends WorkItemOperation{

	private IWorkItemClient workItemClient = null;
	private List<IWorkItem> workItemSyncList;
	private boolean isClosedState;
	
	public SynchronizeWorkItemOperation(IWorkItemClient workItemClient, boolean isClosedState) {
		super("Synchronize Work Items", IWorkItem.FULL_PROFILE);
		this.workItemClient = workItemClient;
		this.isClosedState = isClosedState;
	}
	
	@Override
	protected void execute(WorkItemWorkingCopy workingCopy, IProgressMonitor monitor) throws TeamRepositoryException {
		IWorkItem workItem = workingCopy.getWorkItem();
		IWorkItemType type = workItemClient.findWorkItemType(workItem.getProjectArea(), workItem.getWorkItemType(), monitor);
		IWorkflowInfo workflowInfo = workItemClient.findCachedWorkflowInfo(workItem);
		if(type != null) {
			if(isClosedState){
				updateWorkItem(monitor, workItem, type);
			} else if (workflowInfo.getStateGroup(workItem.getState2()) != IWorkflowInfo.CLOSED_STATES){
				updateWorkItem(monitor, workItem, type);
			}
		}
		
	}

	private void updateWorkItem(IProgressMonitor monitor, IWorkItem workItem, IWorkItemType type)
			throws TeamRepositoryException {
		workItemClient.updateWorkItemType(workItem, type, type, monitor);
		getWorkItemSyncList().add(workItem);
	}
	
	public List<IWorkItem> getWorkItemSyncList() {
		if(workItemSyncList == null)
			workItemSyncList = new ArrayList<>();
		return workItemSyncList;
	}

	public void setWorkItemSyncList(List<IWorkItem> workItemSyncList) {
		this.workItemSyncList = workItemSyncList;
	}

}
