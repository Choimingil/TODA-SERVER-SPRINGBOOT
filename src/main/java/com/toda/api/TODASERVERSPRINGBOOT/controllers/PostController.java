package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {
    // $r->addRoute('POST', '/post/ver3', ['PostController', 'addPostVer3']);                                                  //16-2. 게시물 작성 API(날짜 폰트 추가)
    // $r->addRoute('DELETE', '/post/{postID:\d+}', ['PostController', 'deletePost']);
    // $r->addRoute('PATCH', '/post/ver3', ['PostController', 'updatePostVer3']);
    // $r->addRoute('PATCH', '/post/ver2', ['PostController', 'updatePost']);                                                  //18-1. 게시물 수정 API(이미지 추가)

    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts', ['PostController', 'getPostList']);                                 //19. 게시물 리스트 조회 API
    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts/ver2', ['PostController', 'getPostListNew']);                         //19-0. 게시물 리스트 조회 API(날짜)
    // $r->addRoute('GET', '/diaries/{diaryID:\d+}/posts/countbydate', ['PostController', 'getPostNumByDate']);                //19-1. 게시물 날짜별 개수 조회 API
    // $r->addRoute('GET', '/posts/{postID:\d+}/ver2', ['PostController', 'getPostDetailVer2']);                               //20-1. 게시물 상세 조회 API(날짜 및 폰트 추가 버전)
}
