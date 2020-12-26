package com.lukflug.hartree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Label;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Screen extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int WIDTH=800,HEIGHT=600;
	private final Atom atom;
	private double dr=.02,waveScale=100,densityScale=100,potentialScale=100;

	public Screen (String title, Atom atom) {
		this.atom=atom;
		JFrame frame=new JFrame(title);
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		createSettingDialog();
	}
	
	private void createSettingDialog() {
		JSlider drSlider=new JSlider(1,100,20);
		drSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int value=drSlider.getValue();
				synchronized (Screen.this) {
					dr=value/1000.0;
				}
				Screen.this.repaint();
			}
		});
		JSlider waveScaleSlider=new JSlider(100,500,200);
		waveScaleSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int value=waveScaleSlider.getValue();
				synchronized (Screen.this) {
					waveScale=Math.pow(10,value/100.0);
				}
				Screen.this.repaint();
			}
		});
		JSlider densityScaleSlider=new JSlider(100,500,200);
		densityScaleSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int value=densityScaleSlider.getValue();
				synchronized (Screen.this) {
					densityScale=Math.pow(10,value/100.0);
				}
				Screen.this.repaint();
			}
		});
		JSlider potentialScaleSlider=new JSlider(100,500,200);
		potentialScaleSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int value=potentialScaleSlider.getValue();
				synchronized (Screen.this) {
					potentialScale=Math.pow(10,value/100.0);
				}
				Screen.this.repaint();
			}
		});
		JPanel panel=new JPanel();
		panel.add(new Label("Length scale:"));
		panel.add(drSlider);
		panel.add(new Label("Wavefunction scale:"));
		panel.add(waveScaleSlider);
		panel.add(new Label("Density scale:"));
		panel.add(densityScaleSlider);
		panel.add(new Label("Potential scale:"));
		panel.add(potentialScaleSlider);
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JFrame dialog=new JFrame("Display Settings");
		dialog.setMinimumSize(new Dimension(300,0));
		dialog.add(panel);
		dialog.pack();
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
	
	@Override
	public synchronized void paintComponent (Graphics g) {
		super.paintComponent(g);
		Atom atom=this.atom.createCopy();
		g.setColor(new Color(0,0,0));
		g.drawLine(0,HEIGHT/2,WIDTH,HEIGHT/2);
		double unit=Math.pow(10,Math.round(Math.log10(WIDTH*dr)-1));
		DecimalFormat df = new DecimalFormat("#.#");
		for (double r=0;r<=WIDTH*dr;r+=unit) {
			g.drawLine((int)(r/dr),HEIGHT/2-5,(int)(r/dr),HEIGHT/2+5);
			int fontWidth=g.getFontMetrics().stringWidth(df.format(r));
			g.drawString(df.format(r),(int)(r/dr-fontWidth/2),HEIGHT/2-6);
		}
		int y=g.getFontMetrics().getHeight();
		for (Electron e: atom.getElectrons()) {
			g.setColor(new Color(0,0,0));
			g.drawString("E="+e.getEnergy(),0,y);
			y+=g.getFontMetrics().getHeight();
			g.setColor(new Color(0,0,255));
			drawGraph(g,e,waveScale);
		}
		g.setColor(new Color(0,255,0));
		drawGraph(g,atom.getElectronDensity(),densityScale);
		g.setColor(new Color(255,0,0));
		drawGraph(g,atom.getElectronPotential(),potentialScale);
		g.setColor(new Color(255,0,255));
		g.drawString("<E>="+atom.getTotalEnergy(),0,HEIGHT);
	}
	
	private void drawGraph (Graphics g, Field field, double scale) {
		for (int i=0;i<WIDTH-2;i++) {
			g.drawLine(i,(int)(HEIGHT/2-scale*field.getValue(i*dr)),i+1,(int)(HEIGHT/2-scale*field.getValue((i+1)*dr)));
		}
	}
}
