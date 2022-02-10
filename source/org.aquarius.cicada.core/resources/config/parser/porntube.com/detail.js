function internalParseMovieDetailInfo() {
	//The following script is to return the json root object

	var movie = new Object();
	var downloadInfoList = [];
	movie.downloadInfoList = downloadInfoList;

	var actors = [];
	var actorElements = document.querySelectorAll("span.pornstars > h3 >a");
	for (var i = 0; i < actorElements.length; i++) {
		actors.push(actorElements[i].textContent);
	}
	movie.actor = actors.join(",");


	var tags = [];
	var tagElements = document.querySelectorAll("div.labels > a");
	for (var i = 0; i < tagElements.length; i++) {
		tags.push(tagElements[i].textContent);
	}
	movie.tag = tags.join(",");


	var titleElement = document.querySelector("h1.title");
	movie.title = titleElement.textContent;

	var dateElement = document.querySelector("div.date-added");
	var dateStrings = dateElement.textContent.split('/');

	var publishDate = dateStrings[2] + "-" + dateStrings[0] + "-" + dateStrings[1]
	movie.publishDate = publishDate;

	var link = new Object();

	var videoElement = document.querySelector("source[size='720']");
	if (null == videoElement) {
		videoElement = document.querySelector('video');
	}


	link.sourceUrl = videoElement.src;
	link.selected = true;

	downloadInfoList[0] = new Object();
	downloadInfoList[0].downloadLinks = [];
	downloadInfoList[0].downloadLinks[0] = link;
	downloadInfoList[0].mp4File = movie.title + ".mp4";

	var result = new Object();
	result.movie = movie;

	return result;

}

function doParseMovieDetailInfo()
{
	return JSON.stringify(internalParseMovieDetailInfo());
}

return doParseMovieDetailInfo();