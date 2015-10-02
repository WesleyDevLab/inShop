package com.akartkam.inShop.controller.admin.product;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.akartkam.inShop.domain.product.Brand;
import com.akartkam.inShop.domain.product.Category;
import com.akartkam.inShop.domain.product.Product;
import com.akartkam.inShop.domain.product.ProductStatus;
import com.akartkam.inShop.domain.product.Sku;
import com.akartkam.inShop.domain.product.attribute.AbstractAttribute;
import com.akartkam.inShop.domain.product.attribute.AbstractAttributeValue;
import com.akartkam.inShop.domain.product.attribute.AttributeDecimalValue;
import com.akartkam.inShop.domain.product.attribute.AttributeType;
import com.akartkam.inShop.domain.product.attribute.SimpleAttributeFactory;
import com.akartkam.inShop.domain.product.option.ProductOption;
import com.akartkam.inShop.domain.product.option.ProductOptionValue;
import com.akartkam.inShop.service.product.AttributeCategoryService;
import com.akartkam.inShop.service.product.BrandService;
import com.akartkam.inShop.service.product.CategoryService;
import com.akartkam.inShop.service.product.ProductService;
import com.akartkam.inShop.util.ImageUtil;
import com.akartkam.inShop.exception.ImageUploadException;
import com.akartkam.inShop.exception.ProductNotFoundException;
import com.akartkam.inShop.formbean.ProductForm;

@Controller
@RequestMapping("/admin/catalog/product")
public class AdminProductController {
	
	  	
	  @Autowired
	  ProductService productService;
	  
	  @Autowired
	  CategoryService categoryService;
	  
	  @Autowired
	  BrandService brandService;
	  
	  @Autowired
	  AttributeCategoryService attributeCategoryService;

	  @Autowired
	  private MessageSource messageSource;
	  
	  @Autowired(required=false)
	  private ImageUtil imageUtil;

	  
	  @Value("#{appProperties['inShop.imagesPath']}")
	  private String imagePath;
	  
	  @Value("#{appProperties['inShop.imagesUrl']}")
	  private String imageUrl;
	  
	  
	  @ModelAttribute("allProduct")
	  public List<Product> getAllProduct() {
	      return productService.getAllProduct();
	  }
	  
	  @ModelAttribute("allCategory")
	  public List<Category> getAllCategory() {
	      return categoryService.getAllCategoryHierarchy(true);
	  }	  
	  
	  
	  @ModelAttribute("allBrand")
	  public List<Brand> getAllBrand() {
	      return brandService.getAllBrand(false);
	  }	
	  
	  @ModelAttribute("allPo")
	  public List<ProductOption> getAllPO() {
	      return productService.getAllPO();
	  }	  
	  
	  @ModelAttribute("allProdStatus")
	  public List<ProductStatus> getAllProdStatus() {
	      return Arrays.asList(ProductStatus.ALL);
	  }	  
	  
	  @InitBinder
	  public void initBinder(WebDataBinder binder) {
			binder.setAllowedFields(new String[] { "*id", "*name", "url", "*description", "*longDescription", 
					 							   "*code", "category", "brand", "*model", "attributeValues*", "ordering", 
					 							   "*productOptions", "canSellWithoutOptions", "*images*", "enabled",
					 							   "*retailPrice", "*salePrice", "*costPrice", "*value", "*productStatus*"});
			//binder.setAutoGrowNestedPaths(false);
			binder.registerCustomEditor(UUID.class, "id", new PropertyEditorSupport() {
			    @Override
			    public void setAsText(String text) {
			    	 setValue(UUID.fromString(text));
			    }
			    });
			
			binder.registerCustomEditor(Category.class, "category", new PropertyEditorSupport() {
			    @Override
			    public void setAsText(String text) {
			    	if (!"".equals(text)) {
			    		Category ch = categoryService.loadCategoryById(UUID.fromString(text), false);
			            setValue(ch);
			    	}			    
			    }
			    });

			binder.registerCustomEditor(Brand.class, "brand", new PropertyEditorSupport() {
			    @Override
			    public void setAsText(String text) {
			    	if (!"".equals(text)) {
			    		Brand ch = brandService.loadBrandById(UUID.fromString(text), false);
			            setValue(ch);
			    	}			    
			    }
			    });
			binder.registerCustomEditor(ProductStatus.class,"defaultSku.productStatus", new PropertyEditorSupport() {
			    @Override
			    public void setAsText(String text) {
			    	if (!"".equals(text)) {
			    		ProductStatus p = ProductStatus.forName(text); 
			            setValue(p);
			    	}			    
			    }
			    });
		
	  }
	  

	  
	  @RequestMapping(method=GET)
	  public String product() {
		  return "/admin/catalog/product"; 
		  }	  
	  
	  
	  @SuppressWarnings("rawtypes")
	  @RequestMapping("/edit")
	  public String productEdit(@RequestParam(value = "ID", required = false) String ID, Model model,
			   				  @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		  if(!model.containsAttribute("product")) {
			  if ("".equals(ID)) throw new ProductNotFoundException("ID Product is empty.");
		      Product product = productService.getProductById(UUID.fromString(ID)); 
		      ProductForm productForm = new ProductForm(product);
		      productForm.complementNecessaryAttributes();
			  model.addAttribute("product", productForm);
		  }
          if ("XMLHttpRequest".equals(requestedWith)) {
              return "/admin/catalog/productEdit :: editProductForm";
            }		  
          return "/admin/catalog/productEdit";		  
		  }	  
	  
	  @RequestMapping("/add")
	  public String productAdd(@RequestParam(value = "ID", required = false) String copyID, Model model,
				                @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) throws CloneNotSupportedException {
		  Product product = null;
		  ProductForm productForm = null;
		  if (copyID != null && !"".equals(copyID)) {
			  product = productService.cloneProductById(UUID.fromString(copyID));
			  if (product != null) productForm = new ProductForm(product); 
		  }
		  if (productForm == null) {
			  productForm = new ProductForm();
			  productForm.setDefaultSku(new Sku());
			  
		  }		  
 	      model.addAttribute("product", productForm);
          if ("XMLHttpRequest".equals(requestedWith)) {
              return "/admin/catalog/productEdit :: editProductForm";
            } 	      
          return "/admin/catalog/productEdit";		  
		  }		  

	  @RequestMapping(value="/delete", method = RequestMethod.POST)
	  public String productDelete(@RequestParam(value = "ID", required = false) String ID, 
			                       @RequestParam(value = "phisycalDelete", required = false) Boolean phisycalDelete,
				                   final RedirectAttributes ra) {
		  Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		  if (phisycalDelete != null && phisycalDelete)  {
			  Product product = productService.loadProductById(UUID.fromString(ID), false);
			  if(product.canRemove() && authorities.contains(new SimpleGrantedAuthority("ADMIN"))) {
				  productService.deleteProduct(product);   
			  } else {
				  ra.addFlashAttribute("errormessage", this.messageSource.getMessage("admin.error.cannotdelete.message", new String[] {"Товар"} , null));
				  ra.addAttribute("error", true);
			  }

		  } else {
			  productService.softDeleteProductById(UUID.fromString(ID));
		  }
          return "redirect:/admin/catalog/product";		  
		  }
	
	   @RequestMapping(value="/edit", method = RequestMethod.POST )
	   public String saveProduct(
			                   @Valid ProductForm product,
				   			   final BindingResult bindingResult,
			                   final RedirectAttributes ra
			                         ) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		   if (bindingResult.hasErrors()) {
			    product.complementNecessaryAttributes();
	        	product.setAttributeValuesFromMap();
	        	ra.addFlashAttribute("product", product);
	        	ra.addFlashAttribute("org.springframework.validation.BindingResult.product", bindingResult);
	            return "redirect:/admin/catalog/product/edit";
	        }

	        productService.mergeWithExistingAndUpdateOrCreate(product);	       
	        return "redirect:/admin/catalog/product";
	    }
	   
	   
	   @RequestMapping(value="/image/add", method = RequestMethod.POST )
	   public String addNewImage(final @ModelAttribute Product product,
			   				   @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			   				   @RequestParam(value = "newImage", required = false)	MultipartFile image,
			                   final BindingResult bindingResult,
			                   final Model model
			                         ) throws CloneNotSupportedException {
		   if (!"XMLHttpRequest".equals(requestedWith)) throw new IllegalStateException("The addNewImage method can be called only via ajax!");
	       String fileName="";
	       String filePath="";
	       imageUtil.validateImage(image, "images", bindingResult);
	       if(!bindingResult.hasErrors()) {
		        fileName = new File(image.getOriginalFilename()).getName(); 
		        filePath = imagePath + fileName;
	        	imageUtil.saveImage(filePath, image);	
	        	product.getDefaultSku().getImages().add(imageUrl+fileName);
	       }
		   model.addAttribute("product", product);
		   model.addAttribute("tabactive","images");
	       return "/admin/catalog/productEdit :: editProductForm";
	    }
	      
}
