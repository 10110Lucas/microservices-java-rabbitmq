package com.algaworks.algasensors.temperature.monitoring.api.config.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingInterceptor extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final int MAX_PAYLOAD_LENGTH = 10_000; // 10KB

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper req = (request instanceof ContentCachingRequestWrapper)
                ? (ContentCachingRequestWrapper) request
                : new ContentCachingRequestWrapper(request);

        ContentCachingResponseWrapper res = (response instanceof ContentCachingResponseWrapper)
                ? (ContentCachingResponseWrapper) response
                : new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(req, res);
        } finally {
            long tookMs = System.currentTimeMillis() - start;

            logRequest(req);
            logResponse(res, tookMs);

            // SEMPRE copiar o body de volta para a resposta real
            res.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        log.info("{} {}{} ---------------------------->", request.getMethod(), uri, (query != null ? "?" + query : ""));

        Collections.list(request.getHeaderNames())
                .forEach(h -> log.info("{}: {}", h, request.getHeader(h)));

        String body = readPayload(
                request.getContentAsByteArray(),
                request.getCharacterEncoding(),
                request.getContentType()
        );
        log.info("");
        if (body != null) log.info("Request Body: {}", body);
        log.info("Fim Request ({} bytes) ------------>", body != null ? body.getBytes(StandardCharsets.UTF_8).length : 0);
    }

    private void logResponse(ContentCachingResponseWrapper response, long tookMs) {
        log.info("<---------------------------- HttpStatus: {} ({} ms)", response.getStatus(), tookMs);

        response.getHeaderNames().forEach(h -> log.info("{}: {}", h, response.getHeader(h)));

        String body = readPayload(
                response.getContentAsByteArray(),
                response.getCharacterEncoding(),
                response.getContentType()
        );
        log.info("");
        if (response.getStatus() >= 400) log.info("Response Body (erro): {}", body);
        else if (body != null) log.info("Response Body: {}", body);
        log.info("<---------------------------- Fim");
    }

    private String readPayload(byte[] buf, String charset, String contentType) {
        if (buf == null || buf.length == 0) return "";
        if (contentType != null && isBinary(contentType)) {
            return "[conteúdo binário omitido: " + contentType + ", " + buf.length + " bytes]";
        }
        int length = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
        try {
            String payload = new String(buf, 0, length, StandardCharsets.UTF_8);
            if (contentType != null && contentType.startsWith("application/json")) {
                return prettyJson(payload);
            }
            return payload;
        } catch (Exception e) {
            return "[falha ao decodificar payload: " + e.getMessage() + "]";
        }
    }

    private boolean isBinary(String contentType) {
        String ct = contentType.toLowerCase(Locale.ROOT);
        return ct.startsWith("image/")
                || ct.startsWith("video/")
                || ct.startsWith("audio/")
                || ct.contains("multipart/form-data")
                || ct.equals("application/octet-stream")
                || ct.equals("application/pdf");
    }

    private String prettyJson(String raw) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(raw, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return raw; // se não der para fazer pretty, devolve cru
        }
    }
}
