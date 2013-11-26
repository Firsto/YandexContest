 var BaseUrl = "";

 function setbaseUrl(){
     BaseUrl = $('base').attr("href");
 }

 function getLoc(loc){
     return BaseUrl + loc;
 }

 function loc(loc){
     return document.location.href = getLoc(loc);
 }

 $(document).ready(function() {
            var ctrlKey = "Ctrl";
        $.Shortcuts.add({
        type: 'down',
        mask: ctrlKey + '+G',
        handler: function() {
            name = prompt("Введите название группы или ее ID");
            if(name)
                document.location = "http://www.e-olimp.com/groups-go/search:" + name;
        }
        });
        $.Shortcuts.add({
	    type: 'down',
	    mask: ctrlKey + '+Q',
	    handler: function() {
	        id = prompt("Введите номер задачи");
	        if(parseInt(id))
		        document.location = "http://www.e-olimp.com/problems/" + id;
	    }
        });
        $.Shortcuts.add({
	    type: 'down',
	    mask: ctrlKey + '+S',
	    handler: function() {
	        if($("a#send-problem-solution").length) {
		    if($("a#send-problem-solution").attr("href") != "javascript: void(0)")
		        document.location = $("a#send-problem-solution").attr("href");
		    else
		        $("a#send-problem-solution").click();
	        } else {
		    document.location = "http://www.e-olimp.com/solutions-send";
	        }
	    }
        });
        $.Shortcuts.add({
	    type: 'down',
	    mask: ctrlKey + '+M',
	    handler: function() {
    	        if($("a#send-user-message").length) {
		    if($("a#send-user-message").attr("href") != "javascript: void(0)")
		        document.location = $("a#send-user-message").attr("href");
		    else
	    	        $("a#send-user-message").click();
	        } else {
		    document.location = "http://www.e-olimp.com/message/";
	        }
	    }
        });
        $.Shortcuts.add({
	    type: 'down',
	    mask: ctrlKey + '+O',
	    handler: function() {
	        document.location = "http://www.e-olimp.com/competitions/";
	    }
        });
        $.Shortcuts.add({
	    type: 'down',
	    mask: ctrlKey + '+P',
	    handler: function() {
	        document.location = "http://www.e-olimp.com/problems/";
	    }
        });
        $.Shortcuts.add({
	    type: 'down',
	    mask: ctrlKey + '+F',
	    handler: function() {
	        if($("div.search input.text, #search-line").length) {
		    $("div.search input.text, #search-line").focus();
	        } else {
		    search = prompt("Введите текст для поиска");
		    if(search)
		        qSearch(search);
	        }
	    }
        });
                $.Shortcuts.start();
        $("a[title^='Shortcut']").addClass("shortcut");
    


    $(".search input.text").bind("keypress", function(e){
        if(e.keyCode == 13) qSearch();
    });

     setbaseUrl();
     $("#content input[type='submit']").css('font-weight', 'bold');

     /*  */

     proccessATag();

     /*  */
     /*  */
     /*  */
     /*  */
 });

 function proccessATag() {
     $("a[confirm]").each( function() {
         var hLocation = $(this).attr('href');
         if(hLocation != 'javascript: void(0)') {
             $(this).attr('href', 'javascript: void(0)');
             $(this).click(function() {
                   if(confirm($(this).attr('confirm'))) location.href = hLocation;
             });
         }
     });
 }

 function bindLinks() {
     $("#content input[type='submit']").css('font-weight', 'bold');
     $("#content a.page, #content a.toajax, #content input.toajax[type='button'][href]").each(function() {
         if($(this).hasClass("bind-success")) return true;
         var t = this;
         t.ajaxLocation = $(t).attr('href');
         if(!/ajax$/.test(t.ajaxLocation)) t.ajaxLocation = t.ajaxLocation + 'ajax';
         $(t).attr('href', 'javascript: void(0)');
         $(t).addClass('bind-success');
         $(t).click(function() {
             if($("#ajaxprocess").css('display') != 'none') return;
             $("#ajaxprocess").show();
             /*  */
                 $.get(t.ajaxLocation, function(data) {
                    al = new String(document.location);
                    al = al.replace(/#(.*)$/, "");
                    document.location = al + "#ajax:" + t.ajaxLocation.replace(/ajax$/, "").replace(/^http:\/\/www.e-olimp.com/, "");
                    $("#content").css('opacity', '1');
                    $("#ajaxprocess").hide();
                    $("#content").html(data);
                    $(".tabbox .tab.current").each(function() {
                        this.tabLocation = t.ajaxLocation;
                    });
                    bindLinks();
                    proccessATag();
                 });
         });
     });
 }

    function qSearch(search_text) {
	if(typeof search_text == 'undefined') search_text = $(".search input.text").val();
        document.location = "problems/search:" + search_text;
    }
    
    $(function() {
	var ponetimer = setTimeout(function() {
	    $(".google-plus-one").fadeOut("slow");
	}, 20000);
	$(".header").hover(function() {
	    clearTimeout(ponetimer)
	    $(".google-plus-one").stop(true, true).fadeIn("fast");
	}, function() {
	    clearTimeout(ponetimer)
	    $(".google-plus-one").delay(6000).fadeOut("fast");
	});
    });