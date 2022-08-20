Set WshShell = WScript.CreateObject("WScript.Shell")
strCurFolder = createobject("Scripting.FileSystemObject").GetFolder(".").Path '当前路径
strDesktop = WshShell.SpecialFolders("Desktop") '特殊文件夹“桌面”
'Rem 在桌面创建一个BilibiliDown快捷方式
set oShellLink = WshShell.CreateShortcut(strDesktop & "\Bili 下载器.lnk")
oShellLink.TargetPath = strCurFolder & "\Double-Click-to-Run-for-Win.bat"  '可执行文件路径
oShellLink.Arguments = "" '程序的参数
oShellLink.WindowStyle = 7 '参数1默认窗口激活，参数3最大化激活，参数7最小化
oShellLink.Hotkey = ""  '快捷键
oShellLink.IconLocation = strCurFolder &"\config\favicon.ico"  '图标
oShellLink.Description = "Bili 下载器."  '备注
oShellLink.WorkingDirectory = strCurFolder  '起始位置
oShellLink.Save  '创建保存快捷方式 