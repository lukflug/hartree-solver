package com.lukflug.hartree;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EnergyDialogBox {
	private static Double value=null;
	
	public static Double getEnergy (double start) {
		value=null;
		Lock lock=new ReentrantLock();
		JTextField tf=new JTextField(Double.toString(start));
		tf.setPreferredSize(new Dimension(200,20));
		JButton r=new JButton("Recalc");
		r.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (lock) {
					try {
						value=Double.parseDouble(tf.getText());
						lock.notifyAll();
					} catch (NumberFormatException e1) {
					}
				}
			}
		});
		JButton c=new JButton("Next");
		c.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
		JPanel buttons=new JPanel();
		buttons.add(r);
		buttons.add(c);
		buttons.setLayout(new FlowLayout());
		JPanel panel=new JPanel();
		panel.add(tf);
		panel.add(buttons);
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
		JFrame frame=new JFrame("Energy Selection");
		frame.setContentPane(panel);
		frame.pack();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		synchronized (lock) {
			try {
				lock.wait();
				frame.dispose();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
