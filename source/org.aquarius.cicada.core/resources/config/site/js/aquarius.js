function isFunctionInjected() {
	try {
		if (typeof eval("queryMovieCountFunction") === "function") {
			return "true";

		} else {
			return "false";
		}
	} catch (e) {
		return "false";
	}
}

function querySelectedMovies() {
	var selectedElements = $("li.selected");

	var results = [];
	for (var i = 0; i < selectedElements.length; i++) {
		results.push(selectedElements[i].getAttribute("name"));
	}

	return results.join("|");
}