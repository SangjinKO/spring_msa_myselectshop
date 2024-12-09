package com.sangjin.product.service;

import com.sangjin.common.exception.ProductNotFoundException;
import com.sangjin.product.dto.ItemDto;
import com.sangjin.product.dto.UserRoleEnum;
import com.sangjin.product.config.security.CustomUserPrincipal;
import com.sangjin.product.dto.ProductMypriceRequestDto;
import com.sangjin.product.dto.ProductRequestDto;
import com.sangjin.product.dto.ProductResponseDto;
import com.sangjin.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    public static final int MIN_MY_PRICE = 100;
    private final ProductRepository productRepository;
    private final FolderRepository folderRepository;
    private final ProductFolderRepository productFolderRepository;
    private final MessageSource messageSource;

    public ProductResponseDto createProduct(ProductRequestDto requestDto, Long userId) {
        Product product = productRepository.save(new Product(requestDto, userId));
        return new ProductResponseDto(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if (myprice < MIN_MY_PRICE) {
//            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + " 원 이상으로 설정해 주세요.");

            // messageSource 사용
            String ERR_MSG = messageSource.getMessage(
                    "below.min.my.price",
                    new Integer[]{MIN_MY_PRICE},
                    "Wrong Price",
                    Locale.getDefault()
            );
            throw new IllegalArgumentException(ERR_MSG);
        }


        Product product = productRepository.findById(id).orElseThrow(() ->
                // Custom Exception 사용
                // messageSource 사용
                new ProductNotFoundException(messageSource.getMessage(
                        "not.found.product",
                        new Integer[]{MIN_MY_PRICE},
                        "Not found product",
                        Locale.getDefault()
                ))
        );

        product.update(requestDto);

        return new ProductResponseDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(CustomUserPrincipal user,
                                                int page, int size, String sortBy, boolean isAsc) {
        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        String role = user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority()) // 권한 이름(String) 추출
                .findFirst() // 첫 번째 값 가져오기
                .orElse(null); // 값이 없으면 null 반환

        Page<Product> productList;

        if (role == UserRoleEnum.USER.toString()) {
            // 사용자 권한이 USER 일 경우
            productList = productRepository.findAllByUserId(user.getUserId(), pageable);
        } else {
            productList = productRepository.findAll(pageable);
        }

        return productList.map(ProductResponseDto::new);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsAll(CustomUserPrincipal user) {

        // CustomUserPrincipal에서 권한(authorities) 추출
        String role = user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority()) // 권한 이름(String) 추출
                .findFirst() // 첫 번째 값 가져오기
                .orElse(null); // 값이 없으면 null 반환

        List<Product> productList;

        if (role == UserRoleEnum.USER.toString()) {
            // 사용자 권한이 USER 일 경우
            productList = productRepository.findAllByUserId(user.getUserId());
        } else {
            productList = productRepository.findAll();
        }

        return productList.stream().map(ProductResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 상품은 존재하지 않습니다.")
        );
        product.updateByItemDto(itemDto);
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductResponseDto> responseDtoList = new ArrayList<>();

        for (Product product : productList) {
            responseDtoList.add(new ProductResponseDto(product));
        }
        return responseDtoList;
    }


    public void addFolder(Long productId, Long folderId, CustomUserPrincipal user) {

        // 1) 상품을 조회합니다.
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NullPointerException("해당 상품이 존재하지 않습니다.")
        );

        // 2) 폴더를 조회합니다.
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new NullPointerException("해당 폴더가 존재하지 않습니다.")
        );

        // 3) 조회한 폴더와 상품이 모두 로그인한 회원의 소유인지 확인합니다.
        if (!product.getUserId().equals(user.getUserId())
                || !folder.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("회원님의 관심상품이 아니거나, 회원님의 폴더가 아닙니다.");
        }

        // 중복확인
        Optional<ProductFolder> overlapFolder = productFolderRepository.findByProductAndFolder(product, folder);

        if (overlapFolder.isPresent()) {
            throw new IllegalArgumentException("중복된 폴더입니다.");
        }

        // 4) 상품에 폴더를 추가합니다.
        productFolderRepository.save(new ProductFolder(product, folder));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsInFolder(Long folderId, int page, int size, String sortBy, boolean isAsc, Long userId) {

        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 해당 폴더에 등록된 상품을 가져옵니다.
        Page<Product> products = productRepository.findAllByUserIdAndProductFolderList_FolderId(userId, folderId, pageable);

        Page<ProductResponseDto> responseDtoList = products.map(ProductResponseDto::new);

        return responseDtoList;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsInFolderAll(Long folderId, Long userId) {

        // 해당 폴더에 등록된 상품을 가져옵니다.
        List<Product> products = productRepository.findAllByUserIdAndProductFolderList_FolderId(userId, folderId);

        List<ProductResponseDto> responseDtoList = products.stream().map(ProductResponseDto::new).collect(Collectors.toList());

        return responseDtoList;
    }
}
