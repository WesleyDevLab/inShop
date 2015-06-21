package com.akartkam.inShop.controller.admin.product;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.akartkam.inShop.domain.product.Brand;
import com.akartkam.inShop.service.product.BrandService;
import com.akartkam.inShop.exception.ImageUploadException;

@Controller
@RequestMapping("/admin/catalog/brand")
public class AdminBrandController {
	
	  @Autowired
	  BrandService brandService;
	  
	  @Autowired
	  private MessageSource messageSource;

	  
	  @Value("#{appProperties['inShop.imagesPath']}")
	  private String imagePath;
	  
	  @SuppressWarnings("rawtypes")
	  @ModelAttribute("allBrand")
	  public List getAllBrand() {
	      return brandService.getAllBrand();
	  }
	  
	  @InitBinder
	  public void initBinder(WebDataBinder binder) {
			binder.setAllowedFields(new String[] { "id", "name", "url", "description", "logoUrl", "enabled"});
			binder.registerCustomEditor(UUID.class, "id", new PropertyEditorSupport() {
			    @Override
			    public void setAsText(String text) {
			    	 setValue(UUID.fromString(text));
			    }
			    });
			
			binder.registerCustomEditor(String.class, "logoUrl", new PropertyEditorSupport() {
			    @Override
			    public void setAsText(String text) {
			    	if ("".equals(text) || "''".equals(text))
			    	  setValue(null);
			    	else
			    	  setValue(text);	
			    }
			    });
			
			
	  }
	  
	  @RequestMapping(method=GET)
	  public String brand(Model model) {
		  return "/admin/catalog/brand"; 
		  }	  
	  
	  @RequestMapping("/edit")
	  public String brandEdit(@RequestParam(value = "ID", required = false) String categoryID, Model model,
			   				  @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
		  if(!model.containsAttribute("brand")) {
			 Brand brand = brandService.getBrandById(UUID.fromString(categoryID));
		     model.addAttribute("brand", brand);
		  }
          if ("XMLHttpRequest".equals(requestedWith)) {
              return "/admin/catalog/brandEdit :: editBrandForm";
            }		  
          return "/admin/catalog/brandEdit";		  
		  }	  
	  
	  @RequestMapping("/add")
	  public String brandAdd(@RequestParam(value = "ID", required = false) String copyID, Model model,
				                @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) throws CloneNotSupportedException {
		  Brand brand = null;
		  if (copyID != null && !"".equals(copyID)) brand = brandService.cloneBrandById(UUID.fromString(copyID)); 
		  else brand = new Brand();
 	      model.addAttribute("brand", brand);
          if ("XMLHttpRequest".equals(requestedWith)) {
              return "/admin/catalog/brandEdit :: editBrandForm";
            } 	      
          return "/admin/catalog/brandEdit";		  
		  }		  

	  @RequestMapping(value="/delete", method = RequestMethod.POST)
	  public String brandDelete(@RequestParam(value = "ID", required = false) String ID, 
			                       @RequestParam(value = "phisycalDelete", required = false) Boolean phisycalDelete,
				                   final RedirectAttributes ra) {
		  Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		  if (phisycalDelete != null && phisycalDelete)  {
			  Brand brand = brandService.loadBrandById(UUID.fromString(ID), false);
			  if(brand.canRemove() && authorities.contains(new SimpleGrantedAuthority("ADMIN"))) {
				  brandService.deleteBrand(brand);   
			  } else {
				  ra.addFlashAttribute("errormessage", this.messageSource.getMessage("admin.error.cannotdelete.message", new String[] {"�����"} , null));
				  ra.addAttribute("error", true);
			  }

		  } else {
			  brandService.softDeleteBrandById(UUID.fromString(ID));
		  }
          return "redirect:/admin/catalog/brand";		  
		  }
	  
	   @RequestMapping(value="/edit", method = RequestMethod.POST )
	   public String saveBrand(@ModelAttribute @Valid Brand brand,
			                   final BindingResult bindingResult,
			                   @RequestParam(value = "mainLogo", required = false)	MultipartFile image,
			                   final RedirectAttributes ra
			                         ) {
	        if (bindingResult.hasErrors()) {
	        	ra.addFlashAttribute("brand", brand);
	        	ra.addFlashAttribute("org.springframework.validation.BindingResult.brand", bindingResult);
	            return "redirect:/admin/catalog/brand/edit";
	        }
	        String fileName="";
	        String filePath="";
	        if(!image.isEmpty()) {
		        fileName = new File(image.getOriginalFilename()).getName(); 
		        filePath = imagePath + "\\" + fileName;
		        brand.setLogoUrl("/images/"+fileName);
	        }
	        
 
	        brandService.mergeWithExistingAndUpdateOrCreate(brand);
	       
	        if(!image.isEmpty()) {
		        
	        	validateImage(image); // ��������� �����������
	        	saveImage(filePath, image); // ��������� ����
	       	}	        
	        
	        return "redirect:/admin/catalog/brand";
	    }
	   
		private void validateImage(MultipartFile image) {
			String allowedFileType = "image/jpeg,image/png,image/gif";
			if (allowedFileType.indexOf(image.getContentType()) < 0) {
				throw new ImageUploadException("Only jpeg, png, gif images accepted");
			}
		}
		
		private void saveImage(String filePath, MultipartFile image)
				throws ImageUploadException {
			try {
				
				File file = new File(filePath);
				FileUtils.writeByteArrayToFile(file, image.getBytes());
			} catch (IOException e) {
				throw new ImageUploadException("Unable to save image", e);
			}
		}	   

}
