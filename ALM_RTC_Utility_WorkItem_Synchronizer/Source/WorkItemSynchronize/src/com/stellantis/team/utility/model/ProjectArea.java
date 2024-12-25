package com.stellantis.team.utility.model;

import com.ibm.team.process.common.IProjectArea;

public class ProjectArea {

	private String projectAreaName;
	private IProjectArea projectAreaObj;
	
	public ProjectArea(String projectAreaName, IProjectArea projectAreaObj) {
		this.projectAreaName = projectAreaName;
		this.projectAreaObj = projectAreaObj;
	}

	public String getProjectAreaName() {
		return projectAreaName;
	}

	public IProjectArea getProjectAreaObj() {
		return projectAreaObj;
	}

	@Override
    public String toString() {
        return projectAreaName;
    }
}
