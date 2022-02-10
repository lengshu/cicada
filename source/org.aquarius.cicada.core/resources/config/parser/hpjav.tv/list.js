function internalParseMovieList() {
	//The following code is to init the return object
	var result = new Object();
	var movies = [];
	result.movieList = movies;

	//The following code is to get the channel
	var position = window.location.pathname.indexOf("category");
	position = position + 8;
	var path = window.location.pathname.substring(position);
	path = path.split("/")[1]

	var elementList = document.querySelectorAll("div.col-xs-6");

	if (elementList != null) {
		for (var i = 0; i < elementList.length; i++) {
			var movie = new Object();

			var linkElement = elementList[i].querySelector("a");
			movie.pageUrl = linkElement.href;

			var imgElement = linkElement.querySelector("img");
			movie.imageUrl = imgElement.getAttribute("data-original");

			var titleElement = elementList[i].querySelector("div.entry-title > a");
			movie.title = titleElement.text;

			//The following code is to change the publish date in format of "yyyy-MM-dd"
			var publishDate = titleElement.nextSibling.nodeValue;
			publishDate = publishDate.replace(/ /g, "");
			publishDate = publishDate.replace(/\//g, "-");

			movie.publishDate = publishDate;

			movies[i] = movie;
		}
	}

	var currentPageElement = document.querySelector("ul.pagination > li.active");
	if (null != currentPageElement) {
		var previousElement = currentPageElement.previousElementSibling
		if (null != previousElement) {
			result.previousPageUrl = previousElement.children[0].href;
		}

		var nextElement = currentPageElement.nextElementSibling;
		if (null != nextElement) {
			result.nextPageUrl = nextElement.children[0].href;
		}
	}

	var lastPageElement = document.querySelector("ul.pagination > li > a.extend");

	if (null == lastPageElement) {
		result.lastPageUrl = window.location.href;
	} else {
		result.lastPageUrl = lastPageElement.href;
	}

	var numberArray = result.lastPageUrl.match(/\d+/g);
	result.totalPageCount = numberArray[numberArray.length - 1];

	var totalMovieCountElement = document.querySelector("h3.title");
	numberArray = totalMovieCountElement.innerText.match(/\d+/g);

	result.totalMovieCount = numberArray[numberArray.length - 1];

	return result;

}

function doParseMovieList()
{
	return JSON.stringify(internalParseMovieList());
}

return doParseMovieList();