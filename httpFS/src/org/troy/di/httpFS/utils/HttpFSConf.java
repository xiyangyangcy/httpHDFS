package org.troy.di.httpFS.utils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpFSConf {
	private final static Log log = LogFactory.getLog(HttpFSConf.class);

	private static String master;
	private static String datanode1;
	private static String datanode2;
	private static String datanode3;
	private static int port1;
	private static int port2;
	private static String USERNAME;

	public static String getMaster() {
		return master;
	}

	public static String getDatanode1() {
		return datanode1;
	}

	public static String getDatanode2() {
		return datanode2;
	}

	public static String getDatanode3() {
		return datanode3;
	}

	public static int getPort1() {
		return port1;
	}

	public static int getPort2() {
		return port2;
	}
	
	public static void setMaster(String master) {
		HttpFSConf.master = master;
	}

	public static void setDatanode1(String datanode1) {
		HttpFSConf.datanode1 = datanode1;
	}

	public static void setDatanode2(String datanode2) {
		HttpFSConf.datanode2 = datanode2;
	}

	public static void setDatanode3(String datanode3) {
		HttpFSConf.datanode3 = datanode3;
	}

	public static void setPort1(int port1) {
		HttpFSConf.port1 = port1;
	}

	public static void setPort2(int port2) {
		HttpFSConf.port2 = port2;
	}
	
	public static String getUSERNAME() {
		return USERNAME;
	}

	public static void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	static {
		Properties prop = new Properties();
		InputStream in;
		try {
			in = HttpFSConf.class.getResourceAsStream("/resource/httpfs.properties");
			prop.load(in);
			in.close();
		} catch (Exception e) {
			log.error("load httpfs.properties fail", e);
		}

		Set keyValue = prop.keySet();
		for (Iterator it = keyValue.iterator(); it.hasNext();) {
			String key = (String) it.next();
			if ("master".equals(key)) {
				HttpFSConf.setMaster(prop.getProperty(key));
			} else if("datanode1".equals(key)) {
				HttpFSConf.setDatanode1(prop.getProperty(key));
			} else if("datanode2".equals(key)) {
				HttpFSConf.setDatanode2(prop.getProperty(key));
			} else if("datanode3".equals(key)) {
				HttpFSConf.setDatanode3(prop.getProperty(key));
			} else if ("port1".equals(key)) {
				HttpFSConf.setPort1(Integer.parseInt(prop.getProperty(key,
						"50070")));
			} else if ("port2".equals(key)) {
				HttpFSConf.setPort2(Integer.parseInt(prop.getProperty(key,
						"50075")));
			}else if ("user.name".equals(key)) {
				HttpFSConf.setUSERNAME(prop.getProperty(key));
			}
		}
	}
	
	public static void main(String[] args) {
		log.info(HttpFSConf.getMaster());
		log.info(HttpFSConf.getDatanode1());
		log.info(HttpFSConf.getDatanode2());
		log.info(HttpFSConf.getDatanode3());
		log.info(HttpFSConf.getPort1());
		log.info(HttpFSConf.getPort2());
		log.info(HttpFSConf.getUSERNAME());
	}

}
