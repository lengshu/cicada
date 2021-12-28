Cicada 功能介绍
-----------

  
Cicada是一个用来下载和管理电影的软件，有点类似于Youtube-dl，只不过它是使用Java开发的一个Eclipse RCP，自带GUI。  

**它提供了以下功能：**

1.  可以将预先定义好的影视站点里的所有电影或者指定的部分电影信息下载到本地，以浏览器或者表格的方式加以查看和查询。
2.  对于喜欢的电影，可以直接下载到本地。
3.  目前支持普通的Http/Https下载，以及HLS（M3U8）流媒体的下载及合并（合并时需要Ffmpeg，但下载时无须Ffmpeg）。
4.  对于站点视频信息和下载地址的解析，使用了浏览器中调用JavaScript的方式，所以允许任何对Javascript有所了解的人员自行定义如何解析一个站点并下载。
5.  提供了一个简单的脚本开发和测试工具，方便用户自行修改和扩展。
6.  程序使用了Eclipse插件技术，允许用户自行扩展新功能。  

作为一个视频管理和下载软件，它的使用是非常简单的，接下来，你可以直接使用它，或者可以通过下面的详细介绍深入了解其功能。  

[\[主界面说明\]](content/ui.html)  
[\[配置说明\]](content/config.html)  
[\[扩展说明\]](content/extension.html)  
[\[技术说明\]](content/tech.html)  
[\[FAQ\]](content/faq.html)  

这是一个使用 [Eclipse Public License2](https://www.eclipse.org/legal/epl-2.0/) 的开源软件，你在遵守其协议的情况下可随意使用和分发，并为自己的使用和分布负责。

**备注：**由于个人时间和精力原因，所以这个软件主要是在Windows下开发和测试的，虽然也在Linux下做了一下简单的测试，可以正常的运行，但并没有覆盖到所有的功能，如果有问题，[可以点击这里提交反馈](https://github.com/aquariusStudio/cicada/issues)，我会尝试解决。
