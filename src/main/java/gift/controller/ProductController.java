package gift.controller;

import gift.dto.ErrorResponse;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Member;
import gift.resolver.LoginMember;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@LoginMember Member member,
                                        @Valid @RequestBody ProductRequest request,
                                        BindingResult bindingResult) {
        log.info("상품 추가 요청 - 사용자: {}", member.getEmail());

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ErrorResponse(errorMsg));
        }

        ProductResponse response = productService.addProductWithOptions(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@LoginMember Member member,
                                           @PathVariable Long id,
                                           @Valid @RequestBody ProductRequest request,
                                           BindingResult bindingResult) {
        log.info("상품 수정 요청 - 사용자: {}, 상품 ID: {}", member.getEmail(), id);

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ErrorResponse(errorMsg));
        }

        ProductResponse response = productService.updateProductWithOptions(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAllProducts(@LoginMember Member member, Pageable pageable) {
        log.info("전체 상품 조회 요청 - 사용자: {}", member.getEmail());
        Page<ProductResponse> productPage = productService.findAllProducts(pageable);
        return ResponseEntity.ok(productPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@LoginMember Member member, @PathVariable Long id) {
        log.info("특정 상품 조회 요청 - 사용자: {}, 상품 ID: {}", member.getEmail(), id);
        ProductResponse productResponse = productService.findProductResponseById(id);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@LoginMember Member member, @PathVariable Long id) {
        log.info("상품 삭제 요청 - 사용자: {}, 상품 ID: {}", member.getEmail(), id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
