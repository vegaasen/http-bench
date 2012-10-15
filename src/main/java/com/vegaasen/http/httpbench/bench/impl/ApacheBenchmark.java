package com.vegaasen.http.httpbench.bench.impl;

import com.vegaasen.http.httpbench.bench.IBenchmark;
import com.vegaasen.http.httpbench.bench.abs.AbstractBenchmark;
import com.vegaasen.http.httpbench.model.BenchmarkResult;
import com.vegaasen.http.httpbench.model.LatchResult;
import com.vegaasen.http.httpbench.util.Logger;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import static com.vegaasen.http.httpbench.common.ErrorMessages.*;

/**
 * @author <a href="vegaasen@gmail.com">Vegard Aasen</a>
 */
public class ApacheBenchmark extends AbstractBenchmark implements IBenchmark {

    private static HttpClient httpClient;
    private static boolean verifiedAndConfigured = false;

    public ApacheBenchmark(final int threads, final int requestsPrThread, final int warmUpThreads, final String url) {
        super(threads, requestsPrThread, warmUpThreads, url);
    }

    @Override
    public void setUp() {
        super.setUp();

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 20 * 1000);
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();

        if (getAllowedPorts() != null && getAllowedPorts().length > 0) {
            for (int port : getAllowedPorts()) {
                schemeRegistry.register(new Scheme("http", port, PlainSocketFactory.getSocketFactory()));
            }
        } else {
            schemeRegistry.register(new Scheme("http", HTTP_PORT, PlainSocketFactory.getSocketFactory()));
            schemeRegistry.register(new Scheme("https", HTTPS_PORT, SSLSocketFactory.getSocketFactory()));
        }

        ClientConnectionManager ccm = new BasicClientConnectionManager(schemeRegistry);

        httpClient = new DefaultHttpClient(ccm, httpParameters);

        verifiedAndConfigured = true;
    }

    @Override
    public void verifyAttainable() {
        try {
            URL url = new URL(getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getLastModified() >= 0L) {
                verifiedAndConfigured = true;
            }
        } catch (IOException e) {
            Logger.log("Malformed URL. Please verify the URL before continue.");
            verifiedAndConfigured = false;
        }

    }

    @Override
    public Boolean warmUp() {
        if (verifiedAndConfigured) {
            for (; ; ) {
                HttpGet httpGet = new HttpGet(getUrl());
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    InputStream verifiedInputStream = response.getEntity().getContent();
                    if (verifiedInputStream != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        break;
                    }
                } catch (Exception e) {
                    Logger.log("Unable to continue with response.");
                    break;
                }
            }
            return null;
        }
        throw new RuntimeException(E_2001.getMessage());
    }

    @Override
    public BenchmarkResult executeBenchmark() {
        if (verifiedAndConfigured) {
            final CountDownLatch countDownLatch = new CountDownLatch(getThreads());
            final Vector<LatchResult> latchResults = new Vector<>();

            long benchmarkStart = System.nanoTime(); // using nano instead of the "unsafe" currentTimeMills. Its not precise enough
            Logger.log("Started the benchmark at: " + benchmarkStart);

            for (int cntr = 0; cntr < getThreads(); cntr++) {
                getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        int success = 0;
                        int fails = 0;
                        long latchInitiated = System.nanoTime();
                        for(int reqprtr = 0; reqprtr < getRequestsPrThread();reqprtr++) {
                            final HttpRequestBase requestMethodType = getHttpRequestBaseType();
                            List<Header> headers = getAllHeaders();
                            headers.addAll(getKeepAliveHeaders());
                            requestMethodType.setHeaders((Header[]) headers.toArray());
                            try {
                                HttpResponse response = httpClient.execute(requestMethodType);
                                if(response.getEntity().getContentLength()>0) {
                                    response.getEntity().getContent();
                                    success++;
                                }else{
                                    fails++;
                                }
                            } catch (IOException e) {
                                fails++;
                                requestMethodType.abort();
                                Logger.log("Unable to request through the httpClient. Aborting.");
                            }
                            long totalRunTime = System.nanoTime() - latchInitiated;
                            latchResults.add(new LatchResult(getRequestsPrThread(), success, fails, totalRunTime));
                            countDownLatch.countDown();
                        }
                    }
                });
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                Logger.log("Unable to Latch.await()\n"+e);
                Thread.interrupted();
            }

            long benchmarkEndTime = System.nanoTime() - benchmarkStart;

        }
        return null;
    }

    @Override
    public void stop() {
        super.stop();

        httpClient.getConnectionManager().closeExpiredConnections();
        httpClient.getConnectionManager().shutdown();

        Logger.log("Shut down completed.");
    }

}
