package com.lukflug.hartree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Screen extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int WIDTH=800,HEIGHT=600;
	private static final double DR=.02;
	private final Atom atom;

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
	}
	
	@Override
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(0,0,0));
		g.drawLine(0,HEIGHT/2,WIDTH,HEIGHT/2);
		double unit=Math.pow(10,Math.round(Math.log10(WIDTH*DR)-1));
		for (double r=0;r<=WIDTH*DR;r+=unit) {
			g.drawLine((int)(r/DR),HEIGHT/2-5,(int)(r/DR),HEIGHT/2+5);
			int fontWidth=g.getFontMetrics().stringWidth(Double.toString(r));
			g.drawString(Double.toString(r),(int)(r/DR-fontWidth/2),HEIGHT/2-6);
		}
		try {
			if (!atom.lock.tryLock(1000,TimeUnit.MILLISECONDS)) return;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		int y=g.getFontMetrics().getHeight();
		for (Electron e: atom.getElectrons()) {
			g.setColor(new Color(0,0,0));
			g.drawString("E="+e.getEnergy(),0,y);
			y+=g.getFontMetrics().getHeight();
			g.setColor(new Color(0,0,255));
			drawGraph(g,e,100);
		}
		g.setColor(new Color(0,255,0));
		drawGraph(g,atom.getElectronDensity(),10);
		g.setColor(new Color(255,0,0));
		drawGraph(g,atom.getElectronPotential(),100);
		g.setColor(new Color(255,0,255));
		g.drawString("E="+atom.getTotalEnergy(),0,HEIGHT);
		atom.lock.unlock();
	}
	
	private void drawGraph (Graphics g, Field field, double scale) {
		for (int i=0;i<WIDTH-2;i++) {
			g.drawLine(i,(int)(HEIGHT/2-scale*field.getValue(i*DR)),i+1,(int)(HEIGHT/2-scale*field.getValue((i+1)*DR)));
		}
	}
}
