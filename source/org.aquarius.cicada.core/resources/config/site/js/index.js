function initListeners() {
	$("li").click(function() {
		$(this).toggleClass("selected");
		if ($("li.selected").length == 0) {
			$(".select").removeClass("selected");
			selectMovieFunction("");
		} else {
			$(".select").addClass("selected");
			selectMovieFunction($(this).attr("name"));
		}
		counter();
	});
	$(".select").click(function() {
		if ($("li.selected").length == 0) {
			$("li").addClass("selected");
			$(".select").addClass("selected");
			selectMovieFunction($(this).attr("name"));
		} else {
			$("li").removeClass("selected");
			$(".select").removeClass("selected");
			selectMovieFunction("");
		}
		counter();
	});


}

function cancelSelection() {
	$("li").removeClass("selected");
	$(".select").removeClass("selected");
	selectMovieFunction("");
	counter();

};

function counter() {

	var selectedElements = $("li.selected");

	if (selectedElements.length > 0) {
		$(".send").addClass("selected")
	} else {
		$(".send").removeClass("selected")
	}

	$(".send").attr("data-counter", $("li.selected").length)
};