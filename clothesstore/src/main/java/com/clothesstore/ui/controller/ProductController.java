package com.clothesstore.ui.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clothesstore.dto.ProductDto;
import com.clothesstore.service.IProductService;
import com.clothesstore.ui.model.request.ProductRequest;
import com.clothesstore.ui.model.response.ErrorMessages;
import com.clothesstore.ui.model.response.OperationStatusModel;
import com.clothesstore.ui.model.response.ProductResponse;
import com.clothesstore.ui.model.response.RequestOperationName;
import com.clothesstore.ui.model.response.RequestOperationStatus;

@RestController
@RequestMapping("api/products")
public class ProductController {

	@Autowired
	IProductService productService;

	@GetMapping(path = "/most-searched")
	public List<ProductResponse> getMostSearched() throws Exception {

		List<ProductDto> productDtoList = productService.getMostSearched();
		ModelMapper modelMapper = new ModelMapper();
		List<ProductResponse> productResponse = productDtoList.stream().map(p -> {
			return modelMapper.map(p, ProductResponse.class);
		}).collect(Collectors.toList());

		return productResponse;
	}

	@GetMapping
	public List<ProductResponse> getProductsByName(@RequestBody ProductRequest productRequest,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit) throws Exception {

		if (productRequest.getName() == null || productRequest.getName().equals(""))
			throw new Exception(ErrorMessages.PRODUCT_NAME_NEEDED.getErrorMessage());

		List<ProductDto> productDtoList = productService.getProductsByName(productRequest.getName(), page, limit);
		ModelMapper modelMapper = new ModelMapper();
		List<ProductResponse> productResponse = productDtoList.stream().map(p -> {
			return modelMapper.map(p, ProductResponse.class);
		}).collect(Collectors.toList());

		return productResponse;

	}

	@GetMapping(path = "/{id}")
	public ProductResponse getProduct(@PathVariable String id) throws Exception {

		ProductDto productDto = productService.getProductByProductId(id);
		ModelMapper modelMapper = new ModelMapper();

		return modelMapper.map(productDto, ProductResponse.class);
	}

	@PostMapping
	public ProductResponse createProduct(@RequestBody ProductRequest productRequest) throws Exception {

		ModelMapper modelMapper = new ModelMapper();
		ProductDto productDto = modelMapper.map(productRequest, ProductDto.class);
		productDto.calculateSalePrice();

		ProductDto productCreated = productService.createProduct(productDto);

		return modelMapper.map(productCreated, ProductResponse.class);
	}

	@PutMapping(path = "/{id}")
	public ProductResponse updateProduct(@PathVariable String id, @RequestBody ProductRequest productRequest)
			throws Exception {

		ProductResponse productResponse = new ProductResponse();

		ProductDto productDto = new ProductDto();
		BeanUtils.copyProperties(productRequest, productDto);

		ProductDto productCreated = productService.updateProduct(id, productDto);
		BeanUtils.copyProperties(productCreated, productResponse);

		return productResponse;
	}

	@DeleteMapping(path = "/{id}")
	public OperationStatusModel deleteProduct(@PathVariable String id) throws Exception {

		productService.deleteProduct(id);

		OperationStatusModel returnValue = new OperationStatusModel(RequestOperationName.DELETE.name(),
				RequestOperationStatus.SUCCESS.name());

		return returnValue;
	}
}
