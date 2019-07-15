package nicelee.bilibili.util.net;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustAllCertSSLUtil implements TrustManager, X509TrustManager {

	private TrustAllCertSSLUtil() {
	}

	private static SSLSocketFactory sslFactory = null;

	public static SSLSocketFactory getFactory() throws Exception {
		if (sslFactory == null) {
			TrustManager[] trustAllCerts = new TrustManager[1];
			TrustManager tm = new TrustAllCertSSLUtil();
			trustAllCerts[0] = tm;
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
			sslFactory = sc.getSocketFactory();
		}
		return sslFactory;
	}

	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
		return true;
	}

	public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
		return true;
	}

	public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
			throws java.security.cert.CertificateException {
		return;
	}

	public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
			throws java.security.cert.CertificateException {
		return;
	}
}
