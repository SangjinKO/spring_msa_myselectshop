package com.sangjin.product.service;

import com.sangjin.product.dto.ItemDto;

import java.util.List;

// DIP적용을 위한 인터페이스
public interface ApiService {
    public List<ItemDto> searchItems(String query);
}
