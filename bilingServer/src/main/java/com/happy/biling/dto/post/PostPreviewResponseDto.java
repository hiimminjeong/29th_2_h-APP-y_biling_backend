package com.happy.biling.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostPreviewResponseDto {
    private String postStatus; //거래중, 대여중 , 거래완료
    private Long postId;          // 게시글 ID
    private String title;         // 게시글 제목
    private Integer price;        // 가격
    private String previewImage;  // 대표 이미지 URL
    private String locationName;  // 위치 이름
    private String postType;  // 빌려드려요/빌려주세요
    private Long reviewId;        // 리뷰 ID (상태가 거래완료일 경우)
}


