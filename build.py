#encoding=UTF-8
import os
import sys
import base64
import time
import shutil
import subprocess

javapath = r'C:\Program Files (x86)\Java\jdk1.6.0_43\bin\javac.exe'  # javac路径
classpath = os.getcwd()+"/lib"  # apache lib路径
classpath = classpath+"/servlet-api.jar;" + classpath+"/jsp-api-2.1.jar"  # 拼接classpath
if os.path.exists("./dist/"):
    shutil.rmtree("./dist/")
shutil.copytree("./template/", "./dist/")

for root, dirs, files in os.walk('.'):
    for f in files:
        if f.endswith(".java"):
            path = os.path.join(root, f)
            print('------------------------------------------------------------')
            print(path)
            p=subprocess.Popen(f'"{javapath}" -cp {classpath} {path}',stdout=subprocess.PIPE,stderr=subprocess.PIPE, shell=True)
            out,err=p.communicate()
            try:
                print(str(err,"UTF-8"))
            except:
                print(str(err,"GBK"))
            targetclass = path.replace('.java', '.class')
            if os.path.exists(targetclass):
                with open(targetclass, 'rb') as f:
                    content = f.read()
                res = str(base64.b64encode(content), "UTF-8")
                if len(path.split('\\')) == 4:
                    dispath = "./dist/"+path.split('\\')[2]+".js"
                    with open(dispath, encoding="UTF-8") as disp:
                        result = disp.read().replace('###'+path.split('\\')[3].split('.')[0]+'###', res)
                    with open(dispath, encoding="UTF-8", mode="w") as disp:
                        disp.write(result)
                elif len(path.split('\\')) == 5:
                    dispath = "./dist/" + \
                        path.split('\\')[2]+'/'+path.split('\\')[3]+".js"
                    with open(dispath, encoding="UTF-8") as disp:
                        result = disp.read().replace('###'+path.split('\\')[4].split('.')[0]+'###', res)
                    with open(dispath, encoding="UTF-8", mode="w") as disp:
                        disp.write(result)
                else:
                    exit("error")
