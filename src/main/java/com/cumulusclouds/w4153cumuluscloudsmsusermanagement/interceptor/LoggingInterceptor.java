package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String user = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous";
    logger.info("Request from user: {} to {}", user, request.getRequestURI());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    logger.info("Response status: {} for Path: {}", response.getStatus(), request.getRequestURI());
  }
}