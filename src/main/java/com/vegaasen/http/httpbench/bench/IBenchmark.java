package com.vegaasen.http.httpbench.bench;

import com.vegaasen.http.httpbench.model.BenchmarkResult;

/**
 * @author <a href="vegaasen@gmail.com">Vegard Aasen</a>
 */
public interface IBenchmark {

    public void setUp();

    public void verifyAttainable();

    public Boolean warmUp();

    public void stop();

    public BenchmarkResult executeBenchmark();

}
