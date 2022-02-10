function internalParseMovieList() {
	var result = new Object();
	var movies = [];
	result.movieList = movies;

	var elementList = document.querySelectorAll("div.col-inner");

	if (null != elementList) {
		for (var i = 0; i < elementList.length; i++) {
			var element = elementList[i];
			var movie = new Object();

			var titleElement = element.querySelector("h5.post-title");

			movie.title = titleElement.textContent;

			if (movie.title.indexOf("test") < 0) {

				var linkElement = element.querySelector("a");
				movie.pageUrl = linkElement.href;

				var imageElement = element.querySelector("img");
				movie.imageUrl = imageElement.src;

				if (movie.imageUrl.indexOf("data") == 0) {
					movie.imageUrl = imageElement.getAttribute("data-src")
				}

				movies.push(movie);
			}
		}
	}

	var pageElements = document.querySelectorAll("ul.nav-pagination > li");
	var firstElement = pageElements[0];
	var lastElement = pageElements[pageElements.length - 1];

	if (lastElement.textContent.indexOf("»") < 0) {
		result.lastPage = true;
	} else {
		lastElement = lastElement.previousElementSibling;
	}

	result.totalPageCount = parseInt(lastElement.textContent);

	return result;
}

function doParseMovieList() {
	return JSON.stringify(internalParseMovieList());
}

return doParseMovieList();