package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.ProductResponse;
import com.example.demo.Dto.UserSummaryDto;
import com.example.demo.model.Product;
import com.example.demo.model.Users;
import com.example.demo.repository.ProductRepo;
import com.example.demo.repository.UserRepo;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final UserRepo usersRepository;

    @Autowired
    public ProductService(ProductRepo productRepo, UserRepo usersRepository) {
        this.productRepo = productRepo;
        this.usersRepository = usersRepository;
    }

    @Cacheable("products")
    public List<ProductResponse> getAllProducts() {
        System.out.println("Fetching from database...");

        return productRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProductById(int id) {
        Product product = productRepo.findById(id).orElse(null);

        return product == null ? null : toResponse(product);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "search", allEntries = true)
    })
    public Product addProduct(
            Product product,
            MultipartFile imageFile,
            String username) throws IOException {

        Users user = usersRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        product.setCreatedBy(user);
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        product.setImageData(imageFile.getBytes());

        return productRepo.save(product);
    }

    @Caching(put = {
            @CachePut(value = "product", key = "#id")
    }, evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "search", allEntries = true)
    })
    public Product updateProduct(int id, Product product, MultipartFile imageFile) throws IOException {
        product.setImageData(imageFile.getBytes());
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        return productRepo.save(product);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "search", allEntries = true),
            @CacheEvict(value = "product", key = "#id")
    })
    public void deleteProduct(int id) {
        productRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "search", key = "#keyword.trim().toLowerCase()")
    public List<ProductResponse> searchProducts(String keyword) {

        return productRepo.searchProducts(keyword)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .price(product.getPrice())
                .category(product.getCategory())
                .releaseDate(product.getReleaseDate())
                .available(product.isAvailable())
                .quantity(product.getQuantity())
                .imageData(product.getImageData())
                .imageName(product.getImageName())
                .imageType(product.getImageType())
                .createdBy(
                        new UserSummaryDto(
                                product.getCreatedBy().getId(),
                                product.getCreatedBy().getUsername()))
                .build();
    }
}
