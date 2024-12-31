# Txing 编程在线学习平台
## 项目简介
本项目旨在为编程爱好者和开发者提供一个高效、便捷的在线学习平台，该平台为集在线做题、编程竞赛、即时通讯、文章创作、视频教程、技术论坛为一体的综合平台，一站式地满足用户的各种编程学习需求，而无需在多个平台间来回切换学习。

系统分为客户端以及后台管理端两部分，两部分均采用前后端分离的架构。在前端中，主要使用 Vue3、TypeScript、CSS 等主流技术进行开发，在后端中，主要使用 SpringBoot、Mybatis-Plus 以及 Redis、Rabbitmq 等中间件以及 ElasticSearch 搜索引擎等技术进行开发。此外，代码沙箱基于 Docker、Shell 等技术进行实现。

## 系统设计
### 总体架构设计
在系统中使用Nginx作反向代理服务器以及部署前端项目，由于Nginx采用了异步、事件驱动的架构，能够有效地处理大量并发连接和请求，因此有助于提供稳定和可靠的服务。同时在项目中使用Nginx作反向代理服务器，可以快速配置用户访问限流、IP 黑白名单等，有效保证安全性。同时当日后用户量增加了之后，单个服务器可能无法满足正常使用，可以根据需要部署多台服务器，再通过Nginx进行负载均衡，将请求分配到多个服务器上，有效分摊各个服务器的压力，避免某个服务器出现性能瓶颈[11]。

此外，项目代码沙箱将会被作为一个单独的项目进行部署。对于各服务的部署，包括Nginx、Redis、RabbitMq、Mysql等，均使用了Docker进行部署，大大简化了部署流程以及保证了宿主机的安全性。系统的大致架构如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735706032257-e3859d3e-51dc-4e2c-b323-b632eb3f9bcc.png)

### 做题模块设计
在对用户作答代码的执行与判题的过程中，由于需要远程调用代码沙箱执行代码，因此这是一个执行时间较长的过程，如果在用户提交代码后立即进行处理，这样遇到高峰期，在短时间内有大量的用户提交判题请求时，系统很大可能会由于不堪重负而宕机，这在系统中是绝对不允许的。因此，在本系统中引入了消息队列，使用Rabbitmq消息队列进行异步处理、流量削峰[9]，这样当用户提交代码后，会先将用户的提交记录保存至数据库并且把submitId发送至消息队列中，随即把submitId返回给前端即可。系统会监听对应的消息队列，消费到队列里的消息，进而执行代码、整理结果等操作，待得到结果后把结果保存到数据库即可。这样一来，尽管有再多的请求，也会在队列里等待，系统可以根据自己的能力对消息进行一个一个消费，而不会出现一下子把系统击垮的情况。

对于判题结果的获取，由于引入了消息队列，用户的作答是以异步的方式被处理的，因此在接收到前端的作答后，并不能直接向其返回判题结果，而是返回submitId，前端拿到submitId后，设置一个定时器，每间隔设定的时间就使用submitId向后端请求结果，如此轮询，直到获取到结果为止。整个流程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735706062356-7330a90c-744a-4bb8-9c46-209ab09fef39.png)

在判题的过程中，首先会检查提交记录的状态，判断其状态是否是“waitting”，如果不是，表明该提交记录已经被处理过了，不会往下处理，如果是，就会先把该提交记录状态修改为“judgeing”，以避免发生重复处理的情况。接下来就是调用代码沙箱执行代码了，会先获取到代码沙箱实例，然后通过该实例来调用代码沙箱，在调用前，还会使用代理模式对代码沙箱实例进一步封装，以方便在对远程沙箱调用前后添加打印日志等一些操作。在调用沙箱获取到执行结果后，开始进行判题，首先判断是否有编译错误，如果有，直接作为判题结果即可，否则把代码执行的输出结果和预期输出用例一个一个进行比对，判断是否一致，直到遍历到结果错误的一个输出或者完成遍历再终止，当出现结果错误的输出时，会将该输出对应的输入用例保存下来，以记录第一个出错的测试用例，方便用户进行排查代码，然后计算通过用例比例，即可成功得出判题结果。若所有的测试用例都通过，就会判断此次执行的占用内存和执行时间是否超出该题目的限制，如果是，就会记录下来，得出判题结果，否则，进行计算本次提交结果超越的用户比例，主要结合代码的占用内存以及执行用时进行排行计算。在得出判题结果后，将结果保存进数据库，之后修改该题目的提交次数以及当前用户的做题数、提交数、通过数等数据即可。判题过程如下图所示。![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735714942963-0e3047ac-6691-4dd4-84ca-2c1120502f3e.png)

### 代码沙箱设计
在代码沙箱的实现中，灵活应用了多种设计模式，有效减低其代码耦合度以及提高其可扩展性。代码沙箱需要提供对多种语言代码执行的支持，不同的编程语言的执行过程以及环境是不一样的，因此需要为每种编程语言实现一个代码执行类，在系统中创建了一个代码执行接口CodeSandBox，定义了execCode执行代码方法，只需要为各种编程语言提供一个该接口的实现类，实现其中execCode方法即可，同时定义了一个CodeSandBoxManager管理器，其主要负责根据用户代码的编程语言类型选择一个相对应的代码执行实现类实例，进而执行代码，在这里的做法便是策略模式的应用。

此外，由于不同的编程语言代码的执行在整体上流程是比较相似的，整体流程都是由将代码保存为文件、编译代码、运行代码、整理结果、删除代码文件五大步骤组成，因此在此应用了模板方法设计模式，定义了一个模板类CodeSandBoxTemplate，该模板类实现了前面的代码执行接口CodeSandBox，并且实现了其中execCode方法，以及定义了将代码保存为文件、编译代码、运行代码、整理结果、删除代码文件5个方法，在execCode方法对该5个方法进行调用，完成对代码的执行，当需要添加对一种语言的支持时，只需要实现该模板方法类，并且重写其中的运行代码方法即可，当然也可以根据实际情况重写其他的方法。通过这种做法，相当于使用了模板方法类抽取出了大量的共用代码，无需在每个语言的执行实现类中都重复编写很多重复的代码，有效地降低了代码的重复度。综上所述，代码沙箱处理代码的整体流程如下图所示。![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735715575031-e3dfd07c-f3a9-43a8-8187-d2a6ee8d6341.png)

对于代码的运行，可以直接在服务上运行，但是这种方法代码是直接运行在主机上的，因此无论是对于用户代码访问权限的控制、还是对资源使用的限制等，都是较难实现的。然而在评测系统中，用户提交的恶意代码导致的服务器损坏、数据泄露等计算机安全类问题经常出现[16]，因此这种方法不能很好地保证主机的安全。

因此，在代码沙箱中，选用了Docker容器来执行用户提交的代码，基于Docker容器的隔离性，可以将用户代码与宿主机进行隔离，并且通过限制Docker容器的内存大小、运行时长、网络访问限制等，可以在很大程度上保证宿主机的安全，尽管Docker的内部环境受到破坏，宿主机也不会被波及，因此基于Docker容器实现代码沙箱是一个十分不错的选择。

本代码沙箱使用了第三方库Docker-Java来操作Docker，当需要执行代码，首先需要使用准备好的Docker镜像创建容器，然后启动容器，同时会设置容器的内存使用上限、CPU核数上限、禁止网络访问、文件系统只读权限等。在容器启动后，遍历输入用例集合，对于每个用例，都会被作为参数输入运行一遍代码，并且保存其运行结果、内存使用以及耗时等信息，运行代码是通过docker exec命令来实现的，使用docker exec命令执行准备好的Shell脚本，在Shell脚本中，会通过java命令运行用户代码，同时也会通过ps命令获取到用户代码运行进程占用的内存大小，并且输出。对于运行时间的获取，在系统中为每种编程语言提前准备了一个主类，在主类的主方法中对用户的代码进行了调用，而在调用的前后会获取到时间戳，最终即可计算出代码的运行时间并且打印输出。对于Docker容器的输出，主要使用了Docker-Java库提供的ExecStartResultCallback回调函数来获取，并且从中解析出运行结果、内存占用、执行耗时等信息，其中为了能辨别出内存占用以及执行耗时信息，打印时会在这两者的前后分别拼接上一个特殊字符序列，这样在识别时就能有效地区分了。直到把所有输入用例都遍历完，就会删除容器，并且返回执行结果。此外，对于代码沙箱使用到的Docker镜像，主要是使用了DockerFile为各种编程语言自制作了具有相对应环境以及具有前面提到的Shell脚本的镜像，这样一来，只要通过这些镜像启动容器后，就可以通过执行shell脚本来运行代码。代码的执行过程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735715598070-1317e076-6f88-4f7d-9828-cbd5efb4fa00.png)



### 竞赛模块设计
#### 周赛
平台的设定是每周周六上午10:00~ 11:30举行周赛，竞赛内容一共是5道程序题，在比赛结束后会对所有的用户的作答进行执行、判题、整理、排行，最终给出用户的成绩排行结果，在此过程中，会综合用户的作答通过题目数、通过测试用例比例、代码运行的内存占用以及时长、用户作答的用时等多方面的情况进行评比，尽量为用户提供一个公平公正的结果。

用户参加比赛时，系统会进行校验，限定每场比赛每个用户只能参加一次以及比赛正处于进行中状态。同时也会记录当场比赛的参与人数。用户在作答的过程中，可以通过“提交并保存”按钮对单个题目的作答进行保存与执行，同时可以查看运行的结果，以便于定位代码的问题。在用户主动点击提交按钮或者作答时间到了时就会为用户提交作答。周赛进行的流程如下图所示。![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722132341-ac31065f-afbe-4ce2-8c25-3fbfe92c22f5.png)

在用户通过“提交并保存”按钮提交单个题目时，在后端首先会判断前面是否有提交过，如果没有的话直接保存该作答并且发送到消息队列等待执行，否则，就会将当前提交的作答和前面的作答代码进行比较，判断代码是否有改动，若有，就会更新作答到数据库并且发送到消息队列等待执行，否则，直接给前端返回“无需执行”的提示信息即可，这样设计的好处是当用户多次提交相同的代码时，例如用户连续点击提交按钮，后端系统只会执行一次作答代码，有效避免了系统资源的浪费。流程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722141618-caee99e2-c7aa-44dc-b6ee-594f34938e43.png)

对于周赛作答提交的处理，项目中还是使用了Rabbitmq消息进行流量削峰的方式，即后端接收到用户的作答后，不会直接进行处理，而是先发送到Rabbitmq消息队列，等待被系统监听消费而进行处理。之所以采用这种方式，是考虑到临近作答时间结束时或者作答时间到时，可能会大量的用户进行提交，如果不采用一些流量削峰的手段的话，很有可能系统会因请求量过大而瘫痪，无法继续提供服务。因此，在这里引入消息队列来保证系统的稳定性、可靠性。流程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722169746-619e3b5d-dd21-4b2a-8d7b-2c5527a5bd8a.png)

系统在监听到周赛作答等待处理队列的消息后，首先会收集需要执行判题的作答集合，因为有可能有些题目的作答该用户在作答过程中已经提交并执行过了，并且在之后就没修改过代码，因此无需重复执行，所以此时要先过滤一遍，以免引起不必要的资源浪费。在所有作答执行结束后，会计算并且保存该用户的AC题目数、AC题目数的得分总和、未AC题目的通过用例比例总和等，其中AC题目的得分计算方法是score  
= 通过用例比例 * 60 + （内存使用上限 - 实际内存使用）/ 内存使用上限 * 20 + （耗时上限 - 实际执行时长）/ 耗时上限 * 20。接下来会判断此场比赛是否已结束并且所有用户作答均已处理完，若是，即可以统计最终排名结果，并且把结果存储在Mysql中，再更新比赛状态即可。其中对用户进行排行时，基于AC题目数、AC题目数得分总和、未AC题目通过用例比例总和、用户作答用时四个指标进行从前到后进行比较排行，从而得到最终的结果。作答处理流程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722188523-ff44a2aa-181a-4f13-95d6-6605b069493b.png)

此外，考虑到比赛可能会存在比赛时间结束后而不被执行统计结果并且修改比赛状态的情况，所以在系统中使用了Rabbitmq的延时插件实现了延时队列，在比赛被创建出来时，就会向Rabbitmq发送一个消息，此消息会包含该比赛的信息，并且该消息会被设置延迟到比赛结束时才会被投放到目标队列中进而被消费。因此，在比赛结束时，系统会消费到该消息，到数据库查出此场比赛并且检查此状态，如果状态为未完成，那么就会进行结果统计并且修改其状态为已完成。这里通过延时队列的来实现自动检查并整理比赛结果，避免比赛结束后不及时被关闭的情况。

#### 在线 PK
在系统中，在线PK的比赛内容是在20分钟内完成一道编程题目的作答。在线Pk功能实现的难点之一是比赛对手的匹配实现。在多个用户同时进行操作的情况下，如果处理不妥当，就会有线程安全方面的问题，例如同一个用户被多个用户同时匹配上等情况，系统中主要基于Redis + synchronized同步锁实现对手匹配功能，通过同步锁来确保在多个用户同时操作的情况下，仍能正确完成匹配。

匹配功能实现中，使用了Redis中Set集合数据结构，用于暂存正在匹配的用户Id，鉴于该数据结构的元素不可重复这一特性，能有效避免集合中存在重复的用户id。在用户点击开始在线PK后，会到Redis的Set集合中随机取出一个元素（同时会从集合中删除），如果取到的值是null值，说明此时不存在其他的用户正在匹配中，无法匹配成功，然后就先把当前用户的id添加到set集合中，以供其他用户进行匹配，这个过程是使用synchronized保证同步执行的，因此不会有线程安全问题，接下来返回给前端，告知前端暂未匹配成功，需要继续等待；如果前面取到的值不为null，说明此时已成功匹配到对手，就会通过Websocket连接实时通知对方，确保双方均进入PK的界面，开始PK。

在用户完成作答并提交后，会进入到比赛结果展示页，此时如果对方暂未完成比赛，就会显示等待结果中的状态，并且使用定时器每间隔3s的时间去向后端查询比赛结果，直到查询到比赛结果再在界面上显示比赛结果信息。其中在线PK的得分计算公式为score= 通过用例比例 * 60 + （内存使用上限 - 实际内存使用）/ 内存使用上限 * 10 + （题目执行耗时上限 -实际执行时长）/ 题目执行耗时上限 * 10 +（比赛总时长 - 实际作答用时）/ 比赛总时长 * 20，其中，只有作答通过所有用例才会计算内存、执行用时、作答用时方面的得分，否则只会计算通过用例的基础分，这是因为在评比中，通过用例比例被作为主要的评分指标，需要避免出现完全通过测试用例的用户比通过部分测试用例的用户的得分要低。在线PK的过程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/jpeg/29312866/1735722214791-fe4e87bd-5fa3-48c9-85fe-0b08181991d3.jpeg)

考虑到在线PK竞赛也和周赛一样存在比赛在结束后没及时被统计结果并修改状态的可能，因此在这里采用了和周赛中一样的做法，即采用延时队列实现在比赛结束时及时进行检查，具体的实现由于前面在周赛的实现中已经出现过，因此这里不重复赘述。

### 聊天模块设计
系统中，使用Netty框架以及Websocket协议实现了聊天功能。在后端中，使用Netty搭建了Websocket服务器，负责与客户端建立Websocket连接以及消息的收发。在前端部分，由于JavaScript单线程的特点，如果所有任务均在应用主线程进行，一些耗时的计算任务会影响应用的及时响应。引入 Web Worker 后，可以将一些大计算量代码交由 Web Worker 并行执行，使得应用主线程可以及时响应[13]。因此使用Web worker管理 Websocket连接与收发消息，降低主线程负载，提高运行效率。WebSocket连接是建立在Http连接之上的，与Http不同的是，一旦WebSocket连接建立之后，不但客户端可以主动给服务端发送数据，而且服务端也可以给主动发动数据，并且可以一直保持连接，因此WebSocket协议很适合用于实现聊天功能。

在用户登录成功时，就会为其建立WebSocket连接，因此只要用户登录上系统，就能收发消息。此外，在建立WebSocket连接时，客户端需要带上Token值，此Token在登录成功后就会给前端返回，前端将其保存到localStorage中，在建立WebSocket连接时带上，后端就会获取该Token并对其进行校验。如果校验成功，说明该用户已登录，给予建立连接，否则，则不能成功建立连接。

在连接建立成功后，需要一种方式让服务端确保每个客户端的连接是活跃的，当某个连接失活后，要及时检测出来并将其剔除掉，以免造成不必要的资源浪费。在系统中采用了心跳机制来实现，即在客户端与服务端建立好WebSocket连接后，就启动一个定时器，每隔10s就向服务端发送一个心跳包，让服务端感知其存活，而在服务端，通过给socketChannel添加了一个空闲状态处理器，设置读空闲事件触发时间为30s，当服务端超过30s没有收到客户端的任意数据包时，就会触发读空闲事件，进而将该连接进行剔除。

关于消息的发送，当用户点击发送消息后，前端会请求后端提供的发送消息接口，后端首先会判断目标聊天房间是否可用，例如是否处于禁用、群聊已解散、好友已删除等状态，如果是群聊的话，还会判断发送方是否不是群聊的成员，如果是，就不能发送消息，给前端直接返回；否则，就会将消息保存到数据库当中，同时向消息发送队列发送msgId，也会把msgId向前端返回。前端拿到msgId后，会向后端请求完整的消息内容，并且把此消息添加到消息列表中展示出来。同时，后端也会监听消息发送队列，从中消费消息，拿到消息后，会根据消息内容更新对应的聊天房间以及会话的活跃时间以及最新消息字段，然后获取消息接收方的用户id集合，进而封装好消息推送实体，发送到Ws消息推送队列。后端也会监听Ws消息推送队列，从中消费消息，从消息中获取到用户发送的消息以及接收方的用户id集合，通过在线用户的channel集合来判断目标用户是否在线，如果在线，就进行推送，否则，无须做任何处理。发送消息过程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722249253-690a682c-5cb4-4c7b-af84-1f2e4be848e1.png)

### 文章模块设计
在文章模块的实现中，需要提供一个Markdown编辑器，在项目中使用了字节跳动开源的Bytemd Markdown编辑器，其提供一套完整的Markdown编辑器组件，具有定制性强、插件化、轻量级、易用等特点，支持文本、图片、引用、链接、代码块、列表、表格、公式、流程图、时序图、类图、饼状图等多种内容的输入，可以很好地满足用户的写作需求。

对于文章插图以及文章封面等图片的上传与访问，项目中使用了腾讯云的对象存储COS。使用对象存储有诸多优势，可以有效保证数据的可靠性，同时可以减低系统的压力，毕竟图片是属于较大的资源，如果使用服务器去处理，会对服务器的带宽等配置会有较高的要求，否则无法保证图片资源的访问速度，此外，腾讯云对象存储提供了一整套健全的解决方案，例如可以配置防盗链、跨域访问以及告警策略等，可以更好的满足系统的使用需求。

在系统中，为了提高文章的检索效率以及用户的使用体验，使用了ElasticSearch搜索引擎对文章进行检索。其基于倒排索引和分词器可以实现高效率的全文检索，同时也支持高亮查询、分页查询等。其中在项目中，使用了IK分词器，可以有效对中文检索词进行分词。ElasticSearch对用户的搜索词进行文本分析，在索引数据库中快速搜索目标文档，评估搜索结果之间的关系，最终对结果进行排序返回[15]。系统中基于文章的标题以及简介进行全文检索，同时可对匹配关键词进行高亮显示，与Mysql实现的模糊查询相比有了一个很大性能上、体验上的提升。

引入了ElasticSearch后，自然需要解决ElasticSearch和Mysql两者之间的数据同步问题。项目中对此也有对应的解决方案，在系统启动时，会把文章数据从Mysql中上传到Es中，此次同步相当于全量同步，同时考虑到数据量可能比较大，因此每次上传500条记录，分多次进行上传。在系统启动后，就是增量同步了，使用定时器，每分钟进行上传，上传只查出update_time字段值在5分钟以内的记录进行同步，以免造成资源浪费。

在进行点赞文章、收藏文章等操作时，只会更新Mysql。由于使用了定时器进行同步的方法，因此，当文章的点赞数、收藏数等信息发生变更时，用户看到的可能不是最新的数据，存在数据不一致的情况。对此，在查询文章接口中，当从Es中查出文章数据后，会到Mysql查出对应的文章记录，以获取到最新的动态数据，确保用户看到的数据是最新的。

用户在发表文章时，需要进入到文章撰写界面，其中需要填充的信息有文章标题、正文内容、封面、简介等，当用户点击提交后，后端会先将文章保存到Mysql中，再上传至Es中，最终返回文章的id，前端会跳转至文章详情页展示该文章，当然，此时该文章还不能在检索页面被检索出来，而是先要等待审核通过！发表文章流程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722264459-0ac6728f-82d6-4e51-bcf5-9df23f16e40d.png)

### 课程模块设计
在课程模块中，涉及到到视频的播放、上传等功能，引用了腾讯云的VOD云点播服务，与对象存储相似，其有着高可靠性、安全性等特点，提供了丰富的视频处理功能，包括转码、截图、水印、字幕、视频拼接等，可以根据自身系统的需求进行选用，同时其结合CDN技术，可以快速地将视频内容分发给各地的用户，大大提高用户的播放速度以及观看体验。其还提供实时的数据统计与分析，可以实时的查看视频的播放量、观看时长等信息，方便于系统的运营，此外，其提供一系列的安全机制，例如访问权限控制、防盗链等。

为了确保安全性，在前端上传视频或者是播放视频时，都需要先向后端请求签名，然后使用签名进行访问，签名会有一定的使用时限。

当用户需要发布课程，需要进入到课程发布界面，需要填充课程的标题、方面、课程简介、课程小节（上传视频和填写小节名称）等信息，用户在创建小节时，当用户选定要上传的视频后，在前端会使用Js获取到视频的首帧图片作为该视频的封面图，随着视频一起上传到腾讯云VOD中。用户可以查看课程的小节列表、小节数目、每个小节的视频封面以及视频时长，同时可以修改或移除某个小节以及点击某小节视频的封面图预览视频。在填充完所有信息后，点击立即发布按钮即把该课程所有信息提交到后端，后端对课程基本信息以及各个小节的信息进行保存，同时会计算课程的总时长，以便在前端展示时显示时长，最后后端返回该课程的课程id，前端跳转至视频播放页播放该视频，但此时视频暂未通过审核，无法在课程搜索页被检索出来。课程的发布流程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722758891-b6a4fd8a-e30a-413d-ba3c-5544fc523984.png)

对于课程的检索，根据用户在搜索框内输入的关键词在数据库中对课程标题以及课程简介进行模糊分页查询，返回匹配上的目标记录。

### 论坛模块设计
论坛中的贴子内容主要由标题、内容、配图三部分组成。当用户发表帖子时，在贴子发表界面填充完三部分的信息，即可点击发布。其中配图数量限制为最多9张。后端在接收到帖子数据后，会对帖子进行合法性校验以及保存至Mysql数据库，同时返回帖子id，前端即弹出帖子详情展示框展示该帖子内容。发布帖子的流程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722777774-0c7c3084-15bf-4e95-aa4f-3543a1039d32.png)

对于帖子的检索，根据用户在搜索框内输入的关键词在数据库中对帖子标题或者帖子内容进行模糊分页查询，返回匹配上的目标记录。

评论与回复功能是论坛模块中很重要的一个功能，也是论坛中用户间主要的一个互动途径。对于这一功能的实现，项目中通过在帖子评论表中加上reply_id（回复id）字段记录评论记录之间的回复关系，在查询评论时，首先查出帖子的一级评论，再通过reply_id字段查出所有一级评论的回复评论，对于二级评论，依旧如此，如此类推，最终封装好帖子的评论信息返回给前端进行展示。

## 效果展示
### 做题模块
在题库中心界面，分页展示题目，其中分页查询主要使用了Mybatis-Plus自带的page方法实现，查询结果默认按照创建时间createTime降序排序，在拼接查询条件时，判断用户查询请求是否带上标签参数或者关键字参数，如果有，则在QueryWrapper对象中拼接上对应的参数，最后再到数据库查出目标记录。

题库中心界面如下图所示，找到合适的题目点击“Go”即可前往做题。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722793224-1d9f56cb-3d1f-4e04-a885-6e85d92aada3.png)

在做题界面，可以查看到题目的内容、评论、参考答案以及提交记录，其中代码编辑器是在开源的代码编辑器上进一步封装实现的，支持Java、JavaScript、Python以及C语言等多种编程语言的输入，同时支持代码关键字的高亮。此外，系统中基于字节跳动开源的Bytemd Markdown编辑器进一步封装，得到了用于展示MarkDown语法文本的组件MdView，在本界面中用于展示了题目的内容详情与答案。

用户在完成代码的输入后点击“提交作答”，即可在右下方的运行结果区域查看代码执行结果，如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/jpeg/29312866/1735722811678-988e2383-cfe0-4124-a8c2-d265fdf1d9ca.jpeg)

点击页头导航栏的图表图标，即显示用户个人在近10天的做题情况，其中包括折线图以及柱状图显示，如下图所示。可视化图表的实现，主要使用Echarts开源可视化库，使用折线图展示用户的做题数、提交数、通过数等，使用柱状图展示用户的做题通过率。

![](https://cdn.nlark.com/yuque/0/2025/jpeg/29312866/1735722833814-897bc966-bf7c-44fa-821c-003b9080cbc2.jpeg)

此外，在用户提交作答后，作答将会交由后端处理，后端调用代码沙箱执行代码，进而获取执行结果以及完成判题。

### 文章模块
在文章检索界面，可以快速查找到目标文章，并且在检索结果中会高亮显示检索关键字，如下图所示。对于文章记录的请求，主要使用了懒加载的方式，文章记录分页返回，通过监听浏览器滚动条的滚动事件，只有当界面滚动到底部才会触发请求下一页文章记录并且展示，以免造成资源浪费。

![](https://cdn.nlark.com/yuque/0/2025/jpeg/29312866/1735722848622-083c5a82-dafa-4ac6-acfa-5b906dd9e237.jpeg)

在文章撰写界面，左侧基于MarkDown语法撰写文章内容，在界面右侧可以看到文章的实际效果，主要是基于ByteMd编辑器实现，效果如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722858857-0bd308bd-fb9b-45a9-abf1-bf3fe006d60d.png)

撰写完文章内容后，点击右上角的“发布”按钮，就会弹出可以上传文章封面图、编写文章简介的弹窗，完成后点击“发布文章”即可，如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722869995-5f8d2b13-bc4c-40b1-ab7f-e06138ff6202.png)

### 课程模块
在课程检索界面，可以输入关键词查找课程，并且在检索结果中显示每个课程的封面图、作者信息、课程时长、课程标题等信息，如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722885476-2c87bf6c-4768-4de1-b2de-26f8247540d9.png)

点击课程后，即进入到课程的播放界面，通过左侧的视频播放器播放视频，通过右侧的视频选集区域进行选集播放，通过视频播放器的下方的爱心图标收藏课程。如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722895069-7b70c428-b8f3-4a88-b4de-a4b4801526a3.png)

### 论坛模块
在帖子检索界面，可以输入关键词查找帖子，并且在检索结果中显示每个帖子的标题、配图等信息，过多图片的可通过滑动滚动条查看帖子的所有图片，如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722906686-2987e0ee-c2a0-48a1-9e75-df0db8aae788.png)

点击帖子记录的标题或者内容即可查看帖子的详情，并且在下方可以评论帖子或者回复评论，此外也可以点赞或者收藏帖子，如下图所示。![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722916331-bcf4dbc5-42a8-4a62-8f1a-cbdf0c8b55c6.png)

### 聊天模块
点击顶部导航栏的铃铛图标即可打开聊天框，支持群聊或者私聊，如下图所示。在消息发送前，会进行一系列的校验，例如判断当前聊天房间是否可用、对方是否是当前用户好友、群聊是否已解散等，只有通过校验，符合发送消息条件，才能发送成功。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735722927832-d07b1d9d-69be-449d-8034-c6ddf26683b1.png)

### 竞赛模块
在周赛中心界面，可以查看当前周赛以及往期周赛的信息，以及竞赛的排行信息，用户可以选择参加周赛或者模拟练习周赛，如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735734383384-3062936c-e1f4-40b1-933c-1b6e30e897b7.png)

在周赛进行中或者模拟进行中，右上角会显示本场比赛的时间倒计时以及交卷按钮，通过左上方圆型的题目序号可进行题目的切换，如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735734395457-5a42c56a-aaf1-4fb1-8e55-1b0c22cd478f.png)

在PK竞赛中，点击PK竞赛时，首先会进入到对手PK界面，如下图所示。匹配对手成功后，就会进入到PK进行中界面，和前面的做题界面比较相似。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735734411972-fc0dd808-a5e1-444b-a696-b4694c3180d9.png)

