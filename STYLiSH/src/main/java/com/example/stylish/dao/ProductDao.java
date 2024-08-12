package com.example.stylish.dao;

import com.example.stylish.model.Color;
import com.example.stylish.model.Product;
import com.example.stylish.model.Variant;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductDao {

    private final JdbcTemplate jdbcTemplate;

    public ProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> getAllProducts(int offset, int limit) {
        return getProducts(offset, limit, null);
    }

    public List<Product> getWomenProducts(int offset, int limit) {
        return getProducts(offset, limit, "women");
    }

    public List<Product> getMenProducts(int offset, int limit) {
        return getProducts(offset, limit, "men");
    }

    public List<Product> getAccProducts(int offset, int limit) {
        return getProducts(offset, limit, "accessories");
    }

    public List<Product> findById(int id) {
        String sql = getBaseSQL() + "WHERE p.id = ?";
        // pass in parameters
        Object[] params = new Object[]{id};
        return jdbcTemplate.query(sql, params, new ProductResultSetExtractor());
    }


    public List<Product> findByTitle(String keyword, int offset, int limit) {
        // 1. get limit ID amount that  title contains keyword
        String subQuery = "SELECT DISTINCT p2.id FROM products p2 WHERE p2.title LIKE ? ORDER BY p2.id ASC LIMIT ?";
        List<Integer> limitedProductIds = jdbcTemplate.queryForList(subQuery, new Object[]{"%" + keyword + "%", limit}, Integer.class);
        if (limitedProductIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 2. by id collect product info
        String baseSql = getBaseSQL();
        String sql = baseSql + "WHERE p.id IN (" + limitedProductIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") " + "ORDER BY p.id ASC";
        return jdbcTemplate.query(sql, new ProductResultSetExtractor());
    }
    private String getBaseSQL() {
        return "SELECT p.id, p.title, p.description, p.price, p.texture, p.wash, p.place, p.note, p.story, " +
                "c.name as category_name, " +
                "co.id as color_id, co.name as color_name, co.code as color_code, " +
                "s.id as size_id, s.size as size_name, " +
                "v.id as variant_id, v.stock as variant_stock, v.color_id as variant_color_id, v.size_id as variant_size_id, " +
                "i.url as image_url " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "LEFT JOIN (SELECT DISTINCT co.id, co.name, co.code, co.product_id FROM colors co) co ON p.id = co.product_id " +
                "LEFT JOIN (SELECT DISTINCT s.id, s.size, s.product_id FROM sizes s) s ON p.id = s.product_id " +
                "LEFT JOIN variants v ON p.id = v.product_id AND v.color_id = co.id AND v.size_id = s.id " +
                "LEFT JOIN images i ON p.id = i.product_id ";
    }



    private String buildProductQuery(String category) {
        String subQuery = "SELECT p2.id FROM products p2 " +
                "LEFT JOIN categories c2 ON p2.category_id = c2.id ";
        if (category != null) {
            subQuery += "WHERE c2.name = ? ";
        }
        subQuery += "ORDER BY p2.id ASC LIMIT ? OFFSET ?";

        return "SELECT p.id, p.title, p.description, p.price, p.texture, p.wash, p.place, p.note, p.story, " +
                "c.name as category_name, " +
                "co.id as color_id, co.name as color_name, co.code as color_code, " +
                "s.id as size_id, s.size as size_name, " +
                "v.id as variant_id, v.stock as variant_stock, v.color_id as variant_color_id, v.size_id as variant_size_id, " +
                "i.url as image_url " +
                "FROM products p " +
                "JOIN (" + subQuery + ") AS limited_products ON p.id = limited_products.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "LEFT JOIN (SELECT DISTINCT co.id, co.name, co.code, co.product_id FROM colors co) co ON p.id = co.product_id " +
                "LEFT JOIN (SELECT DISTINCT s.id, s.size, s.product_id FROM sizes s) s ON p.id = s.product_id " +
                "LEFT JOIN variants v ON p.id = v.product_id AND v.color_id = co.id AND v.size_id = s.id " +
                "LEFT JOIN images i ON p.id = i.product_id";
    }

    private List<Product> getProducts(int offset, int limit, String category) {
        String sql = buildProductQuery(category);
        List<Object> params = new ArrayList<>();
        if (category != null) {
            params.add(category);
        }
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql, params.toArray(), new ProductResultSetExtractor());
    }

    private static class ProductResultSetExtractor implements ResultSetExtractor<List<Product>> {
        @Override
        public List<Product> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, Product> productMap = new HashMap<>();
            while (rs.next()) {
                int productId = rs.getInt("id");
                Product product = productMap.get(productId);
                if (product == null) {
                    product = new Product();
                    product.setId(productId);
                    product.setCategory(rs.getString("category_name"));
                    product.setTitle(rs.getString("title"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getInt("price"));
                    product.setTexture(rs.getString("texture"));
                    product.setWash(rs.getString("wash"));
                    product.setPlace(rs.getString("place"));
                    product.setNote(rs.getString("note"));
                    product.setStory(rs.getString("story"));
                    product.setColors(new ArrayList<>());
                    product.setSizes(new ArrayList<>());
                    product.setVariants(new ArrayList<>());
                    product.setImages(new ArrayList<>());
                    productMap.put(productId, product);
                }

                // color
                String colorCode = rs.getString("color_code");
                if (colorCode != null) {
                    boolean colorExists = product.getColors().stream().anyMatch(c -> c.getCode().equals(colorCode));
                    if (!colorExists) {
                        Color color = new Color();
                        color.setCode(colorCode);
                        color.setName(rs.getString("color_name"));
                        product.getColors().add(color);
                    }
                }

                // size
                String sizeName = rs.getString("size_name");
                if (sizeName != null && !product.getSizes().contains(sizeName)) {
                    product.getSizes().add(sizeName);
                }

                // variant
                int variantColorId = rs.getInt("variant_color_id");
                int variantSizeId = rs.getInt("variant_size_id");
                if (variantColorId > 0 && variantSizeId > 0) {
                    String variantColorCode = rs.getString("color_code");
                    String variantSizeName = rs.getString("size_name");

                    boolean variantExists = product.getVariants().stream().anyMatch(v ->
                            v.getColor().equals(variantColorCode) && v.getSize().equals(variantSizeName));
                    if (!variantExists) {
                        Variant variant = new Variant();
                        variant.setColor(variantColorCode);
                        variant.setSize(variantSizeName);
                        variant.setStock(rs.getInt("variant_stock"));
                        product.getVariants().add(variant);
                    }
                }

                // image
                String imageUrl = rs.getString("image_url");
                if (imageUrl != null) {
                    if (product.getMainImage() == null) {
                        product.setMainImage(imageUrl);
                    } else if (!product.getImages().contains(imageUrl) && !imageUrl.equals(product.getMainImage())) {
                        product.getImages().add(imageUrl);
                    }
                }
            }
            return new ArrayList<>(productMap.values());
        }
    }

    public void save(Product product) {
        Integer categoryId = getCategoryOrInsert(product.getCategory());

        String productSql = "INSERT INTO products (category_id, title, description, price, texture, wash, place, note, story) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(productSql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, categoryId);
            ps.setString(2, product.getTitle());
            ps.setString(3, product.getDescription());
            ps.setInt(4, product.getPrice());
            ps.setString(5, product.getTexture());
            ps.setString(6, product.getWash());
            ps.setString(7, product.getPlace());
            ps.setString(8, product.getNote());
            ps.setString(9, product.getStory());
            return ps;
        });

        Long productId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        saveImages(productId, product.getImages());
        saveColors(productId, product.getColors());
        saveSizes(productId, product.getSizes());
        saveVariants(productId, product.getVariants());
    }

    private Integer getCategoryOrInsert(String categoryName) {
        String getCategorySql = "SELECT id FROM categories WHERE name = ?";
        Integer categoryId = null;
        try {
            categoryId = jdbcTemplate.queryForObject(getCategorySql, new Object[]{categoryName}, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            String insertCategorySql = "INSERT INTO categories (name) VALUES (?)";
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertCategorySql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, categoryName);
                return ps;
            });
            categoryId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        }
        return categoryId;
    }

    private void saveImages(Long productId, List<String> images) {
        String sql = "INSERT INTO images (product_id, url) VALUES (?, ?)";
        for (String image : images) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, productId);
                ps.setString(2, image);
                return ps;
            });
        }
    }

    private void saveColors(Long productId, List<Color> colors) {
        String sql = "INSERT INTO colors (product_id, name, code) VALUES (?, ?, ?)";
        for (Color color : colors) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, productId);
                ps.setString(2, color.getName());
                ps.setString(3, color.getCode());
                return ps;
            });
        }
    }

    private void saveSizes(Long productId, List<String> sizes) {
        String sql = "INSERT INTO sizes (product_id, size) VALUES (?, ?)";
        for (String size : sizes) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, productId);
                ps.setString(2, size);
                return ps;
            });
        }
    }

    private void saveVariants(Long productId, List<Variant> variants) {
        String sql = "INSERT INTO variants (product_id, color_id, size_id, stock) VALUES (?, (SELECT id FROM colors WHERE product_id = ? AND name = ?), (SELECT id FROM sizes WHERE product_id = ? AND size = ?), ?)";
        for (Variant variant : variants) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, productId);
                ps.setLong(2, productId);
                ps.setString(3, variant.getColor());
                ps.setLong(4, productId);
                ps.setString(5, variant.getSize());
                ps.setInt(6, variant.getStock());
                return ps;
            });
        }
    }
}