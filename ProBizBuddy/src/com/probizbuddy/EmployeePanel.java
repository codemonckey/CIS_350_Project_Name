package com.probizbuddy;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Work space for employees. */
public class EmployeePanel {
	
	/** window. */
	private JFrame window;
	
	/** clock in and clock out buttons. */
	private JButton clockIn, clockOut;
	
	/** panel for displaying hours. */
	private JPanel toolbar = new JPanel();
	
	/** panel for clocking in and out. */
	private JPanel clock = new JPanel();
    
	/** panel for displaying hours. */
	private JPanel hours = new JPanel();
	
	/** panel for displaying hours. */
	private JPanel organizedPanel = new JPanel();
	
	/** Table that displays the user's hours. */
	private JTable table;
    
    /** Creates an expandable data table. */
    private DefaultTableModel tableModel;
    
    /** Creates an employee object. */
    private Worker user;
    
    /** Stores the user id for comparing with a list<string>. */
    private String uID;

	/** 
	 * constructor to set up the window.
	 * @param employee : employee object of person logged in
	 * @param pWindow : the gui
	 * @throws FileNotFoundException file not found 
	 * */
	public EmployeePanel(final JFrame pWindow, final Worker employee) throws FileNotFoundException {
		user = employee;
		window = pWindow;
		
		uID = employee.getID();
		
		System.out.println("Logged in as " + user.getName());
		
		// show their current hours if any exist
	    try {
			createHoursTable();
			fillTable();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    
	}
	
	
	/** Display the panel to the GUI. 
	 * @throws FileNotFoundException  file not found */
	public void showPanel() throws FileNotFoundException {
		
		/** set layout and bg */
		clock.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		Color blue = new Color(66, 153, 229);
		clock.setBackground(blue);

		// clock in button
		clockIn = new JButton("Clock In");
		clockIn.setFont(new Font("Arial Black", Font.BOLD, 20));

		c.ipadx = 50;
		c.ipady = 25;
		
		clock.add(clockIn, c);

		clockIn.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
        		try {
					clockIn();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
            }
        });
		
		// clock out button
		clockOut = new JButton("Clock Out");
		clockOut.setFont(new Font("Arial Black", Font.BOLD, 20));
		
		clock.add(clockOut, c);
		
		clockOut.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
        		try {
					clockOut();
				} catch (ParseException | IOException e1) {
					e1.printStackTrace();
				}
            }
        });
		
		// reset
		c.fill = GridBagConstraints.NONE;
		c.ipady = 0;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 0;
		c.anchor = GridBagConstraints.NONE; 
		c.insets = new Insets(0, 0, 0, 0);
		
		// hours table header
	    hours.setLayout(new GridBagLayout());
	    c.ipady = 5;
	    c.fill = GridBagConstraints.BOTH;
	    c.anchor = GridBagConstraints.CENTER; 
		c.gridx = 0;
		c.gridy = 0;
		hours.add(table.getTableHeader(), c);
		
		// hours table
		c.gridx = 0;
		c.gridy = 1;
		hours.add(table, c);
		
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		
		JButton logout = new JButton("Log Out");
		logout.setFont(new Font("Arial Black", Font.BOLD, 12));
		logout.setMargin(new Insets(5, 5, 5, 5));
		
		logout.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
        		System.out.println("Logging out.");
        		Login l = new Login(window);
        		organizedPanel.setVisible(false);
        		organizedPanel.setEnabled(false);
        		l.showPanel();
            }
        });

		GridBagConstraints cTool = new GridBagConstraints();
		toolbar.setLayout(new GridBagLayout());
		cTool.gridx = 0;
		cTool.gridy = 0;
		cTool.gridwidth = 1;
		cTool.gridheight = 1;
		cTool.weightx = 1.0;
		cTool.weighty = 0.0;
		cTool.anchor = GridBagConstraints.EAST;
		cTool.fill = GridBagConstraints.NONE;
		cTool.insets = new Insets(10, 6, 10, 6);
		cTool.ipadx = 0;
		cTool.ipady = 0;
		
		toolbar.setBackground(new Color(49, 128, 159));
		toolbar.add(logout, cTool);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = .2;
		
		toolbar.setPreferredSize(new Dimension(window.getWidth(), 45));
		organizedPanel.add(toolbar, constraints);
		
		constraints.weightx = .5;
		constraints.weighty = .4;
		constraints.gridy = 1;
		constraints.gridx = 1;
		organizedPanel.add(clock, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		
		organizedPanel.add(hours, constraints);
		
		AnalyzeData pay = new AnalyzeData();
		
		JLabel totalPaidHours = new JLabel("Total Paid Hours: " 
				+ pay.sumLogTotalsList(pay.getCertainTimeLogs(pay.getPaidTimeLogs(pay.getAllTimeLogs()), user)));
		
		totalPaidHours.setFont(new Font("Arial Black", Font.BOLD, 12));
		constraints.gridy = 3;
		constraints.ipady = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER; 
		organizedPanel.add(totalPaidHours, constraints);
		
		/* FIX ME - null pointer

			JLabel totalPaidWages = new JLabel("Total Paid Wages: " 
					+ pay.calculateWages(pay.getCertainTimeLogs(pay.getAllTimeLogs(), user), user));
		
		
			totalPaidWages.setFont(new Font("Arial Black", Font.BOLD, 12));
			constraints.gridy = 4;
			organizedPanel.add(totalPaidWages, constraints);
		
		*/

		organizedPanel.setBackground(blue);
		window.getContentPane().add(organizedPanel);
		
		// determine which button to show the user
		isClockedIn();
	}
	
	
	/** Show the Clock Out button on the GUI. */
	private void showClockInButton() {
		clockOut.setEnabled(false);
		clockOut.setVisible(false);
		
		clockIn.setEnabled(true);
		clockIn.setVisible(true);
	}
	
	
	/** Show the Clock Out button on the GUI. */
	private void showClockOutButton() {
		clockOut.setEnabled(true);
		clockOut.setVisible(true);
		
		clockIn.setEnabled(false);
		clockIn.setVisible(false);
	}
	
	
	/** Check whether they are clocked in.
	 *  @return true if they have previously clocked in without clocking out */
	public boolean isClockedIn() {
		// check the database
		if (tableModel != null && tableModel.getRowCount() > 0) {
			if (tableModel.getValueAt(tableModel.getRowCount() - 1, 2).toString().equals("-")) {
				// show clock out button
				showClockOutButton();
			} else {
				// show clock in button
				showClockInButton();
			}
		} else {
			showClockInButton();
		}
		
		
		return false;
	}
	
	
	/** User still clocks in.
	 *  Makes sure the user has previously clocked out. 
	 * @throws IOException */
	private void clockIn() throws IOException {
		
		// add the time to the database
	    SimpleDateFormat formattedDate = new SimpleDateFormat("MM-dd-yyyy");
	    String simpleDate = formattedDate.format(new Date());
	    
	    SimpleDateFormat formattedTime = new SimpleDateFormat("hh:mm a"); // ad :ss for seconds too
	    String simpleTime = formattedTime.format(new Date());
	    
	    try (FileWriter fw = new FileWriter("TimeLogDB.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
				
		    PrintWriter out = new PrintWriter(bw)) {
				
				// 00000, 10-4-18, 11:35, 19:23, 8 hours 12 minutes, false
				// id, date, in, null, null, false
				out.println(user.getID() + ", " + simpleDate + ", " + simpleTime + ", null, null, false");

			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
	    
	    // refresh after the entry is added
		fillTable();
		isClockedIn();
	}
	
	
	/** User clocks out. 
	 *  Makes sure the user has previous clocked in.
	 * @throws ParseException 
	 * @throws IOException */
	private void clockOut() throws ParseException, IOException {
		// replace that value in the text file
		System.out.println("Replacing null with current time.");
		
		String time1 = tableModel.getValueAt(tableModel.getRowCount() - 1, 1).toString();
		
	    SimpleDateFormat formattedTime = new SimpleDateFormat("hh:mm a"); // ad :ss for seconds too
	    String time2 = formattedTime.format(new Date());
		
		Date date1 = formattedTime.parse(time1);
		Date date2 = formattedTime.parse(time2);
		
		long totalSecs = date2.getTime() - date1.getTime();
		
		totalSecs /= 1000;
		
		long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;

		System.out.println("Hours: " + hours);
		System.out.println("Minutes: " + mins);
		
		String timeDifference = "";
		
		if (hours > 0) {
			if (hours == 1) {
				timeDifference += "1 Hour ";
			} else if (hours > 1) {
				timeDifference += "" + hours + " Hours ";
			}
		}
		
		if (mins > 0) {
			if (mins == 1) {
				timeDifference += "1 Minute";
			} else if (mins > 1) {
				timeDifference += "" + mins + " Minutes";
			}
		}
		

		// update the database
		File doc = new File("TimeLogDB.txt");
		final Scanner scanner = new Scanner(doc, "UTF-8");
		
		String tempData = "";

		while (scanner.hasNextLine()) {
			final String lineFromFile = scanner.nextLine();
			List<String> user = Arrays.asList(lineFromFile.split("\\s*,\\s*"));
			// id, name, password
			
			if (user.get(0).equals(uID) && user.get(3).equals("null")) { 
			    
			    String updatedLine = 
			    		user.get(0) + ", " + user.get(1) + ", " + user.get(2) + ", " + time2 + ", " + timeDifference + ", false";
			    
			    
			    if (!timeDifference.equals("")) {
			    	tempData += updatedLine;
			    	tempData += "\n";
			    }

			} else {
				tempData += lineFromFile;
				tempData += "\n";
			}
		}
		
		// replace the file data with the value of tempData
		System.out.println(tempData);
		
		FileWriter workers = new FileWriter("TimeLogDB.txt", false);
		workers.write(tempData);
		workers.close();
			
		scanner.close();
		
		
		// refresh after the entry is updated
		fillTable();
		isClockedIn();
	}
	
	
	/** Return a vector containing available table data.
	 * @throws FileNotFoundException if time log db does not exist
	 */
	public void fillTable() throws FileNotFoundException {
		
		//wipe the current table
		int rowCount = tableModel.getRowCount();
		//Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		
		// 00000, 10-4-18, 11:35, 19:23, 8 hours 12 minutes
		// id, date, in, null, null
		
		AnalyzeData a = new AnalyzeData();
		
		for (TimeLog timeLog : a.getAllTimeLogs()) {
			if (timeLog.getID().equals(uID) && !timeLog.getPaid()) {
				String data1 = timeLog.getStartDate();
			    String data2 = timeLog.getStartTime();
			    String data3 = replaceNull(timeLog.getEndTime()); // possible null
			    String data4 = replaceNull(timeLog.getTotalTime()); // possible null

			    Object[] rowData = new Object[] {data1, data2, data3, data4};
			    
				tableModel.addRow(rowData);
			}
		}

	}
	
	
	/** Create the hours table.
	 * @throws FileNotFoundException if the DB doesn't exist */
	public void createHoursTable() throws FileNotFoundException {
		
		String[] columnNames = {
				"Date",
                "Clocked In",
                "Clocked Out",
                "Total Hours",
                };
		
		// fill a multidimensional array with a loop from the database

		tableModel = new DefaultTableModel(columnNames, 0);
		
		table = new JTable(tableModel);
		

	}
	
	/** Replace word null with a dash for user readability.
	 *  @return a dash if it is null 
	 *  @param x string to test */
	private String replaceNull(final String x) {
		if (x == null || x.equals("null")) {
			return "-";
		}
		
		return x;
	}
	

}
