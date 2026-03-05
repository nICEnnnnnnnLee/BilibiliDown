import time
import asyncio
import os
import shutil

from strategy.bilibili_executor import BilibiliExecutor
from strategy.bilibili_executor import BilibiliDownloader
from strategy.bilibili_executor import VideoMerge
import config


def resolve_urls():
    """解析并校验配置中的 URL 列表"""
    urls = [url.strip() for url in config.URL if isinstance(url, str) and url.strip()]

    if not urls:
        raise ValueError("config.URL 为空，请先在 config.py 中填写至少 1 个视频链接")

    return urls


def resolve_save_mode():
    """解析保存模式（merge / separate / audio_only / video_only）"""
    mode = str(getattr(config, "SAVE_MODE", "merge")).strip().lower()
    return mode if mode in {"merge", "separate", "audio_only", "video_only"} else "merge"


class BFacade():

    def __init__(self):
        self.crawler = BilibiliExecutor()
        self.downloader = BilibiliDownloader()
        self.merger = VideoMerge()
        self.downloaded_videos = []  # 成功下载的视频
        self.failed_videos = []  # 下载失败的视频 (video, url)
        self._lock = asyncio.Lock()

    async def download_single(self, url):
        """下载单个视频"""
        video = await self.crawler.get(url)
        # 保存原始 URL 到 video 对象，便于失败时输出
        video.source_url = url

        # 视频信息将在下载时显示，这里不再重复输出
        success = await self.downloader.download_video(video)

        if success:
            # 下载成功才合并
            self.merger.merge_video(video)
            async with self._lock:
                self.downloaded_videos.append(video)
        else:
            # 下载失败，记录到失败列表
            async with self._lock:
                self.failed_videos.append(video)

    async def download(self, urls, max_concurrent: int = 2):
        """并发下载所有视频（限制并发数）"""
        # 使用信号量限制并发数，避免同时请求太多导致超时
        semaphore = asyncio.Semaphore(max_concurrent)

        async def download_with_limit(url):
            async with semaphore:
                await self.download_single(url)

        # 创建所有下载任务
        tasks = [download_with_limit(url) for url in urls]
        # 并发执行所有任务
        await asyncio.gather(*tasks)


async def async_main():
    """异步主函数"""
    # 开始下载时刻
    start_time = time.time()
    urls = resolve_urls()
    save_mode = resolve_save_mode()

    # 显示下载配置信息
    max_concurrent = 2  # 并发下载2个视频
    print(f"\n{'=' * 60}")
    print(f"📦 下载配置")
    print(f"{'=' * 60}")
    print(f"📋 待下载视频数量: {len(urls)}")
    print(f"⚡ 下载模式: 最多同时下载 {max_concurrent} 个视频 (每个视频内音视频并发)")
    mode_text = {
        "merge": "音视频合并保存",
        "separate": "音视频分开保存",
        "audio_only": "仅保存音频",
        "video_only": "仅保存视频",
    }
    print(f"🎛️ 保存方式: {mode_text.get(save_mode, '音视频合并保存')}")
    print(f"💾 输出目录: {config.OUTPUT_PATH}")
    print("🔗 本次将下载以下 URL:")
    for i, url in enumerate(urls, 1):
        print(f"  {i}. {url}")
    print(f"{'=' * 60}\n")

    b = BFacade()
    await b.download(urls, max_concurrent=max_concurrent)

    # 计算用时
    end_time = time.time()
    times = round(end_time - start_time)
    minutes = times // 60
    times %= 60
    seconds = times

    # 清理临时目录
    if os.path.exists(config.TEMP_PATH):
        try:
            shutil.rmtree(config.TEMP_PATH)
            print(f"\n🧹 已清理临时文件")
        except Exception as e:
            print(f"\n⚠️  清理临时目录失败: {e}")

    # 输出下载摘要
    print(f"\n{'=' * 60}")
    print("📊 下载摘要")
    print(f"{'=' * 60}")
    print(f"✅ 成功: {len(b.downloaded_videos)} 个音视频")
    if b.failed_videos:
        print(f"❌ 失败: {len(b.failed_videos)} 个音视频")
    print(f"⏱️  总计用时：{minutes}分钟{seconds}秒")

    if b.downloaded_videos:
        print(f"\n已下载的音视频：")
        for i, video in enumerate(b.downloaded_videos, 1):
            quality_name = video.quality.get(video.quality_id, f"未知 (ID={video.quality_id})")
            print(f"  {i}. {video.title} ({quality_name})")

    # 显示失败的音视频及其 URL，便于用户重试
    if b.failed_videos:
        print(f"\n{'─' * 60}")
        print("❌ 下载失败的音视频：")
        print("─" * 60)
        for i, video in enumerate(b.failed_videos, 1):
            # 显示音视频标题
            if hasattr(video, 'part_number') and video.part_number >= 1:
                print(f"  {i}. {video.title}_P{video.part_number}")
            else:
                print(f"  {i}. {video.title}")
            # 显示原始 URL，便于用户复制重试
            if hasattr(video, 'source_url'):
                print(f"     URL: {video.source_url}")

        # 导出失败的 URL 到文件
        failed_urls_file = os.path.join(config.BASE_PATH, "failed_urls.txt")
        with open(failed_urls_file, "w", encoding="utf-8") as f:
            f.write("# 下载失败的音视频 URL，可复制到 config.py 中的 URL 列表重试\n")
            for video in b.failed_videos:
                if hasattr(video, 'source_url'):
                    f.write(f"'{video.source_url}',\n")
        print(f"\n💡 失败的 URL 已保存到: {failed_urls_file}")

    print(f"\n💾 音视频保存位置：{config.OUTPUT_PATH}")
    print(f"{'=' * 60}\n")


def main():
    """同步入口，运行异步主函数"""
    asyncio.run(async_main())


if __name__ == '__main__':
    main()
