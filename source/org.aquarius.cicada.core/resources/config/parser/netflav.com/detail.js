
function internalUpdateMovieTitle(movie)
{
	var titleElement=document.querySelector('div.videocoverheader_title');

	if(null==titleElement)
	{
		titleElement=document.querySelector('div.videodetail_title');
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

	//The following code is to init the return object
	var downloadInfoList = [];
	var movie = new Object();

	var jsonText = document.querySelector("script#__NEXT_DATA__").innerText;
	var jsonresult = JSON.parse(jsonText);

	if (jsonresult.err != null) {
		var result = new Object();

		result.errorMessage = jsonresult.err.message;
		result.shouldRetry = false;
		return result;
	}

	var isError = jsonresult.props.initialState.video.isError;

	if (isError == true) {
		var result = new Object();

		result.errorMessage = "No movie founded";
		result.shouldRetry = false;
		return result;
	}

	movie.downloadInfoList = downloadInfoList;

	//The following code is to fetch the information of the movie

	var videoData = jsonresult.props.initialState.video.data;

	movie.uniId = videoData.code;
	internalUpdateMovieTitle(movie);

	movie.imageUrl = videoData.preview;

	var actorList = videoData.actors;

	var language=jsonresult.props.initialState.global.language+":";

	var actorMark = (actorList.join(" ").indexOf(language) > -1);
	
	var actors = [];
	for (var i = 0; i < actorList.length; i++) {
		var actor = actorList[i];

		if (actorMark) {
			var index = actor.indexOf(language);

			if (index != -1) {
				actors.push(actor.substring(index + 3).trim());
			}
		} else {
			actors.push(actor.trim());
		}

	}

	if (actors.length == 0) {
		actors = actorList;
	}
	movie.actor = actors.join("|");

	var tagList = videoData.tags;


	var tagZhMark = (tagList.join(" ").indexOf(language) > -1);
	var tags = [];
	for (var i = 0; i < tagList.length; i++) {
		var tag = tagList[i];

		if (tagZhMark) {
			var index = tag.indexOf(language);
			if (index != -1) {
				tag = tag.substring(index + 3).trim();
				if (tag.length > 0) {
					tags.push(tag);
				}
			}
		} else {
			tags.push(tag);
		}
	}

	if (tags.length == 0) {
		tags = tagList;
	}
	movie.tag = tags.join("|");
	if (null == movie.tag) {
		movie.tag = "";
	}

	var name = movie.title.trim();

	var code = movie.uniId;

	if (name.startsWith("[" + code + "]")) {
		name = name.substring(("[" + code + "]").length);
	}

	if (name.startsWith(code)) {
		name = name.substring(code.length);
	}

	name = name.replace(code, "");
	name = name.replace(code, "");

	movie.publishDate = videoData.sourceDate;
	movie.producer = videoData.producer;

	var links = [];
	var linkElements = videoData.srcs;

	if ((null == linkElements) || (linkElements.length == 0)) {
		linkElements = videoData.uSrc;
	}

	if ((null != linkElements) && (linkElements.length > 0)) {
		for (var i = 0; i < linkElements.length; i++) {

			var urlString = linkElements[i];
			if ((null != urlString) && (urlString.length > 0)) {
				
				var link = new Object();

				link.sourceUrl = linkElements[i];
				link.selected = true;

				if (link.sourceUrl.indexOf("google.com") < 0) {
					links.push(link);
				}
			}
		}
	}


	downloadInfoList[0] = new Object();
	downloadInfoList[0].downloadLinks = links;

	movie.name = name.trim();

	var result = new Object();
	result.movie = movie;

	return result;
}

function doParseMovieDetailInfo()
{
	return JSON.stringify(internalParseMovieDetailInfo());
}

return doParseMovieDetailInfo();