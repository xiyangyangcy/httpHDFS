package org.troy.di.httpFS.test;

import org.troy.di.httpFS.httpClient.HttpFSClient;
import org.troy.di.httpFS.httpClient.HttpFSUtils;
import org.troy.di.httpFS.utils.HttpFSConf;

public class Demo {

	public static void main(String[] args) {
		HttpFSClient client = new HttpFSClient();
		String user_name = HttpFSConf.getUSERNAME();
		client.initCookie();

		// ��ȡ��ǰ�û���Ŀ¼
		client.get("", "op=gethomedirectory&user.name="+user_name);
		// ����Ŀ¼
//		client.put("/user/john/test", "op=MKDIRS&user.name="+user_name);

		// �ϴ��ļ�
		String testPath = "/user/HDFS/test.txt";
		String testParams = "op=CREATE&buffersize=10240&overwrite=true&permission=777&user.name=";
		client.put(testPath, testParams+user_name);
		client.uploadUrl(testPath, testParams+user_name+"&"+HttpFSUtils.DEFAULT_HDFS);
//		client.upload(testPath, testParams+user_name+"&"+HttpFSUtils.DEFAULT_HDFS, "xz-1.0.jar");
		client.uploadFile(testPath, testParams+user_name+"&"+HttpFSUtils.DEFAULT_HDFS, "F:\\test\\test.txt");

		//		
//		 // ɾ���ļ�
//		 client.delete("/test2/demo.xml", "op=DELETE&user.name="+user_name);
	
//		 // ��ȡ�ļ�
//		String testFile = "/user/HDFS/test.kjb";
//		String testParams2 = "op=OPEN&buffersize=10240&data=true&user.name=";
//		client.get(testFile, testParams2+user_name);
/*
		// ��ȡ�ļ��б���Ϣ
		String result = client.get("/user/HDFS", "op=LISTSTATUS&user.name="+user_name);
		System.out.println(result);
		// ��������Ϣ
		HttpFSUtils.parseResult(result);
*/
	}

}
