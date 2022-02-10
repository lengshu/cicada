
function internalParseMovieDetailInfo() {
	var result = new Object();

	var infoElement = document.querySelector("section.video-info");
	if (null == infoElement) {
		result.missing = true;
		result.errorMessage = "Page Lost";
		return (result);
	}
	var memberElement = document.querySelector("h4.title-inside");
	if (null != memberElement) {
		result.errorMessage = "Member Page";
		return (result);
	}

	//The following script is to return the json result object

	var movie = new Object();
	var downloadInfoList = [];
	movie.downloadInfoList = downloadInfoList;

	var imageElement = document.querySelector("meta[property='og:image']");
	if (null != imageElement) {
		movie.imageUrl = imageElement.content;
	}

	//The following code is to fetch the information of the movie
	var title = document.querySelector("h4").innerText.trim();
	movie.title = title;
	var index = title.indexOf(" ");


	if (index > 0) {
		movie.uniId = title.substring(0, index).trim();
		movie.name = title.substring(index).trim();
	} else {
		var regex = /[A-Za-z0-9- ]{4,20}/g;

		var matchResult = regex.exec(title);

		if ((null != matchResult) && (matchResult.length > 0)) {
			var uniId = matchResult[0].trim();
			movie.uniId = uniId;
			movie.name = title.replace(matchResult[0], "");
		}
	}


	var dateElement = document.querySelector("section.video-info > div.info-header > div.header-right > span");
	if (null != dateElement) {
		var dateString = dateElement.textContent;

		var index = dateString.indexOf(" ");
		if (index != -1) {
			dateString = dateString.substring(index + 1);
		}

		movie.publishDate = dateString;
	}



	//The following script is to get the actors.
	var actors = [];
	var elementList = document.querySelectorAll("img[data-original-title]");

	if (null == elementList || elementList.length == 0) {
		elementList = document.querySelectorAll("div.models > a > span");
	}

	for (var i = 0; i < elementList.length; i++) {
		actors[i] = elementList[i].getAttribute("data-original-title");
	}
	movie.actor = actors.join("|");

	//The following script is to get the category.
	var categories = [];
	var elementList = document.querySelectorAll("h5.tags > a.cat");
	for (var i = 0; i < elementList.length; i++) {
		categories[i] = elementList[i].text;
	}

	movie.category = categories.join("|");

	//The following script is to get the tags.
	var tags = [];
	var elementList = document.querySelectorAll("h5.tags > a:not(.cat)");
	for (var i = 0; i < elementList.length; i++) {
		tags[i] = elementList[i].text;
	}

	movie.tag = tags.join("|");

	//The following script is to get the download urls.
	var downloadInfo = new Object();

	downloadInfo.downloadLinks = [];
	downloadInfo.downloadLinks[0] = new Object();
	downloadInfo.downloadLinks[0].sourceUrl = hlsUrl;
	downloadInfo.downloadLinks[0].selected = true;

	downloadInfoList[0] = downloadInfo;
	
	result.movie = movie;

	return result;

}

function doParseMovieDetailInfo()
{
	return JSON.stringify(internalParseMovieDetailInfo());
}

return doParseMovieDetailInfo();