package com.probizbuddy;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Main area for managers to control Pro Biz Buddy. */
public class ManagerPanel {
	
	/** Window. */
	private JFrame window;
	
	/** Panel holding manager tools. */
	private JPanel layout, nav, dash;
	
	/** Optional panels to view. */
	private JPanel overviewPanel, payrollPanel, addEmployeePanel, currentWorkersPanel, editWorkersPanel;
	
	/** Navigation buttons. */
	private JButton overview, payroll, currentWorkers, addEmployee, editEmployees, logout;
	
	/** Strings storing the manager's info. */
	private String mID, name;
	
	/** Manager list from db. */
	private List<String> manager;
	
	/** Format of currency. */
	private NumberFormat currency;
	
	/** Totals the payroll data. */
	private Object[] totalRowData;
	
	/** Style of all buttons. */
	private Font buttonStyle = new Font("Arial", Font.PLAIN, 16);
	
	/** logged in as manager. 
	 * @throws FileNotFoundException 
	 * @param pWindow : window
	 * @param pName : manager's name */
	ManagerPanel(final JFrame pWindow, final String pName) throws FileNotFoundException {
		window = pWindow;
		layout = new JPanel();
		nav = new JPanel();
		dash = new JPanel();
		
		name = pName;
		
		ValidateAccess v = new ValidateAccess(window);
		manager = v.getUserData("ManagersDB.txt", name);
		//mID = manager.get(0);
		
		// set up all components
		addEmployee();
		payroll();
		currentWorkers();
		editWorkers();
		
		setDashComponent(overview());
	}
	
	
	/** Show the panel on the GUI. */
	public void showPanel() {
		System.out.println("Logged in, show the manager's panel.");
		
		layout.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		 displayNav();
		 displayDashboard();

		window.getContentPane().add(layout);
	}
	
	/**  Sets the component to show in the dash.
	 * @param newPanel : panel to replace in */
	public void setDashComponent(final JPanel newPanel) {
		newPanel.setVisible(true);
		newPanel.setEnabled(true);
		newPanel.setBackground(new Color(66, 153, 229));
		dash.add(newPanel);
	}
	
	/** Shows dashboard components on the GUI. 
	 * The content inside here will be replaced with 
	 * whatever is navigated to. */
	public void displayDashboard() {
		dash.setBackground(new Color(66, 153, 229)); // blue
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = .7;
		constraints.weighty = 1.0;
		dash.setPreferredSize(new Dimension(window.getWidth() - 325, window.getHeight()));
		layout.add(dash, constraints);
	}
	
	
	/** Shows navigation components on the GUI. */
	public void displayNav() {
		nav.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1;
		
		JLabel navigationLabel = new JLabel();
		navigationLabel.setText("Navigation");
		navigationLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
		
		c.gridx = 0;
		c.gridy = 0;
		nav.add(navigationLabel, c);
		
		// default view for the dashboard
		overview = new JButton("Overview");
		overview.setFont(buttonStyle);
		overview.setEnabled(false);
		
		c.gridx = 0;
		c.gridy++;
		c.ipadx = 10;
		c.ipady = 5;
		nav.add(overview, c);
		

		
		
		payroll = new JButton("Pay Wages");
		payroll.setFont(buttonStyle);
		c.gridy++;
		nav.add(payroll, c);
		

		
		
		currentWorkers = new JButton("View Current Workers");
		currentWorkers.setFont(buttonStyle);
		c.gridy++;
		nav.add(currentWorkers, c);


		
		
		addEmployee = new JButton("Add Employee");
		addEmployee.setFont(buttonStyle);
		c.gridy++;
		nav.add(addEmployee, c);
		
		
		editEmployees = new JButton("Edit Employees");
		editEmployees.setFont(buttonStyle);
		c.gridy++;
		nav.add(editEmployees, c);
		
		
		overview.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
        		setDashComponent(overview());
            	
        		overview.setEnabled(false); overviewPanel.setVisible(true);
            	payroll.setEnabled(true); payrollPanel.setVisible(false); payrollPanel.removeAll();
        		currentWorkers.setEnabled(true); currentWorkersPanel.setVisible(false); currentWorkersPanel.removeAll();
        		addEmployee.setEnabled(true); addEmployeePanel.setVisible(false); addEmployeePanel.removeAll();
        		editEmployees.setEnabled(true); editWorkersPanel.setVisible(false); editWorkersPanel.removeAll();
            }
        });
		
		payroll.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
        		try {
					setDashComponent(payroll());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
            	
       			overview.setEnabled(true); overviewPanel.setVisible(false); overviewPanel.removeAll();
        		payroll.setEnabled(false); payrollPanel.setVisible(true);
        		currentWorkers.setEnabled(true); currentWorkersPanel.setVisible(false); currentWorkersPanel.removeAll();
        		addEmployee.setEnabled(true); addEmployeePanel.setVisible(false); addEmployeePanel.removeAll();
        		editEmployees.setEnabled(true); editWorkersPanel.setVisible(false); editWorkersPanel.removeAll();
            }
        });
		
		currentWorkers.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	try {
					setDashComponent(currentWorkers());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
            	
            	overview.setEnabled(true); overviewPanel.setVisible(false); overviewPanel.removeAll();
            	payroll.setEnabled(true); payrollPanel.setVisible(false); payrollPanel.removeAll();
        		currentWorkers.setEnabled(false); currentWorkersPanel.setVisible(true);
        		addEmployee.setEnabled(true); addEmployeePanel.setVisible(false); addEmployeePanel.removeAll();
        		editEmployees.setEnabled(true); editWorkersPanel.setVisible(false); editWorkersPanel.removeAll();
            }
        });
		
		addEmployee.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	setDashComponent(addEmployee());
            	
            	overview.setEnabled(true); overviewPanel.setVisible(false); overviewPanel.removeAll();
            	payroll.setEnabled(true); payrollPanel.setVisible(false); payrollPanel.removeAll();
        		currentWorkers.setEnabled(true); currentWorkersPanel.setVisible(false); currentWorkersPanel.removeAll();
        		addEmployee.setEnabled(false); addEmployeePanel.setVisible(true);
        		editEmployees.setEnabled(true); editWorkersPanel.setVisible(false); editWorkersPanel.removeAll();
            }
        });
		
		editEmployees.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	try {
					setDashComponent(editWorkers());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
            	
            	overview.setEnabled(true); overviewPanel.setVisible(false); overviewPanel.removeAll();
            	payroll.setEnabled(true); payrollPanel.setVisible(false); payrollPanel.removeAll();
        		currentWorkers.setEnabled(true); currentWorkersPanel.setVisible(false); currentWorkersPanel.removeAll();
        		addEmployee.setEnabled(true); addEmployeePanel.setVisible(false); addEmployeePanel.removeAll();
        		editEmployees.setEnabled(false); editWorkersPanel.setVisible(true); 
            }
        });
		
		
		logout = new JButton("Log Out");
		logout.setFont(buttonStyle);
		
		c.gridy++;
		nav.add(logout, c);
		
		logout.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
        		System.out.println("Logging out.");
        		Login l = new Login(window);
        		layout.setVisible(false);
        		layout.setEnabled(false);
        		l.showPanel();
            }
        });
		

		nav.setPreferredSize(new Dimension(325, window.getHeight()));
		nav.setBackground(new Color(49, 128, 159));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.VERTICAL;

		layout.add(nav, constraints);
	}
	
	/** Overview (default) panel.
	 * @return the JPanel to display. */
	private JPanel overview() {
		overviewPanel = new JPanel();
		overviewPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel userLabel = new JLabel();
		userLabel.setText("Welcome, " + name);
		userLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
		overviewPanel.add(userLabel);
		
		return overviewPanel;
	}
	
	/** Payroll panel.
	 * @return the JPanel to display
	 * @throws FileNotFoundException */
	private JPanel payroll() throws FileNotFoundException {
		payrollPanel = new JPanel();
		
		String[] columnNames = {
				"Name",
				"Date",
                "Clocked In",
                "Clocked Out",
                "Total Hours",
                "Hourly Rate",
                "Wages Earned"
                };
		
		// fill a multidimensional array with a loop from the database

		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
		JTable table = new JTable(tableModel);
		File doc = new File("TimeLogDB.txt");
		final Scanner scanner = new Scanner(doc);
		
		//wipe the current table
		int rowCount = tableModel.getRowCount();
		//Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		
		// 00000, 10-4-18, 11:35, 19:23, 8 hours 12 minutes
		// id, date, in, null, null
		
		double totalPayout = 0.0;
		int totalHrs = 0;
		int totalMins = 0;

		while (scanner.hasNextLine()) {
			final String lineFromFile = scanner.nextLine();
			List<String> user = Arrays.asList(lineFromFile.split("\\s*,\\s*"));
			// id, name, password

			// add to an arraylist of their hours
			String data0 = user.get(0);
			String data1 = user.get(1);
		    String data2 = user.get(2);
		    String data3 = user.get(3);
		    String data4 = user.get(4);
		    String data5 = user.get(5);
		    
		    // only show all unpaid, completed time logs
		    if (!data3.equals("null") && data5.equals("false")) {
			    // get that employee's name, wage
			    ValidateAccess v = new ValidateAccess();
			    List<String> thisEmployee = v.getUserData("WorkersDB.txt", data0);
			    String employeeName = thisEmployee.get(1);
			    double employeeRate = Double.parseDouble(thisEmployee.get(3));
			    
			    int hours = 0;
			    int minutes = 0;
			    String[] log = data4.split(" ");
			    
			    if (log.length == 4) {
			    	hours = Integer.parseInt(log[0]);
			    	minutes = Integer.parseInt(log[2]);
			    	System.out.println(log[0] + " " + log[1] + " " + log[2] + " " + log[3]);
			    	
			    	totalHrs += hours;
			    	totalMins += minutes;
			    } else {
			    	minutes = Integer.parseInt(log[0]);
			    	System.out.println(log[0] + " " + log[1]);
			    	totalMins += minutes;
			    }
			    
			    double timeWorked = (double) hours + (minutes / 60.0);
			    
			    currency = NumberFormat.getCurrencyInstance(Locale.US);
		        String pay = currency.format(timeWorked * employeeRate);
		        String hourlyRate = currency.format(employeeRate);
		        
		        totalPayout += (timeWorked * employeeRate);
	
			    Object[] rowData = new Object[] {employeeName, data1, data2, data3, data4, hourlyRate, pay};
			    
				tableModel.addRow(rowData);
		    }
		}

		scanner.close();
		
		String totalHours = "";
		if (totalHrs == 1) {
			totalHours += "1 H ";
		} else {
			totalHours += "" + totalHrs + " Hs ";
		}
		
		if (totalMins == 1) {
			totalHours += "1 M";
		} else {
			totalHours += "" + totalMins + " Ms";
		}
		
		
		if (totalHours != null && totalPayout != 0.00) {
			totalRowData = new Object[] {"TOTALS:", null, null, null, totalHours, null, currency.format(totalPayout)};
			tableModel.addRow(totalRowData);
		}

		
		// hours table header
		GridBagConstraints c = new GridBagConstraints();
	    payrollPanel.setLayout(new GridBagLayout());
	    c.ipady = 5;
	    c.fill = GridBagConstraints.BOTH;
	    c.anchor = GridBagConstraints.CENTER; 
		c.gridx = 0;
		c.gridy = 0;
		payrollPanel.add(table.getTableHeader(), c);
		
		// hours table
		c.gridx = 0;
		c.gridy = 1;
		payrollPanel.add(table, c);
		
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setPreferredSize(new Dimension(window.getWidth() - 325, table.getRowCount() * table.getRowHeight()));
		
		
		JButton setPaid = new JButton("Mark as Paid");
		setPaid.setFont(buttonStyle);
		c.gridy++;
		payrollPanel.add(setPaid, c);
		
		if (totalHours == null || totalPayout == 0.00) {
			setPaid.setVisible(false);
			setPaid.setEnabled(false);
			
			JLabel allPaidLabel = new JLabel();
			allPaidLabel.setText("Payroll is up-to-date.");
			allPaidLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
			c.gridy++;
			payrollPanel.add(allPaidLabel, c);
		}
		
		setPaid.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
        		// replace all the false values with true
            	// update the database
    			File doc = new File("TimeLogDB.txt");
    			Scanner scanner = null;
				try {
					scanner = new Scanner(doc);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
    			
    			String tempData = "";

    			while (scanner.hasNextLine()) {
    				final String lineFromFile = scanner.nextLine();
    				List<String> user = Arrays.asList(lineFromFile.split("\\s*,\\s*"));
    				// id, name, password
    				if (user.get(5).equals("false") && !user.get(3).equals("null")) { 
    				    
    				    String updatedLine = 
    				    		user.get(0) + ", " + user.get(1) + ", " + user.get(2) + ", " + user.get(3) + ", " + user.get(4) + ", true";
    				    
				    	tempData += updatedLine;
				    	tempData += "\n";

    				} else {
    					tempData += lineFromFile;
    					tempData += "\n";
    				}
    			}
    			
    			// replace the file data with the value of tempData
    			System.out.println(tempData);
    			
    			FileWriter workers = null;
				try {
					workers = new FileWriter("TimeLogDB.txt", false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			try {
					workers.write(tempData);
	    			workers.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			
    			scanner.close();
           
    			// refresh the panel
    			payrollPanel.removeAll();
    			try {
					setDashComponent(payroll());
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
    			payrollPanel.revalidate();
    			payrollPanel.repaint();
            }
        });
		
		return payrollPanel;
	}
	
	
	/** Add an employee to the database.
	 * @return the panel */
	private JPanel addEmployee() {
		addEmployeePanel = new JPanel();
		
		addEmployeePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel formLabel = new JLabel();
		formLabel.setText("Have the employee fill out this form:");
		formLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
		c.gridx = 0;
		c.gridy = 0;
		addEmployeePanel.add(formLabel, c);
		
		JLabel nameLabel = new JLabel();
		nameLabel.setText("Name: ");
		c.gridx = 0;
		c.gridy++;
		nameLabel.setFont(new Font("Arial", Font.PLAIN | Font.BOLD, 14));
		addEmployeePanel.add(nameLabel, c);
		
		JTextField empName = new JTextField(15); 
		empName.setFont(new Font("Arial", Font.PLAIN | Font.BOLD, 14));
		c.gridx = 1;
		c.gridy++;
		addEmployeePanel.add(empName, c);
		
		JLabel passwordLabel = new JLabel();
		passwordLabel.setText("Password: ");
		c.gridx = 0;
		c.gridy++;
		passwordLabel.setFont(new Font("Arial", Font.PLAIN | Font.BOLD, 14));
		addEmployeePanel.add(passwordLabel, c);
		
		JPasswordField empPass = new JPasswordField(15); 
		empPass.setFont(new Font("Arial", Font.PLAIN | Font.BOLD, 14));
		c.gridx = 1;
		c.gridy++;
		addEmployeePanel.add(empPass, c);
		
		JButton createEmployee = new JButton("Create New Employee");
		createEmployee.setFont(buttonStyle);
		c.gridx = 1;
		c.gridy++;
		createEmployee.setMargin(new Insets(5, 5, 5, 5));
		addEmployeePanel.add(createEmployee, c);
		
		createEmployee.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	 String password = String.valueOf(empPass.getPassword());
            	
        		AddWorker newEmp = new AddWorker();
        		newEmp.setWorker(empName.getText(), password, 0.00);
            }
        });
		
		return addEmployeePanel;
	}
	
	/** Show a list of the employees working right now.
	 * @return panel to show
	 * @throws FileNotFoundException */
	private JPanel currentWorkers() throws FileNotFoundException {
		currentWorkersPanel = new JPanel();
		ValidateAccess v = new ValidateAccess();
		
		currentWorkersPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		String employeesClockedIn = "";
		
		File doc = new File("TimeLogDB.txt");
		final Scanner scanner = new Scanner(doc);
		
		 if (doc.length() > 0) {
		
			while (scanner.hasNextLine()) {
				final String lineFromFile = scanner.nextLine();
				List<String> user = Arrays.asList(lineFromFile.split("\\s*,\\s*"));
			    
			    // only show all unpaid, completed time logs
			    if (user.get(3).equals("null")) {
				    List<String> thisEmployee = v.getUserData("WorkersDB.txt", user.get(0));
				    String employeeName = thisEmployee.get(1);
				    
			    	employeesClockedIn += employeeName + ", ";
			    }
			}
			
			scanner.close();
			
			JLabel clockedInLabel = new JLabel();
			clockedInLabel.setText("Current Employees Clocked In: " + employeesClockedIn.substring(0, employeesClockedIn.length() - 2));
			clockedInLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
			currentWorkersPanel.add(clockedInLabel);
		 } else {
			 JLabel clockedInLabel = new JLabel();
			 clockedInLabel.setText("There are currently no employees. Please add one.");
			 clockedInLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
			 currentWorkersPanel.add(clockedInLabel);
		 }	
		
		return currentWorkersPanel;
	}
	
	
	/** Edit employee info.
	 * @return panel to show
	 * @throws FileNotFoundException */
	private JPanel editWorkers() throws FileNotFoundException {
		editWorkersPanel = new JPanel();
		ValidateAccess v = new ValidateAccess();
		
		editWorkersPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		String employeesClockedIn = "";
		
		File doc = new File("TimeLogDB.txt");
		final Scanner scanner = new Scanner(doc);
		
		while (scanner.hasNextLine()) {
			final String lineFromFile = scanner.nextLine();
			List<String> user = Arrays.asList(lineFromFile.split("\\s*,\\s*"));
		    
		    // only show all unpaid, completed time logs
		    if (user.get(3).equals("null")) {
			    List<String> thisEmployee = v.getUserData("WorkersDB.txt", user.get(0));
			    
		    }
		}
		
		scanner.close();
		
		JLabel clockedInLabel = new JLabel();
		clockedInLabel.setText("Edit employee info:");
		clockedInLabel.setFont(new Font("Arial Black", Font.BOLD, 16));
		currentWorkersPanel.add(clockedInLabel);
		
		return editWorkersPanel;
	}
}
