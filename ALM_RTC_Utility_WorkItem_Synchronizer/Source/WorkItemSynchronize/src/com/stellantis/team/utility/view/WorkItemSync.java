package com.stellantis.team.utility.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.stellantis.team.utility.controller.LoginController;
import com.stellantis.team.utility.model.ProjectArea;
import com.stellantis.team.utility.model.Server;
import com.stellantis.team.utility.model.Status;
import com.stellantis.team.utility.model.WorkItemQuery;
import com.stellantis.team.utility.service.LoginWorker;
import com.stellantis.team.utility.service.ValidateWorkItemQueryWorker;
import com.stellantis.team.utility.service.WorkItemQueryWorker;
import com.stellantis.team.utility.service.WorkItemSyncWorker;
import com.stellantis.team.utility.utils.CommonUtils;
import com.stellantis.team.utility.utils.CustomLogger;
import com.stellantis.team.utility.utils.ExtractProperties;
import com.stellantis.team.utility.utils.UtilityConstants;

public class WorkItemSync extends BaseApplicationFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JTextField txtSearchProject;
	private JTextField txtSearchWorkItemType;
	private JComboBox<Server> cmbServer;
	private JButton btnLoginLogout;
	private JLabel lblLogs;
	private JLabel lblHelp;
	private JList<ProjectArea> lstProjectArea;
	private DefaultListModel<ProjectArea> lstProjectAreaModel = new DefaultListModel<ProjectArea>();
	private List<ProjectArea> allProjectAreas = new ArrayList<>();
	private JList<WorkItemQuery> lstWorkItemType;
	private DefaultListModel<WorkItemQuery> lstWorkItemTypeModel = new DefaultListModel<WorkItemQuery>();
	private List<WorkItemQuery> allWorkItemTypes = new ArrayList<>();
	private JButton btnMap;
	private List<ProjectArea> selectedProjectItems = new ArrayList<>();
	private List<WorkItemQuery> selectedWorkItemTypeItems = new ArrayList<>();
	private JButton btnCancel;
	private JButton btnProceed;
	private JTextField txtExportLocation;
	private JButton btnChoose;
	private boolean isPopupShown = false;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExtractProperties.getInstance();
					WorkItemSync frame = new WorkItemSync();
					frame.setVisible(true);
				} catch (Exception e) {
					CustomLogger.logException(e);
				}
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void customInitialize() {
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlLogin = new JPanel();
		pnlLogin.setBackground(Color.WHITE);
		pnlLogin.setBorder(new TitledBorder(null, UtilityConstants.LOGIN, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(pnlLogin, BorderLayout.NORTH);
		pnlLogin.setLayout(new BoxLayout(pnlLogin, BoxLayout.X_AXIS));
		
		cmbServer = new JComboBox<Server>();
		cmbServer.setFont(getItalicFont());
		pnlLogin.add(cmbServer);
		bindServerComboBox(cmbServer);
		
		Component horizontalStrut = Box.createHorizontalStrut(10);
		pnlLogin.add(horizontalStrut);
		
		txtUsername = new JTextField();
		txtUsername.setFont(getItalicFont());
		txtUsername.setPreferredSize(getSize());
		pnlLogin.add(txtUsername);
		txtUsername.setColumns(10);
		txtUsername.setText(UtilityConstants.USERNAME);
		txtUsername.addFocusListener(txtUsernameFocusListener(txtUsername));
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		pnlLogin.add(horizontalStrut_1);
		
		txtPassword = new JPasswordField();
		txtPassword.setFont(getItalicFont());
		txtPassword.setPreferredSize(getSize());
		pnlLogin.add(txtPassword);
		txtPassword.setColumns(10);
		txtPassword.setText(UtilityConstants.PASSWORD);
		txtPassword.setEchoChar((char)0);
		txtPassword.addFocusListener(txtPasswordFocusListener(txtPassword));
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(10);
		pnlLogin.add(horizontalStrut_3);
		
		btnLoginLogout = new JButton(UtilityConstants.LOGIN);
		btnLoginLogout.setBackground(setButtonBackgroundColor());
		btnLoginLogout.setFont(setButtonFontToBold(btnLoginLogout));
		pnlLogin.add(btnLoginLogout);
		btnLoginLogout.addActionListener(btnLogin_Click());
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(10);
		pnlLogin.add(horizontalStrut_2);
		
		lblLogs = new JLabel(UtilityConstants.LOGS);
		Font font = lblLogs.getFont();
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		lblLogs.setFont(font.deriveFont(attributes));
		pnlLogin.add(lblLogs);
		lblLogs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lblLogs.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblLogs.setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				CommonUtils.openNotepadFile(UtilityConstants.EXCEPTION_FILE_PATH);
			}
		});
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(10);
		pnlLogin.add(horizontalStrut_4);
		
		lblHelp = new JLabel(UtilityConstants.HELP);
		Font font1 = lblLogs.getFont();
		Map attributes1 = font1.getAttributes();
		attributes1.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		lblHelp.setFont(font.deriveFont(attributes1));
		pnlLogin.add(lblHelp);
		lblHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lblHelp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblHelp.setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				showHelpContent();
			}
		});
		
		JPanel pnlMain = new JPanel();
		pnlMain.setBackground(Color.WHITE);
		contentPane.add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel pnlProjectProps = new JPanel();
		pnlProjectProps.setBackground(Color.WHITE);
		pnlProjectProps.setBorder(new TitledBorder(null, "Synchronize", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlMain.add(pnlProjectProps);
		pnlProjectProps.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel pnlProjectArea = new JPanel();
		pnlProjectArea.setBackground(Color.WHITE);
		pnlProjectArea.setBorder(new TitledBorder(null, "Project Area", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlProjectProps.add(pnlProjectArea);
		pnlProjectArea.setLayout(new BorderLayout(0, 0));
		
		txtSearchProject = new JTextField();
		pnlProjectArea.add(txtSearchProject, BorderLayout.NORTH);
		txtSearchProject.setColumns(10);
		txtSearchProject.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterProjectAreaList(txtSearchProject.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterProjectAreaList(txtSearchProject.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterProjectAreaList(txtSearchProject.getText());
            }
        });
		
		lstProjectArea = new JList<ProjectArea>();
		lstProjectArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrProjectArea = new JScrollPane(lstProjectArea);
		pnlProjectArea.add(scrProjectArea, BorderLayout.CENTER);
		lstProjectArea.addListSelectionListener(e -> {
		    if (!e.getValueIsAdjusting()) {
		    	if(!isPopupShown){
		    		JOptionPane.showMessageDialog(
                            this,
                            "Ensure that the work item query for the selected project area contains only one type of WorkItem Type",
                            "Info", JOptionPane.INFORMATION_MESSAGE
                    );
                    isPopupShown = true;
		    	}
		    	setComponentsEnabled(false, txtSearchProject, txtSearchWorkItemType, btnMap, btnChoose, btnCancel, btnProceed, btnLoginLogout);
		        selectedProjectItems.addAll(lstProjectArea.getSelectedValuesList());
		        lstWorkItemTypeModel.clear();
		        allWorkItemTypes.clear();
		        selectedWorkItemTypeItems.clear();
		        ProjectArea selectedValue = lstProjectArea.getSelectedValue();
		        if(selectedValue != null){
		        	WorkItemQueryWorker workItemQueryWorker = new WorkItemQueryWorker(selectedValue.getProjectAreaObj()){
		        		@Override
		        		protected void done() {
		        			try {
		        				boolean isValid = get();
		        				if (isValid) {
		        					List<WorkItemQuery> workItemQueryList = getLstWorkItemQuery();
		        					for (WorkItemQuery workItemQuery : workItemQueryList) {
		        						lstWorkItemTypeModel.addElement(workItemQuery);
		        						allWorkItemTypes.add(workItemQuery);
		        					}
		        					lstWorkItemType.setModel(lstWorkItemTypeModel);
		        					Notification.addMessage(Status.SUCCESSFUL.toString(), "All queries successfully fetched for Project area [" + selectedValue.getProjectAreaObj().getName() + "]");
		        				} 
		        				else {
		        					Notification.addMessage(Status.INFO.toString(), "No queries found for Project area [" + selectedValue.getProjectAreaObj().getName() + "]");
		        				}
		        				setComponentsEnabled(true, txtSearchProject, txtSearchWorkItemType, btnMap, btnChoose, btnCancel, btnProceed, btnLoginLogout);
		        			} catch (InterruptedException e) {
		        				CustomLogger.logException(e);
		        			} catch (ExecutionException e) {
		        				CustomLogger.logException(e);
		        			}
		        		}
		        	};
		        	workItemQueryWorker.execute();
		        }
		    }
		});
		
		JPanel pnlWorkItemTypes = new JPanel();
		pnlWorkItemTypes.setBackground(Color.WHITE);
		pnlWorkItemTypes.setBorder(new TitledBorder(null, "WorkItem Query", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlProjectProps.add(pnlWorkItemTypes);
		pnlWorkItemTypes.setLayout(new BorderLayout(0, 0));
		
		txtSearchWorkItemType = new JTextField();
		pnlWorkItemTypes.add(txtSearchWorkItemType, BorderLayout.NORTH);
		txtSearchWorkItemType.setColumns(10);
		txtSearchWorkItemType.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterWorkItemTypeList(txtSearchWorkItemType.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	filterWorkItemTypeList(txtSearchWorkItemType.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            	filterWorkItemTypeList(txtSearchWorkItemType.getText());
            }
        });
		
		lstWorkItemType = new JList<WorkItemQuery>();
		lstWorkItemType.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrWorkItemType = new JScrollPane(lstWorkItemType);
		pnlWorkItemTypes.add(scrWorkItemType, BorderLayout.CENTER);
		lstWorkItemType.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				selectedWorkItemTypeItems.addAll(lstWorkItemType.getSelectedValuesList());
			}
		});
		
		btnMap = new JButton("Map");
		btnMap.setBackground(setButtonBackgroundColor());
		btnMap.setFont(setButtonFontToBold(btnMap));
		pnlWorkItemTypes.add(btnMap, BorderLayout.SOUTH);
		btnMap.addActionListener(btnMap_Click());
		
		JPanel pnlMappings = new JPanel();
		pnlMappings.setBackground(Color.WHITE);
		pnlMappings.setBorder(new TitledBorder(null, "Mappings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlMain.add(pnlMappings, BorderLayout.SOUTH);
		pnlMappings.setLayout(new BorderLayout(0, 0));
		
		WorkItemSyncMappingTable workItemSyncMappingTable = new WorkItemSyncMappingTable();
		JScrollPane scrMappings = new JScrollPane(workItemSyncMappingTable);
		scrMappings.setPreferredSize(new Dimension(600, 200));
		pnlMappings.add(scrMappings, BorderLayout.CENTER);
		
		JPanel pnlButtonContainer = new JPanel();
		pnlButtonContainer.setBackground(Color.WHITE);
		pnlMappings.add(pnlButtonContainer, BorderLayout.SOUTH);
		pnlButtonContainer.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlButtons = new JPanel();
		pnlButtons.setBackground(Color.WHITE);
		pnlButtonContainer.add(pnlButtons, BorderLayout.EAST);
		pnlButtons.setLayout(new BoxLayout(pnlButtons, BoxLayout.X_AXIS));
		
		btnCancel = new JButton("Cancel");
		btnCancel.setBackground(setButtonBackgroundColor());
		btnCancel.setFont(setButtonFontToBold(btnCancel));
		pnlButtons.add(btnCancel);
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(10);
		pnlButtons.add(horizontalStrut_5);
		
		btnProceed = new JButton("Proceed");
		btnProceed.setBackground(setButtonBackgroundColor());
		btnProceed.setFont(setButtonFontToBold(btnProceed));
		pnlButtons.add(btnProceed);
		btnProceed.addActionListener(btnProcess_Click());
		
		JPanel pnlExportLocation = new JPanel();
		pnlExportLocation.setBackground(Color.WHITE);
		pnlButtonContainer.add(pnlExportLocation, BorderLayout.CENTER);
		pnlButtonContainer.setBorder(new TitledBorder(null, "Output Location", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlExportLocation.setLayout(new BoxLayout(pnlExportLocation, BoxLayout.X_AXIS));
		
		txtExportLocation = new JTextField();
		pnlExportLocation.add(txtExportLocation);
		txtExportLocation.setEditable(false);
		txtExportLocation.setColumns(10);
		
		btnChoose = new JButton("Choose");
		btnChoose.setBackground(setButtonBackgroundColor());
		btnChoose.setFont(setButtonFontToBold(btnChoose));
		pnlExportLocation.add(btnChoose);
		btnChoose.addActionListener(btnChoose_Click());
		
		Component horizontalStrut_7 = Box.createHorizontalStrut(10);
		pnlExportLocation.add(horizontalStrut_7);
		
		JPanel pnlNotification = new JPanel();
		pnlNotification.setBackground(Color.WHITE);
		pnlNotification.setBorder(new TitledBorder(null, "Notifications", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(pnlNotification, BorderLayout.SOUTH);
		pnlNotification.setLayout(new GridLayout(1, 0, 0, 0));
		
		Notification notification = new Notification();
		JScrollPane scrNotifications = new JScrollPane(notification);
		scrNotifications.setPreferredSize(new Dimension(600, 180));
		pnlNotification.add(scrNotifications);
		
		setComponentsEnabled(false, btnMap, btnCancel, btnProceed, txtSearchProject, txtSearchWorkItemType, btnChoose);
	}

	private ActionListener btnChoose_Click() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileName = "WorkItemSync_" + CommonUtils.getLatestDateTime();
				CommonUtils.selectFolderUsingNativeDialog(txtExportLocation, fileName);
			}
		};
	}

	private ActionListener btnProcess_Click() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String outputFileLocation = txtExportLocation.getText();
				if(outputFileLocation.length() == 0){
					Notification.addMessage(Status.ERROR.toString(), "Please select output file path to generate report!");
					return;
				}
				if(WorkItemSyncMappingTable.getDefaultTableModel().getRowCount() == 0){
					Notification.addMessage(Status.ERROR.toString(), "Please add Project areas and Queries in Mapping to proceed!");
					return;
				}
				WorkItemSyncWorker workItemSyncWorker = new WorkItemSyncWorker(outputFileLocation, WorkItemSyncMappingTable.getDefaultTableModel()){
					@Override
					protected void done(){
						try {
							boolean isValid = get();
							if (isValid) {
								WorkItemSyncMappingTable.getDefaultTableModel().setRowCount(0);
								Notification.addMessage(Status.SUCCESSFUL.toString(), "Workitem Sync complete!");
							} else {
								Notification.addMessage(Status.ERROR.toString(), "Workitem Sync Failed! Please check logs.");
							}
						} catch (InterruptedException e) {
							CustomLogger.logException(e);
						} catch (ExecutionException e) {
							CustomLogger.logException(e);
						}
					}
				};
				workItemSyncWorker.execute();
			}
		};
	}

	private ActionListener btnMap_Click() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ProjectArea selectedProjectArea = lstProjectArea.getSelectedValue();
				List<WorkItemQuery> selectedWorkItemTypeList = lstWorkItemType.getSelectedValuesList();
				
				if(selectedProjectArea == null){
					Notification.addMessage(Status.ERROR.toString(), "Please select Project Area!");
					return;
				}
				
				if(selectedWorkItemTypeList == null || selectedWorkItemTypeList.size() == 0){
					Notification.addMessage(Status.ERROR.toString(), "Please select WorkItem Query!");
					return;
				}
				
				setComponentsEnabled(false, btnLoginLogout, btnMap, btnChoose, btnCancel, btnProceed);
				ValidateWorkItemQueryWorker validateWorkItemQueryWorker = new ValidateWorkItemQueryWorker(selectedProjectArea, selectedWorkItemTypeList){

					@Override
					protected void done() {
						try {
							boolean isValid = get();
							if (isValid) {
//								lstProjectArea.clearSelection();
								lstWorkItemType.clearSelection();
								setComponentsEnabled(true, btnLoginLogout, btnMap, btnChoose, btnCancel, btnProceed);
							}
						} catch (InterruptedException e) {
							CustomLogger.logException(e);
						} catch (ExecutionException e) {
							CustomLogger.logException(e);
						}
					}
					
				};
				validateWorkItemQueryWorker.execute();
			}
		};
	}

	private ActionListener btnLogin_Click() {
		return new ActionListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnLoginLogout.getText().equals(UtilityConstants.LOGIN)){
					CustomLogger.logMessage("Button Login Clicked");
					Server selectedServer = (Server) cmbServer.getSelectedItem();
					String username = txtUsername.getText();
					String password = txtPassword.getText();
					if(username.isEmpty() || password.isEmpty()){
						Notification.addMessage(Status.ERROR.toString(), "Please enter username and password!");
						return;
					}
					setComponentsEnabled(false, cmbServer, txtUsername, txtPassword, btnLoginLogout);
					LoginWorker loginWorker = new LoginWorker(selectedServer, username, password) {
						@Override
						protected void done() {
							try {
								boolean isValid = get();
								if (isValid) {
									
									Notification.addMessage(Status.SUCCESSFUL.toString(),
											"Project area fetched successfully. Please select the project area to proceed.");
									
									List<ProjectArea> getAllProjectArea = getFetchAllProjectAreas();
									for (ProjectArea projectArea : getAllProjectArea) {
										lstProjectAreaModel.addElement(projectArea);
										allProjectAreas.add(projectArea);
									}
									lstProjectArea.setModel(lstProjectAreaModel);
									btnLoginLogout.setText(UtilityConstants.LOGOUT);
									
									setComponentsEnabled(true, btnMap, btnCancel, btnProceed, txtSearchProject, txtSearchWorkItemType, btnLoginLogout, btnChoose);
								} 
							} catch (InterruptedException e) {
								CustomLogger.logException(e);
							} catch (ExecutionException e) {
								CustomLogger.logException(e);
							}
						}
					};
					loginWorker.execute();
				} else if (btnLoginLogout.getText().equals(UtilityConstants.LOGOUT)){
					CustomLogger.logMessage("Button Logout Clicked");
					btnLoginLogout.setText(UtilityConstants.LOGIN);
					
					isPopupShown = false;
					lstProjectAreaModel.clear();
					allProjectAreas.clear();
					lstWorkItemTypeModel.clear();
					allWorkItemTypes.clear();
					WorkItemSyncMappingTable.getDefaultTableModel().setRowCount(0);
					
					LoginController loginController = new LoginController();
					loginController.logout();
					txtUsername.setText(UtilityConstants.USERNAME);
					txtPassword.setText(UtilityConstants.PASSWORD);
					txtPassword.setEchoChar((char)0);
					
					setComponentsEnabled(false, btnMap, btnCancel, btnProceed, txtSearchProject, txtSearchWorkItemType, btnChoose);
					setComponentsEnabled(true, cmbServer, txtUsername, txtPassword, btnLoginLogout);
				    System.gc();
				}
			}
		};
	}
	
	private void filterProjectAreaList(String filterText) {
        List<ProjectArea> filteredList;
        
        if (filterText.isEmpty()) {
            filteredList = allProjectAreas;
        } else {
            filteredList = allProjectAreas.stream()
                .filter(pa -> pa.getProjectAreaName().toLowerCase().contains(filterText.toLowerCase()))
                .collect(Collectors.toList());
        }

        lstProjectAreaModel.clear();
        filteredList.forEach(lstProjectAreaModel::addElement);
        
        List<Integer> indicesToSelect = new ArrayList<>();
        for (int i = 0; i < filteredList.size(); i++) {
            if (selectedProjectItems.contains(filteredList.get(i))) {
                indicesToSelect.add(i);
            }
        }
        lstProjectArea.setSelectedIndices(indicesToSelect.stream().mapToInt(i -> i).toArray());
    }
	
	private void filterWorkItemTypeList(String filterText) {
        List<WorkItemQuery> filteredList;
        
        if (filterText.isEmpty()) {
            filteredList = allWorkItemTypes;
        } else {
            filteredList = allWorkItemTypes.stream()
                .filter(pa -> pa.getQueryName().toLowerCase().contains(filterText.toLowerCase()))
                .collect(Collectors.toList());
        }

        lstWorkItemTypeModel.clear();
        filteredList.forEach(lstWorkItemTypeModel::addElement);
        
        List<Integer> indicesToSelect = new ArrayList<>();
        for (int i = 0; i < filteredList.size(); i++) {
            if (selectedWorkItemTypeItems.contains(filteredList.get(i))) {
                indicesToSelect.add(i);
            }
        }
        lstWorkItemType.setSelectedIndices(indicesToSelect.stream().mapToInt(i -> i).toArray());
    }
	
	private void showHelpContent() {
		String filePath = "help.txt";
		String helpContent = readHelpContent(filePath);

		JFrame helpFrame = new JFrame("Help");
		helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		helpFrame.setSize(600, 600);

		JTextArea textArea = new JTextArea();
		textArea.setText(helpContent);
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		helpFrame.add(scrollPane, BorderLayout.CENTER);

		helpFrame.setVisible(true);
    }
	
	private String readHelpContent(String filePath) {
	    StringBuilder content = new StringBuilder();
	    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            content.append(line).append("\n");
	        }
	    } catch (IOException e) {
	    	CustomLogger.logException(e);
	    }
	    return content.toString();
	}

}
