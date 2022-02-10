function internalParseMovieList() {
	var result = new Object();
	var movies = [];
	result.movieList = movies;

	var elementList = document.querySelectorAll("div.post-list > a");

	if (null != elementList) {
		for (var i = 0; i < elementList.length; i++) {
			var element = elementList[i];
			var movie = new Object();

			var titleElement = element.querySelector("span");
			movie.title = titleElement.textContent;

			var pageUrl = element.href;
			movie.pageUrl = pageUrl;

			var splitContents = element.href.split("/");

			for (var j = (splitContents.length - 1); j >= 0; j--) {
				var content = splitContents[j];

				if (content.length > 2) {

					if (content.substring(0, 2).toUpperCase() == "X_") {
						content = content.substring(2);
					}

					if (content.substring(0, 3).toUpperCase() == "XL_") {
						content = content.substring(3);
					}

					var segments = content.split('-');
					if (segments == null || segments.length < 2) {
						movie.uniId = content.toUpperCase();
					} else {
						movie.uniId = segments[0].toUpperCase() + '-' + segments[1].toUpperCase();
					}

					break;
				}
			}

			var imageElement = element.querySelector("img");
			var imageUrl = imageElement.src;

			//var imageIndex = imageUrl.toLowerCase().indexOf("?w=");
			//if (imageIndex > -1) {
			//	imageUrl = imageUrl.substring(0, imageIndex);
			//}

			movie.imageUrl = imageUrl;

			var durationElement = element.querySelector("div.post-list-duration");
			if (null != durationElement) {
				var durationString = durationElement.textContent;
				durationString = durationString.replace("min.", "");
				var durationMinute = parseInt(durationString);
				if (null != durationMinute) {
					var duration = (Math.floor(durationMinute / 60) + ":" + (durationMinute % 60) + ":00");
					movie.duration = duration;
				}

			}

			var dateElement = element.querySelector("div.post-list-time");
			if (null != dateElement) {
				var dateString = dateElement.textContent;

				var index = dateString.indexOf(" ");
				while (index > -1) {
					dateString = dateString.replace(" ", "");
					index = dateString.indexOf(" ");
				}

				dateString = dateString.replace("年", "-");
				dateString = dateString.replace("月", "-");
				dateString = dateString.replace("日", "");

				movie.publishDate = dateString;
			}

			movies.push(movie);
		}
	}

	var pageElements = document.querySelectorAll("div.wp-pagenavi > a");
	var lastElement = pageElements[pageElements.length - 1];

	if (lastElement.textContent.indexOf("»") < 0) {
		result.lastPage = true;
	}

	var pageNumberElement = document.querySelector("div.wp-pagenavi > span.pages");
	var pageContent = pageNumberElement.textContent;

	var lastIndex = pageContent.lastIndexOf(" ");
	var numberContent = pageContent.substring(lastIndex + 1);
	numberContent = numberContent.replace(",", "");
	numberContent = numberContent.replace(",", "");

	result.totalPageCount = parseInt(numberContent);

	return result;
}

function doParseMovieList()
{
	return JSON.stringify(internalParseMovieList());
}

return doParseMovieList();