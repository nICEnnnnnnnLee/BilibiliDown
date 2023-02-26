package nicelee.bilibili.downloaders.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.ui.item.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.bilibili.annotations.Bilibili;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.VersionManagerUtil;
import nicelee.ui.Global;

@Bilibili(name = "version-beta-downloader", type = "downloader", note = "最新的Beta版本下载")
public class VersionBetaDownloader extends VersionDownloader {

	private static final Pattern pattern = Pattern.compile("^BilibiliDown\\.PreRelease$");

	@Override
	public boolean matches(String url) {
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 下载matches
	 * 
	 * @param url
	 * @param avId
	 * @param qn 
	 * @param page 
	 * @return
	 */
	@Override
	public boolean download(String _url, String avId, int qn, int page) {
		convertingStatus = StatusEnum.NONE;
		currentTask = 1;

		HashMap<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Bearer " + Global.githubToken);
		headers.put("X-GitHub-Api-Version", "2022-11-28");
		headers.put("Accept", "application/vnd.github+json");
		// 获取artifacts
		Logger.println("开始获取artifacts...");
		String apiGetWorkflows = "https://api.github.com/repos/nICEnnnnnnnLee/BilibiliDown/actions/runs"
				+ "?branch=dev&event=workflow_dispatch&status=success&per_page=5";
		String r1 = util.getContent(apiGetWorkflows, headers);
		JSONObject workflowRun = null;
		JSONArray workflowRuns = new JSONObject(r1).getJSONArray("workflow_runs");
		for (int i = 0; i < workflowRuns.length(); i++) {
			JSONObject run = workflowRuns.getJSONObject(0);
			if ("Build Pre Release".equals(run.optString("name"))) {
				workflowRun = run;
				break;
			}
		}
		// 获取artifact 名称和下载链接
		Logger.println("开始获取artifact 名称和下载链接...");
		String apiGetArtifacts = workflowRun.getString("artifacts_url");
		String r2 = util.getContent(apiGetArtifacts, headers);
		JSONObject artifact = new JSONObject(r2).getJSONArray("artifacts").getJSONObject(0);
		String downName = artifact.getString("name") + "." + artifact.optString("id") + ".zip";
		String url = artifact.getString("archive_download_url");
		sumSuccessDownloaded = artifact.optLong("size_in_bytes", 0);
		Logger.println(downName);
		Logger.println(url);
		// 开始下载
		if (file == null) {
			file = new File(updateDir, downName);
		}
		try {
			util.setSavePath(updateDir.getCanonicalPath());
		} catch (IOException e1) {
		}
		boolean succ = util.download(url, downName, headers);
		if (succ) {
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
			try {
				VersionManagerUtil.unzipTargetJar(downName);
				Object[] options = { "是", "否" };
				int m = JOptionPane.showOptionDialog(null, "已经下载成功，需要关闭程序才能更新，现在是否重启?", "成功！",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (m == 0) {
					VersionManagerUtil.RunCmdAndCloseApp("1");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return succ;
	}

}
