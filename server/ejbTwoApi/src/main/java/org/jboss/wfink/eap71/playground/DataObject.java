package org.jboss.wfink.eap71.playground;

import java.io.Serializable;

public class DataObject implements Serializable {
	private static final long serialVersionUID = 1L;
	public String aString;
	public int aNumber;

	public DataObject(String testString, int testInt) {
		aString = testString;
		aNumber = testInt;
	}

	@Override
	public String toString() {
		return "DataObject [aString=" + aString + ", aNumber=" + aNumber + "] reference=" + super.toString();
	}

}
