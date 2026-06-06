package com.example.caloriemate.network;

import com.example.caloriemate.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("cgi/search.pl")
    Call<SearchResponse> searchFood(
            @Query("search_terms") String query,
            @Query("search_simple") int searchSimple,
            @Query("action") String action,
            @Query("json") int json,
            @Query("page_size") int pageSize
    );
}