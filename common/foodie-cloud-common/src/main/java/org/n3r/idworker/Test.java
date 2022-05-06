package org.n3r.idworker;

import java.util.UUID;

public class Test {

	public static void main(String[] args) {
		Sid sid = new Sid();
		for (int i = 0 ; i < 1000 ; i ++) {
			System.out.println(sid.next());
			System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
		}
	}

}
