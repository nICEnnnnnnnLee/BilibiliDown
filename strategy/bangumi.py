from bs4 import BeautifulSoup
import httpx
import asyncio

import config
from strategy.bilibili_strategy import BilibiliStrategy
from models.video import Video

import re
import json


class BangumiStrategy(BilibiliStrategy):

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
        # 番剧/电影使用 <title> 标签（因为可能没有 <h1>）
        title_tag = bs.find('title')
        if title_tag:
            video_title = title_tag.get_text()
            return video_title

        raise ValueError("无法获取视频标题")

    def get_video_json(self, bs: BeautifulSoup) -> dict:
        """处理 OGV 内容的多种数据格式"""
        # 路径 1: 尝试 window.__playinfo__ (部分番剧)
        pattern = re.compile(r"window\.__playinfo__=(.*?)$", re.MULTILINE | re.DOTALL)
        script = bs.find("script", text=pattern)
        if script is not None:
            result = pattern.search(script.next).group(1)
            video_json = json.loads(result)
            return video_json

        # 路径 2: 尝试 playurlSSRData (会员内容/电影)
        script = bs.find("script", string=re.compile("playurlSSRData"))
        if script:
            pattern = re.compile(
                r"const\s+playurlSSRData\s*=\s*(\{.*?\})\s*window",
                re.DOTALL
            )
            match = pattern.search(script.string)
            if match:
                json_str = match.group(1)
                data = json.loads(json_str)
                return data.get('data')

        return None

    def _check_area_limit(self, json_data: dict) -> None:
        """检查并抛出地区限制错误"""
        if not json_data or 'result' not in json_data:
            return

        plugins = json_data['result'].get('plugins', [])
        for plugin in plugins:
            if plugin.get('name') == 'AreaLimitPanel':
                if plugin.get('config', {}).get('is_block', False):
                    raise Exception(
                        "⚠️  该视频受地区限制，无法在当前地区播放\n\n"
                        "可能的解决方案：\n"
                        "1. 使用中国大陆的 VPN/代理服务\n"
                        "2. 尝试下载其他无地区限制的视频\n"
                        "3. 检查视频是否需要特定的会员权限"
                    )

    async def get(self, video: Video) -> Video:
        bs = await self.get_video_page(video.url)
        title = self.get_video_title(bs)
        json_data = self.get_video_json(bs)

        # 检查地区限制
        self._check_area_limit(json_data)

        if not json_data:
            raise ValueError("无法获取视频数据")

        # 根据不同格式提取数据
        if 'result' in json_data:
            video_info = json_data['result'].get('video_info', {})

            if 'dash' not in video_info:
                raise ValueError(
                    "该番剧/电影无法获取播放链接\n"
                    "可能原因：\n"
                    "1. 地区限制（即使通过检测，某些内容仍可能无法播放）\n"
                    "2. 需要特定会员权限\n"
                    "3. 需要额外的 API 调用来获取播放地址"
                )

            # 有 dash 数据的情况 (result.video_info.dash)
            video_streams = video_info['dash']['video']
            audio_streams = video_info['dash']['audio']

            # 选择最高质量（第一个元素）
            best_video = video_streams[0]
            quality_id = best_video['id']
            video_url = best_video['base_url']

            # 选择最高质量音频
            best_audio = audio_streams[0]
            audio_url = best_audio['base_url']
        else:
            # 使用标准格式 (data.dash)
            video_streams = json_data['data']['dash']['video']
            audio_streams = json_data['data']['dash']['audio']

            # 选择最高质量（第一个元素）
            best_video = video_streams[0]
            quality_id = best_video['id']
            video_url = best_video['baseUrl']

            # 选择最高质量音频
            best_audio = audio_streams[0]
            audio_url = best_audio['baseUrl']

        # 显示所有可用质量（用于调试）
        available_qualities = list(set([v['id'] for v in video_streams]))
        available_qualities.sort(reverse=True)

        video.set_title(title)
        video.set_quality(quality_id)
        video.set_video_url(video_url)
        video.set_audio_url(audio_url)

        return video
