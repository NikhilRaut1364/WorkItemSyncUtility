package com.stellantis.team.utility.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IQueryClient;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.IQueryCommon;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemType;
import com.ibm.team.workitem.common.query.IQueryDescriptor;
import com.ibm.team.workitem.common.query.IQueryResult;
import com.ibm.team.workitem.common.query.IResolvedResult;
import com.ibm.team.workitem.common.query.QueryTypes;
import com.stellantis.team.utility.model.TeamRepositoryInstance;
import com.stellantis.team.utility.utils.CustomLogger;

public class QueryController {
	
	private ITeamRepository repo;
	private IProgressMonitor monitor;

	public QueryController() {
		setRepo(TeamRepositoryInstance.INSTANCE.getRepo());
		setMonitor(TeamRepositoryInstance.INSTANCE.getMonitor());
	}

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, IQueryDescriptor> fetchQueryAssignedToSelectedProjectArea(IProjectArea projectArea) {
		CustomLogger.logMessage("fetchQueryAssignedToSelectedProjectArea");
		Map<String, IQueryDescriptor> hmapQueryNameAndId = new TreeMap<>();
		try {
			IWorkItemClient client = (IWorkItemClient) getRepo()
					.getClientLibrary(IWorkItemClient.class);
			IQueryClient queryClient = client.getQueryClient();
			IQueryCommon iQueryCommon = (IQueryCommon) getRepo()
					.getClientLibrary(IQueryCommon.class);

			List sharingTargets = new ArrayList<>();
			sharingTargets.add(projectArea);

			List sharedQueryList = getSharedQueryList(queryClient, iQueryCommon, projectArea.getProjectArea(), sharingTargets);
			if (sharedQueryList != null) {
				for (Object object : sharedQueryList) {
					if (object instanceof IQueryDescriptor) {
						hmapQueryNameAndId.put(((IQueryDescriptor) object).getItemId().getUuidValue(),
								((IQueryDescriptor) object));
					}
				} 
			}
			List personalQueryList = getPersonalQueryList(getRepo(), queryClient, iQueryCommon,
					projectArea.getProjectArea());
			if (personalQueryList != null) {
				for (Object object : personalQueryList) {
					if (object instanceof IQueryDescriptor) {
						hmapQueryNameAndId.put(((IQueryDescriptor) object).getItemId().getUuidValue(),
								((IQueryDescriptor) object));

					}
				} 
			}
			return sortMapByDescriptorName(hmapQueryNameAndId);

		} catch (Exception e) {
			CustomLogger.logException(e);
		}
		return hmapQueryNameAndId;
	}
	
	private Map<String, IQueryDescriptor> sortMapByDescriptorName(Map<String, IQueryDescriptor> hashMap) {
        Comparator<Map.Entry<String, IQueryDescriptor>> valueComparator = new Comparator<Map.Entry<String, IQueryDescriptor>>() {
            @Override
            public int compare(Map.Entry<String, IQueryDescriptor> e1, Map.Entry<String, IQueryDescriptor> e2) {
                String name1 = e1.getValue().getName();
                String name2 = e2.getValue().getName();
                return name1.compareTo(name2);
            }
        };

        List<Map.Entry<String, IQueryDescriptor>> listOfEntries = new ArrayList<>(hashMap.entrySet());
        Collections.sort(listOfEntries, valueComparator);

        Map<String, IQueryDescriptor> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, IQueryDescriptor> entry : listOfEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getSharedQueryList(IQueryClient queryClient, IQueryCommon iQueryCommon,
			IProjectAreaHandle projectAreaHandle, List sharingTargets) {
		CustomLogger.logMessage("getSharedQueryList");
		List lstSharedQueries = new ArrayList<>();
		try {
			List lstAllSharedQueries = queryClient.findSharedQueries(projectAreaHandle, sharingTargets,
					QueryTypes.WORK_ITEM_QUERY, IQueryDescriptor.FULL_PROFILE, getMonitor());
			for (Object object : lstAllSharedQueries) {
				if (!lstSharedQueries.contains(object)) {
					lstSharedQueries.add(object);
				}
			}
			return lstSharedQueries;
		} catch (TeamRepositoryException e) {
			CustomLogger.logException(e);
		}
		return lstSharedQueries;
	}

	@SuppressWarnings("rawtypes")
	private List getPersonalQueryList(ITeamRepository repo, IQueryClient queryClient, IQueryCommon iQueryCommon,
			IProjectAreaHandle projectAreaHandle) {
		CustomLogger.logMessage("getPersonalQueryList");
		try {
			return queryClient.findPersonalQueries(projectAreaHandle, repo.loggedInContributor(),
					QueryTypes.WORK_ITEM_QUERY, IQueryDescriptor.FULL_PROFILE, getMonitor());
		} catch (Exception e) {
			CustomLogger.logException(e);
		}
		return null;
	}

	private Map<String, String> sortValueSetOfMap(Map<String, String> map) {
		CustomLogger.logMessage("sortValueSetOfMap");
		List<Map.Entry<String, String>> entryList = new ArrayList<>(map.entrySet());

		Collections.sort(entryList, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Map.Entry<String, String> entry1, Map.Entry<String, String> entry2) {
				return entry1.getValue().compareTo(entry2.getValue());
			}
		});

		Map<String, String> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : entryList) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public Map<String, String> getWorkItemFromQuery(String projectAreaName, String queryUUID){
		CustomLogger.logMessage("getWorkItemFromQuery");
		Map<String, String> mapWorkItem = new TreeMap<>();
		try {
			ProcessAreaController processAreaController = new ProcessAreaController();
			IProjectAreaHandle projectAreaHandle = (IProjectAreaHandle) processAreaController
					.getProjectArea(projectAreaName);
			IWorkItemClient client = (IWorkItemClient) getRepo()
					.getClientLibrary(IWorkItemClient.class);
			IQueryClient queryClient = client.getQueryClient();
			IQueryCommon iQueryCommon = (IQueryCommon) getRepo()
					.getClientLibrary(IQueryCommon.class);
			IQueryDescriptor queryDescriptor = getSharedQueryDescriptor(processAreaController, queryClient,
					iQueryCommon, projectAreaHandle, projectAreaName, queryUUID);
			if (queryDescriptor == null) {
				queryDescriptor = getPersonalQueryDescriptor(processAreaController, queryClient, iQueryCommon,
						projectAreaHandle, projectAreaName, queryUUID);
			}
			
			if (queryDescriptor != null){
				IQueryResult<IResolvedResult<IWorkItem>> resolvedQueryResults = queryClient
						.getResolvedQueryResults(queryDescriptor, IWorkItem.SMALL_PROFILE);
				if (resolvedQueryResults.getResultSize(getMonitor()).getTotal() > 0){
					while (resolvedQueryResults.hasNext(getMonitor())){
						IResolvedResult<IWorkItem> next = resolvedQueryResults
								.next(getMonitor());
						IWorkItem item = (IWorkItem) client.getAuditableCommon().resolveAuditable(next.getItem(),
								IWorkItem.FULL_PROFILE, getMonitor()).getWorkingCopy();
						
						mapWorkItem.put(String.valueOf(item.getId()), item.getId() + " - " + item.getHTMLSummary().getPlainText());
					}
				}
			}
			return sortValueSetOfMap(mapWorkItem);
		} catch (Exception e) {
			CustomLogger.logException(e);
		}
		return mapWorkItem;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private IQueryDescriptor getSharedQueryDescriptor(ProcessAreaController processAreaController,
			IQueryClient queryClient, IQueryCommon iQueryCommon, IProjectAreaHandle projectAreaHandle,
			String projectAreaName, String queryUUID) throws TeamRepositoryException {
		CustomLogger.logMessage("getSharedQueryDescriptor");
		List sharingTargets = new ArrayList<>();
		sharingTargets.add(processAreaController.getProjectArea(projectAreaName));

		List sharedQueryList = getSharedQueryList(queryClient, iQueryCommon, projectAreaHandle, sharingTargets);
		for (Object object : sharedQueryList) {
			if (object instanceof IQueryDescriptor) {
				if (((IQueryDescriptor) object).getItemId().getUuidValue().equals(queryUUID)) {
					return ((IQueryDescriptor) object);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private IQueryDescriptor getPersonalQueryDescriptor(ProcessAreaController processAreaController,
			IQueryClient queryClient, IQueryCommon iQueryCommon, IProjectAreaHandle projectAreaHandle,
			String projectAreaName, String queryUUID) {
		CustomLogger.logMessage("getPersonalQueryDescriptor");
		List personalQueryList = getPersonalQueryList(getRepo(), queryClient, iQueryCommon,
				projectAreaHandle);
		for (Object object : personalQueryList) {
			if (object instanceof IQueryDescriptor) {
				if (((IQueryDescriptor) object).getItemId().getUuidValue().equals(queryUUID)) {
					return ((IQueryDescriptor) object);
				}
			}
		}
		return null;
	}
	
	public Map<String, IWorkItemType> getWorkItemTypeFromQuery(IProjectArea projectArea ,IQueryDescriptor queryDescriptor) throws TeamRepositoryException{
		Map<String, IWorkItemType> mapWorkItemType = new HashMap<>();
		IWorkItemClient client = (IWorkItemClient) getRepo()
				.getClientLibrary(IWorkItemClient.class);
		IQueryClient queryClient = client.getQueryClient();
		if (queryDescriptor != null) {
			IQueryResult<IResolvedResult<IWorkItem>> resolvedQueryResults = queryClient
					.getResolvedQueryResults(queryDescriptor, IWorkItem.SMALL_PROFILE);
			if (resolvedQueryResults.getResultSize(getMonitor()).getTotal() > 0) {
				while (resolvedQueryResults.hasNext(getMonitor())){
					IResolvedResult<IWorkItem> next = resolvedQueryResults
							.next(getMonitor());
					IWorkItem item = (IWorkItem) client.getAuditableCommon().resolveAuditable(next.getItem(),
							IWorkItem.FULL_PROFILE, getMonitor()).getWorkingCopy();
					IWorkItemType workItemType = client.findWorkItemType(projectArea.getProjectArea(), item.getWorkItemType(), getMonitor());
					mapWorkItemType.put(item.getWorkItemType(), workItemType);
				}
			}
		}
		
		return mapWorkItemType;
	}
	
	public Map<String, IWorkItemType> getSingleWorkItemType(IProjectArea projectArea ,IQueryDescriptor queryDescriptor) throws TeamRepositoryException{
		Map<String, IWorkItemType> mapWorkItemType = new HashMap<>();
		IWorkItemClient client = (IWorkItemClient) getRepo()
				.getClientLibrary(IWorkItemClient.class);
		IQueryClient queryClient = client.getQueryClient();
		if (queryDescriptor != null) {
			IQueryResult<IResolvedResult<IWorkItem>> resolvedQueryResults = queryClient
					.getResolvedQueryResults(queryDescriptor, IWorkItem.SMALL_PROFILE);
			if (resolvedQueryResults.getResultSize(getMonitor()).getTotal() > 0) {
				while (resolvedQueryResults.hasNext(getMonitor())){
					IResolvedResult<IWorkItem> next = resolvedQueryResults
							.next(getMonitor());
					IWorkItem item = (IWorkItem) client.getAuditableCommon().resolveAuditable(next.getItem(),
							IWorkItem.FULL_PROFILE, getMonitor()).getWorkingCopy();
					IWorkItemType workItemType = client.findWorkItemType(projectArea.getProjectArea(), item.getWorkItemType(), getMonitor());
					mapWorkItemType.put(item.getWorkItemType(), workItemType);
					break;
				}
			}
		}
		
		return mapWorkItemType;
	}

}
