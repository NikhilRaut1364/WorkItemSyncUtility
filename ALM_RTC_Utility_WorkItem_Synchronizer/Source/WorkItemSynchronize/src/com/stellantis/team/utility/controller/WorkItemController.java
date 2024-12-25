package com.stellantis.team.utility.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IQueryClient;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.WorkItemOperation;
import com.ibm.team.workitem.common.IAuditableCommon;
import com.ibm.team.workitem.common.expression.AttributeExpression;
import com.ibm.team.workitem.common.expression.Expression;
import com.ibm.team.workitem.common.expression.IQueryableAttribute;
import com.ibm.team.workitem.common.expression.QueryableAttributes;
import com.ibm.team.workitem.common.expression.Term;
import com.ibm.team.workitem.common.model.AttributeOperation;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;
import com.ibm.team.workitem.common.query.IQueryResult;
import com.ibm.team.workitem.common.query.IResult;
import com.stellantis.team.utility.model.ProjectArea;
import com.stellantis.team.utility.model.TeamRepositoryInstance;
import com.stellantis.team.utility.model.WorkItemQuery;
import com.stellantis.team.utility.model.WorkItemType;
import com.stellantis.team.utility.utils.SynchronizeWorkItemOperation;

public class WorkItemController {
	private ITeamRepository repo;
	private IProgressMonitor monitor;
	
	public ITeamRepository getRepo() {
		return repo;
	}

	public void setRepo(ITeamRepository repo) {
		this.repo = repo;
	}
	
	public IProgressMonitor getMonitor() {
		return monitor;
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public WorkItemController() {
		setRepo(TeamRepositoryInstance.INSTANCE.getRepo());
		 setMonitor(TeamRepositoryInstance.INSTANCE.getMonitor());
	}
	
	public List<WorkItemType> getWorkItemType(IProjectArea projectArea) throws TeamRepositoryException {
		List<WorkItemType> lstWorkitemType = new ArrayList<>();
		List<IWorkItemType> findWorkItemTypes = getIWorkItemTypeList(projectArea);
		for (IWorkItemType iWorkItemType : findWorkItemTypes) {
			lstWorkitemType.add(new WorkItemType(iWorkItemType.getDisplayName() + " [" + iWorkItemType.getIdentifier() + "]" , iWorkItemType));
		}
		return lstWorkitemType;
	}

	private List<IWorkItemType> getIWorkItemTypeList(IProjectArea projectArea) throws TeamRepositoryException {
		IWorkItemClient client = (IWorkItemClient) getRepo().getClientLibrary(IWorkItemClient.class);
		List<IWorkItemType> findWorkItemTypes = client.findWorkItemTypes(projectArea.getProjectArea(), getMonitor());
		return findWorkItemTypes;
	}
	
	public boolean isWorkItemTypeExist(IProjectArea projectArea, String workItemTypeIdentifier) throws TeamRepositoryException {
		boolean isExist = false;
		List<IWorkItemType> findWorkItemTypes = getIWorkItemTypeList(projectArea);
		for (IWorkItemType iWorkItemType : findWorkItemTypes) {
			if(workItemTypeIdentifier.equals(iWorkItemType.getIdentifier())){
				isExist = true;
				break;
			}
		}
		
		return isExist;
	}

	public List<IWorkItem> synchronizeWorkItem(DefaultTableModel model) throws TeamRepositoryException{
//		IProcessClientService processClientService = (IProcessClientService) getRepo().getClientLibrary(IProcessClientService.class);
		IWorkItemClient client = (IWorkItemClient) getRepo().getClientLibrary(IWorkItemClient.class);
		IAuditableCommon auditableCommon = (IAuditableCommon) getRepo().getClientLibrary(IAuditableCommon.class);
		List<IWorkItem> lstSyncedWorkItem = new ArrayList<>();
		for (int row = 0; row < model.getRowCount(); row++){
			ProjectArea existingProjectArea = (ProjectArea) model.getValueAt(row, 0);
			WorkItemQuery workItemQuery = (WorkItemQuery) model.getValueAt(row, 1);
			Map<String, IWorkItemType> singleWorkItemType = new QueryController().getSingleWorkItemType(existingProjectArea.getProjectAreaObj(), workItemQuery.getQueryDescriptor());
			if(singleWorkItemType.size() > 0){
				for (Map.Entry<String, IWorkItemType> entry : singleWorkItemType.entrySet()){
					IWorkItemType existingWorkItemType = entry.getValue();
					boolean isClosedState = true;
					
					IQueryableAttribute attribute = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(
							existingProjectArea.getProjectAreaObj(), IWorkItem.PROJECT_AREA_PROPERTY, auditableCommon,
							getMonitor());
					
					IQueryableAttribute type = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(
							existingProjectArea.getProjectAreaObj(), IWorkItem.TYPE_PROPERTY, auditableCommon, getMonitor());
					
					Expression inProjectArea = new AttributeExpression(attribute, AttributeOperation.EQUALS,
							existingProjectArea.getProjectAreaObj());
					
					Expression inType = new AttributeExpression(type, AttributeOperation.EQUALS,
							existingWorkItemType.getIdentifier());
					
					Term typeInProjectArea = new Term(Term.Operator.AND);
					typeInProjectArea.add(inProjectArea);
					typeInProjectArea.add(inType);
					
					IQueryResult<IResult> expressionResult = resultsUnresolvedByExpression(existingProjectArea.getProjectAreaObj(), typeInProjectArea);
					expressionResult.setLimit(Integer.MAX_VALUE);
					
					SynchronizeWorkItemOperation synchronizeWorkItemOperation = new SynchronizeWorkItemOperation(client, isClosedState);
					processUnresolvedResults(existingProjectArea.getProjectAreaObj(), expressionResult, synchronizeWorkItemOperation, getMonitor());
					lstSyncedWorkItem.addAll(synchronizeWorkItemOperation.getWorkItemSyncList());
				}
			}
			
			System.gc();
		}
		
		return lstSyncedWorkItem;
	}
	
	private IQueryResult<IResult> resultsUnresolvedByExpression(IProjectArea projectArea, Expression expression){
		IWorkItemClient client = (IWorkItemClient) getRepo().getClientLibrary(IWorkItemClient.class);
		IQueryClient queryClient = client.getQueryClient();
		IQueryResult<IResult> results = queryClient.getExpressionResults(projectArea, expression);
		return results;
		
	}
	
	private void processUnresolvedResults(IProjectArea projectArea, IQueryResult<IResult> results, WorkItemOperation operation,
			IProgressMonitor monitor) throws TeamRepositoryException {

		long processed = 0;
		while (results.hasNext(monitor)) {
			IResult result = (IResult) results.next(monitor);
			operation.run((IWorkItemHandle) result.getItem(), monitor);
			processed++;
		}
		System.out.println("Processedlts: " + processed);
	}
}
