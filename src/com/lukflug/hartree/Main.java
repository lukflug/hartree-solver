package com.lukflug.hartree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// lukflug's Hartree Equation Solver 22.12.2020-24.12.2020
public class Main {
	private static double numberStep;
	private static Atom atom;
	
	public static void main (String args[]) {
		Lock lock=new ReentrantLock();
		JTextField initAtomNumber=new JTextField("24.0");
		JTextField atomNumber=new JTextField("24");
		JTextField atomStep=new JTextField("0.01");
		JTextField atomWeight=new JTextField("1.008");
		JTextField radialStep=new JTextField("0.005");
		JTextField fFactor=new JTextField("1.0");
		JTextField tolerance=new JTextField("0.0001");
		JTextField attempts=new JTextField("1000");
		String data[][]={{"200","1","0"},{"200","1","0"},
				{"400","2","0"},{"400","2","0"},
				{"600","2","1"},{"600","2","1"},
				{"600","2","1"},{"600","2","1"},
				{"600","2","1"},{"600","2","1"},
				{"1200","3","0"},{"1200","3","0"},
				{"1600","3","1"},{"1600","3","1"},
				{"1600","3","1"},{"1600","3","1"},
				{"1600","3","1"},{"1600","3","1"},
				{"5600","4","0"},{"2800","3","2"},
				{"2800","3","2"},{"2800","3","2"},
				{"2800","3","2"},{"2800","3","2"}};
		JTable electronTable=new JTable(new DefaultTableModel(data,new String[]{"Range","n","l"}));
		JButton button=new JButton("Start");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Electron.f=Double.parseDouble(fFactor.getText());
					Electron.tol=Double.parseDouble(tolerance.getText());
					Electron.maxAtt=Integer.parseInt(attempts.getText());
					numberStep=Double.parseDouble(atomStep.getText());
					atom=new Atom(Double.parseDouble(radialStep.getText()),Double.parseDouble(atomWeight.getText()),Integer.parseInt(atomNumber.getText()),Double.parseDouble(initAtomNumber.getText()));
					DefaultTableModel model=(DefaultTableModel)(electronTable.getModel());
					for (int i=0;i<model.getRowCount();i++) {
						atom.addElectron(Integer.parseInt((String)model.getValueAt(i,0)),Integer.parseInt((String)model.getValueAt(i,1)),Integer.parseInt((String)model.getValueAt(i,2)));
					}
					synchronized (lock) {
						lock.notifyAll();
					}
				} catch (NumberFormatException e1) {
				}
			}
		});
		JButton addRow=new JButton("Add");
		addRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model=(DefaultTableModel)(electronTable.getModel());
				model.addRow(new String[]{"","",""});
			}
		});
		JButton removeRow=new JButton("Remove");
		removeRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model=(DefaultTableModel)(electronTable.getModel());
				model.removeRow(model.getRowCount()-1);
			}
		});
		JPanel tableControl=new JPanel();
		tableControl.add(addRow);
		tableControl.add(removeRow);
		tableControl.setLayout(new FlowLayout());
		JPanel panel=new JPanel();
		panel.add(new Label("Initial Atomic Number:"));
		panel.add(initAtomNumber);
		panel.add(new Label("Final Atomic Number:"));
		panel.add(atomNumber);
		panel.add(new Label("Atomic Number Step:"));
		panel.add(atomStep);
		panel.add(new Label("Atomic Weight:"));
		panel.add(atomWeight);
		panel.add(new Label("Radial Step:"));
		panel.add(radialStep);
		panel.add(new Label("Damping Factor:"));
		panel.add(fFactor);
		panel.add(new Label("Wave Function Tolerance:"));
		panel.add(tolerance);
		panel.add(new Label("Electron Energy Attempts:"));
		panel.add(attempts);
		panel.add(button);
		panel.add(new Label("Electronic Configuration:"));
		JScrollPane sp=new JScrollPane(electronTable);
		sp.setPreferredSize(new Dimension(0,200));
		panel.add(sp);
		panel.add(tableControl);
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		JFrame dialog=new JFrame("Simulation Options");
		dialog.setMinimumSize(new Dimension(300,0));
		dialog.add(panel);
		dialog.pack();
		dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		try {
			synchronized (lock) {
				lock.wait();
			}
			dialog.dispose();
			beginSimulation();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void beginSimulation() {
		Screen screen=new Screen("Hartree Solver",atom);
		while (true) {
			String s=atom.updateElectrons(numberStep);
			screen.repaint();
			if (s!=null) {
				JOptionPane.showMessageDialog(screen,s,"Error!",JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
	}
}
