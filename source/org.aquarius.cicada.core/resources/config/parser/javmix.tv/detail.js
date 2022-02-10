function internalUpdateMovieTitle(movie)
{
	var titleElement=document.querySelector('h1');
		
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
	var result = new Object();
	var movie = new Object();

	internalUpdateMovieTitle(movie);

	var tagElements = document.querySelectorAll("div > a[rel]");

	if (null != tagElements) {
		var tags = [];

		for (var i = 0; i < tagElements.length; i++) {
			tags[i] = tagElements[i].textContent;
		}

		movie.tag = tags.join("|");
	}

	var splitContents = document.location.pathname.split("/");

	for (var j = (splitContents.length - 1); j >= 0; j--) {
		var content = splitContents[j];

		if (content.length > 2) {

			if (content.substring(0, 2).toUpperCase() == "X_") {
				content = content.substring(2);
			}

			if (content.substring(0, 3).toUpperCase() == "XL_") {
				content = content.substring(3);
			}

			var segments=content.split('-');
			if(segments==null || segments.length<2)
			{
				movie.uniId = content.toUpperCase();
			}
			else
			{
				movie.uniId = segments[0].toUpperCase()+'-'+segments[1].toUpperCase();
			}
			
			break;
		}
	}

	result.movie = movie;

	var tabElements = document.querySelectorAll("div#episode > div.tab > span");
	if (null == tabElements) {
		result.errorMessage="No video founded";
		result.shouldRetry=false;
		
		return result;
	}

	var downloadInfoList = [];
	movie.downloadInfoList = downloadInfoList;
	var linkTabElements = document.querySelectorAll("div#server > div.tab");

	for (var i = 0; i < tabElements.length; i++) {
		var tabElement = tabElements[i];
		tabElement.click();

		var linkElements = linkTabElements[i].querySelectorAll("span");

		if (null != linkElements) {

			var downloadInfo = new Object();
			downloadInfoList.push(downloadInfo);
			downloadInfo.downloadLinks = [];

			for (var j = 0; j < linkElements.length; j++) {
				var linkElement = linkElements[j];
				linkElement.click();

				var link = new Object();

				link.sourceUrl = document.querySelector("div#download > a").href;
				link.selected = true;
				link.requestHeaders = new Object();
				link.requestHeaders['Referer'] = document.location.href;

				downloadInfo.downloadLinks.push(link);
			}
		}
	}

	return result;
}

function doParseMovieDetailInfo()
{
	return JSON.stringify(internalParseMovieDetailInfo());
}

return doParseMovieDetailInfo();