import os
import shutil
import subprocess
import asyncio

import httpx
import moviepy.editor as mp

from tqdm import tqdm

from strategy.bilibili_strategy import BilibiliStrategy
from strategy.default import DefaultStrategy
from strategy.bangumi import BangumiStrategy
from models.category import Category
from models.video import Video
import config


class BilibiliExecutor():

    _strategies = {
        Category.default: DefaultStrategy(),
        Category.bangumi: BangumiStrategy(),
    }

    @property
    def strategy(self) -> BilibiliStrategy:
        return self._strategy

    @strategy.setter
    def strategy(self, strategy: BilibiliStrategy):
        self._strategy = strategy

    def get_video(self, url) -> Video:
        """根据 URL 自动识别视频类型"""
        # 规范化URL：确保 /video/BVID 后有斜杠，避免重定向导致清晰度降低
        url = self._normalize_url(url)
        category = self._detect_category(url)
        video = Video(url, category)
        return video

    def _normalize_url(self, url: str) -> str:
        """
        规范化B站URL格式，确保获取最高清晰度

        问题：BV15FK6zTEuj?p=2 会被重定向，导致丢失会员状态，返回480P
        解决：规范化为 BV15FK6zTEuj/?p=2，避免重定向，获取1080P
        """
        import re
        # 匹配 /video/BVXXXXXX? 或 /video/avXXXXXX? (没有斜杠的情况)
        pattern = r'(/video/(?:BV[0-9A-Za-z]+|av\d+))(\?)'
        replacement = r'\1/\2'
        normalized_url = re.sub(pattern, replacement, url)
        return normalized_url

    def _detect_category(self, url: str) -> int:
        """
        根据 URL 模式识别视频分类

        普通视频：
        - https://www.bilibili.com/video/BV*
        - https://www.bilibili.com/video/av*

        番剧/电影/OGV：
        - https://www.bilibili.com/bangumi/play/ss*  (season)
        - https://www.bilibili.com/bangumi/play/ep*  (episode)
        """
        if '/bangumi/play/' in url:
            return Category.bangumi
        return Category.default

    async def get(self, url: str) -> Video:
        video = self.get_video(url)
        strategy = self._strategies[video.category]
        # 按照不同mode获取视频各项信息
        video = await strategy.get(video)

        return video


class BilibiliDownloader():
    '''下载视频和音频类（异步）'''

    def __init__(self) -> None:
        # 存放下载视频的文件夹路径
        self.temp_path = config.TEMP_PATH
        # 用于给视频编号的计数器
        self._video_counter = 0
        self._counter_lock = asyncio.Lock()
        # 用于保护打印输出的锁
        self._print_lock = asyncio.Lock()
        # 保存模式（merge / separate / audio_only / video_only）
        mode = str(getattr(config, "SAVE_MODE", "merge")).strip().lower()
        self.save_mode = mode if mode in {"merge", "separate", "audio_only", "video_only"} else "merge"
        self.base_headers = {
            'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
            'accept-encoding': 'gzip, deflate, br',
            'accept-language': 'en-US,en;q=0.9',
            'cache-control': 'max-age=0',
            "Content-Type": "application/json; charset=utf-8",
            'cookie': config.COOKIE,
            'pragma': 'no-cache',
            'referer': 'https://space.bilibili.com/',
            'sec-ch-ua': '"Not?A_Brand";v="8", "Chromium";v="108", "Microsoft Edge";v="108"',
            'sec-ch-ua-mobile': '?0',
            'sec-ch-ua-platform': '"Windows"',
            'sec-fetch-dest': 'document',
            'sec-fetch-mode': 'navigate',
            'sec-fetch-site': 'same-origin',
            'sec-fetch-user': '?1',
            'upgrade-insecure-requests': '1',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.46',
        }

    async def download_video(self, video) -> bool:
        """
        下载视频

        Returns:
            bool: 下载成功返回 True，失败返回 False
        """
        # 为当前视频分配一个编号
        async with self._counter_lock:
            self._video_counter += 1
            video_num = self._video_counter

        video_url = video.video_url
        audio_url = video.audio_url

        # 如果是分P视频（包括第1部分），在文件名中添加分P标识
        # 这样可以避免多个分P之间的文件名冲突
        if hasattr(video, 'part_number') and video.part_number >= 1:
            video_filename = f'{video.title}_P{video.part_number}.mp4'
            audio_filename = f'{video.title}_P{video.part_number}.mp3'
            part_label = f"P{video.part_number}"
        else:
            video_filename = video.title + '.mp4'
            audio_filename = video.title + '.mp3'
            part_label = ""

        # 创建文件夹存放下载的视频
        if not os.path.exists(self.temp_path):
            os.mkdir(self.temp_path)

        # 根据视频格式选择下载方式
        if video.is_durl:
            # durl 格式：只下载单个合并的视频文件
            async with self._print_lock:
                print(f"\n{'─' * 60}")
                print(f"[{video_num}] 📹 {video_filename}")
                print(f"    清晰度: {video.get_quality_name()}")
                print(f"{'─' * 60}")

            async with httpx.AsyncClient() as client:
                success = await self._download(
                    client, video_url,
                    os.path.join(self.temp_path, video_filename),
                    f"[{video_num}] 🎬 {part_label}".strip()
                )

            async with self._print_lock:
                if success:
                    print(f"\n[{video_num}] ✅ 下载完成")
                else:
                    print(f"\n[{video_num}] ❌ 下载失败")
            return success
        else:
            # dash 格式：并发下载视频和音频
            async with self._print_lock:
                print(f"\n{'─' * 60}")
                print(f"[{video_num}] 📹 {video_filename}")
                print(f"    清晰度: {video.get_quality_name()}")
                print(f"{'─' * 60}")

            async with httpx.AsyncClient() as client:
                download_tasks = []

                need_video = self.save_mode in {"merge", "separate", "video_only"}
                need_audio = self.save_mode in {"merge", "separate", "audio_only"}

                if need_video:
                    video_task = self._download(
                        client, video_url,
                        os.path.join(self.temp_path, video_filename),
                        f"[{video_num}] 🎬视频 {part_label}".strip()
                    )
                    download_tasks.append(video_task)

                if need_audio:
                    if not audio_url:
                        async with self._print_lock:
                            print(f"\n[{video_num}] ❌ 当前视频无独立音频流，无法执行仅音频/含音频模式")
                        return False
                    audio_task = self._download(
                        client, audio_url,
                        os.path.join(self.temp_path, audio_filename),
                        f"[{video_num}] 🎵音频 {part_label}".strip()
                    )
                    download_tasks.append(audio_task)

                # 按模式并发下载需要的流
                results = await asyncio.gather(*download_tasks)

            # 只有视频和音频都下载成功才算成功
            success = all(results)
            async with self._print_lock:
                if success:
                    print(f"\n[{video_num}] ✅ 下载完成")
                else:
                    print(f"\n[{video_num}] ❌ 下载失败")
            return success

    async def _download(self, client: httpx.AsyncClient, url, filename, file_type="文件", max_retries=5, retry_delay=5) -> bool:
        """
        下载文件

        Returns:
            bool: 下载成功返回 True，失败返回 False
        """
        retries = 0

        while retries < max_retries:
            progress_bar = None
            try:
                # 检查文件是否已存在
                file_size = 0
                if os.path.exists(filename):
                    file_size = os.path.getsize(filename)

                # 为每次请求创建独立的 headers 副本
                headers = self.base_headers.copy()
                headers["Range"] = f"bytes={file_size}-"

                async with client.stream("GET", url, headers=headers) as response:
                    if response.status_code == 416:
                        print(f"  {file_type} 已经下载完毕")
                        return True

                    # 总的文件大小包括已下载的部分
                    total_size = (
                        int(response.headers.get("content-length", 0)) + file_size
                    )

                    mode = "ab" if file_size > 0 else "wb"

                    # 使用 tqdm 显示进度条，不使用 position 避免输出混乱
                    progress_bar = tqdm(
                        total=total_size,
                        unit="B",
                        unit_scale=True,
                        initial=file_size,
                        desc=f"  {file_type}",
                        leave=True,  # 保留已完成的进度条
                        dynamic_ncols=True,
                        miniters=1,
                        mininterval=0.5
                    )

                    with open(filename, mode) as file:
                        try:
                            async for chunk in response.aiter_bytes():
                                if chunk:
                                    file.write(chunk)
                                    progress_bar.update(len(chunk))
                        finally:
                            # 成功完成时不关闭，让进度条保留
                            pass

                # 下载成功后关闭进度条
                if progress_bar is not None:
                    progress_bar.close()
                return True
            except (httpx.RemoteProtocolError, httpx.RequestError) as e:
                retries += 1
                # 出错时必须关闭进度条，避免重复显示
                if progress_bar is not None:
                    progress_bar.clear()  # 清除显示
                    progress_bar.close()  # 关闭进度条
                print(f"  {file_type} 下载出现错误: {e}，正在重试 ({retries}/{max_retries})...")
                await asyncio.sleep(retry_delay)

        print(f"  ❌ {file_type} 下载失败，已达到最大重试次数")
        return False


class VideoMerge():
    '''合并视频和音频类'''

    def __init__(self) -> None:
        # 存放下载视频的文件夹路径
        self.temp_path = config.TEMP_PATH
        # 存放合并后的文件夹路径
        self.path = config.OUTPUT_PATH
        # 保存模式（merge / separate / audio_only / video_only）
        mode = str(getattr(config, "SAVE_MODE", "merge")).strip().lower()
        self.save_mode = mode if mode in {"merge", "separate", "audio_only", "video_only"} else "merge"

    def merge_video(self, video) -> None:
        # 如果是分P视频（包括第1部分），在文件名中添加分P标识
        # 这样可以避免多个分P之间的文件名冲突
        if hasattr(video, 'part_number') and video.part_number >= 1:
            video_filename = f'{video.title}_P{video.part_number}.mp4'
            audio_filename = f'{video.title}_P{video.part_number}.mp3'
        else:
            video_filename = video.title + '.mp4'
            audio_filename = video.title + '.mp3'

        # 创建文件夹存放合并的视频
        if not os.path.exists(self.path):
            os.mkdir(self.path)

        # durl 格式：音视频已合并
        if video.is_durl:
            temp_video_path = os.path.join(self.temp_path, video_filename)

            if self.save_mode == "audio_only":
                output_audio_path = os.path.join(self.path, audio_filename)
                print(f"\n🎵 从视频中提取音频...")
                extracted = False

                if shutil.which("ffmpeg"):
                    result = subprocess.run(
                        [
                            "ffmpeg",
                            "-i",
                            temp_video_path,
                            "-vn",
                            "-c:a",
                            "libmp3lame",
                            output_audio_path,
                            "-y",
                        ],
                        stdout=subprocess.DEVNULL,
                        stderr=subprocess.DEVNULL,
                    )
                    extracted = result.returncode == 0

                if not extracted:
                    clip = mp.VideoFileClip(temp_video_path)
                    clip.audio.write_audiofile(output_audio_path)
                    clip.close()

                try:
                    os.remove(temp_video_path)
                except OSError as e:
                    print(f"  ⚠️  清理临时文件失败: {e}")

                print("✅ 音频提取完成")
                return

            print(f"\n📁 移动视频到输出目录...")
            shutil.move(
                temp_video_path,
                os.path.join(self.path, video_filename)
            )
            print("✅ 视频处理完成")
            return

        # 分开保存：直接移动音视频文件到输出目录
        if self.save_mode == "separate":
            print(f"\n📁 分开保存音视频到输出目录...")
            shutil.move(
                os.path.join(self.temp_path, video_filename),
                os.path.join(self.path, video_filename)
            )
            shutil.move(
                os.path.join(self.temp_path, audio_filename),
                os.path.join(self.path, audio_filename)
            )
            print("✅ 音视频已分开保存")
            return

        # 仅保存音频：只移动音频，删除临时视频
        if self.save_mode == "audio_only":
            print(f"\n📁 仅保存音频到输出目录...")
            shutil.move(
                os.path.join(self.temp_path, audio_filename),
                os.path.join(self.path, audio_filename)
            )
            try:
                os.remove(os.path.join(self.temp_path, video_filename))
            except OSError:
                pass
            print("✅ 音频已保存")
            return

        # 仅保存视频：只移动视频，删除临时音频
        if self.save_mode == "video_only":
            print(f"\n📁 仅保存视频到输出目录...")
            shutil.move(
                os.path.join(self.temp_path, video_filename),
                os.path.join(self.path, video_filename)
            )
            try:
                os.remove(os.path.join(self.temp_path, audio_filename))
            except OSError:
                pass
            print("✅ 视频已保存")
            return

        # dash 格式：需要合并音视频
        # 如果 ffmpeg 存在，则用其合并视频和音频
        if shutil.which("ffmpeg"):
            print(f"\n🎬 合并视频和音频...")
            result = subprocess.run(
                [
                    "ffmpeg",
                    "-i",
                    os.path.join(self.temp_path, video_filename),
                    "-i",
                    os.path.join(self.temp_path, audio_filename),
                    "-c:v",
                    "copy",
                    "-c:a",
                    "copy",
                    os.path.join(self.path, video_filename),
                    "-y",  # 自动覆盖已存在的文件
                ],
                stdout=subprocess.DEVNULL,  # 隐藏标准输出
                stderr=subprocess.DEVNULL,  # 隐藏错误输出
            )
            if result.returncode != 0:
                print(f"⚠️  ffmpeg 合并失败，退出代码: {result.returncode}")
        else:
            print(f"\n🎬 使用 moviepy 合并视频和音频...")
            clip = mp.VideoFileClip(os.path.join(
                self.temp_path, video_filename)).subclip()
            clip.write_videofile(os.path.join(self.path, video_filename), audio=os.path.join(
                self.temp_path, audio_filename), preset="ultrafast", threads=8)

        print("✅ 视频合成完成")

        # 删除临时文件
        try:
            os.remove(os.path.join(self.temp_path, video_filename))
            os.remove(os.path.join(self.temp_path, audio_filename))
        except OSError as e:
            print(f"  ⚠️  清理临时文件失败: {e}")
