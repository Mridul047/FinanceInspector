package com.mycodethesaurus.financeinspector.controller.api.common;

/** Common API parameter constants for reuse across controllers */
public final class ApiParameters {

  private ApiParameters() {
    // Utility class - prevent instantiation
  }

  // Parameter descriptions
  public static final String USER_ID_DESC = "User ID";
  public static final String USER_ID_EXAMPLE = "1";

  public static final String CATEGORY_ID_DESC = "Category ID";
  public static final String CATEGORY_ID_EXAMPLE = "1";

  public static final String EXPENSE_ID_DESC = "Expense ID";
  public static final String EXPENSE_ID_EXAMPLE = "1";

  public static final String INCOME_ID_DESC = "Income ID";
  public static final String INCOME_ID_EXAMPLE = "1";

  public static final String PAGINATION_DESC = "Pagination parameters";

  public static final String START_DATE_DESC = "Start date (inclusive)";
  public static final String START_DATE_EXAMPLE = "2024-01-01";

  public static final String END_DATE_DESC = "End date (inclusive)";
  public static final String END_DATE_EXAMPLE = "2024-01-31";

  public static final String SEARCH_TEXT_DESC = "Search text for description or merchant";
  public static final String SEARCH_TEXT_EXAMPLE = "restaurant";

  public static final String LIMIT_DESC = "Number of recent items to retrieve";
  public static final String LIMIT_EXAMPLE = "10";

  public static final String INCLUDE_INACTIVE_DESC = "Include inactive items";
  public static final String INCLUDE_INACTIVE_EXAMPLE = "false";

  public static final String PARENT_ONLY_DESC = "Search only parent categories";
  public static final String PARENT_ONLY_EXAMPLE = "false";

  public static final String QUERY_DESC = "Search query";
  public static final String QUERY_EXAMPLE = "food";
}
