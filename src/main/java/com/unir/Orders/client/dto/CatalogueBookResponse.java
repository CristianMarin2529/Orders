package com.unir.Orders.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta del microservicio Catalogue GET /books/{id}
 * Mapea automáticamente BookResponseDto de Catálogo
 * Solo usamos campos: id, title, price, stock, isbn, isActive
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogueBookResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("stock")
    private Integer stock;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("pages")
    private Integer pages;

    @JsonProperty("publisher")
    private Object publisher; // Ignoramos la relación completa

    @JsonProperty("imageUrls")
    private List<String> imageUrls;

    @JsonProperty("authors")
    private List<Object> authors; // Ignoramos relación

    @JsonProperty("categories")
    private List<Object> categories; // Ignoramos relación

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Constructor sin argumentos: REQUERIDO para desserialización JSON
    public CatalogueBookResponse() {
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public String getIsbn() {
        return isbn;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Integer getPages() {
        return pages;
    }

    public Object getPublisher() {
        return publisher;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public List<Object> getAuthors() {
        return authors;
    }

    public List<Object> getCategories() {
        return categories;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public void setPublisher(Object publisher) {
        this.publisher = publisher;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setAuthors(List<Object> authors) {
        this.authors = authors;
    }

    public void setCategories(List<Object> categories) {
        this.categories = categories;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}