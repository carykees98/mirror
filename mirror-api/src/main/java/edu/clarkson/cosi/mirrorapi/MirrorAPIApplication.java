package edu.clarkson.cosi.mirrorapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class
 */
@SpringBootApplication
public class MirrorAPIApplication {

	/**
	 * Starts MirrorAPI.
	 * @param args Unused as of now
	 */
	public static void main(String[] args) {
		SpringApplication.run(MirrorAPIApplication.class, args);
	}

}
