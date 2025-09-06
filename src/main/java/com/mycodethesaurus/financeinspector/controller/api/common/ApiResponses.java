package com.mycodethesaurus.financeinspector.controller.api.common;

/** Common API response constants for reuse across controllers */
public final class ApiResponses {

  private ApiResponses() {
    // Utility class - prevent instantiation
  }

  // Response codes
  public static final String OK_CODE = "200";
  public static final String CREATED_CODE = "201";
  public static final String NO_CONTENT_CODE = "204";
  public static final String BAD_REQUEST_CODE = "400";
  public static final String UNAUTHORIZED_CODE = "401";
  public static final String FORBIDDEN_CODE = "403";
  public static final String NOT_FOUND_CODE = "404";
  public static final String CONFLICT_CODE = "409";
  public static final String INTERNAL_ERROR_CODE = "500";

  // Response descriptions
  public static final String OK_DESC = "Operation successful";
  public static final String CREATED_DESC = "Resource created successfully";
  public static final String NO_CONTENT_DESC = "Resource deleted successfully";
  public static final String BAD_REQUEST_DESC = "Invalid input data or validation errors";
  public static final String UNAUTHORIZED_DESC = "Authentication required";
  public static final String FORBIDDEN_DESC = "Access denied";
  public static final String NOT_FOUND_DESC = "Resource not found";
  public static final String CONFLICT_DESC = "Resource conflict";
  public static final String INTERNAL_ERROR_DESC = "Internal server error";

  // Media type constants
  public static final String APPLICATION_JSON = "application/json";
}
