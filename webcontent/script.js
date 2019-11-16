/**
 * Javascript functions.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 */

//To get form inputs as JSON.
(function ($) {
	$.fn.serializeFormJSON = function () {

		var o = {};
		var a = this.serializeArray();
		$.each(a, function () {
			if (o[this.name]) {
				if (!o[this.name].push) {
					o[this.name] = [o[this.name]];
				}
				o[this.name].push(this.value || '');
			} else {
				o[this.name] = this.value || '';
			}
		});
		return o;
	};
})(jQuery);




$( function() { //Same as $(document).ready(function() {

	function fetchBidirResults(text,terms){
		console.log(terms);
		if(terms.length>1 && terms[terms.length-1].length>0){
			$('#relaxdiv').hide();
			$("#relaxdiv").html("");
			$("#result").html("");
			$('#loading-indicator').fadeIn();
			text=text.trim();
			var formData = {query:text};
			$.post("lmclusteredKS.jsp",formData,function(data,status){
				//console.log("Success!"+data+" Status:"+status);
				$("#result").html($.trim(data));
			})
			.fail(function(xhr, status, error) {
				//console.log("Failed with:"+e.status);
				$("#result").html("Error occurred!"+xhr.responseText);
			})
			.always(function() {
				$('#loading-indicator').fadeOut();
			});
		}
	};
//	$("#result").html("Will show LM clustered results using BANKS. Coming soon!");
//	var formData = $('#inputform').serializeFormJSON();
//	};

	function fetchResults(text,terms){
		if(terms.length>1 && terms[terms.length-1].length>0){
			$('#relaxdiv').hide();
			$("#relaxdiv").html("");
			$("#result").html("");
			$('#loading-indicator').fadeIn();
			var formData = {query:text};
			$.post("queryprocess.jsp",formData,function(data,status){
				$("#result").html($.trim(data));
			})
			.fail(function(xhr, status, error) {
				//console.log("Failed with:"+e.status);
				$("#result").html("Error occurred!"+xhr.responseText);
			})
			.always(function() {
				$('#loading-indicator').fadeOut();
				fetchRelaxations(text,terms);
			});
		}
	};
	function fetchRelaxations(text,terms){
		var formData = {query:text};
		$.post("getrelaxations.jsp",formData,function(data,status){
			$('#relaxdiv').show();
			$("#relaxdiv").html($.trim(data));
		});
	}

	function split(val) {
		return val.split( /\s+/ );
	};

	$("#userquery")
	.autocomplete({
		source: "autocomplete.jsp",
		minLength: 5,
		delay: 500,
		open: function() {
			$(this).data('is_open',true);
		},
		search: function(event, ui ) { 
			var text=$('#userquery').val();
			var terms = text.split( /\s+/ );
			if(terms.length>1 && terms[terms.length-1].length<5) 
				event.preventDefault();
		},
		focus: function() {
			// prevent value inserted on focus
			return false;
		},
		select: function( event, ui ) {
			var terms = split( this.value );
			// remove the current input
			terms.pop();
			// add the selected item
			terms.push( ui.item.value );
			// add placeholder to get the comma-and-space at the end
			terms.push( "" );
			this.value = terms.join( " " );
			text = this.value;
			terms = text.split( /\s+/ );
			if(terms[terms.length-1].length<1)
				terms.pop();
			if(terms.length>1){
				fetchResults(text,terms);
			}
			return false;
		},
		close: function( event, ui ) { 
			$(this).data('is_open',false);
		}
	})
	/*This validation will prevent autocomplete on the second word after space.
	.on( "autocompletesearch", function( event, ui ) {
		console.log( "Into prevent default function." );
		var text=$('#userquery').val();
		var terms = text.split( /\s+/ );
		if(terms.length>1 && terms[terms.length-1].length<5) 
			event.preventDefault();
	} );*/
//	});


	$("#inputform")
	.keypress(function(event){
		var key = (event.keyCode ? event.keyCode : event.which); 
		var ch=String.fromCharCode(key) 
		if(ch==" "){
			if($(this).data('is_open')) {
				$('#userquery').autocomplete('close');
			}

			var text=$('#userquery').val();
			var terms = text.split( /\s+/ );
			fetchResults(text,terms);

		}
	})
	.submit(function(event){
		event.preventDefault();
		// Write function.
		var text=$('#userquery').val().trim();
		var terms = text.split( /\s+/ );
		fetchBidirResults(text,terms);
	});
})