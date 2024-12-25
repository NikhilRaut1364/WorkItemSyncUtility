package com.stellantis.team.utility.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.stellantis.team.utility.model.Server;
import com.stellantis.team.utility.utils.CustomLogger;
import com.stellantis.team.utility.utils.ExtractProperties;
import com.stellantis.team.utility.utils.UtilityConstants;

public abstract class BaseApplicationFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public BaseApplicationFrame() {
        super();
        initializeCommonUtilities();
        customInitialize();
    }

    private void initializeCommonUtilities() {
        setUIFont(new FontUIResource(UtilityConstants.FONT_NAME, Font.PLAIN, 18));
        setTitle(UtilityConstants.UTILITY_NAME + " - " + UtilityConstants.UTILITY_VERSION);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(getImage(UtilityConstants.IMAGES_FCA_PNG));
    }

    protected Dimension getDefaultDimension() {
        return new Dimension(UtilityConstants.TEXT_FIELD_WIDTH, UtilityConstants.TEXT_FIELD_HEIGHT);
    }

    protected Font getItalicFont() {
        return new Font(UtilityConstants.FONT_NAME, Font.ITALIC, 18);
    }

    protected void setUIFont(FontUIResource font) {
        @SuppressWarnings("rawtypes")
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }
    
    protected void bindServerComboBox(JComboBox<Server> comboBox) {
		CustomLogger.logMessage("Binding Server combo box");
		String[] parts = ExtractProperties.getServerURLList().split(";");

		for (String part : parts) {
			String[] keyValue = part.split("=");
			if (keyValue.length == 2) {
				comboBox.addItem(new Server(keyValue[0], keyValue[1]));
			}
		}
	}
    
    protected FocusAdapter txtUsernameFocusListener(JTextField txtUsername) {
		return new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if (txtUsername.getText().equals(UtilityConstants.USERNAME)) {
					txtUsername.setText(UtilityConstants.CONSTANT_NO_VALUE);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (txtUsername.getText().equals(UtilityConstants.CONSTANT_NO_VALUE)) {
					txtUsername.setText(UtilityConstants.USERNAME);
				}
			}
			
		};
	}
    
    protected FocusAdapter txtPasswordFocusListener(JPasswordField txtPassword) {
		return new FocusAdapter() {

			@SuppressWarnings("deprecation")
			@Override
			public void focusGained(FocusEvent e) {
				if (txtPassword.getText().equals(UtilityConstants.PASSWORD)) {
					txtPassword.setEchoChar('*');
					txtPassword.setText(UtilityConstants.CONSTANT_NO_VALUE);
				}
			}

			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if (txtPassword.getText().equals(UtilityConstants.CONSTANT_NO_VALUE)) {
					txtPassword.setEchoChar((char)0);
					txtPassword.setText(UtilityConstants.PASSWORD);
				} else {
					txtPassword.setEchoChar('*');
				}
			}
			
		};
	}
    
    protected Font setButtonFontToBold(JButton button){
		return new Font(button.getFont().getName(), Font.BOLD, button.getFont().getSize());
	}
    
    protected Color setButtonBackgroundColor(){
    	return Color.LIGHT_GRAY;
    }

    protected Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(getClass().getResource(path));
    }
    
    protected void setComponentsEnabled(boolean enabled, JComponent... components) {
		for (JComponent component : components) {
			component.setEnabled(enabled);
		}
	}

    // Abstract method to be implemented by subclasses for additional initialization
    protected abstract void customInitialize();
}
