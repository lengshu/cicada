function doRemoveInvalidChar(textContent)
{
	if(null==textContent)
	{
		return null;
	}
	
	var result=textContent.trim();
	
	result=result.replace("\n\n", "");
	result=result.replace("\n\n", "");
	result=result.replace("\n\n", "");
	result=result.replace("\n\n", "");
	result=result.replace("\n\n", "");
	
	result=result.replace("\n", "");
	result=result.replace("\n", "");
	result=result.replace("\n", "");
	result=result.replace("\n", "");
	result=result.replace("\n", "");
	
	return result;
}

function doFindDownloadUrl() {
	var scriptElements = document.querySelectorAll('script');
	var scriptText = null;

	for (var i = 0; i < scriptElements.length; i++) {
		var tempString = scriptElements[i].text;

		if (tempString.indexOf("DOMContentLoaded") > 0) {
			scriptText = tempString;
		}
	}

	if (null != scriptText) {
		var begin = scriptText.indexOf("let");

		var end = scriptText.indexOf("const");

		var code = scriptText.substring(begin, end);
		return eval(code);
	}

	return null;
}

function doMergeInfo(element)
{
	var childrenElements=element.children;
	var infos = [];
	
	for(var i=1;i<childrenElements.length;i++)
	{
		var childrenElement=childrenElements[i];
		var info=childrenElement.textContent;
		info=info.replace("・","|");
		info=info.replace("・","|");
		
		infos.push(doRemoveInvalidChar(info));
	}
	
	return infos.join("|");
}

function internalUpdateMovieTitle(movie) {
	var titleElement = document.querySelector('h1.text-base');

	if (null != titleElement) {
		movie.title = titleElement.textContent;
	} else {
		movie.title = document.title;
	}
}

function internalParseMovieDetailInfo() {
	var result = new Object();

	var movie = new Object();

	internalUpdateMovieTitle(movie);

	var downloadInfoList = [];

	movie.downloadInfoList = downloadInfoList;

	var infoElement = document.querySelector("div.detail-item");



	if (null != infoElement) {

		var dateRegex =
			"(((19|20)([2468][048]|[13579][26]|0[48])|2000)[/-]02[/-]29|((19|20)[0-9]{2}[/-](0[4678]|1[02])[/-](0[1-9]|[12][0-9]|30)|(19|20)[0-9]{2}[/-](0[1359]|11)[/-](0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}[/-]02[/-](0[1-9]|1[0-9]|2[0-8])))";


		var infoChildrenElements = infoElement.children;

		var publishDateElement = infoChildrenElements[0].lastElementChild;
		var uniIdElement = infoChildrenElements[0].lastElementChild;
		
		var actorElement = infoChildrenElements[3];
		var categoryElement = infoChildrenElements[4];
		var producerElement = infoChildrenElements[5];
		var tagElement = infoChildrenElements[infoChildrenElements.length - 2];

		var dateMatchResult = publishDateElement.textContent.match(dateRegex);

		if (dateMatchResult != null && dateMatchResult.length > 0) {
			var publishDate = dateMatchResult[0];
			publishDate = publishDate.replace("/", "-");
			publishDate = publishDate.replace("/", "-");
			publishDate = publishDate.replace("/", "-");
			publishDate = publishDate.replace("/", "-");
			publishDate = publishDate.replace("/", "-");
			publishDate = publishDate.replace("/", "-");
			
			movie.publishDate = publishDate;
		}
		
		movie.uniId = doRemoveInvalidChar(uniIdElement.textContent);

		movie.actor = doMergeInfo(actorElement);
		movie.category = doMergeInfo(categoryElement);		
		movie.producer = doMergeInfo(producerElement);
		movie.tag = doMergeInfo(tagElement);
		

var playerElement = document.querySelector("#player");
if (null != playerElement) {
  var posterUrl = playerElement.getAttribute("data-poster");
  if (null != posterUrl) {
    movie.imageUrl = posterUrl;
  }
}







		var links = [];


		try {

			var link = new Object();
			link.sourceUrl = doFindDownloadUrl();
			link.refererUrl="https://njav.tv/";
			link.hls=true;

			links.push(link);

		} catch (e) {

		}


		downloadInfoList[0] = new Object();
		downloadInfoList[0].downloadLinks = links;

		result.movie = movie;
		return result;
	}
}

function doParseMovieDetailInfo() {
	return JSON.stringify(internalParseMovieDetailInfo());
}

return doParseMovieDetailInfo();
