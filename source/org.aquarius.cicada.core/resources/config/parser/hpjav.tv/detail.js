function internalUpdateMovieTitle(movie)
{
	var titleElement=document.querySelector('li.active');
	if(null==titleElement)
	{
		titleElement=document.querySelector('h1');
	}
	
	if(null!=titleElement)
	{
		movie.title=titleElement.textContent;
	}
	else
	{
		movie.title=document.title;
	}
}

function internalParseMovieDetailInfo() {
	
	var logoElement = document.querySelector("div.logo")

	if (logoElement == null) {
		var result = new Object();
		result.missing = true;
		result.errorMessage = "Page Empty";

		var currentPageUrl = document.location.href;
		if (currentPageUrl.indexOf("/tw/") >= 0) {
			result.redirectUrl = currentPageUrl.replace("/tw/", "/ja/");
			return result;
		}

		if (currentPageUrl.indexOf("/ja/") >= 0) {
			result.redirectUrl = currentPageUrl.replace("/ja/", "/");
		}

		return result;
	}

	//The following code is to init the return object
	var movie = new Object();
	var downloadInfoList = [];

	movie.downloadInfoList = downloadInfoList;

	var downloadButton = document.querySelector("div#download_div");
	if (null != downloadButton) {
		downloadButton.click();
	}

	

	internalUpdateMovieTitle(movie);
	var title=movie.title;


	var regex = "[^\x00-\xff]{0,10}[0-9a-zA-Z]{0,20}[^\x00-\xff]{0,10}[ -]{0,2}[0-9a-zA-Z_]{1,12}";

	var matchResult = title.match(regex);
	if ((null != matchResult) && (matchResult.length > 0)) {
		var uniId = matchResult[0];
		var index = title.indexOf(uniId);
		if (index == 0) {
			movie.uniId = uniId;
			movie.name = title.substring(index + movie.uniId.length).trim();
		}
	}

	if ((null == movie.uniId) || (movie.uniId.length == 0)) {
		regex = /[A-Za-z0-9- ]{4,20}/g;
		matchResult = regex.exec(title);

		if ((null != matchResult) && (matchResult.length > 0)) {
			var uniId = matchResult[0].trim();
			movie.uniId = uniId;
			movie.name = title.replace(matchResult[0], "");
		}
	}

	var imageElement = document.querySelector("img.countext-img");
	if (null != imageElement) {
		movie.imageUrl = imageElement.src;
	}

	//The following script is to get the actors.
	var actors = [];
	var elementList = document.querySelectorAll("div.video-countext-Models > div.models-content");
	for (var i = 0; i < elementList.length; i++) {
		actors[i] = elementList[i].innerText;
	}

	movie.actor = actors.join("|");

	//The following script is to get the category.
	var categories = [];
	var elementList = document.querySelectorAll("div.video-countext-categories > a");
	for (var i = 0; i < elementList.length; i++) {
		categories[i] = elementList[i].text;
	}

	movie.category = categories.join("|");

	//The following script is to get the tags.
	var tags = [];
	var elementList = document.querySelectorAll("div.video-countext-tags > a");
	for (var i = 0; i < elementList.length; i++) {
		tags[i] = elementList[i].text;
	}

	movie.tag = tags.join("|");

	var elementList = document.querySelectorAll("ul.pricing-table");

	var info = document.querySelector("div.video-countext-categories").textContent;
	
	for (var i = 0; i < elementList.length; i++) {
		var nodeList = elementList[i].querySelectorAll("a[href^='/download']");
		var links = [];

		if (nodeList.length > 0) {

			for (var j = 0; j < nodeList.length; j++) {
				links[j] = new Object();

				links[j].sourceUrl = "https://hpjav.tv/" + nodeList[j].getAttribute("href");
				links[j].selected = true;
			}

			downloadInfoList[i] = new Object();
			downloadInfoList[i].downloadLinks = links;
		}
	}

	var result = new Object();
	result.movie = movie;

	return result;

}

function doParseMovieDetailInfo()
{
	return JSON.stringify(internalParseMovieDetailInfo());
}

return doParseMovieDetailInfo();