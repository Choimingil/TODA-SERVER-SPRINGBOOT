package com.toda.api.TODASERVERSPRINGBOOT.controllers;

import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.AbstractController;
import com.toda.api.TODASERVERSPRINGBOOT.controllers.base.BaseController;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class StickerController extends AbstractController implements BaseController {
    // $r->addRoute('GET', '/user/stickers', ['StickerController', 'getUserStickers']);                                        //7-1. 유저 보유 스티커 조회 API(스티커 Controller에 존재)
    // $r->addRoute('POST', '/posts/{postID:\d+}/stickers', ['StickerController', 'addSticker']);                              //22. 스티커 사용 API
    // $r->addRoute('PATCH', '/posts/{postID:\d+}/stickers', ['StickerController', 'updateSticker']);                          //23. 스티커 수정 API
    // $r->addRoute('GET', '/stickers/{stickerPackID:\d+}', ['StickerController', 'getStickerDetail']);                        //24-1. 스티커 상세 조회 API
    // $r->addRoute('GET', '/store', ['StickerController', 'getStore']);                                                       //24-2. 스티커 상점 조회 API
    // $r->addRoute('GET', '/posts/{postID:\d+}/stickers', ['StickerController', 'getStickerView']);                           //25. 사용자가 사용한 스티커 리스트 조회 API
}
