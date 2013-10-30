/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop 
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2000 CSELT S.p.A. 

 GNU Lesser General Public License

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, 
 version 2.1 of the License. 

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 *****************************************************************/

package personFinder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Cong Wang
 */
class PersonalInfoProviderGui extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PersonalInfoPrividerAgent myAgent;

	private JTextField nameField, emailField, genderField, ageField,
			addressField;

	PersonalInfoProviderGui(PersonalInfoPrividerAgent a) {
		super(a.getLocalName());

		myAgent = a;

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(5, 2));

		p.add(new JLabel("Name:"));
		nameField = new JTextField(15);
		p.add(nameField);

		p.add(new JLabel("Email:"));
		emailField = new JTextField(15);
		p.add(emailField);

		p.add(new JLabel("Gender:"));
		genderField = new JTextField(15);
		p.add(genderField);

		p.add(new JLabel("Age(Num only):"));
		ageField = new JTextField(15);
		p.add(ageField);

		p.add(new JLabel("Address:"));
		addressField = new JTextField(15);
		p.add(addressField);

		getContentPane().add(p, BorderLayout.CENTER);

		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String name = nameField.getText().trim();
					String email = emailField.getText().trim();
					String gender = genderField.getText().trim();
					int age = Integer.valueOf(ageField.getText().trim());
					String address = addressField.getText().trim();

					myAgent.updateCatalogue(name, email, gender, age, address);
					nameField.setText("");
					emailField.setText("");
					genderField.setText("");
					ageField.setText("");
					addressField.setText("");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(PersonalInfoProviderGui.this,
							"Invalid values. " + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);

		// Make the agent terminate when the user closes
		// the GUI using the button on the upper right corner
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		});

		setResizable(false);
	}

	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int) screenSize.getWidth() / 2;
		int centerY = (int) screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}
}
