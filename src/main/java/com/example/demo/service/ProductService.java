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

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepo;

@Service
public class ProductService {

    private final ProductRepo productRepo;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Cacheable("products")
    public List<Product> getAllProducts() {
        System.out.println("Fetching from database...");
        return productRepo.findAll();
    }

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(int id) {
        return productRepo.findById(id).orElse(null);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "search", allEntries = true)
    })
    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
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
    public List<Product> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword);
    }
}
