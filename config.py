import os

# 程序根目录（请勿修改）
BASE_PATH = os.path.dirname(os.path.abspath(__file__))
# 文件临时输出目录
TEMP_PATH = os.path.join(BASE_PATH, "temp")
# 视频输出目录
OUTPUT_PATH = os.path.join(BASE_PATH, "output")

# 保存模式：
# - "merge": 合并音视频为 mp4（默认）
# - "separate": 音视频分开保存（.mp4 + .mp3）
# - "audio_only": 仅保存音频（.mp3）
# - "video_only": 仅保存视频（.mp4）
SAVE_MODE = "audio_only"

# B站登录后获取的SESSDATA，CURRENT_QUALITY
# 定期更换COOKIE的值即可
COOKIE = 'buvid3=B81AA3F6-E445-7009-48BB-71B8233DD48D05880infoc; b_nut=1767713405; _uuid=107A99C8E-1022B-101AE-51108-2CC987C63AFD09314infoc; home_feed_column=4; browser_resolution=1016-1046; buvid4=8F6AF051-DAB0-5A97-EBF9-B0EC1D0910F107112-026010623-bA9lBAlO14RTok3CI6PBrA%3D%3D; buvid_fp=f29d2d4a64ec63c9b45ac3cae4a351b4; SESSDATA=8059b023%2C1787377153%2Cc931e%2A22CjBmUui2aX84Qgfv_m4Nq6t8PxMLNYBcPyWAfdm0vdsXMpsM2l7dvn_6i2PhRMS7RZkSVnVkX2VPdUlZYWxvOF94dmpNTzhLNktqVzhBMWN6bnFudlc1Q2o2RG56ME9kbzVTTUd0bzhVZHk5dWw1M1YyTGNMRXBjWUNtQ2R5ZWRBOGZ2dGVPcmRnIIEC; bili_jct=1635a01ee404d5faedd82a07d711ca20; DedeUserID=8366997; DedeUserID__ckMd5=b6567189d34e3723; rpdid=|(u)~Jkmlu~u0J\'u~Y~~uJRRR; theme-tip-show=SHOWED; theme-avatar-tip-show=SHOWED; CURRENT_QUALITY=80; b_lsid=8E6E7753_19BCAEE18AE; bmg_af_switch=1; bmg_src_def_domain=i1.hdslb.com; bp_t_offset_8366997=1158772534259220480; bili_ticket=eyJhbGciOiJIUzI1NiIsImtpZCI6InMwMyIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3Njg4OTU0MDgsImlhdCI6MTc2ODYzNjE0OCwicGx0IjotMX0.8GNGf3Zbv2RMDe553BDRpJVmt-Tghl1CGJuItEowSzo; bili_ticket_expires=1768895348; sid=784uch0i; CURRENT_FNVAL=4048'



URL = [
    'https://www.bilibili.com/video/BV1bue1zCEqw/?share_source=copy_web&vd_source=27b696afad95676d506ce6db6baac8e0',

    # # 普通视频
    # 'https://www.bilibili.com/video/BV1M4411c7P4/?vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63',
    # 'https://www.bilibili.com/video/BV1hB4y147j8/?spm_id_from=333.337.search-card.all.click&vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63',

    # # 分P视频（第1个分P）
    # 'https://www.bilibili.com/video/BV1TnsZzHEcz/?vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63&spm_id_from=333.788.videopod.episodes',

    # # 分P视频（第2个分P）
    # 'https://www.bilibili.com/video/BV1TnsZzHEcz/?p=2&vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63',

    # 充电专属视频
    # 'https://www.bilibili.com/video/BV12gYxz7ESf/?spm_id_from=333.1387.homepage.video_card.click&vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63',
    # 'https://www.bilibili.com/video/BV12gYxz7ESf?spm_id_from=333.788.videopod.episodes&vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63&p=2',
    # 'https://www.bilibili.com/video/BV12gYxz7ESf?spm_id_from=333.788.videopod.episodes&vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63&p=3',
    # 'https://www.bilibili.com/video/BV12gYxz7ESf?spm_id_from=333.788.videopod.episodes&vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63&p=4',
    # 'https://www.bilibili.com/video/BV12gYxz7ESf?spm_id_from=333.788.videopod.episodes&vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63&p=5',
    # 'https://www.bilibili.com/video/BV12gYxz7ESf?spm_id_from=333.788.videopod.episodes&vd_source=9c3224b88b8a3c4cc210fc6ff9b28f63&p=6',

    # 番剧/电影（需要中国大陆 IP）
    # 'https://www.bilibili.com/bangumi/play/ss39429',      # 电影
    # 'https://www.bilibili.com/bangumi/play/ep271002',     # 番剧单集（暂不支持）
]