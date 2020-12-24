package com.lukflug.hartree;

// lukflug's Hartree Equation Solver 22.12.2020-24.12.2020
public class Main {
	public static void main (String args[]) {
		Atom atom=new Atom(.005,51.940505,24,24);
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
		atom.addElectron(2400,4,0);
		atom.addElectron(2400,3,2);
		atom.addElectron(2400,3,2);
		atom.addElectron(2400,3,2);
		atom.addElectron(2400,3,2);
		atom.addElectron(2400,3,2);
		Screen screen=new Screen("Hartree Solver",atom);
		while (true) {
			synchronized (atom.lock) {
				atom.lock.lock();
				atom.updateElectrons(.01);
				atom.lock.unlock();
			}
			screen.repaint();
		}
	}
}
