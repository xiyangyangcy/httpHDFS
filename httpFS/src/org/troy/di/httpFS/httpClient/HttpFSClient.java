package org.troy.di.httpFS.httpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.troy.di.httpFS.utils.HttpFSConf;

public class HttpFSClient {
	private final static Log log = LogFactory.getLog(HttpFSClient.class);
	private final static String CHARSET = "UTF8";

	private static String user_name = HttpFSConf.getUSERNAME();
	public static String redirectUrl;
	public static String uploadUrl;
	private Cookie[] cookies;
	private boolean isInitCookie = false;
	private boolean isRedirect = false;

	public void initCookie() {
		String url = HttpFSUtils.createURL("",
				"op=gethomedirectory&user.name="+user_name);
		System.out.println(url);
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			client.executeMethod(method);
			getCookie(client);
		} catch (Exception e) {
			log.error("init cookie value fail", e);
		}
		this.isInitCookie = true;
		method.releaseConnection();
	}

	public void uploadUrl(String path, String params) {
		String uploadurl = HttpFSUtils.redirectURL(path, params);
		System.out.println(uploadurl);
	}
	
	public String get(String path, String params) {
		return this.request(path, params, GetMethod.class);
	}

	public String get(String path, String params, boolean isGenFile) {
		return this.request(path, params, GetMethod.class, isGenFile, null);
	}

	public String delete(String path, String params) {
		return this.request(path, params, DeleteMethod.class);
	}

	public String put(String path, String params) {
		return this.request(path, params, PutMethod.class);
	}

	public String post(String path, String params) {
		return this.request(path, params, PostMethod.class);
	}

	public String upload(String path, String params) {
		return this.request2(path, params, PutMethod.class);
	}

	public String uploadFile(String path, String params, String filePath) {
		return this.request3(path, params, filePath);
	}

	private String request(String path, String params, Class clz) {
		return this.request(path, params, clz, false, null);
	}

	private String request(String path, String params, String fileName) {
		return this.request(path, params, null, false, fileName);
	}

	private String request2(String path, String params, Class clz) {
		return this.request2(path, params, clz, false, null);
	}

	private String request3(String path, String params, String filePath) {
		return this.request3(path, params, null, false, filePath);
	}
	
	private String request(String path, String params, Class clz,
			boolean isGenFile, String fileName) {
		if (this.isInitCookie == false) {
			return "please init cookie first";
		}

		HttpClient client = new HttpClient();
		// 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		client.getState().addCookies(cookies);
		StringBuffer sb = new StringBuffer();
		int status = -1;

		HttpMethod method = getMethod(path, params, clz, fileName);
		try {
			status = client.executeMethod(method);
			if (isGenFile == false) {
				if(status == 307) {
					Header[] headers = method.getResponseHeaders();
					sb.append(headers[7]);
					redirectUrl = sb.substring(10);
					System.out.println(redirectUrl);
					String str = redirectUrl.substring(7, 16);
					System.out.println(str);
					if(str.equals("datanode1")){
						uploadUrl = "192.168.1.2";
					}else if(str.equals("datanode2")){
						uploadUrl = "192.168.1.3";
					}else if(str.equals("datanode3")){
						uploadUrl = "192.168.1.4";
					}else{
						uploadUrl = "192.168.1.1";
					}
					System.out.println(uploadUrl);
				}else{
					this.dealResponse(method.getResponseBodyAsStream(), sb);
				}			
			} else {
				this.dealResponse(method.getResponseBodyAsStream(), path);
			}
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		method.releaseConnection();
		log.info(method.getStatusLine() + "," + sb.toString());
		return sb.toString();
	}

	private String request2(String path, String params, Class clz,
			boolean isGenFile, String fileName) {
		if (this.isRedirect != false) {
			return "please redirect url first";
		}

		HttpClient client2 = new HttpClient();
		// 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
		client2.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		client2.getState().addCookies(cookies);
		StringBuffer sb2 = new StringBuffer();
		int status = -1;

		HttpMethod method2 = getMethod2(path, params, clz, fileName);
		try {
			status = client2.executeMethod(method2);
			if (isGenFile != false) {
				this.dealResponse(method2.getResponseBodyAsStream(), path);						
			} else {
				if(status == 201){
					Header[] headers = method2.getResponseHeaders();
					sb2.append(headers[7]);
				}else{
					this.dealResponse(method2.getResponseBodyAsStream(), sb2);
				}
			}
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		method2.releaseConnection();
		log.info(method2.getStatusLine() + "," + sb2.toString());
		return sb2.toString();
	}

	private String request3(String path, String params, Class clz,
			boolean isGenFile, String filePath) {
		if (this.isRedirect != false) {
			return "please redirect url first";
		}

		HttpClient client2 = new HttpClient();
		// 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
		client2.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		client2.getState().addCookies(cookies);
		StringBuffer sb2 = new StringBuffer();
		int status = -1;

		HttpMethod method2 = getMethod3(path, params, clz, filePath);
		try {
			status = client2.executeMethod(method2);
			if (isGenFile != false) {
				this.dealResponse(method2.getResponseBodyAsStream(), path);						
			} else {
				if(status == 201){
					Header[] headers = method2.getResponseHeaders();
					sb2.append(headers[7]);
					System.out.println(sb2.substring(39));
				}else{
					this.dealResponse(method2.getResponseBodyAsStream(), sb2);
				}
			}
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		method2.releaseConnection();
		log.info(method2.getStatusLine() + "," + sb2.toString());
		return sb2.toString();
	}
	
	private HttpMethod getMethod(String path, String params, Class clz,
			String fileName) {
		HttpMethod method = null;
		String url = HttpFSUtils.createURL(path, params);
		try {
			if (null == clz) {
				method = getMethod(path, params, fileName);
			} else {
				method = (HttpMethod) clz.getConstructor(String.class)
						.newInstance(url);
			}
			method.setRequestHeader("content-type", "application/octet-stream");
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		return method;
	}

	private HttpMethod getMethod2(String path, String params, Class clz,
			String fileName) {
		HttpMethod method = null;
		String url = HttpFSUtils.redirectURL(path, params);
		try {
			if (null == clz) {
				method = getMethod(path, params, fileName);
			} else {
				method = (HttpMethod) clz.getConstructor(String.class)
						.newInstance(url);
			}
			method.setRequestHeader("content-type", "application/octet-stream");
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		return method;
	}

	private HttpMethod getMethod3(String path, String params, Class clz, String filePath) {
		HttpMethod method = null;
		String url = HttpFSUtils.redirectURL(path, params);
		try {
			if (clz == null) {
				method = getMethod3(path, params, filePath);
			} else {
				method = (HttpMethod) clz.getConstructor(String.class).newInstance(url);
			}
			method.setRequestHeader("content-type", "application/octet-stream");
		} catch(Exception e) {
			log.error("getMethod fail", e);
		}
		return method;
	}
	
	private HttpMethod getMethod(String path, String params, String fileName) {
		String url = HttpFSUtils.createURL(path, params);
		PutMethod method = new PutMethod(url);
		method.setRequestHeader("content-type", "application/octet-stream");
		try {
			// 设置上传文件
			File targetFile = new File(fileName);
			Part[] parts = { new FilePart(targetFile.getName(), targetFile) };
			method.setRequestEntity(new MultipartRequestEntity(parts, method
					.getParams()));
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		return method;
	}

	private HttpMethod getMethod2(String path, String params, String fileName) {
		String url = HttpFSUtils.redirectURL(path, params);
		PutMethod method = new PutMethod(url);
		method.setRequestHeader("content-type", "application/octet-stream");
		try {
			// 设置上传文件
			File targetFile = new File(fileName);
			Part[] parts = { new FilePart(targetFile.getName(), targetFile) };
			method.setRequestEntity(new MultipartRequestEntity(parts, method
					.getParams()));
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		return method;
	}

	private HttpMethod getMethod3(String path, String params, String filePath) {
		String url3 = HttpFSUtils.redirectURL(path, params);
		PutMethod method3 = new PutMethod(url3);
		method3.setRequestHeader("content-type", "application/octet-stream");
		try {
			//设置上传文件
//			File uploadfile = new File(filePath, fileName);
			File uploadFile = new File(filePath);
			Part[] parts = { new FilePart(uploadFile.getName(), uploadFile) };
			method3.setRequestEntity(new MultipartRequestEntity(parts, method3.getParams()));		
		} catch (Exception e) {
			log.error("getMethod fail", e);
		}
		return method3;
	}
	
	private void getCookie(HttpClient client) {
		CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
		this.cookies = cookiespec.match(HttpFSConf.getDatanode1(), HttpFSConf
				.getPort1(), "/", false, client.getState().getCookies());
	}

	/**
	 * deal HttpResponse content
	 * 
	 * @param conn
	 * @param sb
	 * @return
	 * @throws IOException
	 */
	private String dealResponse(InputStream in, StringBuffer sb) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			log.error("deal response content fail", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("close BufferedReader error", e);
				}
			}
		}
		return sb.toString();
	}

	private void dealResponse(InputStream in, String path) {
		int pos = path.lastIndexOf("/");
		String fileName = path.substring(pos + 1);
		BufferedReader reader = null;
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileName);
			reader = new BufferedReader(new InputStreamReader(in, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				out.append(line + "\n");
			}
		} catch (IOException e) {
			log.error("deal response content fail", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("close BufferedReader error", e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					log.error("close PrintWriter error", e);
				}
			}
		}
	}

}