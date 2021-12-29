## Cicada function introduction

Cicada is a software used to download and manage movies, a bit similar to Youtube-dl, except that it is an Eclipse RCP developed in Java with its own GUI.



**It provides the following functions:**



1. You can download all the movies or specified part of the movie information in the pre-defined film and television sites to the local, and view and query them in the form of a browser or a table.
2. For favorite movies, you can download them directly to the local.
3. Currently supports ordinary Http/Https download, and HLS (M3U8) streaming media download and merging (Ffmpeg is required for merging, but Ffmpeg is not required for downloading).
4. For the analysis of site video information and download address, the method of invoking JavaScript in the browser is used, so anyone who knows Javascript is allowed to define how to parse a site and download it.
5. A simple script development and testing tool is provided, which is convenient for users to modify and extend by themselves.
6. The program uses Eclipse plug-in technology, allowing users to extend new functions by themselves.



As a video management and download software, its use is very simple. Next, you can use it directly, or you can learn more about its functions through the detailed introduction below. \



[\[Main interface description\]](content/ui.md)\
[\[Configuration Instructions\]](content/config.md)\
[\[Extension Description\]](content/extension.md)\
[\[Technical Description\]](content/tech.md)\
[\[FAQ\]](content/faq.md)



This is an application using [Eclipse Public License2](https://www.eclipse.org/legal/epl-2.0/)
You can use and distribute freely under the condition of complying with its agreement, and you are responsible for your own use and distribution.


**Remarks:** Due to personal time and energy, this software is mainly developed and tested under Windows. Although a simple test has been done under Linux, it can run normally, but it does not cover all of them. Function, if there is a problem, [you can click here to submit feedback](https://github.com/aquariusStudio/cicada/issues), I will try to solve it.