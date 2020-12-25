package com.lukflug.hartree;

import javax.swing.JOptionPane;

// lukflug's Hartree Equation Solver 22.12.2020-24.12.2020
public class Main {
	public static void main (String args[]) {
		Atom atom=new Atom(.005,52,24,24);
		atom.addElectron(200,1,0);
		atom.addElectron(200,1,0);
		atom.addElectron(400,2,0);
		atom.addElectron(400,2,0);
		atom.addElectron(600,2,1);
		atom.addElectron(600,2,1);
		atom.addElectron(600,2,1);
		atom.addElectron(600,2,1);
		atom.addElectron(600,2,1);
		atom.addElectron(600,2,1);
		atom.addElectron(1200,3,0);
		atom.addElectron(1200,3,0);
		atom.addElectron(1600,3,1);
		atom.addElectron(1600,3,1);
		atom.addElectron(1600,3,1);
		atom.addElectron(1600,3,1);
		atom.addElectron(1600,3,1);
		atom.addElectron(1600,3,1);
		atom.addElectron(5600,4,0);
		atom.addElectron(3200,3,2);
		atom.addElectron(3200,3,2);
		atom.addElectron(3200,3,2);
		atom.addElectron(3200,3,2);
		atom.addElectron(3200,3,2);
		Screen screen=new Screen("Hartree Solver",atom);
		while (true) {
			String s=atom.updateElectrons(.1);
			screen.repaint();
			if (s!=null) {
				JOptionPane.showMessageDialog(screen,s,"Error!",JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
	}
}
