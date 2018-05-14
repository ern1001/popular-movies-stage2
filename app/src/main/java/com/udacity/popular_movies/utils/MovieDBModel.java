package com.udacity.popular_movies.utils;
// POJO model generated at http://www.jsonschema2pojo.org/

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.udacity.popular_movies.utils.MovieDBInfo;
//import org.apache.commons.lang.builder.ToStringBuilder;

public class MovieDBModel {

@SerializedName("page")
@Expose
private Integer page;
@SerializedName("total_results")
@Expose
private Integer totalResults;
@SerializedName("total_pages")
@Expose
private Integer totalPages;
@SerializedName("results")
@Expose
private List<MovieDBInfo> results = null;

public Integer getPage() {
return page;
}

public void setPage(Integer page) {
this.page = page;
}

public Integer getTotalResults() {
return totalResults;
}

public void setTotalResults(Integer totalResults) {
this.totalResults = totalResults;
}

public Integer getTotalPages() {
return totalPages;
}

public void setTotalPages(Integer totalPages) {
this.totalPages = totalPages;
}

public List<MovieDBInfo> getResults() {
return results;
}

public void setResults(List<MovieDBInfo> results) {
this.results = results;
}

/*@Override
public String toString() {
return new ToStringBuilder(this).append("page", page).append("totalResults", totalResults).append("totalPages", totalPages).append("results", results).toString();
}*/

}

