from abc import ABC, abstractmethod
from bs4 import BeautifulSoup

from models.video import Video


class BilibiliStrategy(ABC):

    def __init__(self) -> None:
        super().__init__()

    def set_proxy(self):
        pass

    @abstractmethod
    def get_video_page(self, url: str) -> BeautifulSoup:
        pass

    @abstractmethod
    def get_video_title(self, bs: BeautifulSoup) -> str:
        pass

    @abstractmethod
    def get_video_json(self, bs: BeautifulSoup) -> str:
        pass

    @abstractmethod
    def get(self, video: Video):
        pass
