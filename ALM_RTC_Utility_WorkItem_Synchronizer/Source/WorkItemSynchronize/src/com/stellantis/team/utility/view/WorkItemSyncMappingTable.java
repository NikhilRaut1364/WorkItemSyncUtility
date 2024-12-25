package com.stellantis.team.utility.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.stellantis.team.utility.model.ProjectArea;
import com.stellantis.team.utility.model.Status;
import com.stellantis.team.utility.model.WorkItemQuery;
import com.stellantis.team.utility.model.WorkItemType;
import com.stellantis.team.utility.utils.CommonUtils;

@SuppressWarnings("serial")
public class WorkItemSyncMappingTable extends JTable{

	private static DefaultTableModel model = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return ProjectArea.class;
                case 1:
                    return WorkItemQuery.class;
//                case 2:
//                    return Boolean.class;
                default:
                    return String.class;
            }
        }
    };
	
	@Override
    public boolean isCellEditable(int row, int column) {
		return column == 2; 
    }
	
	public WorkItemSyncMappingTable() {
		CommonUtils.customizeTableRowHeight(WorkItemSyncMappingTable.this);
		CommonUtils.customizeTableHeaders(getTableHeader());
		CommonUtils.setTableHeaderFontToBold(getTableHeader());
		model.addColumn("Project Area");
		model.addColumn("WorkItem Query");
		model.addColumn("Remove");

		setModel(model);

		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(300); // Project Area column width
		columnModel.getColumn(1).setPreferredWidth(500); // WorkItem Type column width
		columnModel.getColumn(2).setPreferredWidth(20); // Allow States column width
//		columnModel.getColumn(3).setPreferredWidth(20); // Remove column width

		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		// Set custom renderer and editor for the "Remove" button
        getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Set custom renderer and editor for the "Allow States" checkbox
//        getColumnModel().getColumn(2).setCellRenderer(new CheckBoxRenderer());
//        getColumnModel().getColumn(2).setCellEditor(new CheckBoxEditor(new JCheckBox()));
	}
	
	private static boolean rowExists(ProjectArea projectArea, WorkItemQuery workItemQuery) {
        for (int row = 0; row < model.getRowCount(); row++) {
            ProjectArea existingProjectArea = (ProjectArea) model.getValueAt(row, 0);
            WorkItemQuery existingWorkItemType = (WorkItemQuery) model.getValueAt(row, 1);
            if (existingProjectArea.getProjectAreaObj().getItemId().getUuidValue().equals(projectArea.getProjectAreaObj().getItemId().getUuidValue())
            		&& existingWorkItemType.getQueryDescriptor().getId().equals(workItemQuery.getQueryDescriptor().getId())) {
                return true;
            }
        }
        return false;
    }
	
	private static boolean rowExists(ProjectArea projectArea, WorkItemType workItemType) {
        for (int row = 0; row < model.getRowCount(); row++) {
            ProjectArea existingProjectArea = (ProjectArea) model.getValueAt(row, 0);
            WorkItemType existingWorkItemType = (WorkItemType) model.getValueAt(row, 1);
            if (existingProjectArea.getProjectAreaObj().getItemId().getUuidValue().equals(projectArea.getProjectAreaObj().getItemId().getUuidValue())
            		&& existingWorkItemType.getWorkItemType().getIdentifier().equals(workItemType.getWorkItemType().getIdentifier())) {
                return true;
            }
        }
        return false;
    }
	
	// Static function to add a row to the table
    public static void addRow(ProjectArea projectArea, WorkItemType workItemType) {
    	if(!rowExists(projectArea, workItemType))
    		model.addRow(new Object[]{projectArea, workItemType, false, "Remove"});
    }
    
    public static void addRow(ProjectArea projectArea, WorkItemQuery workItemQuery) {
    	if(!rowExists(projectArea, workItemQuery))
    		model.addRow(new Object[]{projectArea, workItemQuery, "Remove"});
    	else
    		Notification.addMessage(Status.ERROR.toString(), "WorkItem type in selected query ["+ workItemQuery.getQueryDescriptor().getName() +"] already exist in Mapping table!");
    }
    
	// Function to remove a row from the table
	public static void removeRow(int row) {
		model.removeRow(row);
	}

    public static DefaultTableModel getDefaultTableModel() {
    	return model;
    }
    
    // Custom renderer for the button
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);
            CommonUtils.setButtonFontToBold(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Remove" : value.toString());
            return this;
        }
    }

    // Custom editor for the button
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        @SuppressWarnings("unused")
		private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    int modelRow = convertRowIndexToModel(row);
                    ((DefaultTableModel) getModel()).removeRow(modelRow);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "Remove" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return new String(label);
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    // Custom renderer for the checkbox
    class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setHorizontalAlignment(JCheckBox.CENTER);
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(30, 30));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Boolean) {
                setSelected((Boolean) value);
            }
            return this;
        }
    }

    // Custom editor for the checkbox
    class CheckBoxEditor extends DefaultCellEditor {
        private JCheckBox checkBox;

        public CheckBoxEditor(JCheckBox checkBox) {
            super(checkBox);
            this.checkBox = checkBox;
            this.checkBox.setHorizontalAlignment(JCheckBox.CENTER);
            this.checkBox.setPreferredSize(new Dimension(30, 30));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (value instanceof Boolean) {
                checkBox.setSelected((Boolean) value);
            }
            return checkBox;
        }

        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
        }
    }
}
