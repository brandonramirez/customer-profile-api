package com.brandonsramirez.customerProfileApi;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Hold a paginated result set.  Search queries are always paginated.  Results
 * must indicate the actual raw number of hits, then include the results that
 * fell between the offset and max of the query.
 */
@ToString
public class SearchResult<T> {
  @Getter @Setter private int totalCount;
  @Getter @Setter private List<T> results;

  public SearchResult() {
    super();
  }

  public SearchResult(int totalCount) {
    this(totalCount, null);
  }

  public SearchResult(int totalCount, List<T> results) {
    setTotalCount(totalCount);
    setResults(results);
  }
}
