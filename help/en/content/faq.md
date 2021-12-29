### FAQ

#### Compared with Youtbe-dl, what are the characteristics of this software, or why do users use it?

1. Youtbe-dl is a very good software, but it is only a software without a GUI, so for ordinary users, it is necessary to install an additional software with a GUI interface for easy use.
2. This software brings a complete GUI operation interface to facilitate video management, and it can also directly generate Youtube-dl download instructions.
3. Youtbe-dl is developed in python language. I hope to lower the threshold even more. By executing JS directly in the browser, the threshold can be lowered even more.

#### Why can't I update some video information?

1. There may be a problem with the page of some videos. For example, the website has deleted it, or for other reasons, it is really helpless for the program.
2. Sometimes, because the speed of updating the video is too frequent, the website may be suspended. In this case, you can wait another 24 hours, and open the corresponding site resolver at the same time, and adjust the interval appropriately.
3. Generally speaking, if the visit is suspended by the website, the program will try to find this situation and try to give a prompt. You can pay more attention to the prompt information.

#### Why can't I download some videos?

Generally, videos that cannot be downloaded will have an error message. You can pay attention to the message. There are mainly the following possibilities:

1. If the corresponding resolution is specified in the preferences, then if a video does not have that resolution, it will not be downloaded. In response to this problem, you can try to allow more resolutions to be downloaded (by default, only the two common formats, 720P and 1080P, are turned on).
2. The link of some website videos will be invalid. In this case, even if the program automatically tries multiple sources, there is really no way to download it.
3. Some video sites may have stricter measures to prevent downloading. If you encounter this situation, [you can click here to submit feedback](https://github.com/aquariusStudio/cicada/issues), I will try to solve it.

#### Why do I need ffmpeg?

1. Many websites use HLS (or M3U8) streaming media solutions, so thousands of small files are actually downloaded. These files need to be converted into corresponding media files, which are needed for conversion. ffmpeg.
2. ffmpeg can also be used to download HLS streaming media and generate individual files.
3. You can visit [ffmpeg's official website](http://www.ffmpeg.org/) to get the corresponding version.

#### Why can't I watch some videos after downloading?

1. First check whether your computer or mobile phone can play various local media normally. If you are sure that it can, please check online to see whether the video can play normally (the videos of many video websites will also become invalid or only available to members).
2. There may be part of the wrong content during the download process. After deleting the video, try to download it again to see if it can be played.
3. Because the HLS protocol is more complicated, I am not too familiar with the content of streaming media, so for some HLS protocol streaming media, it may not be downloaded correctly, and I will try to solve this problem in the future. You can use other software to download by generating external links (such as N\_m3u8DL and ffmpeg can download HLS media files, you can visit the official [N\_m3u8DL](https://github.com/nilaoda/N_m3u8DL-CLI) Website, get the corresponding version).
4. If none of the above methods can be played, you can try to use the method of generating a download link to download the video with an external download tool to see if it can be played. If the problem is solved in this way, [you can click here to submit feedback](https://github.com/aquariusStudio/cicada/issues), I will try to solve it.

#### After copying some links, why can’t I access them in the browser or download them with other software?

1. Many video websites make links dynamic for reasons such as advertisements or clicks. After a certain period of time, or visits with non-designated IP, the link will become invalid.
2. For some websites, a lot of checks are used when downloading. For example, the Referer in the request must be from the specified source. In this case, only Aria2 supports specifying Referer. You can try to generate a link to Aria2 and use Aria2 to download.
3. Due to the limitation of personal technical level, when using Java ssl certificate to visit some websites, the links obtained are dedicated and cannot be used in browsers or other downloading software. In response to this situation, you can try to generate a list of download links (in this case, the browser's SSL access will be called to generate dynamic links to ensure that they are available).

#### In addition to using JS for extension, are there other extension solutions?

1. Groovy can be used, but considering the workload of development and testing, it has not been added. If there is a large demand, Groovy support will be added in the future.

#### When the machine configuration is good, why sometimes it feels like stuck?

1. If you turn on the function of automatically refreshing the video list and video information in the background, it may be caused by the background refresh (the browser is also running in the UI thread), which will bring some feeling of stuck. In view of this situation, you can consider only turning on the automatic refresh video list and turning off the automatic refresh video information function. This will only occupy resources in a small amount of time after startup. The disadvantage is that the video information is not complete and it is not easy to search. You can also consider specifically allowing the software to refresh the historical data automatically or manually in idle time, and then turn on the automatic video refresh function. This way, the refresh speed is very fast for websites that generally only add dozens or hundreds of videos a day. There will be no performance problems.

#### Why do some browser advertisement windows pop up sometimes?

1. Under Windows, please open the Internet option, in the "Privacy" tab, select "Enable pop-up blocker", and then use the "Settings" button to set the "Blocking level" to "High" in the pop-up window. Basically block the vast majority of pop-up windows.
2. After the above settings, advertising windows will not pop up under normal circumstances, but many video sites contain a large number of advertisements. Although the program has worked hard to block these advertisements to prevent pop-up windows, it cannot be completely avoided. If this occurs In case, you can close the window directly. If it is very frequent, you can consider abandoning this video site, or [You can click here to submit feedback](https://github.com/aquariusStudio/cicada/issues), I will try to solve it.

#### Why do some browser warning windows pop up sometimes?

1. Under Windows, please open the Internet option. In the "Advanced" tab window, there is a "Do not match the certificate address" in the security sub-item. Just uncheck it.

#### What is the specific core of the browser used, and the compatibility when writing JS?

1. The software supports two browser kernels, which are the default browser provided by SWT for display (IE on the Windows platform, Webkit on the Linux platform) and EdgeWebView2 based on the Chromium kernel (available only on Windows) , And need to install the runtime library of WebView2, [click here to download and install the runtime library of WebView2](https://developer.microsoft.com/microsoft-edge/webview2/)).
2. Under normal circumstances, the default browsing and display video list provided by SWT is used, as well as to parse web pages and execute JS.
3. Under normal circumstances, it is not recommended to use EdgeWebView2 based on the Chromium kernel. One is that additional software is required, and the other is that EdgeWebView2 occupies too many resources (preliminary estimates require 1-2G more memory than the default browser).
4. If you must use EdgeWebView2, for example, Youtube no longer supports IE12, then set it in the preferences (after the setting is complete, it is recommended to restart the program). To
5. Even if EdgeWebView2 is enabled, the default browser kernel will still be used to display the video list. This is because Eclipse's current support for EdgeWebView2 is not complete, such as the inability to inject menus in the Edge browser.
6. If you want to use Edge to display the video list, you can start Cicada through the command line "cicada -vmargs -Dorg.eclipse.swt.browser.DefaultType=edge" and force EdgeWebView2 as the default browser.
7. When writing JS, try to consider the compatibility of IE/Webkit/Chromium.

#### Can EdgeWebView2 use Chrome extensions?

1. On the Windows platform, although the name of the WebView2 launcher is "msedgewebview2.exe", it is not Edge. The current Microsoft official statement is that extensions are not supported.
2. If one day in the future, it will support Chrome or Edge's own extensions. Based on past experience, directly visit [Edge's official extension site](https://microsoftedge.microsoft.com/addons/Microsoft-Edge- Extensions-Home) or [Chrome's official extension site](https://chrome.google.com/webstore/category/extensions), just install it.

#### What version of JRE do I need to use?

1. It has been tested in several major major versions, such as JDK8, JDK11 and JDK17.
2. But if you want to use the Edge browser, you must use JDK11 and JDK17, because Eclipse supports Edge only at version 4.19 and above, and 4.19 requires JDK11 or above.
3. If you are limited to JDK8, you can also use the Eclipse 4.9 version, so you can't use the Edge browser, which may cause some websites, such as Youtube, to not be supported correctly.

#### Why use Java?

1. I personally use Java more, and it's better cross-platform.
2. Use SWT because neither Swing nor Javafx can provide a convenient and easy-to-use browser component with better compatibility.
3. I have tested selenium, but if you need to open multiple instances for multi-threaded access, it takes up resources too much and it is not easy to share the Session. Although Tab is supported, it cannot be accessed by multiple threads and is not easy to use.
4. I have tried the browser engine (based on Webkit) with Qt, the compatibility is not good, and the availability of many websites is not even as good as IE.
5. CEF is a Chromium-based cross-platform solution, but its compatibility is also very poor. There have been multiple crashes during the verification process.
6. C# cannot be cross-platform, and IE is also used by default. If you use WebView2, it still consumes resources very much.

#### The configuration is wrong, what should I do?

1. Each setting of the preferences has a "restore default value" function, click to restore the initial value.

#### Why some websites, such as Youtube, have pictures when viewed in the browser, but the normal pictures are not displayed in the properties view?

1. Because Youtube uses images in webp format developed by Google, and Java has not officially supported images in this format, it can only be displayed through the browser, and cannot be displayed in the view.
2. We will consider adding this function later.

#### How to use proxy?

1. Sorry, the official support proxy is not currently supported, because it involves the Java program, the browser that comes with SWT and the Edge browser. It is difficult for a single Java program to correctly set up a proxy server for all three of them at the same time ( Involving system permissions and other issues), follow-up will try to solve this problem.
2. If there is such a demand, provide a temporary solution, first through the command line "cicada -vmargs -Duser.language=en -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=1080 -Dhttps.proxyHost=127.0. 0.1 -Dhttps.proxyPort=1080" to start Cicada, please replace "127.0.01" and "1080" in the example with the real proxy address and port, so that the Java program can use the proxy server. Finally, find the configuration of IE or Webkit in the system (if you use Edge, you also need to modify Edge), and use the same proxy server as above.

#### Why sometimes the program cannot be closed normally, or it still runs in the background after it is closed?

1. At present, it is found that there is a bug in the integration of Edge and SWT, that is, if the browser is closed while executing JS script, it may cause deadlock (earlier Eclipse 4.17 also supported CEF, and this problem also exists, so SWT has Chromium will no longer be supported in the follow-up). The author has tried his best to avoid this kind of deadlock, but there may be potential problems in this area, and there will still be a few cases.
2. If this happens frequently, you can consider disabling Edge (some websites may not support IE, so some video websites may not work properly).
3. If you find that the "cicada" process is still running for a long time (a few minutes) after exiting, it is recommended to kill the process.

#### The interface displayed by the browser is not easy to use, can it be customized?

Sorry, as a programmer, I am extremely unfamiliar with the front-end of HTML+JS, so any programmer who is familiar with the front-end can try to modify the contents of the Workspace/config/site directory under the Cicada directory to customize the display interface. [Detailed instructions can be found here. ](siteDefine.html)

#### Why is multi-threaded downloading set up, but only one or two threads are still used for downloading?

When downloading, the file will be segmented. If the size of a download segment exceeds the length of the entire file, only one thread will be used for downloading. For example, the default size of the download segment is 200M. If the file to be downloaded is only 100M, the program will only enable one thread. If you want to use more threads, you can set the download segment to 50M or less, but this is not recommended.

#### How to switch the interface language?

Currently four languages ​​(Simplified Chinese, Traditional Chinese, English, Japanese) are provided, and the current system language is used by default. If the current system language is not among these four languages, English will be used. If you want to specify a specific language, you can start Cicada through the command line "cicada -vmargs -Duser.language=en", where "-Duser.language=en" specifies the language as English. The corresponding code for simplified Chinese is "zh\_CN", the corresponding code for traditional Chinese is "zh\_TW", the corresponding code for English is "en", and the code corresponding to Japanese is "ja".

#### How to backup data?

All current data is saved in the Workspace directory of the program by default. You only need to copy the entire directory to complete the backup, as well as the restore.

#### Why is there only a browser and no tables?

1. The table uses NatTable, if it is not installed correctly, only the browser will be displayed.
2. The default version comes with NatTable. If you pack it yourself, please make sure to bring NatTable.

#### How to update the script?

1. If you get a script developed by others from the community or other sources, you only need to find the parser directory in the Workspace directory of the program, create a new directory based on the corresponding site name, and place the corresponding files in it. Please ensure that the structure is consistent with other sources. The directory is similar.
2. After copying is complete, restart Cicada.

#### How to install the latest version?

1. Download the latest version directly from the official website.
2. You can delete the contents other than the Workspace directory in the original directory and unzip it.

#### Some functions of Linux are invalid, such as double-clicking or deleting files?

1. This may be because the installed JDK does not have sufficient permissions. It is recommended to use sudo to install the JDK to ensure its permissions.

#### Why is there no automatic upgrade function?

1. Mainly because of limited energy, if subsequent users increase, I will add this feature.

#### Why is there no Mac version?

1. Because I personally don't have a Mac laptop, there is no way to test it.
2. If anyone is interested in providing the Mac version, you can contact me [aquarius.github@gmail.com](mailto:aquarius.github@gmail.com).