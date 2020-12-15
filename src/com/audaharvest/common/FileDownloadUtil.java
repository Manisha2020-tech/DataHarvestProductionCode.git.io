package com.audaharvest.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

public class FileDownloadUtil {
	public File waitForNewFileDownload(Path folder, String extension, int timeout_sec) throws IOException, InterruptedException {
		long end_time = System.currentTimeMillis() + timeout_sec * 1000;
		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
			folder.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			for (WatchKey key; null != (key = watcher.poll(end_time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)); key.reset()) {
				for (WatchEvent<?> event : key.pollEvents()) {
					File file = folder.resolve(((WatchEvent<Path>)event).context()).toFile();
					if (file.toString().toLowerCase().endsWith(extension.toLowerCase()))
						return file;
				}
			}
		}
		return null;
	}
}
