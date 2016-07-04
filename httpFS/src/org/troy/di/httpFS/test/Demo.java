package org.troy.di.httpFS.test;

import org.troy.di.httpFS.httpClient.HttpFSClient;
import org.troy.di.httpFS.httpClient.HttpFSUtils;
import org.troy.di.httpFS.utils.HttpFSConf;

public class Demo {

	public static void main(String[] args) {
		HttpFSClient client = new HttpFSClient();
		String user_name = HttpFSConf.getUSERNAME();
		client.initCookie();

		// 获取当前用户的目录
		client.get("", "op=gethomedirectory&user.name="+user_name);
		// 创建目录
//		client.put("/user/john/test", "op=MKDIRS&user.name="+user_name);

		// 上传文件
		String testPath = "/user/HDFS/test.txt";
		String testParams = "op=CREATE&buffersize=10240&overwrite=true&permission=777&user.name=";
		client.put(testPath, testParams+user_name);
		client.uploadUrl(testPath, testParams+user_name+"&"+HttpFSUtils.DEFAULT_HDFS);
//		client.upload(testPath, testParams+user_name+"&"+HttpFSUtils.DEFAULT_HDFS, "xz-1.0.jar");
		client.uploadFile(testPath, testParams+user_name+"&"+HttpFSUtils.DEFAULT_HDFS, "F:\\test\\test.txt");

		//		
//		 // 删除文件
//		 client.delete("/test2/demo.xml", "op=DELETE&user.name="+user_name);
	
//		 // 读取文件
//		String testFile = "/user/HDFS/test.kjb";
//		String testParams2 = "op=OPEN&buffersize=10240&data=true&user.name=";
//		client.get(testFile, testParams2+user_name);
/*
		// 获取文件列表信息
		String result = client.get("/user/HDFS", "op=LISTSTATUS&user.name="+user_name);
		System.out.println(result);
		// 处理返回信息
		HttpFSUtils.parseResult(result);
*/
	}

}
