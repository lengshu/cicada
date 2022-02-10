function internalParseMovieList() {
	//The following link element is to get abs url
	var absLinkElement = document.createElement('a');


	//The following code is to init the return object
	var result = new Object();
	var movies = [];
	result.movieList = movies;

	var elementList = document.querySelectorAll("div.container.app-container > div.profile.row > div.col-12.col-sm-9 > div.row.row-grid >div");
	for (var i = 0; i < elementList.length; i++) {

		var movie = new Object();

		//The following code is to fetch the information of the movie
		var linkElement = elementList[i].querySelector("a");

		if (null != linkElement) {
			movie.title = linkElement.getAttribute("title");

			//Get the abs url
			absLinkElement.href = linkElement.href;
			movie.pageUrl = absLinkElement.href;

			var imgElement = linkElement.querySelector("img");
			absLinkElement.href = imgElement.getAttribute("src");
			movie.imageUrl = absLinkElement.href;

			//The duration format should be "HH:mm:ss"
			var durationElement = elementList[i].querySelector("div.duration > span");
			movie.duration = durationElement.innerHTML;

			if (movie.duration.split(":").length == 2) {
				movie.duration = "00:" + movie.duration;
			}

			movies.push(movie);
		}



	}

	//The following code is to judge if the previous page exists or not.
	var previousPageElement = document.querySelector("li.pagination-item.prev");

	if (null != previousPageElement) {
		result.previousPageUrl = previousPageElement.children[0].href;
	}

	//The following code is to judge if the next page exists or not.
	var nextPageElement = document.querySelector(" li.pagination-item.next");

	if (null != nextPageElement) {
		result.nextPageUrl = nextPageElement.children[0].href;
	} else {
		result.lastPage = true;
	}

	var text = document.querySelector("span.filterCount").textContent;
	var totalMovieCount = text.match(/\d+/g)[0];
	var totalPageCount = Math.ceil(totalMovieCount / 24);

	result.totalPageCount = totalPageCount;
	result.totalMovieCount = totalMovieCount;

	var currentUrl = window.location.href;
	var index = currentUrl.indexOf("p=");
	if (index > 0) {
		currentUrl = currentUrl.substring(0, index + 2);
		result.lastPageUrl = currentUrl + totalPageCount;
	} else {
		result.lastPageUrl = currentUrl + '&p=' + totalPageCount;
	}

	return result;
}

function doParseMovieList() {
	return JSON.stringify(internalParseMovieList());
}

return doParseMovieList();