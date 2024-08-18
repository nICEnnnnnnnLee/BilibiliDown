# 工作目录在顶级目录/win_msi
# 需要设置环境变量 JAVA_TOOL_OPTIONS=-Duser.region=CN -Duser.language=zh 以确定locale
import re, os, stat, zipfile
from subprocess import Popen, PIPE, STDOUT
import shutil

version = os.environ.get("VERSION_NUMBER", '6.31')
version_tail = os.environ.get("VERSION_NUMBER_TAIL", '0')
version_installer = f'1.{version}.{version_tail}'
jpackage_path = "jpackage" #r"D:\Program Files\Java\openjdk-21.0.3_windows-x64_bin\jdk-21.0.3+9\bin\jpackage" jpackage
zip_url_path = f'https://github.com/nICEnnnnnnnLee/BilibiliDown/releases/download/V{version}/BilibiliDown.v{version}.win_x64_jre11.release.zip'
print(zip_url_path)
zip_file_path = "release.zip"
zip_file_path_backup = f"BilibiliDown.v{version}.win_x64_jre11.release.zip"
preserv_folders = ['download', 'config', 'parsers', 'pushers']
# cmd_env = {'JAVA_TOOL_OPTIONS': '-Duser.region=CN -Duser.language=zh'}
cmd_env = None
cmd_package = f'''"{jpackage_path}" \
  --type msi \
  --dest ./target \
  --input ./release \
  --resource-dir ./resource \
  --name BilibiliDown \
  --main-class nicelee.memory.App \
  --main-jar launch.jar \
  --java-options -Dfile.encoding=utf-8 \
  --java-options -Dbilibili.prop.log=false \
  --runtime-image "./runtime" \
  --icon ./resource/favicon.ico \
  --app-version {version_installer} \
  --vendor "nICEnnnnnnnLee" \
  --copyright "Copyright © 2019-2024 nICEnnnnnnnLee" \
  --win-dir-chooser \
  --win-shortcut \
  --temp ./temp \
  --verbose
'''


def exe_command(command, env=None):
    """
    执行 shell 命令并实时打印输出
    :param command: shell 命令
    :return: process, exitcode
    """
    print(command)
    #args = shlex.split(command)
    process = Popen(command, stdout=PIPE, stderr=STDOUT, shell=True, env=env)
    #stdout, stderr = process.communicate()
    with process.stdout:
        for line in iter(process.stdout.readline, b''):
           print(line.decode('utf-8','replace'), end='')
           # print(line.decode('gbk','replace'), end='')
    exitcode = process.wait()
    return process, exitcode
    
def on_err(func, path, _):
    # 用于处理删除文件错误
    os.chmod(path, stat.S_IWRITE)
    func(path)

import hashlib
 
def cal_file_sha1(file_path):
    hash_object = hashlib.sha1()
    with open(file_path, 'rb') as f:
        for chunk in iter(lambda: f.read(4096), b''):
            hash_object.update(chunk)
    return hash_object.hexdigest()

    
def move_app_to_top(bundle):
    # 找到 app文件夹的 id
    pattern = r'<DirectoryRef Id="INSTALLDIR">[\s\S]*?Id="(.*?)" +Name="app"'
    searchObj = re.search(pattern, bundle, re.MULTILINE)
    app_dir_id = searchObj.group(1)
    # print("app 文件夹id", app_dir_id)
    cfg_absolute_path = os.path.abspath("resource/BilibiliDown.cfg")
    # 遍历app文件夹下的所有子节点
    pattern = f'<DirectoryRef Id="{app_dir_id}">[\s\S]*?(?:Source|Name)="([^"]+)"'
    def repl_app_dir(match):
        file_path = match.group(1)
        origin = match.group()
        if file_path.endswith(r"app\.package"):
            return  origin # app\.package 原路返回
        elif file_path.endswith(r"app\BilibiliDown.cfg"):
            # app\BilibiliDown.cfg 将路径换为 {cfg_absolute_path}，不使用temp中的配置
            return  origin.replace(file_path, cfg_absolute_path)
        else:
            # 其它将其父文件夹设为 INSTALLDIR
            return  origin.replace(f'<DirectoryRef Id="{app_dir_id}">', '<DirectoryRef Id="INSTALLDIR">')
    bundle_new = re.sub(pattern, repl_app_dir, bundle, flags = re.MULTILINE)
    return bundle_new
    
def set_nodes_permanent(bundle, dir_name):
    """
    遍历 {dir_name}文件夹下的节点，将其属性设为 Permanent="yes" NeverOverwrite="yes"
    """
    # 找到 {dir_name} 文件夹的 id
    pattern = f'<DirectoryRef Id="INSTALLDIR">[\s\S]*?Id="(.*?)" +Name="{dir_name}"'
    searchObj = re.search(pattern, bundle, re.MULTILINE)
    dir_id = searchObj.group(1)
    print(f"{dir_name} 文件夹id: ", dir_id)
    # 遍历{dir_name}文件夹下的所有子节点，将其属性设为 Permanent="yes" NeverOverwrite="yes"
    pattern = f'<DirectoryRef Id="{dir_id}">[\s\S]*?Guid='
    def repl_set_perm(match):
        origin = match.group()
        return  origin.replace('Win64="yes" ', 'Win64="yes" Permanent="yes" NeverOverwrite="yes" ')
    bundle_new = re.sub(pattern, repl_set_perm, bundle, flags = re.MULTILINE)
    return bundle_new
    """
    # 找到 config文件夹的 id
    pattern = r'<DirectoryRef Id="INSTALLDIR">[\s\S]*?Id="(.*?)" +Name="config"'
    searchObj = re.search(pattern, bundle, re.MULTILINE)
    config_dir_id = searchObj.group(1)
    print("config 文件夹id", config_dir_id)
    # 遍历config文件夹下的所有子节点，将其属性设为 Permanent="yes" NeverOverwrite="yes"
    pattern = f'<DirectoryRef Id="{config_dir_id}">[\s\S]*?Guid='
    def repl_config_dir(match):
        origin = match.group()
        return  origin.replace('Win64="yes" ', 'Win64="yes" Permanent="yes" NeverOverwrite="yes" ')
    bundle_new = re.sub(pattern, repl_config_dir, bundle_new, flags = re.MULTILINE)
    """
    
def step_1_keep_space_clean():
    print("step_1: 确保空间空白\n")
    if os.path.exists("resource"):
        shutil.rmtree("resource", onerror=on_err)
    os.mkdir("resource")
    if os.path.exists("runtime"):
        shutil.rmtree("runtime", onerror=on_err)
        #os.mkdir("runtime")
    if os.path.exists("release"):
        shutil.rmtree("release", onerror=on_err)
    os.mkdir("release")
    if os.path.exists("temp"):
        shutil.rmtree("temp", onerror=on_err)
    os.mkdir("temp")
        
def step_2_get_release_zip():
    print("step_2: 下载程序包\n")
    if not os.path.exists(zip_file_path):
        if os.path.exists(zip_file_path_backup):
            print(f"{zip_file_path_backup}存在，重命名")
            os.rename(zip_file_path_backup, zip_file_path)
        else:
            print("程序不存在，开始下载")
            import requests
            with open(zip_file_path, "wb") as file:
                response = requests.get(zip_url_path, stream=True, timeout=120)
                downloaded = 0
                for data in response.iter_content(chunk_size=1024 * 1024):
                    file.write(data)
                    downloaded += len(data)
                    print("\r下载字节数: ", downloaded, sep='', end='', flush=True)
                if downloaded < 1024:
                    print(str(data))
                response.close()
    
        
def step_3_deal_with_files():
    print("\nstep_3: 确保文件在正确的位置上\n")
    print("解压程序包到 ./release")
    zip_file = zipfile.ZipFile(zip_file_path, 'r')
    zip_file.extractall("./release")
    zip_file.close()
    
    print("移动jre到 ./runtime")
    # shutil.move("./release/minimal-bilibilidown-jre", "./runtime")
    os.rename("./release/minimal-bilibilidown-jre", "./runtime")
    
    print("删除不必要的文件")
    os.remove("./release/config/app.config")
    os.remove("./release/Double-Click-to-Run-for-Mac.command")
    os.remove("./release/Create-Shortcut-on-Desktop-for-Linux.sh")
    os.remove("./release/Create-Shortcut-on-Desktop-for-Mac.sh")
    os.remove("./release/Create-Shortcut-on-Desktop-for-Win.vbs")
    os.remove("./release/Double-Click-to-Run-for-Win.bat")
    os.remove("./release/uninstall.bat")
    
    print("移动相关文件到资源文件夹")
    #for conf in ['InstallDirNotEmptyDlg.wxs', 'main.wxs', 'overrides.wxi', 'ui.wxf', 'Locale_zh_CN.wxl', 'Locale_en_US.wxl']:
    #for conf in ['InstallDirNotEmptyDlg.wxs', 'main.wxs', 'overrides.wxi', 'ui.wxf', 'Locale_zh_CN.wxl', 'MsiInstallerStrings_zh_CN.wxl', 'MsiInstallerStrings_en.wxl']:
    for conf in ['InstallDirNotEmptyDlg.wxs', 'main.wxs', 'overrides.wxi', 'ui.wxf', 'Locale_zh_CN.wxl']:
        if not os.path.exists(f"./resource/{conf}"):
            shutil.copy(f"../.github/scripts/installer-win/{conf}", f"./resource/{conf}")
            pass
    if not os.path.exists("./resource/favicon.ico"):
        os.rename("./release/config/favicon.ico", "./resource/favicon.ico")
    #if os.path.exists("release/config"):
    #    shutil.rmtree("release/config", onerror=on_err)
    print("生成必要的空文件夹")
    for dir_name in preserv_folders:
        if not os.path.exists(f"release/{dir_name}"):
            os.mkdir(f"release/{dir_name}")
    
def step_5_deal_with_BilibiliDown_cfg():
    print("step_5: 个性化配置 resource/BilibiliDown.cfg")
    with open('temp/images/win-msi.image/BilibiliDown/app/BilibiliDown.cfg', 'r', encoding='utf-8') as file:
        cfg = file.read()
    # 将classpath里的 $APPDIR 全部去掉，去掉
    pattern = r'app.classpath=\$APPDIR\\'
    cfg_new = re.sub(pattern, r'app.classpath=', cfg, flags = re.MULTILINE)
    cfg_new = cfg_new.replace('app.classpath=INeedBiliAV.jar', '')
    with open('resource/BilibiliDown.cfg','w', encoding='utf-8') as output:
        output.write(cfg_new)
        
def step_6_deal_with_bundle_wxf():
    print("step_6: 个性化配置 resource/bundle.wxf")
    with open('temp/config/bundle.wxf', 'r', encoding='utf-8') as file:
        bundle = file.read()
        
    print('将app目录中的内容提到根目录')
    print('修改BilibiliDown.cfg')
    bundle = move_app_to_top(bundle)
    
    print('设置目录或文件永久保存并不被覆盖')
    for dir_name in preserv_folders:
        bundle = set_nodes_permanent(bundle, dir_name)
    
    print('设置安装目录在卸载时不被删除')
    pattern = r'<util:RemoveFolderEx On="uninstall" Property=.*?RemoveFolderEx>'
    bundle = re.sub(pattern, '', bundle, flags = re.MULTILINE)
    
    with open('resource/bundle.wxf','w', encoding='utf-8') as output:
        output.write(bundle)

def compress_folder(folder_path, output_path):
    with zipfile.ZipFile(output_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(folder_path):
            for file in files:
                file_path = os.path.join(root, file)
                arc_name = os.path.relpath(file_path, folder_path)
                # print(arc_name)
                zipf.write(file_path, arc_name)
                
def step_7_build_msi():
    print("step_7: 生成打包文件")
    print("删除不必要的文件")
    shutil.rmtree("./temp", onerror=on_err)
    
    msi_path = f"./target/BilibiliDown-{version_installer}.msi"
    msi_sha1_path = f"{msi_path}.sha1"
    if os.path.exists(msi_path):
        os.remove(msi_path)
    
    print("执行jpackage命令")
    exe_command(cmd_package, cmd_env)
    
    print("计算MSI SHA1并输出")
    with open(msi_sha1_path,'w', encoding='utf-8') as output:
        sha1 = cal_file_sha1(msi_path)
        output.write(sha1)
        
def step_8_build_exe_zip():
    print("step_8: 生成包含exe的压缩包")
    print("重新解压程序包到 ./release_exe_zip")
    zip_file = zipfile.ZipFile(zip_file_path, 'r')
    zip_file.extractall("./release_exe_zip")
    zip_file.close()
    
    print("jre由 minimal-bilibilidown-jre 重命名为 runtime")
    # shutil.move("./release/minimal-bilibilidown-jre", "./runtime")
    os.rename("./release_exe_zip/minimal-bilibilidown-jre", "./release_exe_zip/runtime")
    
    print("删除不必要的文件")
    os.remove("./release_exe_zip/Double-Click-to-Run-for-Mac.command")
    os.remove("./release_exe_zip/Create-Shortcut-on-Desktop-for-Linux.sh")
    os.remove("./release_exe_zip/Create-Shortcut-on-Desktop-for-Mac.sh")
    os.remove("./release_exe_zip/Create-Shortcut-on-Desktop-for-Win.vbs")
    os.remove("./release_exe_zip/uninstall.bat")
    os.remove("./release_exe_zip/update.bat")
    
    print("将相关文件复制到app文件夹")
    os.mkdir("release_exe_zip/app")
    shutil.copy("temp/images/win-msi.image/BilibiliDown/app/.package", "release_exe_zip/app/.package")
    shutil.copy("resource/BilibiliDown.cfg", "release_exe_zip/app/BilibiliDown.cfg")
    
    print("复制exe文件")
    shutil.copy("temp/images/win-msi.image/BilibiliDown/BilibiliDown.exe", "release_exe_zip/BilibiliDown.exe")
    
    print("打包成压缩包")
    exe_zip_path = f"BilibiliDown.v{version}.win_x64_jre11.release.zip"
    exe_zip_sha1_path = f"{exe_zip_path}.sha1"
    compress_folder("release_exe_zip", exe_zip_path)
    
    print("计算zip SHA1并输出")
    with open(exe_zip_sha1_path,'w', encoding='utf-8') as output:
        sha1 = cal_file_sha1(exe_zip_path)
        output.write(sha1)
        
if __name__ == '__main__':

    """  """
    step_1_keep_space_clean()

    step_2_get_release_zip()
    
    step_3_deal_with_files()
    
    print("step_4: 执行jpackage命令(为了得到原始的 BilibiliDown.cfg 和bundle.wxf)\n")
    exe_command(cmd_package, cmd_env)

    step_5_deal_with_BilibiliDown_cfg()
    
    step_6_deal_with_bundle_wxf()
    
    step_7_build_msi()
        
    step_8_build_exe_zip()
    """ """