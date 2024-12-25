package com.stellantis.team.utility.service;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.ibm.team.workitem.common.model.IWorkItem;
import com.stellantis.team.utility.controller.WorkItemController;
import com.stellantis.team.utility.model.Status;
import com.stellantis.team.utility.utils.CustomLogger;
import com.stellantis.team.utility.utils.GenerateExcel;

public class WorkItemSyncWorker extends UtilitySwingWorker {
	private DefaultTableModel model;
	private String outputFileLocation;
	
	public WorkItemSyncWorker(String outputFileLocation, DefaultTableModel model) {
		super();
		this.outputFileLocation = outputFileLocation;
		this.model = model;
	}

	@Override
	protected Boolean doInBackground() {
		try {
			publish(Status.INFO.toString() + "@" + "Please wait as we are syncing the Work Items");
			WorkItemController workItemController = new WorkItemController();
			List<IWorkItem> synchronizeWorkItem = workItemController.synchronizeWorkItem(model);
			GenerateExcel.INSTANCE.writeToFile(outputFileLocation, synchronizeWorkItem);
			publish(Status.SUCCESSFUL.toString() + "@" + "Workitem Synchronize report exported at location [" + outputFileLocation + "]");
			if(!synchronizeWorkItem.isEmpty())
				return true;
		} catch (Exception e) {
			CustomLogger.logException(e);
		}
		return false;
	}
}
