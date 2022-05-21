function internalParseMovieList() {
	var result = new Object();
	var movies = [];
	result.movieList = movies;

	var elementList = document.querySelectorAll("div.post-item");

	if (null != elementList) {
		for (var i = 0; i < elementList.length; i++) {
			var element = elementList[i];
			var movie = new Object();

			var titleElement = element.querySelector(".post-title");

			movie.title = titleElement.innerText;

			movie.title = movie.title.replace("\n\n", "");
			movie.title = movie.title.replace("\n\n", "");
			movie.title = movie.title.replace("\n\n", "");
			movie.title = movie.title.replace("\n", "");
			movie.title = movie.title.replace("\n", "");
			movie.title = movie.title.replace("\n", "");

			var linkElement = element.querySelector("a");
			movie.pageUrl = linkElement.href;

			var imageElement = element.querySelector("img");
			movie.imageUrl = imageElement.src;

			if (movie.imageUrl.indexOf("data") == 0) {
				movie.imageUrl = imageElement.getAttribute("data-src");
			}
			
			var durationElement=element.querySelector("span.text-nord5");
			if(null!=durationElement)
			{
				movie.duration=durationElement.textContent;
				movie.duration = movie.duration.replace("\n", "");
				movie.duration = movie.duration.replace("\n", "");
			}

			movies.push(movie);
		}
	}

	var pageFormElement = document.querySelector("span#price-currency");
	if (null != pageFormElement) {
		var formString = pageFormElement.textContent;
		var stringIndex = formString.indexOf(" ");
		formString = formString.substr(stringIndex);
		result.totalPageCount = parseInt(formString);
	}

	var lastElement = document.querySelector("a[aria-label='pagination.next']");

	if (lastElement == null) {
		result.lastPage = true;
	}

	return result;
}

function doParseMovieList() {
	return JSON.stringify(internalParseMovieList());
}

return doParseMovieList();
