package gift.service;

import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Option;
import gift.entity.Product;
import gift.entity.vo.Money;
import gift.entity.vo.ProductName;
import gift.exception.ProductNotFoundException;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse addProductWithOptions(ProductRequest request) {
        Product product = new Product(
                new ProductName(request.name()),
                new Money(request.price()),
                request.imageUrl()
        );

        List<Option> options = request.options().stream()
                .map(optionRequest -> new Option(optionRequest.name(), optionRequest.quantity()))
                .collect(Collectors.toList());

        product.setOptions(options);

        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse updateProductWithOptions(Long id, ProductRequest request) {
        Product product = findProductById(id);

        product.update(
                new ProductName(request.name()),
                new Money(request.price()),
                request.imageUrl()
        );

        List<Option> newOptions = request.options().stream()
                .map(optionRequest -> new Option(optionRequest.name(), optionRequest.quantity()))
                .collect(Collectors.toList());

        product.setOptions(newOptions);

        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllProducts(Pageable pageable) {
        Page<Product> productsPage = productRepository.findAll(pageable);
        return productsPage.map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductResponseById(Long id) {
        return ProductResponse.from(findProductById(id));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("삭제하려는 상품을 찾을 수 없습니다: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public void deleteProducts(List<Long> ids) {
        productRepository.deleteAllById(ids);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("해당 ID의 상품을 찾을 수 없습니다: " + id));
    }
}
