package com.goddownloader.main;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ListHelper {

	public ListHelper() {
	}

	public void addToListFromDataInputStream(ArrayList<String> list, DataInputStream dis) throws IOException {
		while (dis.available() > 0) {
			list.add(dis.readUTF());
		}

	}

}
