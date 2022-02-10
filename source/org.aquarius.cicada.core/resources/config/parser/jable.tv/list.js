function internalParseMovieList() {
	//The following code is to init the return object
	var result = new Object();
	var movies = [];
	result.movieList = movies;

	var elementList = document.querySelectorAll("div.video-img-box");

	for (var i = 0; i < elementList.length; i++) {
		elementList[i].scrollIntoView();

		var movie = new Object();

		//The following code is to fetch the information of the movie
		var linkElements = elementList[i].querySelectorAll("a");
		movie.title = linkElements[1].text;

		//The duration format should be "HH:mm:ss"
		var durationElement = linkElements[0].querySelector("span.label");
		movie.duration = durationElement.innerHTML;

		if (movie.duration.split(":").length == 2) {
			movie.duration = "00:" + movie.duration;
		}

		//Get the abs url
		movie.pageUrl = linkElements[1].href;

		var imgElement = linkElements[0].querySelector("img");
		movie.imageUrl = imgElement.getAttribute("data-src");

		movies[i] = movie;
	}

	var currentPageElement = document.querySelector("ul.pagination > li >span").parentElement;
	if (null != currentPageElement) {
		var previousElement = currentPageElement.previousElementSibling;
		if (null != previousElement) {
			result.previousPageUrl = previousElement.children[0].href;
		}

		var nextElement = currentPageElement.nextElementSibling;
		if (null != nextElement) {
			result.nextPageUrl = nextElement.children[0].href;
		}

	}

	var pageLinkElements = document.querySelectorAll("ul.pagination > li");
	var lastPageLinkElement = pageLinkElements[pageLinkElements.length - 1];
	var linkText = lastPageLinkElement.textContent;

	if (linkText.indexOf("»") >= 0) {
		var parameters = lastPageLinkElement.children[0].getAttribute("data-parameters");
		var index = parameters.lastIndexOf(":");

		result.totalPageCount = parseInt(parameters.substring(index + 1));
	} else {
		result.totalPageCount = parseInt(linkText);
		result.lastPage = true;
	}

	result.lastPageUrl = 'https://jable.tv/new-release/' + result.totalPageCount + '/';


	var totalMovieCountElement = document.querySelector("span.inactive-color");
	numberArray = totalMovieCountElement.innerText.match(/\d+/g);

	result.totalMovieCount = numberArray[numberArray.length - 1];

	return result;
}

function doParseMovieList()
{
	return JSON.stringify(internalParseMovieList());
}

return doParseMovieList();