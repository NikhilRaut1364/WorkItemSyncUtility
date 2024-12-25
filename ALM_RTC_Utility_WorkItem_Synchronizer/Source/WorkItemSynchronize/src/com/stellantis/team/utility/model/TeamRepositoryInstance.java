package com.stellantis.team.utility.model;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.repository.client.ITeamRepository;

public enum TeamRepositoryInstance {
	INSTANCE;
	private ITeamRepository repo;
	private IProgressMonitor monitor;
	private String username;
	private String password;
	private String serverURL;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
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
	
}
