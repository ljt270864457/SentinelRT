#!/bin/bash

# =============================================================================
# SentinelRT DEX 构建脚本
# 
# 功能: 将 Maven 构建的 JAR 转换为 Android DEX 文件，供 Frida 动态加载
# 
# 前置要求:
#   1. JDK 11+
#   2. Maven 3.6+
#   3. Android SDK (需要 d8 工具，位于 build-tools 目录)
#
# 使用方法:
#   ./build-dex.sh [ANDROID_HOME]
#
# 参数:
#   ANDROID_HOME - Android SDK 路径 (可选，默认使用环境变量)
# =============================================================================

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== SentinelRT DEX 构建脚本 ===${NC}"

# 检查 Android SDK 路径
if [ -n "$1" ]; then
    ANDROID_HOME="$1"
fi

if [ -z "$ANDROID_HOME" ]; then
    echo -e "${RED}错误: ANDROID_HOME 未设置${NC}"
    echo "请设置 ANDROID_HOME 环境变量或作为参数传入"
    echo "示例: ./build-dex.sh /path/to/android/sdk"
    exit 1
fi

echo -e "${YELLOW}Android SDK: $ANDROID_HOME${NC}"

# 查找最新的 build-tools 版本
BUILD_TOOLS_DIR="$ANDROID_HOME/build-tools"
if [ ! -d "$BUILD_TOOLS_DIR" ]; then
    echo -e "${RED}错误: 未找到 build-tools 目录: $BUILD_TOOLS_DIR${NC}"
    exit 1
fi

# 获取最新版本的 build-tools
LATEST_BUILD_TOOLS=$(ls -v "$BUILD_TOOLS_DIR" | tail -n 1)
D8_PATH="$BUILD_TOOLS_DIR/$LATEST_BUILD_TOOLS/d8"

if [ ! -f "$D8_PATH" ]; then
    echo -e "${RED}错误: 未找到 d8 工具: $D8_PATH${NC}"
    exit 1
fi

echo -e "${YELLOW}使用 build-tools 版本: $LATEST_BUILD_TOOLS${NC}"

# Step 1: Maven 构建
echo -e "${GREEN}[1/3] 执行 Maven 构建...${NC}"
mvn clean package -DskipTests -q

# 检查 JAR 是否生成
JAR_FILE="target/sentinelrt.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}错误: JAR 文件未生成: $JAR_FILE${NC}"
    exit 1
fi

echo -e "${GREEN}JAR 构建成功: $JAR_FILE${NC}"

# Step 2: 转换为 DEX
echo -e "${GREEN}[2/3] 转换 JAR 为 DEX...${NC}"
OUTPUT_DIR="target"
DEX_FILE="$OUTPUT_DIR/sentinelrt.dex"

"$D8_PATH" --output "$OUTPUT_DIR" "$JAR_FILE"

# d8 默认输出 classes.dex，重命名为 sentinelrt.dex
if [ -f "$OUTPUT_DIR/classes.dex" ]; then
    mv "$OUTPUT_DIR/classes.dex" "$DEX_FILE"
fi

if [ ! -f "$DEX_FILE" ]; then
    echo -e "${RED}错误: DEX 文件未生成${NC}"
    exit 1
fi

echo -e "${GREEN}DEX 构建成功: $DEX_FILE${NC}"

# Step 3: 显示文件信息
echo -e "${GREEN}[3/3] 构建完成!${NC}"
echo ""
echo -e "${YELLOW}输出文件:${NC}"
ls -lh "$JAR_FILE" "$DEX_FILE"
echo ""
echo -e "${YELLOW}Frida 加载示例:${NC}"
cat << 'EOF'
// 将 sentinelrt.dex 推送到设备
// adb push target/sentinelrt.dex /data/local/tmp/

Java.perform(function() {
    // 加载 DEX 文件
    Java.openClassFile("/data/local/tmp/sentinelrt.dex").load();
    
    // 使用 SentinelRT
    var SentinelRT = Java.use("rt.sentinel.SentinelRT");
    
    // 查看帮助
    console.log(SentinelRT.help());
    
    // 序列化对象
    var targetObj = ...; // 你要序列化的对象
    var json = SentinelRT.toJson(targetObj);
    console.log(json);
});
EOF

echo ""
echo -e "${GREEN}=== 构建完成 ===${NC}"
