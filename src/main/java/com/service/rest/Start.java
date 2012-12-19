package com.service.rest;

import java.io.IOException;

import com.utils.wsutils.StartJetty;


public class Start {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		try {
			StartJetty.run(8083);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
