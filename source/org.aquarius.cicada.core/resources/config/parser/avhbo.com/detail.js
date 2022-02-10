function internalUpdateMovieTitle(movie)
{
	var titleElement=document.querySelector('.entry-title');
	if(null==titleElement)
	{
		titleElement=document.querySelector('.breadcrumb_last');
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
	var result = new Object();

	var errorSection = document.querySelector("section.error-404");
	if (null != errorSection) {
		result.missing = true;
		result.errorMessage = "page lost";
		return result;
	}

	var movie = new Object();

	internalUpdateMovieTitle(movie);
	
	var downloadInfoList = [];

	movie.downloadInfoList = downloadInfoList;

	var infoElement = document.querySelector("div.overview > h4");

	if (null == infoElement) {
		infoElement = document.querySelector("div.overview > figure > h4");
	}

	if (null != infoElement) {

		var infoChildrenElements = infoElement.children;

		var fontElement = infoElement.querySelector("font");
		var simpleStyle = false;

		if (null != fontElement) {
			infoChildrenElements = new Array();
			infoChildrenElements.push(fontElement);

			simpleStyle = true;
		}

		var dateRegex = "(((19|20)([2468][048]|[13579][26]|0[48])|2000)[/-]02[/-]29|((19|20)[0-9]{2}[/-](0[4678]|1[02])[/-](0[1-9]|[12][0-9]|30)|(19|20)[0-9]{2}[/-](0[1359]|11)[/-](0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}[/-]02[/-](0[1-9]|1[0-9]|2[0-8])))";

		for (var i = 0; i < infoChildrenElements.length; i++) {
			var textElement = infoChildrenElements[i].previousSibling;

			if (null == textElement || simpleStyle) {
				textElement = infoChildrenElements[i];
			}

			if (null != textElement) {
				var infoString = textElement.textContent;

				var index = infoString.lastIndexOf("AV女優");
				if (index >= 0) {

					var actor = infoString.substring(index + 6).trim();

					index = actor.indexOf("...");
					if (index >= 0) {
						actor = actor.substring(0, index);
					}

					actor = actor.replaceAll('出演者', '');
					actor = actor.replaceAll('0', '');
					actor = actor.replaceAll('.', '');
					actor = actor.replaceAll('：', '|');
					actor = actor.replaceAll(':', '|');

					movie.actor = actor;
				}

				var dateMatchResult = infoString.match(dateRegex);

				if (dateMatchResult != null && dateMatchResult.length > 0) {
					var publishDate = dateMatchResult[0];
					publishDate = publishDate.replaceAll("/", "-");
					movie.publishDate = publishDate;
				}
			}
		}
	}

	var imageElement = document.querySelector("div.overview > img");
	if (null != imageElement) {
		movie.imageUrl = imageElement.src;

		if (movie.imageUrl.indexOf("data") == 0) {
			movie.imageUrl = imageElement.getAttribute("data-src")
		}
	}

	var titleElement = document.querySelector("span.breadcrumb_last");
	if (null == titleElement) {
		titleElement = document.querySelector("h1.entry-title");
	}
	var oldTitle = titleElement.textContent.trim();
	var title = oldTitle;

	if (null == movie.uniId) {
		var regex = /[A-Za-z0-9- ]{4,20}/g;

		if (oldTitle.toLowerCase().indexOf("caribbean") > -1) {
			regex = /[0-9-]{4,20}/g;
		}

		var matchResult = regex.exec(oldTitle);

		if ((null != matchResult) && (matchResult.length > 0)) {
			var uniId = matchResult[0].trim();
			movie.uniId = uniId.replaceAll(" ", "-");

			title = title.replace(movie.uniId, "");
			title = title.replace("[", "");
			movie.name = title.replace("]", "");

			if (oldTitle.toLowerCase().indexOf("caribbean") > -1) {
				movie.uniId = "caribbean-" + movie.uniId;
			}
		}
	}

	//The following script is to get the tags.
	var tags = [];

	var tagElementList = document.querySelectorAll("div.paper-tiles > a");
	for (var i = 0; i < tagElementList.length; i++) {
		var newTag = tagElementList[i].text.trim();
		if (newTag != "0") {
			tags.push(newTag);
		}
	}

	movie.tag = tags.join("|");

	var links = [];
	var spanElements = document.querySelectorAll("figure > span ");

	if (null == spanElements || spanElements.length == 0) {
		spanElements = document.querySelectorAll("div.overview > p ");
	}

	try {

		var iframeElement = document.querySelector("iframe[data-src]");
		if (null != iframeElement) {

			var link = new Object();

			link.sourceUrl = iframeElement.getAttribute("data-src");
			if (link.sourceUrl.substring(0, 4) != "http") {
				link.sourceUrl = "https:" + link.sourceUrl;
			}

			links.push(link);
		}
	} catch (e) {

	}

	var downloadElements = document.querySelectorAll("a.javdownload");
	if (null != downloadElements) {
		for (var i = 0; i < downloadElements.length; i++) {
			var downloadElement = downloadElements[i];

			var link = new Object();

			var downloadUrl = downloadElement.href;

			if (downloadUrl.indexOf("ouo.io") > -1) {
				var index = downloadUrl.indexOf("?s=");
				if (index > -1) {
					downloadUrl = downloadUrl.substring(index + 3);
					downloadUrl = decodeURIComponent(downloadUrl);
				}
			}

			links.push(link);
		}
	}

	downloadInfoList[0] = new Object();
	downloadInfoList[0].downloadLinks = links;

	result.movie = movie;
	return result;
}

function doParseMovieDetailInfo()
{
	return JSON.stringify(internalParseMovieDetailInfo()); 
}

return doParseMovieDetailInfo();