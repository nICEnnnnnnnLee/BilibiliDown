from bs4 import BeautifulSoup
import httpx
import asyncio

import config
from strategy.bilibili_strategy import BilibiliStrategy
from models.video import Video

import re
import json


class DefaultStrategy(BilibiliStrategy):

    def __init__(self) -> None:
        super().__init__()
        # 请求头配置
        self.headers = {
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

    async def get_video_page(self, url: str, max_retries: int = 3) -> BeautifulSoup:
        timeout = httpx.Timeout(60.0, connect=15.0)
        for attempt in range(max_retries):
            try:
                async with httpx.AsyncClient(follow_redirects=True, timeout=timeout) as client:
                    response = await client.get(url=url, headers=self.headers)
                    response.raise_for_status()
                    bs = BeautifulSoup(response.text, 'html.parser')
                    return bs
            except (httpx.ReadTimeout, httpx.ConnectTimeout, httpx.RequestError) as e:
                if attempt < max_retries - 1:
                    wait_time = (attempt + 1) * 5  # 递增等待时间
                    print(f"⚠️  获取页面超时，{wait_time}秒后重试 ({attempt + 1}/{max_retries})...")
                    await asyncio.sleep(wait_time)
                else:
                    raise

    def get_video_title(self, bs: BeautifulSoup) -> str:
        # 普通视频优先使用 <h1> 标签
        h1_tag = bs.find('h1')
        if h1_tag:
            video_title = h1_tag.get_text()
            return video_title

        # 备用方案：使用 <title> 标签
        title_tag = bs.find('title')
        if title_tag:
            return title_tag.get_text()

        raise ValueError("无法获取视频标题")

    def get_video_json(self, bs: BeautifulSoup) -> dict:
        """只处理 window.__playinfo__ 格式（普通视频）"""
        pattern = re.compile(r"window\.__playinfo__=(.*?)$", re.MULTILINE | re.DOTALL)
        script = bs.find("script", text=pattern)

        if script is None:
            raise ValueError(
                "未找到视频数据 (window.__playinfo__)。\n"
                "可能原因：\n"
                "1. URL 不是普通视频类型（可能是番剧/电影，应使用 BangumiStrategy）\n"
                "2. 视频已下架或不存在\n"
                "3. 需要登录或会员权限"
            )

        result = pattern.search(script.next).group(1)
        video_json = json.loads(result)
        return video_json

    async def get(self, video: Video) -> Video:
        bs = await self.get_video_page(video.url)
        title = self.get_video_title(bs)
        json_data = self.get_video_json(bs)

        data = json_data['data']

        # 检查是否为 durl 格式（充电专属视频的分P等场景）
        if 'dash' in data:
            # DASH 格式：音视频分离
            video_streams = data['dash']['video']
            audio_streams = data['dash']['audio']

            # 选择最高质量（第一个元素）
            best_video = video_streams[0]
            quality_id = best_video['id']
            video_url = best_video['baseUrl']

            # 选择最高质量音频
            best_audio = audio_streams[0]
            audio_url = best_audio['baseUrl']

            video.set_title(title)
            video.set_quality(quality_id)
            video.set_video_url(video_url)
            video.set_audio_url(audio_url)

            # 显示所有可用质量（用于调试）
            available_qualities = list(set([v['id'] for v in video_streams]))
            available_qualities.sort(reverse=True)

        elif 'durl' in data:
            # DURL 格式：音视频合并（充电专属视频的分P等）
            video.is_durl = True
            durl = data['durl'][0]  # 通常只有一个元素
            quality_id = data.get('quality', 32)
            video_url = durl['url']

            video.set_title(title)
            video.set_quality(quality_id)
            video.set_video_url(video_url)
            video.set_audio_url(None)  # durl 格式没有单独的音频

            available_qualities = data.get('accept_quality', [quality_id])

        else:
            raise ValueError(
                "未找到视频数据（dash 或 durl）。\n"
                "可能原因：\n"
                "1. 视频已下架或不存在\n"
                "2. 需要登录或会员权限\n"
                "3. 视频格式不支持"
            )


        return video
