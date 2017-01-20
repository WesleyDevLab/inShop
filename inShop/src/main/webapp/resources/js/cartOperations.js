 //<![CDATA[
$(function(){
	
    var modalCartOptions = {
            maxWidth    : 750,
            minHeight   : 100,
            maxHeight   : 500
        };

    var modalProductOptions = {
            maxWidth    : 600,
            minWidth 	: 300,
            minHeight   : 300,
            maxHeight	: 600
        };
	
	function updateHeaderCartItems(newCount, newTotal) {
		$(".cart-amunt").html(newTotal);
		$(".product-count").html(newCount);
	}
	
	$("body").on("click", "a.modalcart", function() {
		$.ajax({
			type: "GET",
			url: $(this).attr("href")	
		}).done(function (data){
      	   $.modal(data, modalCartOptions);
      	   $("#simplemodal-container").css("height", "auto");
    	   $.modal.update();			
		});
		return false;
	});
	
	
    $("body").on("click", ".add-to-cart", function() {
    	var modalClick = $(this).parents('.simplemodal-wrap').length > 0;
    	var $form = $(this).closest("form");
    	var $url = $form.attr("action");
    	var $hasProductOptions = $form.find("[name='hasProductOptions']") 
    	if ($hasProductOptions.length) {
    		$hasProductOptions = ($hasProductOptions.val() == "true" )
    	} else {
    		$hasProductOptions = false;
    	}
    	var token = $form.find("input[name=_csrf]");
    	if ($form.length) $form = $form.serialize();
    	if (token.length) token = token.val();
    	var header = $('#_csrf_header').attr('content');
    	if($hasProductOptions) {
    		 $.ajax({
    	            url: $url,
    	            type: "GET",
    	            cache: false,
    	            beforeSend: function(xhr) {
    	                xhr.setRequestHeader(header, token);
    	            }           
    	          }).done(function (data){
    	        	  if (modalClick){
    	        		  $.modal.close();
    	        	  }
    	        	  $.modal(data, modalProductOptions); 
    	          	  $("#simplemodal-container").css("height", "auto");
    	        	  $.modal.update();    	        	  
    	          });
    		 
    	} else {
    	  $.ajax({
            url: $url,
            type: "POST",
            data: $form,
            cache: false,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            }           
          }).done(function (data){
        	  if (modalClick) $.modal.close();
        	  var ajaxExtraData = getExtraData($(data));
        	  updateHeaderCartItems(ajaxExtraData.cartItemCount, ajaxExtraData.cartTotal);
        	  $.modal(data, modalCartOptions);
          	  $("#simplemodal-container").css("height", "auto");
        	  $.modal.update();        	  
          });
    	};
    	return false;
    });
    
    $("body").on("click", ".remove-from-cart, .recalc-cart", function(){
    	var modalClick = $(this).parents('.simplemodal-wrap').length > 0;
    	var $form = $(this).closest("form");
    	var $url = $form.attr("action");
    	var token = $form.find("input[name=_csrf]");
    	if ($form.length) $form = $form.serialize();
    	if (token.length) token = token.val();
    	var header = $('#_csrf_header').attr('content');
    	$.ajax({
            url: $url,
            type: "POST",
            data: $form,
            cache: false,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            }           
          }).done(function (data){
        	  var ajaxExtraData = getExtraData($(data));
        	  updateHeaderCartItems(ajaxExtraData.cartItemCount, ajaxExtraData.cartTotal);
        	  if (modalClick) {
            	  $("#simplemodal-data").html(data);
        	  } else {
            	  $.modal(data, modalCartOptions);        		  
        	  }	
          	  $("#simplemodal-container").css("height", "auto");
        	  $.modal.update();        	  
          });   	
    });
    

	
});
//]]>