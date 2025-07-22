package gift.controller;

import gift.dto.OptionRequest;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String showProductList(Model model,
                                  @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductResponse> productsPage = productService.findAllProducts(pageable);
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("productRequest", new ProductRequest("", 0, "", new ArrayList<>()));
        return "admin/product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("productRequest", new ProductRequest("", 0, "", new ArrayList<>()));
        return "admin/product-form";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("productRequest") ProductRequest productRequest,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            Page<ProductResponse> productsPage = productService.findAllProducts(Pageable.ofSize(10));
            model.addAttribute("productsPage", productsPage);
            return "admin/product-list";
        }
        productService.addProductWithOptions(productRequest);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        ProductResponse product = productService.findProductResponseById(id);
        List<OptionRequest> optionRequests = product.options().stream()
                .map(opt -> new OptionRequest(opt.name(), opt.quantity()))
                .collect(Collectors.toList());
        model.addAttribute("productRequest", new ProductRequest(product.name(), product.price(), product.imageUrl(), optionRequests));
        model.addAttribute("productId", id);
        return "admin/product-edit-form";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("productRequest") ProductRequest productRequest,
                              BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "admin/product-edit-form";
        }
        productService.updateProductWithOptions(id, productRequest);
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/delete")
    public String deleteSelectedProducts(@RequestParam(value = "productIds", required = false) List<Long> productIds) {
        if (productIds != null && !productIds.isEmpty()) {
            productService.deleteProducts(productIds);
        }
        return "redirect:/admin/products";
    }
}
