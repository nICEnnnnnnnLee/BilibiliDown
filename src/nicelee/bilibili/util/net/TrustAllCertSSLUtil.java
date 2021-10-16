package nicelee.bilibili.util.net;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustAllCertSSLUtil implements X509TrustManager {

	private TrustAllCertSSLUtil() {
	}

	private static SSLSocketFactory sslFactory = null;

	public static SSLSocketFactory getFactory() throws Exception {
		if (sslFactory == null) {
			TrustManager[] trustAllCerts = new TrustManager[1];
			TrustManager tm = new TrustAllCertSSLUtil();
			trustAllCerts[0] = tm;
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, null);
			sslFactory = sc.getSocketFactory();
		}
		return sslFactory;
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		return;
	}

	public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		return;
	}
}
