package com.service.rest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.utils.wsutils.StartJetty;


public class Start {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String messageStr="Hello Android!";
		int server_port = 8084;
		DatagramSocket s = new DatagramSocket();
		InetAddress local = InetAddress.getByName("107.20.217.247");
		int msg_length=messageStr.length();
		byte[] message = messageStr.getBytes();
		DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
		s.send(p);
		System.exit(1);
		try {
			StartJetty.run(8083);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
