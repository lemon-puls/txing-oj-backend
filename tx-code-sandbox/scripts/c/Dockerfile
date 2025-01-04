FROM gcc

# 更新软件包列表并安装所需的工具
# RUN microdnf install yum && yum update -y && \
#    yum install -y procps-ng


# 设置工作目录
WORKDIR /app

# 将你的脚本添加到容器中
ADD run_gcc.sh /sh/

# 设置脚本可执行权限
RUN chmod +x /sh/run_gcc.sh

# 在容器启动时执行你的脚本
# CMD ["/app/hello.sh"]

