## 项目简介
本项目是一个基于 Docker 的代码沙箱系统，用于安全地执行和评测用户提交的代码。系统具有以下主要特点：

1. **多语言支持**：支持 Java、JavaScript、Python、C 等多种编程语言的代码执行。
2. **安全隔离**：使用 Docker 容器技术实现代码执行环境的隔离，有效防止恶意代码对宿主机的攻击。
3. **资源限制**：对代码执行时的内存使用、CPU 占用、运行时间、文件访问权限等资源进行严格限制。
4. **完整的执行反馈**：提供代码运行结果、执行状态、内存占用、运行时间等详细信息。
5. **灵活的设计模式**：
    - 采用策略模式实现不同编程语言的代码执行
    - 使用模板方法模式抽象代码执行流程，提高代码复用性
6. **可扩展性**：统一的接口设计，便于扩展新的语言支持

本项目可作为在线评测系统、在线编程平台等场景的核心组件，提供安全可靠的代码执行环境。

实际应用可参考 [GitHub - lemon-puls/txing-oj-backend 编程在线学习平台](https://github.com/lemon-puls/txing-oj-backend)

## 效果展示
### 正确执行
这里以求两数之和为例

请求体：

```json
{
  // 代码
  "code": "class Solution {\n    public void answer() {\n       // 请开始您的作答\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.print(a + b);\n    }\n}",
  "inputs": ["10 20", "345 456", "33 67"], // 输入用例
  "language": "java" // 编程语言：java、c、python、javascript
}
```

响应内容：

```json
{
  "msg": "success",
  "code": 200,
  "data": {
    "outputs": [ // 输出结果
      "30",
      "801",
      "100"
    ],
    "message": null,
    "status": 1, // 执行状态（0: 编译错误 1：正确运行 3：运行报错）
    "judgeInfo": {
      "time": 53, // 执行用时 ms
      "memory": 36480, // 执行内存占用：36480 KB
      "message": null // 执行信息（错误信息）
    }
  }
}
```

### 编译错误
```json
{
  "msg": "success",
  "code": 200,
  "data": {
    "outputs": null,
    "message": "编译错误",
    "status": 0,
    "judgeInfo": null
  }
}
```

### 运行报错
直接以除数为 0 报错为例。

```json
{
  "msg": "success",
  "code": 200,
  "data": {
    "outputs": [],
    "message": "Exception in thread \"main\" java.lang.ArithmeticException: / by zero\n",
    "status": 3,
    "judgeInfo": {
      "time": 1,
      "memory": 1024,
      "message": null
    }
  }
}
```



## 系统设计
项目中，灵活应用了多种设计模式，有效减低其代码耦合度以及提高其可扩展性。代码沙箱需要提供对多种语言代码执行的支持，不同的编程语言的执行过程以及环境是不一样的，因此需要为每种编程语言实现一个代码执行类，在系统中创建了一个代码执行接口CodeSandBox，定义了execCode执行代码方法，只需要为各种编程语言提供一个该接口的实现类，实现其中execCode方法即可，同时定义了一个CodeSandBoxManager管理器，其主要负责根据用户代码的编程语言类型选择一个相对应的代码执行实现类实例，进而执行代码，在这里的做法便是策略模式的应用。

此外，由于不同的编程语言代码的执行在整体上流程是比较相似的，整体流程都是由将代码保存为文件、编译代码、运行代码、整理结果、删除代码文件五大步骤组成，因此在此应用了模板方法设计模式，定义了一个模板类CodeSandBoxTemplate，该模板类实现了前面的代码执行接口CodeSandBox，并且实现了其中execCode方法，以及定义了将代码保存为文件、编译代码、运行代码、整理结果、删除代码文件5个方法，在execCode方法对该5个方法进行调用，完成对代码的执行，当需要添加对一种语言的支持时，只需要实现该模板方法类，并且重写其中的运行代码方法即可，当然也可以根据实际情况重写其他的方法。通过这种做法，相当于使用了模板方法类抽取出了大量的共用代码，无需在每个语言的执行实现类中都重复编写很多重复的代码，有效地降低了代码的重复度。综上所述，代码沙箱处理代码的整体流程如下图所示。![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735715575031-e3dfd07c-f3a9-43a8-8187-d2a6ee8d6341.png)

对于代码的运行，可以直接在服务上运行，但是这种方法代码是直接运行在主机上的，因此无论是对于用户代码访问权限的控制、还是对资源使用的限制等，都是较难实现的。然而在评测系统中，用户提交的恶意代码导致的服务器损坏、数据泄露等计算机安全类问题经常出现，因此这种方法不能很好地保证主机的安全。

因此，在代码沙箱中，选用了Docker容器来执行用户提交的代码，基于Docker容器的隔离性，可以将用户代码与宿主机进行隔离，并且通过限制Docker容器的内存大小、运行时长、网络访问限制等，可以在很大程度上保证宿主机的安全，尽管Docker的内部环境受到破坏，宿主机也不会被波及，因此基于Docker容器实现代码沙箱是一个十分不错的选择。

本代码沙箱使用了第三方库Docker-Java来操作Docker，当需要执行代码，首先需要使用准备好的Docker镜像创建容器，然后启动容器，同时会设置容器的内存使用上限、CPU核数上限、禁止网络访问、文件系统只读权限等。在容器启动后，遍历输入用例集合，对于每个用例，都会被作为参数输入运行一遍代码，并且保存其运行结果、内存使用以及耗时等信息，运行代码是通过docker exec命令来实现的，使用docker exec命令执行准备好的Shell脚本，在Shell脚本中，会通过java命令运行用户代码，同时也会通过ps命令获取到用户代码运行进程占用的内存大小，并且输出。对于运行时间的获取，在系统中为每种编程语言提前准备了一个主类，在主类的主方法中对用户的代码进行了调用，而在调用的前后会获取到时间戳，最终即可计算出代码的运行时间并且打印输出。对于Docker容器的输出，主要使用了Docker-Java库提供的ExecStartResultCallback回调函数来获取，并且从中解析出运行结果、内存占用、执行耗时等信息，其中为了能辨别出内存占用以及执行耗时信息，打印时会在这两者的前后分别拼接上一个特殊字符序列，这样在识别时就能有效地区分了。直到把所有输入用例都遍历完，就会删除容器，并且返回执行结果。此外，对于代码沙箱使用到的Docker镜像，主要是使用了DockerFile为各种编程语言自制作了具有相对应环境以及具有前面提到的Shell脚本的镜像，这样一来，只要通过这些镜像启动容器后，就可以通过执行shell脚本来运行代码。代码的执行过程如下图所示。

![](https://cdn.nlark.com/yuque/0/2025/png/29312866/1735715598070-1317e076-6f88-4f7d-9828-cbd5efb4fa00.png)

## 项目部署
### 制作各种语言的代码处理镜像
目前代码沙箱支持四种编程语言，分别是 Java、Js、C 和 Python。针对每种语言制作自定义的代码执行 Docker 镜像。

### 拉取各种语言的基础镜像
```bash
docker pull openjdk:17

docker pull gcc

docker pull node

docker pull python:3
```

### 构造镜像
在 tx-code-sandbox 项目根目录的 scripts 包下找到各种语言的 Dockerfile 和执行代码脚本，上传至服务器，然后通过以下命令构造镜像，命令中 Dockerfile 目录指定为了 "."，因为是在 Dockerfile 所在路径下执行的命令，否则，需要对应修改指定 Dockerfile 所在位置。

```bash
# 命令格式 docker build -t <目标镜像名>:<版本号> <Dockerfile 文件目录>
docker build -t my_java_app:2.0 .
docker build -t my_python_app:1.0 .
docker build -t my_js_app:1.0 .
docker build -t my_c_app:1.0 .
```

> 注意：
>
> + 构建中使用到的执行代码脚本，例如：run_java.sh，需要放在 Dockerfile 的同目录下。
> + 上面命令中指定的镜像名，是和代码沙箱项目中调用时使用的名称一致的，如果需要更改，注意要和代码同步修改。
>

### 查看生成的镜像
```bash
docker images
```

 	![](https://cdn.nlark.com/yuque/0/2024/png/29312866/1733564470532-f8ca7d5c-c80e-4560-b62c-0f86eda42a47.png)

### 项目打包上传
和【主后端部署】部分一样，将项目打成 jar 包并且上传，然后创建名为 tx-code-sandbox 指向 jar 包的软链，一样可以使用 scripts 包下的 app.sh 脚本启动项目。但是为了启动更方便以及设置为开机自启，接下来会把 tx-code-sandbox 配置为 service。

### 配置 service 并设置开机自启
#### 新建 Service
```bash
sudo vim /etc/systemd/system/tx-code-sandbox.service
```

在 Service 中输入以下内容，注意需要把其中的 JDK 路径和 tx-code-sandbox 路径需要根据自己实际的进行替换。

```bash
# 定义服务的基本信息
[Unit]
# 服务的描述信息，会显示在 systemctl status 命令的输出中
Description=txing code sandbox
# 指定服务的启动顺序，表示在网络服务启动之后才启动此服务
After=network.target

# 定义服务的具体行为
[Service]
# Environment 这些环境变量只在服务执行过程中生效
# 不会影响到 ExecStart 命令的解析过程
# systemd 在解析 ExecStart 时使用的是更受限的环境
# 设置 JAVA_HOME 环境变量，指定 JDK 的安装路径
Environment=JAVA_HOME=/mydata/jdk17/jdk-17.0.9
# 设置 PATH 环境变量，包含 Java 可执行文件路径和系统默认路径
Environment=PATH=/mydata/jdk17/jdk-17.0.9/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
# 指定服务的类型为简单服务，表示服务启动后会一直运行
Type=simple
# 指定运行服务的用户
User=root
# 指定运行服务的用户组
Group=root
# 指定服务的工作目录，服务会在此目录下执行
WorkingDirectory=/mydata/myprojects/tx-code-sandbox
# 指定服务启动时执行的命令，包含完整的 java 路径和 jar 包路径
# 这里调用 java 使用的是全路径，是因为 systemd 服务在启动时使用的是一个非常干净的环境，与普通# 用户的 shell 环境不同。具体原因如下：
# systemd 的环境隔离机制
# systemd 默认不会加载用户的环境变量配置（如 ~/.bashrc、/etc/profile 等）
# 即使在 service 文件中设置了 Environment，也只在该服务范围内生效
# systemd 这样做是为了提供一个可预测和安全的执行环境
# 此外，可以看到这里 -jar 后面跟的不是一个 jar, 实际上只是创建了一个名为 tx-code-sandbox 指向# 目标 jar 包的软链，这样的好处是当 jar 改变后，只需要修改软链即可，无需修改 Service 脚本。
ExecStart=/mydata/jdk17/jdk-17.0.9/bin/java -jar /mydata/myprojects/tx-code-sandbox/tx-code-sandbox
# 定义服务的重启策略，always 表示服务停止后总是重启
Restart=always
# 定义重启间隔时间，单位为秒
RestartSec=10

# 定义服务的安装信息
[Install]
# 指定服务应该被安装到哪个目标环境中，multi-user.target 表示多用户命令行环境
WantedBy=multi-user.target
```

#### 刷新配置
```bash
sudo systemctl daemon-reload
```

#### 查看服务环境变量是否配置成功
由于在代码沙箱中会调用 javac 命令，而 Service 是不会共享当前的环境变量的，如果在 Service 中没有配置对应的环境变量，那么在实际运行就会找不到命令而报错。

```bash
systemctl show tx-code-sandbox.service --property=Environment
```

#### 服务基本操作
```bash
# 设置为 开机自启 服务
sudo systemctl enable tx-code-sandbox
# 启动服务
sudo systemctl start tx-code-sandbox
# 查看服务状态
sudo systemctl status tx-code-sandbox
# 停止服务
sudo systemctl stop tx-code-sandbox
# 重启服务
sudo systemctl restart tx-code-sandbox
```

设置完后可以重启服务器，验证是否已经能够开机自启。

