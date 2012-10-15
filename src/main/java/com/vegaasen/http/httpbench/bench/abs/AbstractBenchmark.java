package com.vegaasen.http.httpbench.bench.abs;

import com.vegaasen.http.httpbench.common.HttpMethod;
import com.vegaasen.http.httpbench.util.Logger;
import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicHeader;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.vegaasen.http.httpbench.common.ErrorMessages.*;

/**
 * @author <a href="vegaasen@gmail.com">Vegard Aasen</a>
 */
public abstract class AbstractBenchmark {

    private static final int DEFAULT_REQUESTS_PR_THREAD = 1;

    private static final String
            HEADER_KeepAlive_KEY = "Keep-Alive",
            HEADER_KeepAlive_VAL = "timeout=10, max=5",
            HEADER_KeepAlive_C_KEY = "Connection",
            HEADER_KeepAlive_C_VAL = HEADER_KeepAlive_KEY;


    public static final int
            HTTP_PORT = 80,
            HTTPS_PORT = 443;

    protected ExecutorService executor;

    protected int threads;
    protected int requestsPrThread;
    protected int warmUpThreads;
    protected String url;

    private List<String> header;
    private Boolean info;
    private Boolean keepAlive;
    private InputStream inputContent;
    private String userAgent;
    private int[] allowedPorts;
    private HttpMethod httpMethod;

    public AbstractBenchmark(final int threads, final int requestsPrThread, final int warmUpThreads, final String url) {
        if(threads<=0) {
            throw new IllegalArgumentException(E_1001.getMessage());
        }
        if(url==null || url.equals("")) {
            throw new IllegalArgumentException(E_1001.getMessage());
        }
        this.requestsPrThread = requestsPrThread;
        if(this.requestsPrThread<=0) {
            this.requestsPrThread = DEFAULT_REQUESTS_PR_THREAD;
            Logger.log(String.format("Setting default requests {%s}", DEFAULT_REQUESTS_PR_THREAD));
        }
        this.threads = threads;
        this.warmUpThreads = warmUpThreads;
        this.url = url;
    }

    protected void setUp() {
        executor = Executors.newFixedThreadPool(getThreads());
    }

    protected void stop() {
        executor.shutdown();
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }

    public int getThreads() {
        return threads;
    }

    public int getRequestsPrThread() {
        return requestsPrThread;
    }

    public int getWarmUpThreads() {
        return warmUpThreads;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public Boolean getInfo() {
        return info;
    }

    public void setInfo(Boolean info) {
        this.info = info;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public InputStream getInputContent() {
        return inputContent;
    }

    public void setInputContent(InputStream inputContent) {
        this.inputContent = inputContent;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int[] getAllowedPorts() {
        return allowedPorts;
    }

    public void setAllowedPorts(int[] allowedPorts) {
        this.allowedPorts = allowedPorts;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    protected List<Header> getKeepAliveHeaders() {
        if(getKeepAlive()) {
            List<Header> keepAliveHeaders = new ArrayList<>();
            Header keepAliveHeader = new BasicHeader(HEADER_KeepAlive_C_KEY, HEADER_KeepAlive_C_VAL);
            keepAliveHeaders.add(keepAliveHeader);
            keepAliveHeader = new BasicHeader(HEADER_KeepAlive_KEY, HEADER_KeepAlive_VAL);
            keepAliveHeaders.add(keepAliveHeader);
            return keepAliveHeaders;
        }
        return Collections.emptyList();
    }

    protected List<Header> getAllHeaders() {
        if(getHeader()!=null && !getHeader().equals("")) {
            List<Header> headers = new ArrayList<>();
            final int key = 0;
            final int value = 1;
            for(String header : getHeader()) {
                String[] splizedHeader = header.split(":");
                if(splizedHeader.length>1) {
                    Header h = new BasicHeader(splizedHeader[key], splizedHeader[value]);
                    headers.add(h);
                }
            }
            return headers;
        }
        return Collections.emptyList();
    }

    protected HttpRequestBase getHttpRequestBaseType() {
        if(getHttpMethod().getType().equals("")) {
            Logger.log("Missing default HttpRequest Type. Defaulting to GET.");
        }

        if(getUrl().equals("")) {
            throw new RuntimeException("Missing URL. Unable to proceed.");
        }

        HttpRequestBase requestMethodType;

        switch (getHttpMethod().getType()) {
            case HttpMethod.POST:
                requestMethodType = new HttpPost();
                break;
            case HttpMethod.GET:
                requestMethodType = new HttpGet();
                break;
            case HttpMethod.OPTIONS:
                requestMethodType = new HttpOptions();
                break;
            case HttpMethod.TRACE:
                requestMethodType = new HttpTrace();
                break;
            case HttpMethod.DELETE:
                requestMethodType = new HttpDelete();
                break;
            case HttpMethod.HEAD:
                requestMethodType = new HttpHead();
                break;
            default:
                requestMethodType = new HttpGet();
                break;
        }

        try {
            requestMethodType.setURI(new URI(getUrl()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Illegal URL format. Please revisit URL {%s}", getUrl()));
        }

        return requestMethodType;
    }

}
