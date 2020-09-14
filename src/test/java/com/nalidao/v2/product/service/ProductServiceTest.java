package com.nalidao.v2.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nalidao.v2.product.domain.Product;
import com.nalidao.v2.product.domain.dto.ProductDto;
import com.nalidao.v2.product.errorhandling.exception.ProductNotFoundException;
import com.nalidao.v2.product.gateway.ProductGateway;
import com.nalidao.v2.product.utils.ConvertProductEntityToDto;
import com.nalidao.v2.product.utils.TestUtils;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductGateway gateway;
	@Mock
	private ConvertProductEntityToDto convertEntityToDto;
	
	private TestUtils utils = new TestUtils();
	
	@Test
	public void findAllProductList() {
		List<Product> list = this.utils.getProductList();
		List<ProductDto> dtoList = this.utils.getProductDtoList();
		when(this.gateway.findAll()).thenReturn(list);
		when(this.convertEntityToDto.convertList(list)).thenReturn(dtoList);
		
		List<ProductDto> foundList = this.service.findall();
		
		assertThat(foundList).isNotNull().isNotEmpty();
		assertThat(foundList.size()).isEqualTo(dtoList.size());
		assertThat(foundList).isSameAs(dtoList);
	}
	
	@Test
	public void testFindProductById() {
		Product prod = this.utils.getProduct();
		long id = 1L;
		when(this.gateway.findProductById(id)).thenReturn(Optional.of(prod));
		
		Product foundProd = this.service.getProductById(id).get();
		
		assertThat(foundProd).isNotNull();
		assertThat(foundProd).isEqualTo(prod);
	}
	
	@Test
	public void testFindProductByIdThrowProductNotFoundException() {
		long id = 2l;
		Throwable thrown = ThrowableAssert.catchThrowable(() -> {
			this.service.getProductById(id);
		});
		
		assertThat(thrown).isInstanceOf(ProductNotFoundException.class).hasMessage("Produto com id " + id + " não encontrado na base de dados");
	}
	
	@Test
	public void testCreateProduct() {
		Product prod = this.utils.getProduct();
		when(this.gateway.saveProduct(prod)).thenReturn(prod);
		
		Product createdProd = this.service.createProduct(prod);
		
		assertThat(createdProd).isNotNull();
		assertThat(createdProd).isEqualTo(prod);
	}
	
	@Test
	public void testUpdateProduct() {
		Product prod = this.utils.getProduct();
		Product updatedProduct = new Product(prod.getId(), prod.getName(), prod.getPrice(), prod.getAmount());
		updatedProduct.setName("updated name");
		
		when(this.gateway.findProductById(prod.getId())).thenReturn(Optional.of(prod));
		when(this.gateway.saveProduct(updatedProduct)).thenReturn(updatedProduct);
		
		Product finalProduct = this.service.updateProduct(updatedProduct);
		
		assertThat(finalProduct).isNotNull();
		assertThat(finalProduct).isEqualTo(updatedProduct);
		assertThat(finalProduct.getName()).isNotSameAs(prod.getName());
	}
	
	@Test
	public void testUpdateThrowsProductNotFoundException() {
		Product prod = this.utils.getProduct();
		Throwable thrown = ThrowableAssert.catchThrowable(() -> {
			this.service.updateProduct(prod);
		});
		
		assertThat(thrown).isInstanceOf(ProductNotFoundException.class).hasMessage("Id " + prod.getId() + " não encontrado na base de dados, para atualização de produto.");
		
	}
	
	@Test
	public void testRemoveProduct() {
		Product prod = this.utils.getProduct();
		
		when(this.gateway.findProductById(prod.getId())).thenReturn(Optional.of(prod));
		
		this.service.removeProduct(prod.getId());
		
		Mockito.verify(this.gateway).removeProduct(prod.getId());
	}
	
	@Test
	public void testRemoveThrowsProductnotFoundException() {
		Product prod = this.utils.getProduct();
		Assertions.assertThatExceptionOfType(ProductNotFoundException.class).isThrownBy(() -> {
			this.service.removeProduct(prod.getId());
		}).withMessage("Id " + prod.getId() + " não encontrado. Não é possível efetuar a remoção deste produto.");
		
	}
}
