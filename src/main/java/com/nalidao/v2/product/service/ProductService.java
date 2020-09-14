package com.nalidao.v2.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nalidao.v2.product.domain.Product;
import com.nalidao.v2.product.domain.dto.ProductDto;
import com.nalidao.v2.product.errorhandling.exception.ProductNotFoundException;
import com.nalidao.v2.product.gateway.ProductGateway;
import com.nalidao.v2.product.utils.ConvertProductEntityToDto;


/**
 * Classe de serviço da api de produto
 * @author paulo
 */
@Service
public class ProductService {

	@Autowired
	private ProductGateway gateway;
	
	@Autowired
	private ConvertProductEntityToDto productToDtoConverter;

	public Optional<Product> getProductById(long id) {
		Optional<Product> product = this.gateway.findProductById(id);
		if(product.isPresent()) {
			return this.gateway.findProductById(id);
		}
		
		throw new ProductNotFoundException("Produto com id " + id + " não encontrado na base de dados");
	}

	public List<ProductDto> findall() {
		return this.productToDtoConverter.convertList(this.gateway.findAll());
	}

	public Product createProduct(Product entity) {
		Product product = this.gateway.saveProduct(entity);
		return product;
	}

	public Product updateProduct(Product product) {
		Optional<Product> entity = this.gateway.findProductById(product.getId());
		if(entity.isPresent()) {
			return this.gateway.saveProduct(product);
		}
		
		throw new ProductNotFoundException("Id " + product.getId() + " não encontrado na base de dados, para atualização de produto.");
	}

	public void removeProduct(Long id) {
		Optional<Product> product = this.gateway.findProductById(id);
		if(product.isPresent()) {
			this.gateway.removeProduct(id);
		} else {
			throw new ProductNotFoundException("Id " + id + " não encontrado. Não é possível efetuar a remoção deste produto.");
		}
	}

}
