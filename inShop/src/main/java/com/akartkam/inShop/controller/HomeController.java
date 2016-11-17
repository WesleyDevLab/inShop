package com.akartkam.inShop.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.akartkam.inShop.domain.product.Brand;
import com.akartkam.inShop.domain.product.Category;
import com.akartkam.inShop.domain.product.Product;
import com.akartkam.inShop.domain.product.ProductStatus;
import com.akartkam.inShop.service.product.BrandService;
import com.akartkam.inShop.service.product.CategoryService;
import com.akartkam.inShop.service.product.ProductService;


@Controller
public class HomeController extends AbstractController {
	
	private static final Log LOG = LogFactory.getLog(HomeController.class);

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;	
	
	@Autowired
	private BrandService brandService;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Category> rootCategorys = categoryService.getRootCategories(false);
		List<Product> newProducts = productService.getProductsByProductStatus(ProductStatus.NEW);
		List<Product> actionProducts = productService.getProductsByProductStatus(ProductStatus.ACTION);
		List<Brand> brands = brandService.getAllBrand(false);
		ModelAndView model = new ModelAndView("/layouts/home");
		model.addObject("rootCategorys", rootCategorys);
		model.addObject("newProducts", newProducts);
		model.addObject("actionProducts", actionProducts);
		model.addObject("brands", brands);
		return model;
	}

}