function internalParseMovieList() {
	//The following code is to init the return object

	var result = new Object();
	var movies = [];
	result.movieList = movies;

	var jsonText = document.querySelector("script#__NEXT_DATA__").innerText;
	var jsonresult = JSON.parse(jsonText);

	var lang = jsonresult.props.initialState.global.language;

	var object = jsonresult.props.initialState;
	var infoElement;
	var docs;

	for (var key in object) {

		if (!(typeof(object[key]) == 'function')) {
			if (object[key]["docs"] != null) {
				infoElement = object[key];
				docs = infoElement.docs;

				break;
			}
		}
	}


	var siteUrl = window.location.origin;

	for (var i = 0; i < docs.length; i++) {
		var doc = docs[i];

		var movie = new Object();
		movies[i] = movie;



		movie.title = doc.title_zh;

		if ((movie.title == null) || (movie.title.length == 0)) {
			movie.title = doc.title;
		}


		movie.pageUrl = siteUrl + "/video?id=" + doc.videoId;
		movie.publishDate = doc.sourceDate;

		movie.imageUrl = doc.preview;

		if (movie.imageUrl == null) {
			movie.imageUrl = doc.preview_hp;
		}

		if (movie.imageUrl == null) {
			movie.imageUrl = doc.previewImagesUrl;
		}
	}

	var currentPageElement = document.querySelector("a.active.item");

	if (null != currentPageElement) {
		var currentPage = parseInt(currentPageElement.text);

		var currentPageUrl = window.location.href;
		var position = currentPageUrl.indexOf("?");
		if (position < 0) {
			currentPageUrl = window.location.href;
		} else {
			currentPageUrl = currentPageUrl.substr(0, position);
		}

		var nextElement = currentPageElement.nextElementSibling;
		if (null != nextElement) {
			var nextPage = currentPage + 1;
			result.nextPageUrl = currentPageUrl + "?page=" + nextPage.toString();
		}
		if (currentPage > 1) {
			var previousElement = currentPageElement.previousElementSibling;
			if (null != previousElement) {
				var previousPage = currentPage - 1;
				result.previousPageUrl = currentPageUrl + "?page=" + previousPage.toString();
			}
		}
	}

	if (docs.length < 20) {
		result.lastPage = true;
		result.lastPageUrl = window.location.href;
		result.totalPageCount = currentPage;
		//result.totalMovieCount=(currentPage-1)*20+docs.length;
	}


	return result;
}

function doParseMovieList()
{
	return JSON.stringify(internalParseMovieList());
}

return doParseMovieList();