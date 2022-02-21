function internalUpdateMovieTitle(movie) {
	var titleElement = document.querySelector('h1.entry_title');
	if (null == titleElement) {
		titleElement = document.querySelector('div.float-video-title');
	}

	if (null == titleElement) {
		titleElement = document.querySelector('span.current');
	}

	if (null != titleElement) {
		movie.title = titleElement.textContent;
	} else {
		movie.title = document.title;
	}

	var regex = /[A-Za-z0-9- ]{4,20}/g;

	var matchResult = regex.exec(movie.title);

	if ((null != matchResult) && (matchResult.length > 0)) {
		var uniId = matchResult[0].trim();
		movie.uniId = uniId;
	}
}

function internalParseMovieDetailInfo() {
	var result = new Object();
	var movie = new Object();

	internalUpdateMovieTitle(movie);

	var downloadInfoList = [];

	movie.downloadInfoList = downloadInfoList;

	var dateElement = document.querySelector("time.published");
	if (null != dateElement) {
		movie.publishDate = dateElement.textContent;
	}



	var tagElements = document.querySelectorAll("div.tags-items > a");

	if (null != tagElements) {
		var tags = [];

		for (var i = 0; i < tagElements.length; i++) {
			tags[i] = tagElements[i].text;
		}

		movie.tag = tags.join("|");
	}

	var downloadInfo = new Object();
	downloadInfo.downloadLinks = [];

	//const regex = /(https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s]{2,}|www\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\.[^\s]{2,}|https?:\/\/(?:www\.|(?!www))[a-zA-Z0-9]+\.[^\s]{2,}|www\.[a-zA-Z0-9]+\.[^\s]{2,})/gm;
	const regex = /\/\/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]/gm;

	var str = vidorev_jav_js_object.single_video_url;
	while ((m = regex.exec(str)) !== null) {
		// This is necessary to avoid infinite loops with zero-width matches
		if (m.index === regex.lastIndex) {
			regex.lastIndex++;
		}

		if (downloadInfo.downloadLinks.length > 0) {
			break;
		}

		var urlInfo = m.toString();

		var urlInfoLower = urlInfo.toLowerCase();

		var link = new Object();
		urlInfo = "https:" + urlInfo;

		link.sourceUrl = urlInfo;
		link.selected = true;
		link.requestHeaders = new Object();
		link.requestHeaders['Referer'] = document.location.href;

		downloadInfo.downloadLinks.push(link);
	}

	var downloadElement = document.querySelector("div.download-item-btn > a");
	if (null != downloadElement) {
		var link = new Object();
		link.sourceUrl = downloadElement.href;
		link.selected = true;
		link.requestHeaders = new Object();
		link.requestHeaders['Referer'] = document.location.href;

		downloadInfo.downloadLinks.push(link);
	}

	downloadInfoList.push(downloadInfo);

	result.movie = movie;
	return result;
}

function doParseMovieDetailInfo() {
	return JSON.stringify(internalParseMovieDetailInfo());
}

return doParseMovieDetailInfo();