package com.example.music.api;



import com.example.music.domain.CommentList;
import com.example.music.domain.MusicInfo;
import com.example.music.domain.MusicList;
import com.example.music.domain.MusicUrl;
import com.example.music.domain.TopList;
import com.example.music.domain.TopMusicList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("/search")
    Call<MusicList> getMusicList(@Query("keywords") String keywords);

    @GET("/song/detail")
    Call<MusicInfo> getMusicInfo(@Query("ids") String ids);

    @GET("/song/url")
    Call<MusicUrl> getMusicUrl(@Query("id") String id);

    @GET("/toplist")
    Call<TopList> getTopList();

    @GET("/playlist/detail")
    Call<TopMusicList> getTopMusicList(@Query("id") String id);


    @GET("/comment/music")
    Call<CommentList> getComment(@Query("id") String id);
}
