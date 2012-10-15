package com.vegaasen.http.httpbench.model;

/**
 * @author <a href="vegaasen@gmail.com">Vegard Aasen</a>
 */
public class LatchResult {

    private final int requests;
    private final int successfulRequests;
    private final int failedRequests;
    private final long totalRunTime;

    public LatchResult(int requests, int successfulRequests, int failedRequests, long totalRunTime) {
        this.requests = requests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.totalRunTime = totalRunTime;
    }

    public int getRequests() {
        return requests;
    }

    public int getSuccessfulRequests() {
        return successfulRequests;
    }

    public long getTotalRunTime() {
        return totalRunTime;
    }

    public int getFailedRequests() {
        return failedRequests;
    }
}
