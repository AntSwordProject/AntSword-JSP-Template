#!/usr/bin/env python
# coding:UTF-8

import os
import sys
import base64
import time
import shutil
import subprocess
import platform

# javac路径 如果 javac 不在 PATH 中, 请自己添加
javapath = r'javac'
# javac编译版本
version = '1.5'

pathsep = os.pathsep
distDir = "./dist/"

classpath = os.getcwd()+"/lib"  # apache lib路径
classpath = classpath + "/servlet-api.jar" + pathsep + classpath+"/jsp-api-2.1.jar"  # 拼接classpath

if os.path.exists(distDir):
    shutil.rmtree(distDir)
shutil.copytree("./template/", distDir)

for root, dirs, files in os.walk('.'):
    for f in files:
        if f.endswith(".java"):
            path = os.path.join(root, f)
            print('------------------------------------------------------------')
            print(path)
            cmd = '"{javapath}" -cp {classpath} -source {version} -target {version} {path} '.format(
                javapath=javapath,
                classpath=classpath,
                path=path,
                version=version
            )
            print(cmd)
            p=subprocess.Popen(
              cmd,
              stdout=subprocess.PIPE,
              stderr=subprocess.PIPE,
              shell=True
            )
            out, err = p.communicate()
            try:
                print(err.decode("gbk"))
            except:
                print(str(err))
            targetclass = path.replace('.java', '.class')
            if os.path.exists(targetclass):
                with open(targetclass, 'rb') as f:
                    content = f.read()
                res = str(base64.b64encode(content).decode())
                if len(path.split(os.path.sep)) == 4:
                    dispath = os.path.join(
                        distDir,
                        path.split(os.path.sep)[2]+".js"
                    )
                    with open(dispath,encoding='UTF-8') as disp:
                        result = disp.read().replace('###'+path.split(os.path.sep)[3].split('.')[0]+'###', res)
                    with open(dispath, mode="w",encoding='UTF-8') as disp:
                        disp.write(result)
                elif len(path.split(os.path.sep)) == 5:
                    dispath = os.path.join(
                        distDir,
                        path.split(os.path.sep)[2] + os.path.sep + path.split(os.path.sep)[3]+".js"
                    )
                    with open(dispath,encoding='UTF-8') as disp:
                        result = disp.read().replace('###'+path.split(os.path.sep)[4].split('.')[0]+'###', res)
                    with open(dispath, mode="w",encoding='UTF-8') as disp:
                        disp.write(result)
                else:
                    exit("error")
